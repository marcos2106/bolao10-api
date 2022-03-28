package br.com.segmedic.clubflex.excel;

/**
 * Corpo do excel
 */
public class ExcelBody {

	private Integer posicao;
	private String valor;

	public ExcelBody() {
		super();
	}

	public ExcelBody(Integer posicao, String valor) {
		super();
		this.posicao = posicao;
		this.valor = valor;
	}

	public Integer getPosicao() {
		return posicao;
	}

	public void setPosicao(Integer posicao) {
		this.posicao = posicao;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
