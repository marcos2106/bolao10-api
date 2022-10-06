
package br.com.bolao.bolao10.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Aposta;
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
	public void salvarColocacao(ColocacaoRequest request) {

		Colocacao colocacao = new Colocacao();
		colocacao.setCampeao(request.getCampeao());
		colocacao.setVice(request.getVice());
		colocacao.setTerceiro(request.getTerceiro());
		colocacao.setQuarto(request.getQuarto());
		colocacao.setArtilharia(request.getArtilharia());

		try {
			colocacaoRepository.save(colocacao);
		} catch (Exception e) {
			throw new Bolao10Exception("Erro na hora de salvar as Colocações.");
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

			classificacaoService.contabilizarClassificacao(partidaPersist);
			contabilizarPontuacao(partidaPersist);

		} catch (Exception e) {
			throw new Bolao10Exception("Erro ao iniciar partida.");
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

			gol.setJogador(jogadorRepository.findById(gol.getIdJogador()));
			
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
			Ranking rnk = rankingRepository.findById(aposta.getUsuario().getId());
			aposta.setPontuacaoProvisoria(pontuacao);
			
			// soma todas as pontuações provisorias das partidas iniciadas (pode haver 2 partidas acontecendo)
			// buscar "iniciada" com id diferente da atual
			rnk.setPontuacaoProvisoria(rnk.getPontuacao() + pontuacao);
				
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
			
			Ranking rnk = rankingRepository.findById(aposta.getUsuario().getId());
			// não posso limpar, pq pode haver outra partida acontcendo
			// só limpa se não houver mais nenhum jogo acontecendo (ao vivo/iniciado) e não finalizado
			//rnk.setPontuacaoProvisoria(null);
			rnk.setPosicaoAnterior(rnk.getPontuacao());
			rnk.setPontuacao(rnk.getPontuacao() + pontuacao);
			
			rankingRepository.save(rnk);
			apostaRepository.save(aposta);
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
