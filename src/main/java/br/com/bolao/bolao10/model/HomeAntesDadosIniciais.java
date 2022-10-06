
package br.com.bolao.bolao10.model;

import java.io.Serializable;

public class HomeAntesDadosIniciais implements Serializable {

	private static final long serialVersionUID = -2440650223L;

	private String valorTotal;
	private String valor1;
	private String valor2;
	private String valor3;
	private String valor4;
	private String valor5;
	private String valor6;
	private Integer qntdJogadores;
	

	public String getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(String valorTotal) {
		this.valorTotal = valorTotal;
	}
	public String getValor1() {
		return valor1;
	}
	public void setValor1(String valor1) {
		this.valor1 = valor1;
	}
	public String getValor2() {
		return valor2;
	}
	public void setValor2(String valor2) {
		this.valor2 = valor2;
	}
	public String getValor3() {
		return valor3;
	}
	public void setValor3(String valor3) {
		this.valor3 = valor3;
	}
	public String getValor4() {
		return valor4;
	}
	public void setValor4(String valor4) {
		this.valor4 = valor4;
	}
	public String getValor5() {
		return valor5;
	}
	public void setValor5(String valor5) {
		this.valor5 = valor5;
	}
	public String getValor6() {
		return valor6;
	}
	public void setValor6(String valor6) {
		this.valor6 = valor6;
	}
	public Integer getQntdJogadores() {
		return qntdJogadores;
	}
	public void setQntdJogadores(Integer qntdJogadores) {
		this.qntdJogadores = qntdJogadores;
	}
	
}
