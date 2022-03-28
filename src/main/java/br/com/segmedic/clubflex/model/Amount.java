package br.com.segmedic.clubflex.model;

import java.math.BigDecimal;

public class Amount {
	
	private String describe;
	private BigDecimal amount;
	
	public Amount(String describe, BigDecimal amount) {
		super();
		this.describe = describe;
		this.amount = amount;
	}
	
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
