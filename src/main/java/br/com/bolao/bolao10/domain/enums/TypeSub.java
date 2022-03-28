package br.com.segmedic.clubflex.domain.enums;

public enum TypeSub {
	PJ("Pessoa Jurídica"), PF("Pessoa Física");
	
	private String describe;

	private TypeSub(String describe) {
		this.describe = describe;
	}

	public String getDescribe() {
		return describe;
	}
}
