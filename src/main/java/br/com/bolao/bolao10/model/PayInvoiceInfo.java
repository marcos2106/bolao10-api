package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.segmedic.clubflex.domain.enums.PaymentType;

public class PayInvoiceInfo implements Serializable{
	private static final long serialVersionUID = 7257982339376118714L;

	private Long invoiceId;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dateOfPay;
	private String amountPaid;
	private PaymentType paymentType;
	private String altValue;
	private String refoundAmount;
	private Boolean valid;
	private PaymentType newPaymentType;
	private String justification;
	
	public Long getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getDateOfPay() {
		return dateOfPay;
	}
	public void setDateOfPay(LocalDate dateOfPay) {
		this.dateOfPay = dateOfPay;
	}
	public String getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public String getAltValue() {
		return altValue;
	}
	public void setAltValue(String altValue) {
		this.altValue = altValue;
	}
	public String getRefoundAmount() {
		return refoundAmount;
	}
	public void setRefoundAmount(String refoundAmount) {
		this.refoundAmount = refoundAmount;
	}
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	public PaymentType getNewPaymentType() {
		return newPaymentType;
	}
	public void setNewPaymentType(PaymentType newPaymentType) {
		this.newPaymentType = newPaymentType;
	}
	public String getJustification() {
		return justification;
	}
	public void setJustification(String justification) {
		this.justification = justification;
	}
	
}
