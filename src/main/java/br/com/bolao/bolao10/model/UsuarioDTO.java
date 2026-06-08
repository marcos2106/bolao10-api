package br.com.bolao.bolao10.model;

import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.NivelUsuarioEnum;

/**
 * DTO leve para Usuario - evita serializar campos desnecessários
 * e problemas de lazy loading durante JSON serialization.
 */
public class UsuarioDTO {
	
	private Long id;
	private String nome;
	private String avatar;
	private NivelUsuarioEnum nivel;
	private String nivelDescricao;
	
	public UsuarioDTO() {}
	
	public UsuarioDTO(Usuario u) {
		if (u != null) {
			this.id = u.getId();
			this.nome = u.getNome();
			this.avatar = u.getAvatar();
			this.nivel = u.getNivel();
			this.nivelDescricao = u.getNivelDescricao();
		}
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }

	public String getAvatar() { return avatar; }
	public void setAvatar(String avatar) { this.avatar = avatar; }

	public NivelUsuarioEnum getNivel() { return nivel; }
	public void setNivel(NivelUsuarioEnum nivel) { this.nivel = nivel; }

	public String getNivelDescricao() { return nivelDescricao; }
	public void setNivelDescricao(String nivelDescricao) { this.nivelDescricao = nivelDescricao; }
}
