package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Workflow extends Model {

	@Id
	public String uuid;

	public String status;

	public void updateStatus(String status2) {
		this.status = status2;	
	}

	
}
