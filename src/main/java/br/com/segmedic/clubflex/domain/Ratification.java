package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.segmedic.clubflex.domain.enums.Functionality;
import br.com.segmedic.clubflex.domain.enums.TypeFunctionality;

@Entity
@Table(name = "ratification")
public class Ratification implements Serializable {
	
	private static final long serialVersionUID = -2440650018585566905L;

	@Id
    @Column(name = "idratification", nullable = false, columnDefinition="BIGINT(20)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "iduser", nullable = false)
	private User user;       // funcionário que executou a ação 
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idmanager", nullable = true)
	private User manager;       // gerente que ratificou
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idholder", nullable = false)
	private Holder holder;       // usuário que pertence os dados alterados

	@ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "idinvoice", nullable = true)
	private Invoice invoice;       // a fatura a qual a ratificação está relacionada
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt; // data e hora da execução da ação
	
	@Column(name = "ratification_at", nullable = true)
	private LocalDateTime ratificationAt; // data e hora da ratificação
	
	@Enumerated(EnumType.STRING)
	@Column(name = "typeFunctionality", nullable = false)
	private TypeFunctionality typeFunctionality;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "functionality", nullable = false)
	private Functionality functionality;
	
	@Column(name = "action", nullable = true, columnDefinition="VARCHAR(255)")
	private String action;
	
	@Column(name = "justification", nullable = true, columnDefinition="VARCHAR(255)")
	private String justification;
	
	@Column(name = "isPending", nullable = false, columnDefinition="CHAR(1) default '1'")
	private Boolean isPending = true; // registros está pendente de aprovação
	
	@Column(name = "isApproved", nullable = false, columnDefinition="CHAR(1) default '0'")
	private Boolean isApproved = false;	// ação foi aprovada ou reprovada pelo gerente
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public User getManager() {
		return manager;
	}
	public void setManager(User manager) {
		this.manager = manager;
	}
	public Holder getHolder() {
		return holder;
	}
	public void setHolder(Holder holder) {
		this.holder = holder;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	public LocalDateTime getRatificationAt() {
		return ratificationAt;
	}
	public void setRatificationAt(LocalDateTime ratificationAt) {
		this.ratificationAt = ratificationAt;
	}
	public TypeFunctionality getTypeFunctionality() {
		return typeFunctionality;
	}
	public void setTypeFunctionality(TypeFunctionality typeFunctionality) {
		this.typeFunctionality = typeFunctionality;
	}
	public Functionality getFunctionality() {
		return functionality;
	}
	public void setFunctionality(Functionality functionality) {
		this.functionality = functionality;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Boolean getIsPending() {
		return isPending;
	}
	public void setIsPending(Boolean isPending) {
		this.isPending = isPending;
	}
	public Boolean getIsApproved() {
		return isApproved;
	}
	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}
	public String getJustification() {
		return justification;
	}
	public void setJustification(String justification) {
		this.justification = justification;
	}
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ratification other = (Ratification) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
