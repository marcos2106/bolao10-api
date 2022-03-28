package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GatewayTicketRegisterResponse implements Serializable {

	
	private static final long serialVersionUID = -4897248623791621375L;

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("url")
	private String url;
	
	@JsonProperty("paid_at")
	private String paidAt;
	
	@JsonProperty("paid_amount")
	private String paidAmount;
	
	@JsonProperty("line")
	private String line;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(String paidAt) {
		this.paidAt = paidAt;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}
	
}
