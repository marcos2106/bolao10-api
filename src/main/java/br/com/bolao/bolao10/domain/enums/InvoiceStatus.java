package br.com.bolao.bolao10.domain.enums;

public enum InvoiceStatus {
	PAID("Pago"), 
	CANCELLED("Cancelado"), 
	OPENED("Em Aberto"), 
	GENERATING("Gerando"), 
	PENDING("Pendente"), 
	DENIED("Negado"), 
	REFUNDED("Estornado"),
	TOTALREFUNDS("Estornado Total");
	
	private String describe;

	private InvoiceStatus(String describe) {
		this.describe = describe;
	}

	public String getDescribe() {
		return describe;
	}
}
