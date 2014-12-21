package Logic;

import models.Workflow;
import play.db.ebean.Model;

public class DataBaseHandler {
	 public static void CreateWorkFlowDB(String uuid){
	    	Workflow wf = new Workflow();
	    	wf.uuid = uuid;
	    	wf.status = "Initialized"; 
	    	wf.save();
	    }
	    
	 public static void UpdateWorkflowStatus(String uuid, String status){
	    	Workflow dbPerson = (Workflow) new Model.Finder(String.class, Workflow.class).byId(uuid);
	    	
	    	dbPerson.updateStatus(status); 
		    dbPerson.save();
		}
	    
	 public static String GetWorkflowStatus(String uuid){
	    	Workflow dbPerson = (Workflow) new Model.Finder(String.class, Workflow.class).byId(uuid);
	    	return dbPerson.status;
	    }
}
