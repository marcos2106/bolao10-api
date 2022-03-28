package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class SubscriptionOperation implements Serializable{
	
	private static final long serialVersionUID = 3554241732706749973L;
	
	private Operation operation;
	private Long subscriptionId;
	
	public SubscriptionOperation() {
		super();
	}
	
	public SubscriptionOperation(Operation operation, Long subscriptionId) {
		super();
		this.operation = operation;
		this.subscriptionId = subscriptionId;
	}
	
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	
}
