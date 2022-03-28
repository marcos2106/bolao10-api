package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class UpdatePayDayRequest implements Serializable{

	private static final long serialVersionUID = -1033447617126228431L;
	
	private Integer newDay;
	private Long subscriptionId;
	
	public Integer getNewDay() {
		return newDay;
	}
	public void setNewDay(Integer newDay) {
		this.newDay = newDay;
	}
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
}
