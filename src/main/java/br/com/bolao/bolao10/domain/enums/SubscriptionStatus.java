package br.com.bolao.bolao10.domain.enums;

public enum SubscriptionStatus {
	CANCELED("Cancelada"), OK("Ativa"), BLOCKED("Bloqueada"), OUT_OF_DATE("Fora de Vigência"), REQUESTED_CARD("Cartão Solicitado");
	
	private String describe;

	private SubscriptionStatus(String describe) {
		this.describe = describe;
	}

	public String getDescribe() {
		return describe;
	}
}
