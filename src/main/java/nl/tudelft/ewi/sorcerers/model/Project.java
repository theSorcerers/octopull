package nl.tudelft.ewi.sorcerers.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Project {
	@Id
	@GeneratedValue
	public Integer id;
	
	@Basic
	public String name;
}
