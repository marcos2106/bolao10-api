package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.segmedic.clubflex.domain.enums.PaymentType;
import br.com.segmedic.clubflex.support.NumberUtils;

public class DebtReportTotal implements Serializable {

	private static final long serialVersionUID = -7200534695988918608L;
	
	private PaymentType paymentType;
	private BigDecimal total;
	
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public String getTotalFmt() {
		return NumberUtils.formatMoney(this.total);
	}
	
}
