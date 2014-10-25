package controllers;

import models.Person;
import play.*;

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

public class Rest extends Controller {
    /*
    public static Result index() {
        return ok(index.render("Testing Testing	"));
    }
    
    public static Result addPerson(){
    	Person person = Form.form(Person.class).bindFromRequest().get();
    	person.save();
    	Logger.debug(Form.form().bindFromRequest().get("name"));
    	return redirect(routes.Application.index());
    	//return ok(toJson(person));
    }
    
    public static Result getPersons(){
    	List<Person> persons = new Model.Finder(String.class, Person.class).all();
    	return ok(toJson(persons));
    }*/
    
    /*    metodos rest   */
	//@BodyParser.Of(Xml.class)
    
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
	
	@BodyParser.Of(BodyParser.Xml.class)
	public static Result postRuns() throws TransformerException{
		try{
			Logger.debug("postRuns()");
	    	//String document = request().body().toString();
	    	//Logger.debug(document);
	    	
	    	Document dom = request().body().asXml();
	    	Logger.debug(GetStringFromDocument(dom));
	    	//set identificador de workflow		
	    	response().setContentType("text/html");
	    	response().setHeader("Location", "uuid-4289749382749");
	    	
			return status(201, "Created");
    	}catch(Exception ex){
    		return status(403,"Forbidden");
    	}		
    	
    }

	@BodyParser.Of(BodyParser.Xml.class)
	public static Result putRunsInputId(String id){
    	try{
    		Logger.debug("putRunsInputId("+id+")");
    		//String document = request().body().toString();
	    	//Logger.debug(document);
	    	
	    	Document dom = request().body().asXml();
	    	Logger.debug(GetStringFromDocument(dom));
	    	//set identificador de workflow		
	    	//response().setContentType("text/html");
	    	//response().setHeader("Location", "uuid-4289749382749");
	    	
			return ok();
    	}catch(Exception ex){
    		return status(403,"Forbidden");
    	}		
    }

	@BodyParser.Of(BodyParser.Text.class)
	public static Result putRunsStatus(String id){
    	try{
    		Logger.debug("putRunsStatus("+id+")");
        	//String document = request().body().toString();
	    	//Logger.debug(document);
	    	
	    	String text = request().body().asText();
	    	Logger.debug(text);
	    	//set identificador de workflow		
	    	//response().setContentType("text/html");
	    	//response().setHeader("Location", "uuid-4289749382749");
	    	
			return ok();
    	}catch(Exception ex){
    		return status(403,"Forbidden");
    	}	
    }

    public static Result getRunsStatus(String id){
    	Logger.debug("getRunsStatus("+id+")");
    	return ok();
    }

    public static Result getRunsOutputs(String id){
    	Logger.debug("getRunsOutputs("+ id+")");
    	return ok();
    }

    public static Result getRunsOutputPart(String id, String idPart){
    	
    	Logger.debug("getRunsOutputPart("+ id+", "+idPart+")");
    	//Logger.debug(request().body().toString());
    	return ok();
    }
    
}
