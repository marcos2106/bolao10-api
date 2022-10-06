
package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.support.Strings;

@Entity
@Table(name = "Usuario", indexes = @Index(columnList = "email"))
public class Usuario implements Serializable {

	private static final long serialVersionUID = -3576336738296104582L;

	@Id
	@Column(name = "idusuario", nullable = false, columnDefinition = "INT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nome", nullable = false, columnDefinition = "VARCHAR(100)")
	private String nome;
	
	@Column(name = "cidade", nullable = false, columnDefinition = "VARCHAR(45)")
	private String cidade;

	@Column(name = "telefone", nullable = true, columnDefinition = "VARCHAR(11)")
	private String telefone;

	@Column(name = "email", nullable = false, columnDefinition = "VARCHAR(100)")
	private String email;

	@Column(name = "senha", nullable = false, columnDefinition = "VARCHAR(8)")
	private String senha;

	@Enumerated(EnumType.STRING)
	@Column(name = "perfil", nullable = false, columnDefinition = "ENUM('ADMIN', 'USER')")
	private UserProfile perfil;

	@Column(name = "ativo", nullable = false, columnDefinition = "CHAR(1) DEFAULT 0")
	private Boolean ativo;

	@Column(name = "aposta", nullable = false, columnDefinition = "CHAR(1) DEFAULT 0")
	private Boolean aposta;

	@Column(name = "pagamento", nullable = false, columnDefinition = "CHAR(1) DEFAULT 0")
	private Boolean pagamento;

	@Column(name = "avatar", nullable = false, columnDefinition = "VARCHAR(100)")
	private String avatar;

	@DateTimeFormat(pattern = "dd/MM HH:mm")
	@Column(name = "datahoraaposta", nullable = true, columnDefinition = "DATETIME")
	private LocalDateTime dataHoraAposta;
	
	@DateTimeFormat(pattern = "dd/MM HH:mm")
	@Column(name = "datahorapgto", nullable = true, columnDefinition = "DATETIME")
	private LocalDateTime dataHoraPgto;
	
	@Transient
	private String senhaanterior;
		
	@Transient
	private String confirmarsenha;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public UserProfile getPerfil() {
		return perfil;
	}

	public void setPerfil(UserProfile perfil) {
		this.perfil = perfil;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getAposta() {
		return aposta;
	}

	public void setAposta(Boolean aposta) {
		this.aposta = aposta;
	}

	public Boolean getPagamento() {
		return pagamento;
	}

	public void setPagamento(Boolean pagamento) {
		this.pagamento = pagamento;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getTelefoneFmt() {
		return Strings.formatTelefone(this.telefone);
	}
	
	public LocalDateTime getDataHoraAposta() {
		return dataHoraAposta;
	}

	public void setDataHoraAposta(LocalDateTime dataHoraAposta) {
		this.dataHoraAposta = dataHoraAposta;
	}

	public LocalDateTime getDataHoraPgto() {
		return dataHoraPgto;
	}

	public void setDataHoraPgto(LocalDateTime dataHoraPgto) {
		this.dataHoraPgto = dataHoraPgto;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getSenhaanterior() {
		return senhaanterior;
	}

	public void setSenhaanterior(String senhaanterior) {
		this.senhaanterior = senhaanterior;
	}

	public String getConfirmarsenha() {
		return confirmarsenha;
	}

	public void setConfirmarsenha(String confirmarsenha) {
		this.confirmarsenha = confirmarsenha;
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
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

}
