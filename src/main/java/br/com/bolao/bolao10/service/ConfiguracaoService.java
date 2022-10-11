
package br.com.bolao.bolao10.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Aposta;
import br.com.bolao.bolao10.domain.ApostaColocacao;
import br.com.bolao.bolao10.domain.Colocacao;
import br.com.bolao.bolao10.domain.Gol;
import br.com.bolao.bolao10.domain.Jogador;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.domain.Selecao;
import br.com.bolao.bolao10.domain.Situacao;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.exception.Bolao10Exception;
import br.com.bolao.bolao10.model.ColocacaoRequest;
import br.com.bolao.bolao10.model.PartidasOutras;
import br.com.bolao.bolao10.model.PontuacaoPadrao;
import br.com.bolao.bolao10.repository.ApostaColocacaoRepository;
import br.com.bolao.bolao10.repository.ApostaRepository;
import br.com.bolao.bolao10.repository.ColocacaoRepository;
import br.com.bolao.bolao10.repository.GolRepository;
import br.com.bolao.bolao10.repository.JogadorRepository;
import br.com.bolao.bolao10.repository.PartidaRepository;
import br.com.bolao.bolao10.repository.RankingRepository;
import br.com.bolao.bolao10.repository.SelecaoRepository;
import br.com.bolao.bolao10.repository.SituacaoRepository;
import br.com.bolao.bolao10.repository.UserRepository;
import br.com.bolao.bolao10.support.Constants;
import br.com.bolao.bolao10.support.Strings;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ConfiguracaoService {

	@Autowired
	private SelecaoRepository selecaoRepository;

	@Autowired
	private JogadorRepository jogadorRepository;

	@Autowired
	private PartidaRepository partidaRepository;

	@Autowired
	private GolRepository golRepository;

	@Autowired
	private UserRepository usuarioRepository;

	@Autowired
	private ColocacaoRepository colocacaoRepository;

	@Autowired
	private SituacaoRepository situacaoRepository;

	@Autowired
	private ApostaRepository apostaRepository;

	@Autowired
	private ApostaColocacaoRepository acRepository;

	@Autowired
	private ClassificacaoService classificacaoService;

	@Autowired
	private RankingRepository rankingRepository;

	@Transactional
	public List<Selecao> carregarSelecoes() {
		return selecaoRepository.carregarSelecoes();
	}

	@Transactional
	public Jogador adicionarJogador(Jogador jogador) {
		jogador.setAtivo(Boolean.TRUE);
		return jogadorRepository.save(jogador);
	}

	@Transactional
	public Jogador excluirJogador(Long id) {
		Jogador jogador = jogadorRepository.findById(id);
		jogador.setAtivo(Boolean.FALSE);
		return jogadorRepository.save(jogador);
	}

	@Transactional
	public List<Jogador> carregarJogadores() {
		return jogadorRepository.carregarJogadores();
	}

	@Transactional
	public void salvarColocacao(ColocacaoRequest colRequest) {

		try {
			Colocacao colocacao = colocacaoRepository.findById(1L);
			
			// Atualiza as posições antes das contabilizações das colocações
			contabilizaPosicaoRanking();

			contabilizarPontuacaoColocacao(colocacao, colRequest);
			
			if (colocacao.getCampeao() == null && colRequest.getCampeao().getId() != null) {
				colocacao.setCampeao(selecaoRepository.findById(colRequest.getCampeao().getId()));
			}
			if (colocacao.getVice() == null && colRequest.getVice().getId() != null) {
				colocacao.setVice(selecaoRepository.findById(colRequest.getVice().getId()));
			}
			if (colocacao.getTerceiro() == null && colRequest.getTerceiro().getId() != null) {
				colocacao.setTerceiro(selecaoRepository.findById(colRequest.getTerceiro().getId()));
			}
			if (colocacao.getQuarto() == null && colRequest.getQuarto().getId() != null) {
				colocacao.setQuarto(selecaoRepository.findById(colRequest.getQuarto().getId()));
			}
			if (colocacao.getArtilharia() == null && colRequest.getArtilharia().getId() != null) {
				colocacao.setArtilharia(selecaoRepository.findById(colRequest.getArtilharia().getId()));
			}
			colocacaoRepository.save(colocacao);
			
			if (colocacao.getCampeao() != null && colocacao.getVice() != null 
					&& colocacao.getTerceiro() != null && colocacao.getQuarto() != null) {
				acRepository.zerarPontuacaoColocacao();
			}
			
		} catch (Exception e) {
			throw new Bolao10Exception("Erro na hora de salvar as Colocações.");
		}
	}

	@Transactional
	private void contabilizarPontuacaoColocacao(Colocacao colocacao, ColocacaoRequest colRequest) {

		List<ApostaColocacao> listaAC = acRepository.carregarApostaColocacao();

		for (ApostaColocacao ac : listaAC) {

			Integer pontuacaoRnk = Constants.APOSTA_ERRADA; // zerar a pontuação do Rnk

			// Se ainda não foi registrado o campeão, e se eu estou informando o campeão
			if (colocacao.getCampeao() == null && colRequest.getCampeao().getId() != null) {

				Integer pontosCampeao = Constants.APOSTA_ERRADA;
				if (colRequest.getCampeao().getId() == ac.getCampeao().getId()) {
					pontosCampeao = Constants.APOSTA_CAMPEAO;
					ac.setPontosCampeao(pontosCampeao);
				} else if (colRequest.getCampeao().getId() == ac.getVice().getId()) {
					pontosCampeao = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosVice(pontosCampeao);
				} else if (colRequest.getCampeao().getId() == ac.getTerceiro().getId()) {
					pontosCampeao = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosTerceiro(pontosCampeao);
				} else if (colRequest.getCampeao().getId() == ac.getQuarto().getId()) {
					pontosCampeao = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosQuarto(pontosCampeao);
				}
				pontuacaoRnk += pontosCampeao;
			}
			if (colocacao.getVice() == null && colRequest.getVice().getId() != null) {

				Integer pontosVice = Constants.APOSTA_ERRADA;
				if (colRequest.getVice().getId() == ac.getVice().getId()) {
					pontosVice = Constants.APOSTA_VICE;
					ac.setPontosVice(pontosVice);
				} else if (colRequest.getVice().getId() == ac.getCampeao().getId()) {
					pontosVice = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosCampeao(pontosVice);
				} else if (colRequest.getVice().getId() == ac.getTerceiro().getId()) {
					pontosVice = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosTerceiro(pontosVice);
				} else if (colRequest.getVice().getId() == ac.getQuarto().getId()) {
					pontosVice = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosQuarto(pontosVice);
				}
				pontuacaoRnk += pontosVice;
			}
			if (colocacao.getTerceiro() == null && colRequest.getTerceiro().getId() != null) {

				Integer pontosTerceiro = Constants.APOSTA_ERRADA;
				if (colRequest.getTerceiro().getId() == ac.getTerceiro().getId()) {
					pontosTerceiro = Constants.APOSTA_TERCEIRO;
					ac.setPontosTerceiro(pontosTerceiro);
				} else if (colRequest.getTerceiro().getId() == ac.getCampeao().getId()) {
					pontosTerceiro = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosCampeao(pontosTerceiro);
				} else if (colRequest.getTerceiro().getId() == ac.getVice().getId()) {
					pontosTerceiro = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosVice(pontosTerceiro);
				} else if (colRequest.getTerceiro().getId() == ac.getQuarto().getId()) {
					pontosTerceiro = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosQuarto(pontosTerceiro);
				}
				pontuacaoRnk += pontosTerceiro;
			}
			if (colocacao.getQuarto() == null && colRequest.getQuarto().getId() != null) {

				Integer pontosQuarto = Constants.APOSTA_ERRADA;
				if (colRequest.getQuarto().getId() == ac.getQuarto().getId()) {
					pontosQuarto = Constants.APOSTA_QUARTO;
					ac.setPontosQuarto(pontosQuarto);
				} else if (colRequest.getQuarto().getId() == ac.getCampeao().getId()) {
					pontosQuarto = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosCampeao(pontosQuarto);
				} else if (colRequest.getQuarto().getId() == ac.getVice().getId()) {
					pontosQuarto = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosVice(pontosQuarto);
				} else if (colRequest.getQuarto().getId() == ac.getTerceiro().getId()) {
					pontosQuarto = Constants.APOSTA_POSICAO_INCORRETA;
					ac.setPontosTerceiro(pontosQuarto);
				}
				pontuacaoRnk += pontosQuarto;
			}
			if (colocacao.getArtilharia() == null && colRequest.getArtilharia().getId() != null) {				

				Integer pontosArt = Constants.APOSTA_ERRADA;
				if (colRequest.getArtilharia().getId() == ac.getArtilharia().getId()) {
					pontosArt = Constants.APOSTA_ARTILHEIRO;
				}
				ac.setPontosArtilharia(pontosArt);
				pontuacaoRnk += pontosArt;
			}
			try {
				acRepository.save(ac);

				if (pontuacaoRnk > Constants.APOSTA_ERRADA ) {
					Ranking ranking = rankingRepository.findById(ac.getUsuario().getId());
					ranking.setPontuacao(ranking.getPontuacao() + pontuacaoRnk);
					rankingRepository.save(ranking);
				}
			} catch (Exception e) {
				throw new Bolao10Exception("Erro na hora de salvar as Colocações.");
			}
		}
	}

	@Transactional
	public Colocacao carregarColocacao() {
		return colocacaoRepository.carregarColocacao();
	}

	@Transactional
	public List<Usuario> carregarUsuarios() {
		return usuarioRepository.carregarUsuarios();
	}

	@Transactional
	public Usuario adicionarUsuario(Usuario usuario) {
		usuario.setTelefone(Strings.removeNoNumericChars(usuario.getTelefone()));
		return usuarioRepository.save(usuario);
	}

	@Transactional
	public List<Partida> carregarPartidasConfiguracao() {
		return partidaRepository.carregarPartidasConfiguracao();
	}

	@Transactional
	public void iniciarPartida(Long id) {

		Partida partidaPersist = null;
		try {
			Partida partida = partidaRepository.findById(id);
			partida.setIniciada(Boolean.TRUE);
			partida.setPlacarA(0);
			partida.setPlacarB(0);
			partidaPersist =  partidaRepository.save(partida);

			// Só atualiza as posições antes das contabilizações do início da partida
			contabilizaPosicaoRanking();

			classificacaoService.contabilizarClassificacao(partidaPersist);
			contabilizarPontuacao(partidaPersist);

		} catch (Exception e) {
			throw new Bolao10Exception("Erro ao iniciar partida.");
		}
	}

	private void contabilizaPosicaoRanking() {

		List<Ranking> listaRnk = rankingRepository.carregarRanking();
		int i = 1;
		for (Ranking ranking : listaRnk) {
			ranking.setPosicaoAnterior(i++);
			rankingRepository.save(ranking);
		}
	}

	@Transactional
	public void finalizarPartida(Long id) {

		try {
			Partida partida = partidaRepository.findById(id);
			partida.setFinalizada(Boolean.TRUE);
			Partida partidaPersist = partidaRepository.save(partida);

			classificacaoService.finalizarClassificacao(partidaPersist);
			contabilizarPontuacaoFinal(partidaPersist);

		} catch (Exception e) {
			throw new Bolao10Exception("Erro ao finalizar uma partida.");
		}
	}

	public List<Jogador> carregarJogadoresPorSelecao(Long id) {

		return jogadorRepository.carregarJogadoresPorSelecao(id);
	}

	@Transactional
	public List<Gol> carregarGolsPorPartida(Long id) {

		return golRepository.carregarGolsPorPartida(id);
	}

	@Transactional
	public void adicionarGol(Gol gol) {

		try {
			Partida partida = partidaRepository.findById(gol.getPartida().getId());
			gol.setPartida(partida);

			Jogador jogador = jogadorRepository.findById(gol.getIdJogador());
			if (!gol.getGolcontra()) {
				jogador.setGols(jogador.getGols()+1);
				jogadorRepository.save(jogador);
			}
			gol.setJogador(jogador);

			// Adicionar gol ao placar
			if (partida.getSelecaoA().getId() == gol.getSelecao().getId()) {
				partida.setPlacarA(partida.getPlacarA()+1);
			}
			if (partida.getSelecaoB().getId() == gol.getSelecao().getId()) {
				partida.setPlacarB(partida.getPlacarB()+1);
			}
			Partida partidaPersist = partidaRepository.save(partida);
			golRepository.save(gol);

			classificacaoService.contabilizarClassificacao(partidaPersist);
			contabilizarPontuacao(partidaPersist);

		} catch (Exception e) {
			throw new Bolao10Exception("Erro ao adicionar um gol.");
		}
	}

	public PontuacaoPadrao carregarPontuacao() {
		PontuacaoPadrao pp = new PontuacaoPadrao();
		pp.setPontosCampeao(Constants.APOSTA_CAMPEAO);
		pp.setPontosVice(Constants.APOSTA_VICE);
		pp.setPontosTerceiro(Constants.APOSTA_TERCEIRO);
		pp.setPontosQuarto(Constants.APOSTA_QUARTO);
		pp.setPontosArtilharia(Constants.APOSTA_ARTILHEIRO);
		pp.setPontosPosicaoIncorreta(Constants.APOSTA_POSICAO_INCORRETA);
		return pp;
	}

	public List<Jogador> carregarArtilharia() {

		return jogadorRepository.carregarArtilharia();
	}

	public List<Situacao> carregarSituacao() {

		return situacaoRepository.carregarSituacoes();
	}

	public Situacao situacaoAtiva() {

		return situacaoRepository.stiuacaoAtiva();
	}

	@Transactional
	public Boolean ativarSituacao(Long situacao) {

		try {
			situacaoRepository.ativarSituacao(situacao);
		} catch (Exception e) {
			throw new Bolao10Exception("Erro ao ativar uma situação!");
		}
		return Boolean.TRUE;
	}

	@Transactional
	private void contabilizarPontuacao(Partida partida) {

		List<Aposta> listaPartida = apostaRepository.carregarApostaPorPartida(partida.getId());

		Integer placarA = partida.getPlacarA();
		Integer placarB = partida.getPlacarB();

		for (Aposta aposta : listaPartida) {

			Integer pontuacao = Constants.APOSTA_ERRADA;

			// ACERTANDO INDEPENDENTE DO RESULTADO
			if (aposta.getPlacarA() == placarA && aposta.getPlacarB() == placarB) {
				pontuacao = Constants.APOSTA_CORRETA;
			} else {

				// EMPATE
				if (placarA == placarB) {
					// PARCIALMENTE
					if (aposta.getPlacarA() == aposta.getPlacarB() && aposta.getPlacarA() != placarA) {
						pontuacao = Constants.APOSTA_EMPATE_ERRADO;
					}
					// ERRANDO
					if (aposta.getPlacarA() != aposta.getPlacarB()) {
						pontuacao = Constants.APOSTA_ERRADA;
					}
				}

				// VIT A
				if (placarA > placarB) {
					// VITORIA CORRETA
					if (aposta.getPlacarA() > aposta.getPlacarB() && aposta.getPlacarA() == placarA) {
						pontuacao = Constants.APOSTA_VITORIA_CORRETA;
					}
					// VITORIA DERROTA CORRETA
					if (aposta.getPlacarA() > aposta.getPlacarB() && aposta.getPlacarB() == placarB) {
						pontuacao = Constants.APOSTA_DERROTA_CORRETA;
					}
					// VITORIA ERRADA
					if (aposta.getPlacarA() > aposta.getPlacarB() && aposta.getPlacarA() != placarA && aposta.getPlacarB() != placarB) {
						pontuacao = Constants.APOSTA_VITORIA_ERRADA;
					}
					// ERRANDO
					if (aposta.getPlacarA() <= aposta.getPlacarB()) {
						pontuacao = Constants.APOSTA_ERRADA;
					}
				}

				// VIT B
				if (placarA < placarB) {
					// VITORIA CORRETA
					if (aposta.getPlacarA() < aposta.getPlacarB() && aposta.getPlacarB() == placarB) {
						pontuacao = Constants.APOSTA_VITORIA_CORRETA;
					}
					// VITORIA DERROTA CORRETA
					if (aposta.getPlacarA() < aposta.getPlacarB() && aposta.getPlacarA() == placarA) {
						pontuacao = Constants.APOSTA_DERROTA_CORRETA;
					}
					// VITORIA ERRADA
					if (aposta.getPlacarA() < aposta.getPlacarB() && aposta.getPlacarA() != placarA && aposta.getPlacarB() != placarB) {
						pontuacao = Constants.APOSTA_VITORIA_ERRADA;
					}
					// ERRANDO
					if (aposta.getPlacarA() >= aposta.getPlacarB()) {
						pontuacao = Constants.APOSTA_ERRADA;
					}
				}
			}
			aposta.setPontuacaoProvisoria(pontuacao);

			Ranking rnk = rankingRepository.findById(aposta.getUsuario().getId());
			// soma todas as pontuações provisorias das partidas iniciadas (pode haver 2 partidas acontecendo)
			// buscar "iniciada" com id diferente da atual
			//			rnk.setPontuacaoProvisoria(rnk.getPontuacao() + pontuacao);

			rnk.setPontuacao(rnk.getPontuacao() - rnk.getPontuacaoProvisoria() + pontuacao);
			rnk.setPontuacaoProvisoria(pontuacao);

			rankingRepository.save(rnk);
			apostaRepository.save(aposta);
		}
	}	

	private void contabilizarPontuacaoFinal(Partida partida) {

		List<Aposta> listaPartida = apostaRepository.carregarApostaPorPartida(partida.getId());

		for (Aposta aposta : listaPartida) {

			Integer pontuacao = aposta.getPontuacaoProvisoria();
			aposta.setPontuacao(pontuacao);
			aposta.setPontuacaoProvisoria(null);
			apostaRepository.save(aposta);

			Ranking rnk = rankingRepository.findById(aposta.getUsuario().getId());
			rnk.setPontuacaoProvisoria(0);			
			// não posso limpar, pq pode haver outra partida acontecendo
			// só limpa se não houver mais nenhum jogo acontecendo (ao vivo/iniciado) e não finalizado
			rankingRepository.save(rnk);
		}

	}

	public Partida carregarPartida(Long id) {

		if (id == null) {
			throw new Bolao10Exception("Partida não encontrada!");
		}
		Partida partida = partidaRepository.findById(id);

		if (partida == null) {
			throw new Bolao10Exception("Partida não encontrada!");
		}
		return partida;
	}

	public PartidasOutras carregarOutrasPartidas(Long id) {

		Partida partida = partidaRepository.findById(id);

		if (partida == null) {
			throw new Bolao10Exception("Partida não encontrada!");
		}

		PartidasOutras po = new PartidasOutras();

		po.setOutrasPartidasA(partidaRepository.carregarPartidasPorSelecao(partida.getSelecaoA().getId()));
		po.setOutrasPartidasB(partidaRepository.carregarPartidasPorSelecao(partida.getSelecaoB().getId()));

		return po;
	}

	public Long quantidadeJogoAovivo() {

		return partidaRepository.quantidadeJogoAovivo();
	}

	@Transactional
	public void alterarPagamento(Long idUsuario) {

		Usuario usuario = usuarioRepository.findById(idUsuario);
		usuario.setPagamento(!usuario.getPagamento());
		if (usuario.getPagamento()) {
			usuario.setDataHoraPgto(LocalDateTime.now());
		} else {
			usuario.setDataHoraPgto(null);			
		}
		usuarioRepository.save(usuario);
	}
}
