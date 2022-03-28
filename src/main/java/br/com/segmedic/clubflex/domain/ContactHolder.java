package br.com.segmedic.clubflex.domain;

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

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "contact_holder")
public class ContactHolder implements Serializable{
	
	private static final long serialVersionUID = 4332438009256592508L;
	
	@Id
    @Column(name = "idcontact_holder", nullable = false, columnDefinition="BIGINT(20)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "iduser_contact", nullable = false)
	private User user;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "idholder", nullable = false)
	private Holder holder;
	
	@DateTimeFormat(pattern="dd/MM/yyyy HH:mm:ss")
	@Column(name = "date_time_contact", nullable = false, columnDefinition="DATETIME")
	private LocalDateTime dateTimeContact;
	
	@Column(name = "description_contact", nullable = false, columnDefinition="MEDIUMTEXT")
	private String description;

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

	public Holder getHolder() {
		return holder;
	}

	public void setHolder(Holder holder) {
		this.holder = holder;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	public LocalDateTime getDateTimeContact() {
		return dateTimeContact;
	}

	public void setDateTimeContact(LocalDateTime dateTimeContact) {
		this.dateTimeContact = dateTimeContact;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
