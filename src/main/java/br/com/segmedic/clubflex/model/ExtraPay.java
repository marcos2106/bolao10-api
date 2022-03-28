package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.segmedic.clubflex.domain.enums.InvoiceType;
import br.com.segmedic.clubflex.domain.enums.PaymentType;

public class ExtraPay implements Serializable{

	private static final long serialVersionUID = 3884365870296430294L;
	
	private InvoiceType invoiceType;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dueDate;
	private String amount;
	private PaymentType paymentType;
	private String describe;
	private Card creditcard;
	private Long subscriptionId;
	private Long invoiceRelation;
	private Integer installmentNumber;
	@JsonIgnore
	private Long userResponsibleId;
	
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public Card getCreditcard() {
		return creditcard;
	}
	public void setCreditcard(Card creditcard) {
		this.creditcard = creditcard;
	}
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public Long getInvoiceRelation() {
		return invoiceRelation;
	}
	public void setInvoiceRelation(Long invoiceRelation) {
		this.invoiceRelation = invoiceRelation;
	}
	public Long getUserResponsibleId() {
		return userResponsibleId;
	}
	public void setUserResponsibleId(Long userResponsibleId) {
		this.userResponsibleId = userResponsibleId;
	}
	public Integer getInstallmentNumber() {
		return installmentNumber;
	}
	public void setInstallmentNumber(Integer installmentNumber) {
		this.installmentNumber = installmentNumber;
	}
}
