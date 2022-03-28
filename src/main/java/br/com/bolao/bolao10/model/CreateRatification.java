package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class CreateRatification implements Serializable{
	
	private static final long serialVersionUID = 35542417454549973L;
	
	private Long id;
	private String justification;
	
	public CreateRatification() {
		super();
	}
	
	public CreateRatification(Long id, String justification) {
		super();
		this.id = id;
		this.justification = justification;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getJustification() {
		return justification;
	}
	public void setJustification(String justification) {
		this.justification = justification;
	}
}
