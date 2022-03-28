package br.com.segmedic.clubflex.domain.enums;

public enum PaymentType {
	TICKETS("Carnê","carne"), 
	TICKET("Boleto Bancário","boleto bancário"), 
	CREDIT_CARD("Cartão de Crédito","credit"), 
	DEBIT_CARD("Cartão de Débito","debit"),
	CREDIT_CARD_LOCAL("Cartão de Crédito (Máquina)","credit"), 
	DEBIT_CARD_LOCAL("Cartão de Débito (Máquina)","debit"),
	MONEY("Dinheiro","dinheiro");
	
	private String describe;
	private String kind;

	private PaymentType(String describe, String kind) {
		this.describe = describe;
		this.kind = kind;
	}

	public String getDescribe() {
		return describe;
	}

	public String getKind() {
		return kind;
	}
}
