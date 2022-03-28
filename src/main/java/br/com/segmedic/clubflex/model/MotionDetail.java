package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.segmedic.clubflex.domain.enums.InvoiceType;
import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.excel.ExcelField;
import br.com.segmedic.clubflex.support.NumberUtils;

public class MotionDetail implements Serializable{
	
	private static final long serialVersionUID = -3545389088680243399L;
	
	private Long invoiceId;
	private InvoiceType invoiceType;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate competenceBegin;   
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate competenceEnd;
	private BigDecimal amountPaid;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate dueDate;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private LocalDate paymentDate;
	private String holderName;
	private String holderCpf;
	private Integer delay;
	private PaymentType paymentType;
	private Long subscriptionId;
	private String nfeNumber;
	private String authorizationCode;
	private String userResponsable;
	private String transactionId;
	private String nsu;
	private String description;
	
	@ExcelField(nome="Fatura", posicao=1)
	public Long getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	@ExcelField(nome="Tipo", posicao=1)
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getCompetenceBegin() {
		return competenceBegin;
	}
	public void setCompetenceBegin(LocalDate competenceBegin) {
		this.competenceBegin = competenceBegin;
	}
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getCompetenceEnd() {
		return competenceEnd;
	}
	public void setCompetenceEnd(LocalDate competenceEnd) {
		this.competenceEnd = competenceEnd;
	}
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public LocalDate getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	public Integer getDelay() {
		return delay;
	}
	public void setDelay(Integer delay) {
		this.delay = delay;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public String getAmountPaidFmt() {
		return NumberUtils.formatMoney(this.getAmountPaid());
	}
	public String getNfeNumber() {
		return nfeNumber;
	}
	public void setNfeNumber(String nfeNumber) {
		this.nfeNumber = nfeNumber;
	}
	public String getAuthorizationCode() {
		return authorizationCode;
	}
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
	public String getUserResponsable() {
		if(StringUtils.isBlank(this.userResponsable)) {
			return "Sistema";
		}
		return userResponsable;
	}
	public void setUserResponsable(String userResponsable) {
		this.userResponsable = userResponsable;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getNsu() {
		return nsu;
	}
	public void setNsu(String nsu) {
		this.nsu = nsu;
	}
	public String getHolderCpf() {
		return holderCpf;
	}
	public void setHolderCpf(String holderCpf) {
		this.holderCpf = holderCpf;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
