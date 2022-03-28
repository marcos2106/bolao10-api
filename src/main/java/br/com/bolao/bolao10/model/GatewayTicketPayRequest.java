package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import br.com.segmedic.clubflex.domain.Invoice;

@JsonRootName(value="bank_billet")
public class GatewayTicketPayRequest implements Serializable{

	private static final long serialVersionUID = -1634060824099204932L;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("paid_at")
	private String paidAt;
	
	@JsonProperty("paid_amount")
	private String paidAmount;
	
	public GatewayTicketPayRequest() {
		super();
	}

	public GatewayTicketPayRequest(Invoice invoice) {
		super();
		this.id = invoice.getTransactId();
		this.paidAt = invoice.getPaymentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		this.paidAmount = invoice.getPayAmount().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
}
