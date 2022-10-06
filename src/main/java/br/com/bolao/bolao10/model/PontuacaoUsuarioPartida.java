package br.com.bolao.bolao10.model;

import java.io.Serializable;
import java.util.List;

import br.com.bolao.bolao10.domain.Aposta;
import br.com.bolao.bolao10.domain.ApostaColocacao;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Usuario;

public class PontuacaoUsuarioPartida implements Serializable {

	private static final long serialVersionUID = 821133L;
	
	private Usuario usuario;
	private Long pontuacao;
	private List<Partida> listaPartidas;
	private List<Aposta> listaApostas;
	private ApostaColocacao apostaColocacao;
	
	
	public Usuario getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Long getPontuacao() {
		return pontuacao;
	}

	public void setPontuacao(Long pontuacao) {
		this.pontuacao = pontuacao;
	}

	public List<Partida> getListaPartidas() {
		return listaPartidas;
	}
	
	public void setListaPartidas(List<Partida> listaPartidas) {
		this.listaPartidas = listaPartidas;
	}
	
	public List<Aposta> getListaApostas() {
		return listaApostas;
	}
	
	public void setListaApostas(List<Aposta> listaApostas) {
		this.listaApostas = listaApostas;
	}

	public ApostaColocacao getApostaColocacao() {
		return apostaColocacao;
	}

	public void setApostaColocacao(ApostaColocacao apostaColocacao) {
		this.apostaColocacao = apostaColocacao;
	}

}
