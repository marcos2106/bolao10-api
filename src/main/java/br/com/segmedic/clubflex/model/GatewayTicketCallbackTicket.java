package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GatewayTicketCallbackTicket implements Serializable{

	private static final long serialVersionUID = 2443342492348680696L;
	
	@JsonProperty("event_code")
	private String eventCode;
	
	@JsonProperty("object")
	private GatewayTicketRegisterResponse object;

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public GatewayTicketRegisterResponse getObject() {
		return object;
	}

	public void setObject(GatewayTicketRegisterResponse object) {
		this.object = object;
	}

	@Override
	public String toString() {
		return "GatwayTicketCallbackPayload [eventCode=" + eventCode + ", object=" + object + "]";
	}
}	
