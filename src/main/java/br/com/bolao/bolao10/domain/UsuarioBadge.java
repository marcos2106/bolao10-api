package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "usuario_badge")
public class UsuarioBadge implements Serializable {

	private static final long serialVersionUID = -9988776600L;

	@Id
	@Column(name = "id", nullable = false, columnDefinition = "INT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idusuario", nullable = false)
	private Usuario usuario;

	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idbadge", nullable = false)
	private Badge badge;

	@Column(name = "data_conquista", nullable = false, columnDefinition = "DATETIME")
	private LocalDateTime dataConquista;

	@Column(name = "atual", nullable = false, columnDefinition = "CHAR(1) DEFAULT '1'")
	private Boolean atual;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Usuario getUsuario() { return usuario; }
	public void setUsuario(Usuario usuario) { this.usuario = usuario; }

	public Badge getBadge() { return badge; }
	public void setBadge(Badge badge) { this.badge = badge; }

	public LocalDateTime getDataConquista() { return dataConquista; }
	public void setDataConquista(LocalDateTime dataConquista) { this.dataConquista = dataConquista; }

	public Boolean getAtual() { return atual; }
	public void setAtual(Boolean atual) { this.atual = atual; }
}
