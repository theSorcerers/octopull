package nl.tudelft.ewi.sorcerers.model;

import java.util.ArrayList;
import java.util.List;

public class Diff {
	private String base;
	private String head;
	private List<Warning> warnings;
	
	public Diff(String base, String head, List<Warning> warnings) {
		this.base = base;
		this.head = head;
		this.warnings = new ArrayList<Warning>(warnings);
	}

	public String getBase() {
		return this.base;
	}
	
	public String getHead() {
		return this.head;
	}
	
	public List<Warning> getWarnings() {
		return new ArrayList<Warning>(this.warnings);
	}
}
