package br.com.segmedic.clubflex.model;

import java.io.Serializable;

import br.com.segmedic.clubflex.domain.enums.PaymentType;

public class PaymentTypeObject implements Serializable{
	
	private static final long serialVersionUID = -7468656535427398347L;

	private String value;
	private String label;
	
	public PaymentTypeObject() {
		super();
	}
	
	public PaymentTypeObject(PaymentType type) {
		super();
		this.value = type.name();
		this.label = type.getDescribe();
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
