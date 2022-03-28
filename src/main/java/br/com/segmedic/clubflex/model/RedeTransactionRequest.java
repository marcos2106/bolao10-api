package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import br.com.segmedic.clubflex.domain.Invoice;

public class RedeTransactionRequest implements Serializable{
	
	private static final long serialVersionUID = 3984075983147376771L;

	private Boolean capture;
	private String reference;
	private Integer amount;
	private String cardNumber;
	private Integer expirationMonth;
	private Integer expirationYear;
	private String cardholderName;
	private String securityCode;
	
	public RedeTransactionRequest() {
		super();
	}
	
	public RedeTransactionRequest(Invoice invoice, Card card) {
		super();
		this.capture = true;
		this.reference = invoice.getSubscription().getId().toString();
		this.amount = invoice.getAmount().movePointRight(2).intValue();
		this.cardNumber = card.getNumber();
		this.expirationMonth = Integer.valueOf(card.getValidateMonth());
		this.expirationYear = Integer.valueOf(card.getValidateYear());
		this.cardholderName = card.getName();
		this.securityCode = card.getSecurityCode();
	}

	public Boolean getCapture() {
		return capture;
	}

	public void setCapture(Boolean capture) {
		this.capture = capture;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Integer getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(Integer expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public Integer getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(Integer expirationYear) {
		this.expirationYear = expirationYear;
	}

	public String getCardholderName() {
		return cardholderName;
	}

	public void setCardholderName(String cardholderName) {
		this.cardholderName = cardholderName;
	}

	public String getSecurityCode() {
		return securityCode;
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}
}