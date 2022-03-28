package br.com.segmedic.clubflex.domain;

import java.math.BigDecimal;

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

import com.fasterxml.jackson.annotation.JsonBackReference;

import br.com.segmedic.clubflex.support.NumberUtils;

@Entity
@Table(name = "invoice_detail")
public class InvoiceDetail {
	
	@Id
    @Column(name = "idinvoice_detail", nullable = false, columnDefinition="BIGINT(20)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "describe_price", nullable = false, columnDefinition="VARCHAR(255)")
	private String describe;
	
	@Column(name = "price", nullable = false, columnDefinition="DECIMAL(10,2)")
	private BigDecimal price;

	@JsonBackReference
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "idinvoice", nullable = false)
	private Invoice invoice;
	
	public InvoiceDetail() {
		super();
	}
	
	public InvoiceDetail(String describe, BigDecimal price, Invoice invoice) {
		super();
		this.describe = describe;
		this.price = price;
		this.invoice = invoice;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	public String getPriceFmt() {
		return NumberUtils.formatMoney(this.price);
	}
	
}
