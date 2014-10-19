package controllers;

import models.Person;
import play.*;
import java.util.Map;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;
import play.Logger;
import views.html.*;

import java.util.List;

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
    public static Result postRuns(){
    	Logger.debug("postRuns()");
    	final Map<String, String[]> values = request().body().asFormUrlEncoded();
    	//Logger.debug(request().body().toString());
    	Logger.debug(request().body().asMultipartFormData().toString());
    	
    	return ok();
    }

    public static Result putRunsInputId(Long id){
    	Logger.debug("putRunsInputId("+id.toString()+")");
    	return ok();
    }

    public static Result putRunsStatus(Long id){
    	Logger.debug("putRunsStatus("+id.toString()+")");
    	return ok();
    }

    public static Result getRunsStatus(Long id){
    	Logger.debug("getRunsStatus("+id.toString()+")");
    	return ok();
    }

    public static Result getRunsOutputs(Long id){
    	Logger.debug("getRunsOutputs("+ id.toString()+")");
    	return ok();
    }

    public static Result getRunsOutputPart(Long id, String idPart){
    	
    	Logger.debug("getRunsOutputPart("+ id.toString()+", "+idPart+")");
    	return ok();
    }
    
}
