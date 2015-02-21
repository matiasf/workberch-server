package controllers;

import java.io.IOException;
import java.util.UUID;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.Document;

import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import redis.clients.jedis.Jedis;
import utils.FileHandler;

public class TavernaAPIController extends Controller {

	private static final String CREATE_FILE_NAME = "in.t2flow";
	private static final String TEXT_HTML = "text/html";
	private static final String APPLICATION_XML = "application/xml";
	private static final String LOCATION = "Location";
	private static final String CREATED = "Created";
	private static final String OPERATING = "Operating";
	private static final String FINISHED = "Finished";
	private static final String FORBIDDEN = "Forbidden";

	@BodyParser.Of(BodyParser.Xml.class)
	public static Result postRuns() throws TransformerException {
		try {
			final String directoryName = UUID.randomUUID().toString();
			final String directoryFullPath = FileHandler.ReadProperty("topology.workflow").replaceAll("guid", directoryName);

			if (!FileHandler.CreateDirectory(directoryFullPath, true)) {
				return status(403, FORBIDDEN);
			}

			final Document dom = request().body().asXml();
			final String strDom = FileHandler.GetStringFromDocument(dom);
			if (!FileHandler.CreateFile(directoryFullPath, CREATE_FILE_NAME, strDom)) {
				return status(403, FORBIDDEN);
			}
			response().setContentType(TEXT_HTML);
			response().setHeader(LOCATION, directoryName);

			return status(201, CREATED);
		} catch (final Exception ex) {
			return status(403, FORBIDDEN);
		}
	}

	@BodyParser.Of(BodyParser.Xml.class)
	public static Result putRunsInputId(final String id, final String idParam) {
		try {
			String directoryFullPath = FileHandler.ReadProperty("topology.input.workflow").replaceAll("guid", id);

			FileHandler.CreateDirectory(directoryFullPath, false);
			directoryFullPath = directoryFullPath + FileHandler.ReadProperty("topology.input.workflow.folder.name");
			FileHandler.CreateDirectory(directoryFullPath, false);

			final Document dom = request().body().asXml();
			final String strDom = FileHandler.GetStringFromDocument(dom);
			if (!FileHandler.CreateFile(directoryFullPath, idParam + ".xml", strDom)) {
				return status(403, FORBIDDEN);
			}

			return ok();
		} catch (final Exception ex) {
			return status(403, FORBIDDEN);
		}
	}

	@BodyParser.Of(BodyParser.Text.class)
	public static Result putRunsStatus(final String id) {
		try {
			final String text = request().body().asText();

			if (text.equals(OPERATING)) {
				final String topologyJarFile = FileHandler.ReadProperty("topology.jar.file");
				final String topologyWorkFlowFile = FileHandler.ReadProperty("topology.workflow");
				final String topologyWorkflowInputFiles = FileHandler.ReadProperty("topology.input.workflow")
						+ FileHandler.ReadProperty("topology.input.workflow.folder.name");
				final String topologyWorkflowOutputFiles = FileHandler.ReadProperty("topology.ouput.workflow")
						+ FileHandler.ReadProperty("topology.ouput.workflow.folder.name");

				final String runStorm = FileHandler.ReadProperty("storm.command") + " jar " + topologyJarFile + " "
						+ " main.java.DynamicWorkberchTopologyMain " + id + " " + topologyWorkFlowFile + CREATE_FILE_NAME + " "
						+ topologyWorkflowInputFiles + " " + topologyWorkflowOutputFiles + " remote";
				
				Logger.debug("Excecuting command: " + runStorm);

				Runtime.getRuntime().exec(runStorm);
			}

			return ok();
		} catch (final Exception ex) {
			return status(403, FORBIDDEN);
		}
	}

	public static Result getRunsStatus(final String id) {
		final Jedis jedis = new Jedis(FileHandler.ReadProperty("redis.server"));
		final long defOutputs = NumberUtils.toLong(jedis.get(id + "_outputs"), -1L);
		final long finishOutputs = NumberUtils.toLong(jedis.get(id + "_outputs_finished"), -1L);
		jedis.close();
		
		return defOutputs != -1L && defOutputs == finishOutputs ? ok(FINISHED) : ok(OPERATING);
	}

	public static Result getRunsOutputs(final String id) {
		final String directoryFullPath = FileHandler.ReadProperty("topology.ouput.workflow").replaceAll("guid", id)
				+ FileHandler.ReadProperty("topology.ouput.workflow.folder.name");

		final String strDom = FileHandler.GetWorkfowOutputFiles(directoryFullPath);
		response().setContentType(APPLICATION_XML);

		return ok(strDom);
	}

	public static Result getRunsOutputPart(final String id, final String idPart) throws IOException {
		final String directoryFullPath = FileHandler.ReadProperty("topology.ouput.workflow").replaceAll("guid", id)
				+ FileHandler.ReadProperty("topology.ouput.workflow.folder.name");

		final String strDom = FileHandler.GetWorkfowOutputFile(directoryFullPath, idPart);
		return ok(strDom);
	}
	
	public static Result deleteRuns(final String id){
		try {
			final String runStorm = FileHandler.ReadProperty("storm.command") + " kill " + id + " -w 30";
			
			Logger.debug("Excecuting command: " + runStorm);

			Runtime.getRuntime().exec(runStorm);
			return ok();
		} catch (final Exception ex) {
			return status(403, FORBIDDEN);
		}
	}	

}
