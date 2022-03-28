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

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.segmedic.clubflex.domain.enums.ClubCardStatus;
import br.com.segmedic.clubflex.support.Strings;

@Entity
@Table(name = "club_card")
public class ClubCard implements Serializable{
	
	private static final long serialVersionUID = -4114495341285525574L;

	@Id
    @Column(name = "idclubcard", nullable = false, columnDefinition="BIGINT(20)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "idsubscription", nullable = false)
	@JsonIgnore
	private Subscription subscription;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition="ENUM('CANCELED','OK','BLOCKED','BLOCKED_BY_FRAUD','EXPIRED')")
	private ClubCardStatus status;
	
	@Column(name = "flag_card_holder", nullable = false, columnDefinition="CHAR(1) DEFAULT '0'")
	private Boolean isCardOfHolder;
	
	@ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "idholder", nullable = true)
	private Holder holder;
	
	@ManyToOne(cascade = CascadeType.PERSIST, optional = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "iddependent", nullable = true)
	private Dependent dependent;
	
	@Column(name = "token", nullable = false, columnDefinition="VARCHAR(60)")
	private String token;
	
	@Column(name = "date_cancel", nullable = true, columnDefinition="DATETIME")
	private LocalDateTime dateCancel;
	
	@Column(name = "date_generated", nullable = true, columnDefinition="DATETIME")
	private LocalDateTime dateGenerated;

	private transient String qrcode;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Subscription getSubscription() {
		return subscription;
	}
	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}
	public ClubCardStatus getStatus() {
		return status;
	}
	public void setStatus(ClubCardStatus status) {
		this.status = status;
	}
	public Boolean getIsCardOfHolder() {
		return isCardOfHolder;
	}
	public void setIsCardOfHolder(Boolean isCardOfHolder) {
		this.isCardOfHolder = isCardOfHolder;
	}
	public Holder getHolder() {
		return holder;
	}
	public void setHolder(Holder holder) {
		this.holder = holder;
	}
	public Dependent getDependent() {
		return dependent;
	}
	public void setDependent(Dependent dependent) {
		this.dependent = dependent;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	public LocalDateTime getDateCancel() {
		return dateCancel;
	}
	public void setDateCancel(LocalDateTime dateCancel) {
		this.dateCancel = dateCancel;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
	public LocalDateTime getDateGenerated() {
		return dateGenerated;
	}
	public void setDateGenerated(LocalDateTime dateGenerated) {
		this.dateGenerated = dateGenerated;
	}
	
	
	public String getNumberOnCard() {
		if(this.id != null) {
			return Strings.formatClubCardNumber(StringUtils.leftPad(this.id.toString(), 16, '0'));
		}
		return null;
	}
	
	public String getNameOnCard() {
		if(this.isCardOfHolder) {
			return this.holder.getName().toUpperCase();
		}else {
			return this.dependent.getName().toUpperCase();
		}
	}
	
	public String getFirst6DigitsFromCPFOnCard() {
		if(this.isCardOfHolder) {
			return this.holder.getCpfCnpj().substring(0, 6);
		}else {
			return this.dependent.getCpf().substring(0, 6);
		}
	}
	
	
}
