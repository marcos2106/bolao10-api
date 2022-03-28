package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class ProductivityReportObject implements Serializable{

	private static final long serialVersionUID = 7384484584819834072L;

	private String describe;
	private Long quantity;
	
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
}
