package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "audit")
public class Audit implements Serializable{
	
	private static final long serialVersionUID = 7150301560779749597L;

	@Id
    @Column(name = "idaudit", nullable = false, columnDefinition="BIGINT(20)")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "iduser", nullable = false, columnDefinition="BIGINT(20)")
	private Long idUser;
	
	@Column(name = "url", nullable = true, columnDefinition="varchar(255)")
	private String url;
	
	@Column(name = "method", nullable = true, columnDefinition="varchar(10)")
	private String method;
	
	@Column(name = "ip", nullable = true, columnDefinition="varchar(50)")
	private String ip;
	
	@Column(name = "class_method", nullable = true, columnDefinition="varchar(255)")
	private String classMethod;
	
	@Column(name = "body_data", nullable = true, columnDefinition="MEDIUMTEXT")
	private String bodyData;
	
	@Column(name = "date_time_ocorr", nullable = false, columnDefinition="DATETIME")
	private LocalDateTime dateTimeOcorr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getClassMethod() {
		return classMethod;
	}

	public void setClassMethod(String classMethod) {
		this.classMethod = classMethod;
	}

	public String getBodyData() {
		return bodyData;
	}

	public void setBodyData(String bodyData) {
		this.bodyData = bodyData;
	}

	public LocalDateTime getDateTimeOcorr() {
		return dateTimeOcorr;
	}

	public void setDateTimeOcorr(LocalDateTime dateTimeOcorr) {
		this.dateTimeOcorr = dateTimeOcorr;
	}
}
