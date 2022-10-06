
package br.com.bolao.bolao10.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "classificacao")
public class Classificacao implements Serializable {

	private static final long serialVersionUID = -21122L;

	@Id
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idselecao", nullable = false)	
	private Selecao selecao;
	
	@Column(name = "pontos", nullable = false, columnDefinition = "INT")
	private Integer pontos;
	
	@Column(name = "pontosanterior", nullable = false, columnDefinition = "INT")
	private Integer pontosAnterior;

	@Column(name = "vitoria", nullable = false, columnDefinition = "INT")
	private Integer vitoria;

	@Column(name = "vitoriaanterior", nullable = false, columnDefinition = "INT")
	private Integer vitoriaAnterior;
	
	@Column(name = "empate", nullable = false, columnDefinition = "INT")
	private Integer empate;
	
	@Column(name = "empateanterior", nullable = false, columnDefinition = "INT")
	private Integer empateAnterior;
	
	@Column(name = "derrota", nullable = false, columnDefinition = "INT")
	private Integer derrota;
	
	@Column(name = "derrotaanterior", nullable = false, columnDefinition = "INT")
	private Integer derrotaAnterior;
	
	@Column(name = "golspro", nullable = false, columnDefinition = "INT")
	private Integer golspro;
	
	@Column(name = "golsproanterior", nullable = false, columnDefinition = "INT")
	private Integer golsproAnterior;
	
	@Column(name = "golscontra", nullable = false, columnDefinition = "INT")
	private Integer golscontra;
	
	@Column(name = "golscontraanterior", nullable = false, columnDefinition = "INT")
	private Integer golscontraAnterior;
	
	@Column(name = "saldogols", nullable = false, columnDefinition = "INT")
	private Integer saldogols;
	
	@Column(name = "saldogolsanterior", nullable = false, columnDefinition = "INT")
	private Integer saldogolsAnterior;

	
	public Selecao getSelecao() {
		return selecao;
	}

	public void setSelecao(Selecao selecao) {
		this.selecao = selecao;
	}

	public Integer getPontos() {
		return pontos;
	}

	public void setPontos(Integer pontos) {
		this.pontos = pontos;
	}

	public Integer getVitoria() {
		return vitoria;
	}

	public void setVitoria(Integer vitoria) {
		this.vitoria = vitoria;
	}

	public Integer getEmpate() {
		return empate;
	}

	public void setEmpate(Integer empate) {
		this.empate = empate;
	}

	public Integer getDerrota() {
		return derrota;
	}

	public void setDerrota(Integer derrota) {
		this.derrota = derrota;
	}

	public Integer getGolspro() {
		return golspro;
	}

	public void setGolspro(Integer golspro) {
		this.golspro = golspro;
	}

	public Integer getGolscontra() {
		return golscontra;
	}

	public void setGolscontra(Integer golscontra) {
		this.golscontra = golscontra;
	}

	public Integer getSaldogols() {
		return saldogols;
	}

	public void setSaldogols(Integer saldogols) {
		this.saldogols = saldogols;
	}

	public Integer getPartidas() {
		return this.vitoria + this.empate + this.derrota;
	}

	public Integer getAproveitamento() {
		if (getPartidas() > 0) {
			Integer totalPts = (getPartidas() * 3);
			return getPontos() * 100 / totalPts;
		}
		return null;
	}

	public Integer getPontosAnterior() {
		return pontosAnterior;
	}

	public void setPontosAnterior(Integer pontosAnterior) {
		this.pontosAnterior = pontosAnterior;
	}

	public Integer getVitoriaAnterior() {
		return vitoriaAnterior;
	}

	public void setVitoriaAnterior(Integer vitoriaAnterior) {
		this.vitoriaAnterior = vitoriaAnterior;
	}

	public Integer getEmpateAnterior() {
		return empateAnterior;
	}

	public void setEmpateAnterior(Integer empateAnterior) {
		this.empateAnterior = empateAnterior;
	}

	public Integer getDerrotaAnterior() {
		return derrotaAnterior;
	}

	public void setDerrotaAnterior(Integer derrotaAnterior) {
		this.derrotaAnterior = derrotaAnterior;
	}

	public Integer getGolsproAnterior() {
		return golsproAnterior;
	}

	public void setGolsproAnterior(Integer golsproAnterior) {
		this.golsproAnterior = golsproAnterior;
	}

	public Integer getGolscontraAnterior() {
		return golscontraAnterior;
	}

	public void setGolscontraAnterior(Integer golscontraAnterior) {
		this.golscontraAnterior = golscontraAnterior;
	}

	public Integer getSaldogolsAnterior() {
		return saldogolsAnterior;
	}

	public void setSaldogolsAnterior(Integer saldogolsAnterior) {
		this.saldogolsAnterior = saldogolsAnterior;
	}
}
