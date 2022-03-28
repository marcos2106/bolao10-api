package br.com.segmedic.clubflex.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sms_params")
public class SmsParams {

	@Id
	@Column(name = "idparams", nullable = false, columnDefinition = "INT(10)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "due_today", nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean due_today;
	
	@Column(name = "three_days_late", nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean three_days_late;

	
	@Column(name = "eigth_days_late", nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean eigth_days_late;

	
	@Column(name = "thirteen_days_late", nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean thirteen_days_late;

	@Column(name = "eighteen_days_late", nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean eighteen_days_late;
	
	@Column(name = "unlimited_message", nullable = false, columnDefinition = "TINYINT(1)")
	private Boolean unlimited_message;
	
	@Column(name = "message", nullable = false,  columnDefinition="VARCHAR(255)")
	private String message;
	
	@Column(name = "message_amount", nullable = true, columnDefinition="BIGINT(10)")
	private Long message_amount;
	
	//coluna outros
	//coluna ultimo envio
	
	

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the due_today
	 */
	public Boolean getDue_today() {
		return due_today;
	}

	/**
	 * @param due_today the due_today to set
	 */
	public void setDue_today(Boolean due_today) {
		this.due_today = due_today;
	}

	/**
	 * @return the three_days_late
	 */
	public Boolean getThree_days_late() {
		return three_days_late;
	}

	/**
	 * @param three_days_late the three_days_late to set
	 */
	public void setThree_days_late(Boolean three_days_late) {
		this.three_days_late = three_days_late;
	}

	/**
	 * @return the eigth_days_late
	 */
	public Boolean getEigth_days_late() {
		return eigth_days_late;
	}

	/**
	 * @param eigth_days_late the eigth_days_late to set
	 */
	public void setEigth_days_late(Boolean eigth_days_late) {
		this.eigth_days_late = eigth_days_late;
	}

	/**
	 * @return the thirteen_days_late
	 */
	public Boolean getThirteen_days_late() {
		return thirteen_days_late;
	}

	/**
	 * @param thirteen_days_late the thirteen_days_late to set
	 */
	public void setThirteen_days_late(Boolean thirteen_days_late) {
		this.thirteen_days_late = thirteen_days_late;
	}

	/**
	 * @return the eighteen_days_late
	 */
	public Boolean getEighteen_days_late() {
		return eighteen_days_late;
	}

	/**
	 * @param eighteen_days_late the eighteen_days_late to set
	 */
	public void setEighteen_days_late(Boolean eighteen_days_late) {
		this.eighteen_days_late = eighteen_days_late;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the unlimited_message
	 */
	public Boolean getUnlimited_message() {
		return unlimited_message;
	}

	/**
	 * @param unlimited_message the unlimited_message to set
	 */
	public void setUnlimited_message(Boolean unlimited_message) {
		this.unlimited_message = unlimited_message;
	}

	/**
	 * @return the message_amount
	 */
	public Long getMessage_amount() {
		return message_amount;
	}

	/**
	 * @param message_amount the message_amount to set
	 */
	public void setMessage_amount(Long message_amount) {
		this.message_amount = message_amount;
	}

}
