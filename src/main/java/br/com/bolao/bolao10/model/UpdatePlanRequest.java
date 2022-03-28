package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class UpdatePlanRequest implements Serializable{

	private static final long serialVersionUID = -6984110944980010665L;
	
	private Long subscriptionId; 
	private Long planId;
	
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
}
