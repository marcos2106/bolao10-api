package br.com.bolao.bolao10.model;

import java.util.List;
import br.com.bolao.bolao10.domain.Badge;

/**
 * DTO otimizado para exibir ranking com badges.
 * Usa UsuarioDTO leve ao invés de entidade completa para evitar problemas de serialização.
 */
public class RankingComBadges {
	
	private UsuarioDTO usuario;
	private Integer pontuacao;
	private Integer pontuacaoProvisoria;
	private Integer posicaoAnterior;
	private List<Badge> badges;  // badges ativos do usuário
	
	public RankingComBadges() {}
	
	public RankingComBadges(UsuarioDTO usuario, Integer pontuacao, Integer pontuacaoProvisoria, 
	                        Integer posicaoAnterior, List<Badge> badges) {
		this.usuario = usuario;
		this.pontuacao = pontuacao;
		this.pontuacaoProvisoria = pontuacaoProvisoria;
		this.posicaoAnterior = posicaoAnterior;
		this.badges = badges;
	}

	public UsuarioDTO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioDTO usuario) {
		this.usuario = usuario;
	}

	public Integer getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(Integer pontuacao) {
		this.pontuacao = pontuacao;
	}

	public Integer getPontuacaoProvisoria() {
		return pontuacaoProvisoria;
	}

	public void setPontuacaoProvisoria(Integer pontuacaoProvisoria) {
		this.pontuacaoProvisoria = pontuacaoProvisoria;
	}

	public Integer getPosicaoAnterior() {
		return posicaoAnterior;
	}

	public void setPosicaoAnterior(Integer posicaoAnterior) {
		this.posicaoAnterior = posicaoAnterior;
	}

	public List<Badge> getBadges() {
		return badges;
	}

	public void setBadges(List<Badge> badges) {
		this.badges = badges;
	}
}
