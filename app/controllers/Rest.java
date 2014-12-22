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
	static String FILES = "./Files/";
    static String CREATE_FILE_OUT = "out";
	
    static String LOCATION = "Location";
	
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
			//String directoryName = "gid";
			Logger.debug(directoryName);
			if (! FileHandler.CreateDirectory(FILES, directoryName)){
				return status(403,FORBIDDEN);
			}
			//Create xml file
			Document dom = request().body().asXml();
			String strDom = FileHandler.GetStringFromDocument(dom);
			if (!FileHandler.CreateFile(FILES,directoryName,CREATE_FILE_NAME, strDom)){		
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
    		if(!FileHandler.ExistesDirectory(FILES,id)){
    			return status(403,FORBIDDEN);
    		}
    		FileHandler.CreateDirectory(FILES,id+"/in");
    		//Create xml file
			Document dom = request().body().asXml();
			String strDom = FileHandler.GetStringFromDocument(dom);
			if (!FileHandler.CreateFile(FILES,id+"/in",idParam+".xml", strDom)){		
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
	    		//java -jar workberch-topology-0.1-jar-with-dependencies.jar uuid input ouput
	    		//java -jar workberch-topogy-0.1-jar-with-dependencies.jar 12321321 "/home/proyecto/Code/workberch-server/Files/guid/input" "/home/proyecto/Code/workberch-server/Files/guid/ouput/"
	    		
				
	    		//java -jar workberch-topology-0.1-jar-with-dependencies.jar a b c

	    		String runStorm = "java -jar workberch-topology-0.1-jar-with-dependencies.jar " + id + " " +FileHandler.ReadProperty("topology.input.workflow") + " " + FileHandler.ReadProperty("topology.ouput.workflow")+ ""; 
	    		//runStorm =        "java -jar workberch-topology-0.1-jar-with-dependencies.jar " + id +" /home/proyecto/Code/workberch-server/Files/guid/in /home/proyecto/Code/workberch-server/Files/guid/out/";
	    	    
	    		//runStorm = "java -jar /home/proyecto/Code/workberch-tolopogy/target/workberch-topology-0.1.jar "+ id +" /home/proyecto/Code/workberch-tolopogy/ejemlo_base.t2flow /home/proyecto/Code/workberch-server/Files/guid/in/ /home/proyecto/Code/workberch-server/Files/guid/out/";
	    		runStorm  = "java -jar "+FileHandler.ReadProperty("topology.jar.file")+" "+ id + " "+ FileHandler.ReadProperty("topology.workflow")+id+ "/"+CREATE_FILE_NAME + " " +FileHandler.ReadProperty("topology.input.workflow") + " " + FileHandler.ReadProperty("topology.ouput.workflow")+ "";
	    		 
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
    	
    	if (DataBaseHandler.GetWorkflowStatus(id).equals(OPERATING)){
    		//if (FileHandler.EmptyFolder(FILES,id,CREATE_FILE_OUT)){
    		if (FileHandler.ExistesFile(FILES,id,CREATE_FILE_OUT)){
    			DataBaseHandler.UpdateWorkflowStatus(id, FINISHED);
    		}
    	}
    	return ok(DataBaseHandler.GetWorkflowStatus(id));
    }

    public static Result getRunsOutputs(String id){
    	Logger.debug("getRunsOutputs("+ id+")");
    	String strDom = FileHandler.GetWorkfowOutputFiles(FILES,id);
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
    	String strDom = FileHandler.GetWorkfowOutputFile(FILES,id, idPart);
    	//print XML
		if(PRINT_DEBUG){	
			Logger.debug(strDom);
		}
    	return ok(strDom);
    }    
	    
	/* metodos auxiliares */
    
   
    
}
