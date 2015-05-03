package nl.tudelft.ewi.sorcerers.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;

@Entity
public class PageView {
	@Id @GeneratedValue private long id;
	@Basic private String href;
	@Basic private String username;
	@ElementCollection
	@JoinTable(name="ATTRIBUTE_VALUE_META", joinColumns=@JoinColumn(name="ID"))
	@MapKeyColumn (name="META_ID")
	@Column(name="VALUE")
	private Map<String, String> metadata;
	@Basic private Date date;
	
	@SuppressWarnings("unused")
	private PageView() {}
	
	public PageView(String href, String user, Map<String, String> metadata, Date date) {
		this.href = href;
		this.username = user;
		this.metadata = metadata;
		this.date = date;		
	}
}
