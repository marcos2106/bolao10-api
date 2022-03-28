package br.com.segmedic.clubflex.model;

import java.io.Serializable;

public class GatewayTicketCustomerResponse implements Serializable{

	private static final long serialVersionUID = -560786537499845211L;
	
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
