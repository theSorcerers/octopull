package nl.tudelft.ewi.sorcerers.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class WarningId implements Serializable {
	private static final long serialVersionUID = -1379555617623700835L;
	
	@Id private String repo;
	@Id private String commit;
	@Id @GeneratedValue private Integer warningId;
}
