package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class SubscriptionLoad implements Serializable{

	private static final long serialVersionUID = 310582342981L;
	
	private Long id;
	private String descricao;
	
	public SubscriptionLoad() {}

	public SubscriptionLoad(Long idSub, String tipoSub) {
		this.id = idSub;
		this.descricao = "Assinatura #"+ idSub +" ("+ tipoSub +")";
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
