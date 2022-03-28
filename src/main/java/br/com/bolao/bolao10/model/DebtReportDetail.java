package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.segmedic.clubflex.support.NumberUtils;

public class DebtReportDetail implements Serializable{

	private static final long serialVersionUID = 7039602365547338644L;
	
	private Long holderId;
	private String holderName;
	private Long subscriptionId;
	private Integer quantityInvoices;
	private BigDecimal amountDebt;
	private String cellphone;
	private String homephone;
	private String mail;
	private String paymentType;
	private String noCard;
	
	public String getNoCard() {
		return noCard;
	}
	public void setNoCard(String noCard) {
		this.noCard = noCard;
	}
	public Long getHolderId() {
		return holderId;
	}
	public void setHolderId(Long holderId) {
		this.holderId = holderId;
	}
	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}
	public Long getSubscriptionId() {
		return subscriptionId;
	}
	public void setSubscriptionId(Long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}
	public Integer getQuantityInvoices() {
		return quantityInvoices;
	}
	public void setQuantityInvoices(Integer quantityInvoices) {
		this.quantityInvoices = quantityInvoices;
	}
	public BigDecimal getAmountDebt() {
		return amountDebt;
	}
	public void setAmountDebt(BigDecimal amountDebt) {
		this.amountDebt = amountDebt;
	}
	public String getAmountDebtFmt() {
		return NumberUtils.formatMoney(this.amountDebt);
	}
	public String getCellphone() {
		return cellphone;
	}
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}
	public String getHomephone() {
		return homephone;
	}
	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
}
