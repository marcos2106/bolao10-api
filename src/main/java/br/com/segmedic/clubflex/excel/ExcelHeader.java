package br.com.segmedic.clubflex.excel;

/**
 * Cabe�alho do excel
 *
 */
public class ExcelHeader {

	private Integer posicao;
	private String nome;

	public ExcelHeader() {
		super();
	}

	public ExcelHeader(Integer posicao, String nome) {
		super();
		this.posicao = posicao;
		this.nome = nome;
	}

	public Integer getPosicao() {
		return posicao;
	}

	public void setPosicao(Integer posicao) {
		this.posicao = posicao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
