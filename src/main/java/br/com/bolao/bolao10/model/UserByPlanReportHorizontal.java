package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class UserByPlanReportHorizontal implements Serializable{

	private static final long serialVersionUID = -8014014056852370558L;

	private Long planId;
	private String planName;
	private Integer quantity;
	
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
