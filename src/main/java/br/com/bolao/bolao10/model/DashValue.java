package br.com.segmedic.clubflex.model;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.segmedic.clubflex.support.NumberUtils;

public class DashValue implements Serializable {

	private static final long serialVersionUID = -8576270231678239331L;
	
	private BigDecimal actualValue;
	private BigDecimal percentValue;
	
	public BigDecimal getActualValue() {
		return actualValue;
	}
	public void setActualValue(BigDecimal actualValue) {
		this.actualValue = actualValue;
	}
	public BigDecimal getPercentValue() {
		return percentValue;
	}
	public void setPercentValue(BigDecimal percentValue) {
		this.percentValue = percentValue;
	}
	
	public String getActualValueFmt() {
		return NumberUtils.formatMoney(this.actualValue).replace("R$", "");
	}
}
