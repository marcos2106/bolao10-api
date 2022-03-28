package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import br.com.segmedic.clubflex.domain.enums.PaymentType;

public class UpdatePaymentTypeRequest implements Serializable{

	private static final long serialVersionUID = 4100743126579080862L;

	private PaymentType newType;
	private Long subscriptionId;
	private Card card;
	
	public PaymentType getNewType() {
		return newType;
	}
	public void setNewType(PaymentType newType) {
		this.newType = newType;
	}
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public Card getCard() {
		return card;
	}
	public void setCard(Card card) {
		this.card = card;
	}
}
