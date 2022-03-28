package br.com.segmedic.clubflex.domain.enums;

public enum InvoiceType {
	DEFAULT("Mensalidade"), EXTRA("Extra"), AGREEMENT("Acordo");
	
	private String describe;

	private InvoiceType(String describe) {
		this.describe = describe;
	}

	public String getDescribe() {
		return describe;
	}
}
