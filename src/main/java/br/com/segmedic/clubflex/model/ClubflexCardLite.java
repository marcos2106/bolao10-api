package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class ClubflexCardLite implements Serializable{
	
	private static final long serialVersionUID = 5186433650330332886L;

	private String numeration;
	private String name;
	private String type;
	
	public ClubflexCardLite(String numeration, String name, String type) {
		super();
		this.numeration = numeration;
		this.name = name;
		this.type = type;
	}
	public String getNumeration() {
		return numeration;
	}
	public void setNumeration(String numeration) {
		this.numeration = numeration;
	}
	public String getName() {
		if(this.name != null) {
			return StringUtils.left(this.name, 39);
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
