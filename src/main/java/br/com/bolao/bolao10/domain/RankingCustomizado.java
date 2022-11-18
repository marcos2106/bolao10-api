
package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "ranking_customizado")
public class RankingCustomizado implements Serializable {

	private static final long serialVersionUID = -121114L;

	@Id
	@Column(name = "idranking_customizado", nullable = false, columnDefinition = "INT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario", nullable = false)
	private Usuario usuario;
	
	@Column(name = "nome", nullable = false, columnDefinition = "VARCHAR(100)")
	private String nome;

	@ManyToMany
	@JoinTable(
			name = "`ranking_customizado_ranking`", joinColumns = {
					@JoinColumn(name = "idranking_customizado", nullable = false)
			},
			inverseJoinColumns = {
					@JoinColumn(name = "idusuario", nullable = false)
			})
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Ranking> listaRanking;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Ranking> getListaRanking() {
		if (listaRanking == null) {
			return new ArrayList<Ranking>();
		}
		return listaRanking;
	}

	public void setListaRanking(List<Ranking> listaRanking) {
		this.listaRanking = listaRanking;
	}

}
