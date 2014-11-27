package controllers;

import models.Person;
import models.Workflow;
import play.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import play.data.Form;
import play.db.ebean.Model;
import play.libs.XPath;
import play.mvc.*;
import play.Logger;
import views.html.*;

import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;

import static play.libs.Json.toJson;

import java.util.UUID;

public class Rest extends Controller {
    
	static boolean PRINT_DEBUG = false;
	
	
	static String FORBIDDEN = "Forbidden";
	static String CREATE_FILE_NAME = "in.t2flow";
	static String CREATE_FILE_INPUT = "in";
	static String TEXT_HTML = "text/html";
	static String APPLICATION_XML = "application/xml";
	static String LOCATION = "Location";
	static String CREATED = "Created";
	static String OPERATING = "Operating";
	static String CREATE_FILE_OUT = "out";
	static String FINISHED = "Finished";
    static String FILES = "./Files/";
	
	/*    metodos rest   */

	@BodyParser.Of(BodyParser.Xml.class)
	public static Result postRuns() throws TransformerException{
		try{
			Logger.debug("postRuns()");
			
			//Create ID and Folder to store the file
			String directoryName = UUID.randomUUID().toString();
			Logger.debug(directoryName);
			if (! CreateDirectory(directoryName)){
				return status(403,FORBIDDEN);
			}
			//Create xml file
			Document dom = request().body().asXml();
			String strDom = GetStringFromDocument(dom);
			if (!CreateFile(directoryName,CREATE_FILE_NAME, strDom)){		
				return status(403,FORBIDDEN);
			}
			//print XML
			if(PRINT_DEBUG){					 
		    	Logger.debug(strDom);
			}
	    	//set ID of workflow		
	    	response().setContentType(TEXT_HTML);
	    	response().setHeader(LOCATION, directoryName);
	    	
	    	//create db workflow information
	    	CreateWorkFlowDB(directoryName);
	    	
			return status(201, CREATED);			
    	}catch(Exception ex){
    		return status(403,FORBIDDEN);
    	}		
    	
    }

	@BodyParser.Of(BodyParser.Xml.class)
	public static Result putRunsInputId(String id, String idParam){
    	try{    		
    		Logger.debug("putRunsInputId("+ id+", "+idParam+")");
    		if(!ExistesDirectory(id)){
    			return status(403,FORBIDDEN);
    		}
    		CreateDirectory(id+"/in");
    		//Create xml file
			Document dom = request().body().asXml();
			String strDom = GetStringFromDocument(dom);
			if (!CreateFile(id+"/in",idParam, strDom)){		
				return status(403,FORBIDDEN);
			}
			
    		//print XML
			if(PRINT_DEBUG){	
				Logger.debug(strDom);
			}
			
			return ok();
    	}catch(Exception ex){
    		return status(403,FORBIDDEN);
    	}		
    }

	@BodyParser.Of(BodyParser.Text.class)
	public static Result putRunsStatus(String id){
    	try{
    		Logger.debug("putRunsStatus("+id+")");
        	
	    	String text = request().body().asText();
	    	Logger.debug("Receive status: "+text);
	    	
	    	if (text.equals(OPERATING)){
	    		UpdateWorkflowStatus(id, text);	
	    		//ejecutar storm	    		
	    	}	
	    	
			return ok();
    	}catch(Exception ex){
    		return status(403,FORBIDDEN);
    	}	
    }

    public static Result getRunsStatus(String id){
    	Logger.debug("getRunsStatus("+id+")");
    	
    	if (GetWorkflowStatus(id).equals(OPERATING)){
    		if (ExistesFile(id,CREATE_FILE_OUT)){
    			UpdateWorkflowStatus(id, FINISHED);
    		}
    	}
    	return ok(GetWorkflowStatus(id));
    }

    public static Result getRunsOutputs(String id){
    	Logger.debug("getRunsOutputs("+ id+")");
    	String strDom = GetWorkfowOutputFiles(id);
    	//print XML
		if(PRINT_DEBUG){	
			Logger.debug(strDom);
		}
		response().setContentType(APPLICATION_XML);
    	
		return ok(strDom);
    }

    public static Result getRunsOutputPart(String id, String idPart) throws IOException{
    	
    	Logger.debug("getRunsOutputPart("+ id+", "+idPart+")");
    	//Logger.debug(request().body().toString());
    	String strDom = GetWorkfowOutputFile(id, idPart);
    	//print XML
		if(PRINT_DEBUG){	
			Logger.debug(strDom);
		}
    	return ok(strDom);
    }    
	    
	/* metodos auxiliares */
    
	static private String GetStringFromDocument(Document dom) throws TransformerException{
		//set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		
		//create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(dom);
		trans.transform(source, result);
		String xmlString = sw.toString();
		return xmlString;
	}
	
	private static boolean CreateDirectory(String directoryName){
		File theDir = new File(FILES+directoryName);
		boolean result = false;
		if (!theDir.exists()) {
	        theDir.mkdir();
	        result = true;
		}
		return result;
	}
	
	private static boolean ExistesDirectory(String directoryName){
		File theDir = new File(FILES+directoryName);
		return theDir.exists();
	}
	
	private static boolean ExistesFile(String directoryName, String filename){
		File theDir = new File(FILES+directoryName+"/"+filename);
		return theDir.exists();
	}
	
	private static boolean CreateFile(String directoryName, String fileName, String content) throws IOException{
		
		File file = new File(FILES+directoryName+"/"+fileName);
	    FileWriter fw = new FileWriter(file.getAbsoluteFile());
	    BufferedWriter bw = new BufferedWriter(fw);
	    bw.write(content);
	    bw.close();
		return true;
	}

    private static void CreateWorkFlowDB(String uuid){
    	Workflow wf = new Workflow();
    	wf.uuid = uuid;
    	wf.status = "Initialized"; 
    	wf.save();
    }
    
    private static void UpdateWorkflowStatus(String uuid, String status){
    	Workflow dbPerson = (Workflow) new Model.Finder(String.class, Workflow.class).byId(uuid);
    	
    	dbPerson.updateStatus(status); 
	    dbPerson.save();
	}
    
    private static String GetWorkflowStatus(String uuid){
    	Workflow dbPerson = (Workflow) new Model.Finder(String.class, Workflow.class).byId(uuid);
    	return dbPerson.status;
    }
    
    private static String GetWorkfowOutputFiles(String uuid){
    	String responseBegin = "<t2sr:directoryContents xmlns:xlink=\"http://www.w3.org/1999/xlink"+
        "xmlns:t2s=\"http://ns.taverna.org.uk/2010/xml/server/"+
        "xmlns:t2sr=\"http://ns.taverna.org.uk/2010/xml/server/rest/\">";
    
    	String responseEnd = "</t2sr:directoryContents>";
    
    	String responseMiddle = "";//"<t2s:file xlink:href=\"<RUN_URI>/wd/out/FOO.OUT\">out/FOO.OUT</t2s:file>";
    	File folder = new File(FILES+uuid+"/out");
    	File[] listOfFiles = folder.listFiles();

    	for (File file : listOfFiles) {
    	    if (file.isFile()) {
    	    	//Logger.debug(file.getName());
    	    	responseMiddle += "<t2s:file xlink:href=\"<RUN_URI>/wd/out/"+file.getName()+"\">out/"+file.getName()+"</t2s:file>";
    	    }
    	}
    	 
    	return responseBegin + responseMiddle + responseEnd;
    }
    
    private static String GetWorkfowOutputFile(String uuid, String idPart ) throws IOException{    	    
    	String responseMiddle = "";
    	BufferedReader br = null;
		String sCurrentLine;
 
		br = new BufferedReader(new FileReader(FILES+uuid+"/out/"+idPart));
 
		while ((sCurrentLine = br.readLine()) != null) {
			responseMiddle += sCurrentLine;
		}
		if (br != null)br.close();   	 
    	return responseMiddle;
    }
}
