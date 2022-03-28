package br.com.segmedic.clubflex.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "params_subscription")
public class ParamsSubscription {

	@Id
	@Column(name = "idparams", nullable = false, columnDefinition = "INT(10)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "flag_suspend", nullable = false, columnDefinition = "CHAR(1) default '0'")
	private Boolean isSuspend;
	
	@Column(name = "flag_block", nullable = false, columnDefinition = "CHAR(1) default '0'")
	private Boolean isBlock;

	@Column(name = "days_due_suspend", nullable = false, columnDefinition="BIGINT(10) default 0")
	private Long daysDueSuspend;
	
	@Column(name = "days_due_block", nullable = false, columnDefinition="BIGINT(10) default 0")
	private Long daysDueBlock;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsSuspend() {
		return isSuspend;
	}

	public void setIsSuspend(Boolean isSuspend) {
		this.isSuspend = isSuspend;
	}

	public Boolean getIsBlock() {
		return isBlock;
	}

	public void setIsBlock(Boolean isBlock) {
		this.isBlock = isBlock;
	}

	public Long getDaysDueSuspend() {
		return daysDueSuspend;
	}

	public void setDaysDueSuspend(Long daysDueSuspend) {
		this.daysDueSuspend = daysDueSuspend;
	}

	public Long getDaysDueBlock() {
		return daysDueBlock;
	}

	public void setDaysDueBlock(Long daysDueBlock) {
		this.daysDueBlock = daysDueBlock;
	}

}
