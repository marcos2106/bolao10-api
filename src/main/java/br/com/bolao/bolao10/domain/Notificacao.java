package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.bolao.bolao10.domain.enums.TipoNotificacaoEnum;

@Entity
@Table(name = "notificacao")
public class Notificacao implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idnotificacao")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_evento")
	private TipoNotificacaoEnum tipoEvento;

	@Column(name = "mensagem")
	private String mensagem;

	@Column(name = "data_criacao")
	@JsonFormat(pattern = "dd/MM HH:mm")
	private LocalDateTime dataCriacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoNotificacaoEnum getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(TipoNotificacaoEnum tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

}
