package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "invoice_log")
public class InvoiceLog implements Serializable{
	
	private static final long serialVersionUID = -5415755179422137592L;
	
	@Id
    @Column(name = "idinvoice_log", nullable = false, columnDefinition="BIGINT(20)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "idinvoice", nullable = false)
	private Invoice invoice;	
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "iduser", nullable = true)
	private User user;     					// usuário que executou a funcionalidade
	
	@Column(name = "amount", nullable = false, columnDefinition="DECIMAL(10,2)")
	private BigDecimal amount;               //valor
	
	@Column(name = "date_registry", nullable = false, columnDefinition="DATETIME")
	private LocalDateTime dateRegistry;
	
	@Column(name = "describe_log", nullable = true)
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public LocalDateTime getDateRegistry() {
		return dateRegistry;
	}

	public void setDateRegistry(LocalDateTime dateRegistry) {
		this.dateRegistry = dateRegistry;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
