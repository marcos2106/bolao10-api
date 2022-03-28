package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import br.com.segmedic.clubflex.domain.Subscription;

public class CreateSubscriptionResponse implements Serializable {

	private static final long serialVersionUID = 5645867776921901278L;
	
	private Subscription subscription;
	private String ticketUrl;
	
	public Subscription getSubscription() {
		return subscription;
	}
	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}
	public String getTicketUrl() {
		return ticketUrl;
	}
	public void setTicketUrl(String ticketUrl) {
		this.ticketUrl = ticketUrl;
	}
}
