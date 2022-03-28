package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class UserByPlanReportPlan implements Serializable{
	private static final long serialVersionUID = -166248835011325261L;
	
	private Long id;
	private String name;
	private Integer quantity;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
