package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.segmedic.clubflex.support.NumberUtils;

public class MotionReportTotal implements Serializable{

	private static final long serialVersionUID = 1179551970387944330L;
	private BigDecimal total = BigDecimal.ZERO;
	private BigDecimal totalCreditCard = BigDecimal.ZERO;
	private BigDecimal totalDebitCard = BigDecimal.ZERO;
	private BigDecimal totalTickets = BigDecimal.ZERO;
	private BigDecimal totalTicket = BigDecimal.ZERO;
	private BigDecimal totalCreditCardLocal = BigDecimal.ZERO;
	private BigDecimal totalDebitCardLocal = BigDecimal.ZERO;
	private BigDecimal totalMoney = BigDecimal.ZERO;
	
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getTotalCreditCard() {
		return totalCreditCard;
	}
	public void setTotalCreditCard(BigDecimal totalCreditCard) {
		this.totalCreditCard = totalCreditCard;
	}
	public BigDecimal getTotalDebitCard() {
		return totalDebitCard;
	}
	public void setTotalDebitCard(BigDecimal totalDebitCard) {
		this.totalDebitCard = totalDebitCard;
	}
	public BigDecimal getTotalTickets() {
		return totalTickets;
	}
	public void setTotalTickets(BigDecimal totalTickets) {
		this.totalTickets = totalTickets;
	}
	public BigDecimal getTotalTicket() {
		return totalTicket;
	}
	public void setTotalTicket(BigDecimal totalTicket) {
		this.totalTicket = totalTicket;
	}
	public BigDecimal getTotalCreditCardLocal() {
		return totalCreditCardLocal;
	}
	public void setTotalCreditCardLocal(BigDecimal totalCreditCardLocal) {
		this.totalCreditCardLocal = totalCreditCardLocal;
	}
	public BigDecimal getTotalDebitCardLocal() {
		return totalDebitCardLocal;
	}
	public void setTotalDebitCardLocal(BigDecimal totalDebitCardLocal) {
		this.totalDebitCardLocal = totalDebitCardLocal;
	}
	public BigDecimal getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	
	public void addTotal(BigDecimal value) {
		this.total = this.total.add(value);
	}
	public void addCreditCard(BigDecimal value) {
		this.totalCreditCard = this.totalCreditCard.add(value);
	}
	public void addDebitCard(BigDecimal value) {
		this.totalDebitCard = this.totalDebitCard.add(value);
	}
	public void addTickets(BigDecimal value) {
		this.totalTickets = this.totalTickets.add(value);
	}
	public void addTicket(BigDecimal value) {
		this.totalTicket = this.totalTicket.add(value);
	}
	public void addCreditCardLocal(BigDecimal value) {
		this.totalCreditCardLocal = this.totalCreditCardLocal.add(value);
	}
	public void addDebitCardLocal(BigDecimal value) {
		this.totalDebitCardLocal = this.totalDebitCardLocal.add(value);
	}
	public void addMoney(BigDecimal value) {
		this.totalMoney = this.totalMoney.add(value);
	}
	
	
	public String getTotalFmt() {
		return NumberUtils.formatMoney(total);
	}
	public String getTotalCreditCardFmt() {
		return NumberUtils.formatMoney(totalCreditCard);
	}
	public String getTotalDebitCardFmt() {
		return NumberUtils.formatMoney(totalDebitCard);
	}
	public String getTotalTicketsFmt() {
		return NumberUtils.formatMoney(totalTickets);
	}
	public String getTotalTicketFmt() {
		return NumberUtils.formatMoney(totalTicket);
	}
	public String getTotalCreditCardLocalFmt() {
		return NumberUtils.formatMoney(totalCreditCardLocal);
	}
	public String getTotalDebitCardLocalFmt() {
		return NumberUtils.formatMoney(totalDebitCardLocal);
	}
	public String getTotalMoneyFmt() {
		return NumberUtils.formatMoney(totalMoney);
	}
	
}
