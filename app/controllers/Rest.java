package controllers;

import java.io.*;

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

import java.util.Properties;

import org.w3c.dom.Document;

import Logic.DataBaseHandler;
import Logic.FileHandler;

import com.fasterxml.jackson.databind.JsonNode;

import static play.libs.Json.toJson;

import java.util.UUID;

public class Rest extends Controller {
    
	static boolean PRINT_DEBUG = false;
	
	static String CREATE_FILE_NAME = "in.t2flow";
	
	static String TEXT_HTML = "text/html";
	static String APPLICATION_XML = "application/xml";
	
	//static String CREATE_FILE_INPUT = "in";
	//static String FILES = "./Files/";
    //static String CREATE_FILE_OUT = "out";
	
	// taverna header name guid return value
    static String LOCATION = "Location";
    
	// taverna workflow status
	static String CREATED = "Created";
	static String OPERATING = "Operating";
	static String FINISHED = "Finished";
	static String FORBIDDEN = "Forbidden";
	
	/*public static Result index() {
-        return ok(index.render("Workberch-server"));
-    }
    */
	/*    metodos rest   */  
    
	@BodyParser.Of(BodyParser.Xml.class)
	public static Result postRuns() throws TransformerException{
		try{
			Logger.debug("postRuns()");
			
			//Create ID and Folder to store the file
			String directoryName = UUID.randomUUID().toString();
			String directoryFullPath = FileHandler.ReadProperty("topology.workflow").replaceAll("guid", directoryName);
			Logger.debug(directoryName);
			//Logger.error(directoryFullPath);
			if (! FileHandler.CreateDirectory(directoryFullPath, true)){
				return status(403,FORBIDDEN);
			}
			//Create xml file
			Document dom = request().body().asXml();
			String strDom = FileHandler.GetStringFromDocument(dom);
			if (!FileHandler.CreateFile(directoryFullPath,CREATE_FILE_NAME, strDom)){		
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
	    	DataBaseHandler.CreateWorkFlowDB(directoryName);
	    	
			return status(201, CREATED);			
    	}catch(Exception ex){
    		Logger.debug(ex.getMessage());
    		return status(403,FORBIDDEN);
    	}		
    	
    }

	@BodyParser.Of(BodyParser.Xml.class)
	public static Result putRunsInputId(String id, String idParam){
    	try{    		
    		Logger.debug("putRunsInputId("+ id+", "+idParam+")");
    		
    		String directoryFullPath = FileHandler.ReadProperty("topology.input.workflow").replaceAll("guid", id);			
    		Logger.debug(directoryFullPath);
    		 
    		FileHandler.CreateDirectory(directoryFullPath, false);
    		directoryFullPath = directoryFullPath + FileHandler.ReadProperty("topology.input.workflow.folder.name");
    		FileHandler.CreateDirectory(directoryFullPath, false);
		
    		//Create xml file
			Document dom = request().body().asXml();
			String strDom = FileHandler.GetStringFromDocument(dom);
			if (!FileHandler.CreateFile(directoryFullPath,idParam+".xml", strDom)){		
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
	    		DataBaseHandler.UpdateWorkflowStatus(id, text);	
	    		//ejecutar storm
	    		String topologyJarFile = FileHandler.ReadProperty("topology.jar.file");
	    		String topologyWorkFlowFile = FileHandler.ReadProperty("topology.workflow");
	    		String topologyWorkflowInputFiles = FileHandler.ReadProperty("topology.input.workflow")+FileHandler.ReadProperty("topology.input.workflow.folder.name");
	    		String topologyWorkflowOutputFiles =FileHandler.ReadProperty("topology.ouput.workflow")+FileHandler.ReadProperty("topology.ouput.workflow.folder.name");
	    		
	    		String runStorm  = "java -jar "+topologyJarFile+" "+ id + " "+topologyWorkFlowFile +CREATE_FILE_NAME + " " +topologyWorkflowInputFiles + " " + topologyWorkflowOutputFiles + "";
	    		 
	    		Logger.error(runStorm);
	    		
	    		//Process p = Runtime.getRuntime().exec("java -jar workberch-topogy-0.1-jar-with-dependencies.jar");
	    		Process p = Runtime.getRuntime().exec(runStorm);
	    		
	    		//p.waitFor();
	    	    /*
	    	    BufferedReader reader = 
	    	         new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	 
	    	    String line = "";			
	    	    while ((line = reader.readLine())!= null) {
	    	    	Logger.debug(line + "\n");
	    	    }
	    		*/
	    	}	
	    	
			return ok();
    	}catch(Exception ex){
    		Logger.error(ex.toString());
    		return status(403,FORBIDDEN);
    	}	
    }

    public static Result getRunsStatus(String id){
    	Logger.debug("getRunsStatus("+id+")");
    	
    	String directoryFullPath =FileHandler.ReadProperty("topology.ouput.workflow").replaceAll("guid", id)+FileHandler.ReadProperty("topology.ouput.workflow.folder.name");
		    	
    	//Logger.error(directoryFullPath );
    	if (DataBaseHandler.GetWorkflowStatus(id).equals(OPERATING)){
    		if (! FileHandler.IsEmptyFolder(directoryFullPath)){
    		//if (FileHandler.ExistesFile(FILES,id,CREATE_FILE_OUT)){
    			DataBaseHandler.UpdateWorkflowStatus(id, FINISHED);
    		}
    	}
    	return ok(DataBaseHandler.GetWorkflowStatus(id));
    }

    public static Result getRunsOutputs(String id){
    	Logger.debug("getRunsOutputs("+ id+")");
    	String directoryFullPath =FileHandler.ReadProperty("topology.ouput.workflow").replaceAll("guid", id)+FileHandler.ReadProperty("topology.ouput.workflow.folder.name");
		
		
    	String strDom = FileHandler.GetWorkfowOutputFiles(directoryFullPath);
    	//print XML
		if(PRINT_DEBUG){	
			Logger.debug(strDom);
		}
		response().setContentType(APPLICATION_XML);
    	
		return ok(strDom);
    }

    public static Result getRunsOutputPart(String id, String idPart) throws IOException{
    	
    	Logger.debug("getRunsOutputPart("+ id+", "+idPart+")");
    	
    	String directoryFullPath =FileHandler.ReadProperty("topology.ouput.workflow").replaceAll("guid", id)+FileHandler.ReadProperty("topology.ouput.workflow.folder.name");
		
    	String strDom = FileHandler.GetWorkfowOutputFile(directoryFullPath, idPart);
    	//print XML
		if(PRINT_DEBUG){	
			Logger.debug(strDom);
		}
    	return ok(strDom);
    }    
	    
	/* metodos auxiliares */
    
   
    
}
