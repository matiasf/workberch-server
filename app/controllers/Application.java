package controllers;

import models.Person;
import play.*;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;
import play.Logger;
import views.html.*;

import java.util.List;

import static play.libs.Json.toJson;

public class Application extends Controller {

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
    }
    
    /*
    public static Result createPerson(Person newPerson) {
    	newPerson.save();
    	Logger.debug(Form.form().bindFromRequest().get("name"));
        return ok();
    }

    public static Result updatePerson(Long id, Person person) {
        Person dbPerson = (Person) new Model.Finder(String.class, Person.class).byId(id);
        dbPerson.updateDetails(person); 
        dbPerson.save();
        return ok();
    }

    public static Result deletePerson(Long id) {
    	Person dbPerson = (Person) new Model.Finder(String.class, Person.class).byId(id);
        // first check authority
        dbPerson.delete();
        return ok();
    }

    public static Result getPerson(Long id)  {
    	Person dbPerson = (Person) new Model.Finder(String.class, Person.class).byId(id);
        return ok(toJson(dbPerson));
    }

    public static Result getPersonJSON(Long id) {
    	Person dbPerson = (Person) new Model.Finder(String.class, Person.class).byId(id);
        return ok(toJson(dbPerson));
    }
    */
    
}
