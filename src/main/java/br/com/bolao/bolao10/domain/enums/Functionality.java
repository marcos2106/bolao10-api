package br.com.segmedic.clubflex.domain.enums;

public enum Functionality {
	
	QUITAR_FATURA("Quitar Fatura"), 
	CANCELAR_FATURA("Cancelar Fatura"),
	ESTORNAR_FATURA("Estornar Fatura"),
	JUROS_FATURA("Juros na Fatura"),
	ALT_FORMA_PGTO_FATURA("Alterar Forma Pagamento da Fatura"),
	CRIAR_FATURA("Fatura Criada");
	
	private String describe;

	private Functionality(String describe) {
		this.describe = describe;
	}

	public String getDescribe() {
		return this.describe;
	}
}
