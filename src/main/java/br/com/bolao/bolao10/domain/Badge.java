package br.com.bolao.bolao10.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "badge")
public class Badge implements Serializable {

	private static final long serialVersionUID = -9988776655L;

	@Id
	@Column(name = "idbadge", nullable = false, columnDefinition = "INT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome", nullable = false, columnDefinition = "VARCHAR(50)")
	private String nome;

	@Column(name = "descricao", nullable = false, columnDefinition = "VARCHAR(200)")
	private String descricao;

	@Column(name = "icone_classe", nullable = false, columnDefinition = "VARCHAR(50)")
	private String iconeClasse;

	@Column(name = "cor_fundo", nullable = false, columnDefinition = "VARCHAR(20)")
	private String corFundo;

	@Column(name = "cor_icone", nullable = false, columnDefinition = "VARCHAR(20)")
	private String corIcone;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }

	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }

	public String getIconeClasse() { return iconeClasse; }
	public void setIconeClasse(String iconeClasse) { this.iconeClasse = iconeClasse; }

	public String getCorFundo() { return corFundo; }
	public void setCorFundo(String corFundo) { this.corFundo = corFundo; }

	public String getCorIcone() { return corIcone; }
	public void setCorIcone(String corIcone) { this.corIcone = corIcone; }
}
