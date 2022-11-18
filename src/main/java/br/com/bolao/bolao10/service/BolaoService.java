
package br.com.bolao.bolao10.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Aposta;
import br.com.bolao.bolao10.domain.ApostaColocacao;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.domain.RankingCustomizado;
import br.com.bolao.bolao10.domain.RankingHistorico;
import br.com.bolao.bolao10.domain.Situacao;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.exception.Bolao10Exception;
import br.com.bolao.bolao10.model.ApostaColocacaoSelecao;
import br.com.bolao.bolao10.model.ApostaFilter;
import br.com.bolao.bolao10.model.ApostaPartida;
import br.com.bolao.bolao10.model.HomeUsuario;
import br.com.bolao.bolao10.model.HomeUsuarioGrafico;
import br.com.bolao.bolao10.model.PontuacaoUsuarioPartida;
import br.com.bolao.bolao10.model.RankingCustomizadoRequest;
import br.com.bolao.bolao10.model.RankingUsuario;
import br.com.bolao.bolao10.repository.ApostaColocacaoRepository;
import br.com.bolao.bolao10.repository.ApostaRepository;
import br.com.bolao.bolao10.repository.PartidaRepository;
import br.com.bolao.bolao10.repository.RankingCustomizadoRepository;
import br.com.bolao.bolao10.repository.RankingHistoricoRepository;
import br.com.bolao.bolao10.repository.RankingRepository;
import br.com.bolao.bolao10.repository.SelecaoRepository;
import br.com.bolao.bolao10.repository.UserRepository;
import br.com.bolao.bolao10.support.Constants;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BolaoService {

	@Autowired
	private ConfiguracaoService configuracaoService;

	@Autowired
	private PartidaRepository partidaRepository;

	@Autowired
	private SelecaoRepository selecaoRepository;

	@Autowired
	private ApostaRepository apostaRepository;

	@Autowired
	private ApostaColocacaoRepository apostaColocacaoRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RankingRepository rankingRepository;

	@Autowired
	private RankingHistoricoRepository rhRepository;

	@Autowired
	private RankingCustomizadoRepository rcRepository;

	@Transactional
	public List<Ranking> carregarRanking() {
		return rankingRepository.carregarRanking();
	}

	public List<Partida> carregarPartidas(Usuario usuarioLogado) {

		Long idUsuario = usuarioLogado.getId();

		List<Partida> listaPartidas = partidaRepository.carregarPartidasAposta();
		listaPartidas.forEach(partida -> {
			Aposta aposta = apostaRepository.findById(partida.getId(), idUsuario);
			partida.setPlacarA(aposta.getPlacarA());
			partida.setPlacarB(aposta.getPlacarB());
		});
		return listaPartidas;
	}

	@Transactional
	public void salvarAposta(ApostaFilter apostaFilter, Usuario usuarioLogado) {

		if (isBolaoDurante()) {
			throw new Bolao10Exception("Não é possível salvar aposta fora do período!");
		}
		
		Long idUsuario = usuarioLogado.getId();
		try {
			for (Partida partida : apostaFilter.getListaPartidas()) {
				Aposta aposta = apostaRepository.findById(partida.getId(), idUsuario);
				aposta.setPlacarA(partida.getPlacarA());
				aposta.setPlacarB(partida.getPlacarB());
				aposta.setPartida(partidaRepository.findById(partida.getId()));
				apostaRepository.save(aposta);
			}

			ApostaColocacao colocacao = apostaColocacaoRepository.findByUsuario(idUsuario);
			colocacao.setCampeao(selecaoRepository.findByName(apostaFilter.getPosicao().get(0))); //campeão
			colocacao.setVice(selecaoRepository.findByName(apostaFilter.getPosicao().get(1))); //vice
			colocacao.setTerceiro(selecaoRepository.findByName(apostaFilter.getPosicao().get(2))); //terceiro
			colocacao.setQuarto(selecaoRepository.findByName(apostaFilter.getPosicao().get(3))); //quarto
			colocacao.setArtilharia(selecaoRepository.findByName(apostaFilter.getPosicao().get(4))); //artilharia
			apostaColocacaoRepository.save(colocacao);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Bolao10Exception("Erro ao salvar a aposta!");
		}
	}

	private boolean isBolaoDurante() {
		return configuracaoService.situacaoAtiva().getId() == Constants.SITUACAO_DURANTE;
	}

	@Transactional
	public void finalizarAposta(ApostaFilter apostaFilter, Usuario usuarioLogado) {

		// Valida se todos os dados foram preenchidos
		validarAposta(apostaFilter);

		// salva as apostas
		salvarAposta(apostaFilter, usuarioLogado);

		// zera a pontuação e seta como finalizada
		finalizarAposta(usuarioLogado);
	}

	@Transactional
	private void finalizarAposta(Usuario usuarioLogado) {

		Long idUsuario = usuarioLogado.getId();

		Usuario usuario = userRepository.findById(idUsuario);
		usuario.setAposta(Boolean.TRUE);
		usuario.setDataHoraAposta(LocalDateTime.now());
		userRepository.save(usuario);

		Ranking rnk = rankingRepository.findById(idUsuario);
		rnk.setUsuario(usuario);
		rnk.setPontuacao(0);
		rnk.setPosicaoAnterior(999);
		rankingRepository.save(rnk);
	}

	private void validarAposta(ApostaFilter apostaFilter) {

		for (Partida partida : apostaFilter.getListaPartidas()) { 
			if (partida.getFase()==1 && (partida.getPlacarA() == null || partida.getPlacarB() == null)) {
				throw new Bolao10Exception("A aposta do Grupo "+ partida.getSelecaoA().getGrupo() +" entre "+ partida.getSelecaoA().getNome() +" x "+ partida.getSelecaoB().getNome() +" está incompleta!");
			}
			if (partida.getFase()==2 && (partida.getPlacarA() == null || partida.getPlacarB() == null)) {
				throw new Bolao10Exception("A aposta nas oitavas entre "+ partida.getSelecaoA().getNome() +" x "+ partida.getSelecaoB().getNome() +" está incompleta!");
			}
			if (partida.getFase()==3 && (partida.getPlacarA() == null || partida.getPlacarB() == null)) {
				throw new Bolao10Exception("A aposta nas quartas entre "+ partida.getSelecaoA().getNome() +" x "+ partida.getSelecaoB().getNome() +" está incompleta!");
			}
			if (partida.getFase()==4 && (partida.getPlacarA() == null || partida.getPlacarB() == null)) {
				throw new Bolao10Exception("A aposta nas semis entre "+ partida.getSelecaoA().getNome() +" x "+ partida.getSelecaoB().getNome() +" está incompleta!");
			}
			if (partida.getFase()>=5 && (partida.getPlacarA() == null || partida.getPlacarB() == null)) {
				throw new Bolao10Exception("A aposta nas finais entre "+ partida.getSelecaoA().getNome() +" x "+ partida.getSelecaoB().getNome() +" está incompleta!");
			}
		}
		for (String posicao : apostaFilter.getPosicao()) {
			if (posicao == null || posicao.trim().isEmpty()) {
				throw new Bolao10Exception("A aposta em Colocações ou Artilharia está incorreta!");
			}
		}
		List<String> posicao = apostaFilter.getPosicao();

		if (posicao.get(0).equalsIgnoreCase(posicao.get(1)) 
				|| posicao.get(0).equalsIgnoreCase(posicao.get(2))
				|| posicao.get(0).equalsIgnoreCase(posicao.get(3))
				|| posicao.get(1).equalsIgnoreCase(posicao.get(2))
				|| posicao.get(1).equalsIgnoreCase(posicao.get(3))
				|| posicao.get(2).equalsIgnoreCase(posicao.get(3))) {
			throw new Bolao10Exception("Não pode repetir seleção em diferentes colocações!");
		}
	}

	@Transactional
	public ApostaColocacao carregarApostaColocacao(Usuario usuarioLogado) {

		return apostaColocacaoRepository.findByUsuario(usuarioLogado.getId());
	}

	public Boolean verificarFinalizada(Usuario usuarioLogado) {

		Long idUsuario = usuarioLogado.getId();

		Usuario usuario = userRepository.findById(idUsuario);
		if (usuario.getAposta()) {
			return Boolean.TRUE;
			//throw new Bolao10Exception("A aposta já finalizada!");
		}
		return Boolean.FALSE;
	}

	public List<Aposta> carregarApostaPorPartida(Long idPartida) {

		if (isBolaoDurante()) {

			if (idPartida == null) {
				throw new Bolao10Exception("Apostas não encontradas!");
			}
			Partida partida = partidaRepository.findById(idPartida);

			List<Aposta> listaAposta = new ArrayList<Aposta>();
			try {
				listaAposta = apostaRepository.carregarApostaPorPartida(idPartida);

				if (partida.getIniciada()) {
					if (partida.getFinalizada()) {
						Collections.sort(listaAposta, Comparator.comparing(Aposta::getPontuacao).reversed());
					} else {
						Collections.sort(listaAposta, Comparator.comparing(Aposta::getPontuacaoProvisoria).reversed());
					}
				} else {
					Collections.sort(listaAposta, Comparator.comparing(Aposta::getPlacarA));
				}
				return listaAposta;

			} catch (Exception e) {
				return new ArrayList<Aposta>();
			}
		} else {
			return new ArrayList<Aposta>();
		}
	}

	public ApostaColocacaoSelecao carregarApostaColocacaoPorSelecao(Long idSelecaoA, Long idSelecaoB) {

		return apostaColocacaoRepository.carregarApostaColocacaoPorSelecao(idSelecaoA, idSelecaoB);
	}

	public List<Aposta> carregarApostaPorUsuario(Long idUsuario) {

		if (idUsuario == null) {
			throw new Bolao10Exception("Usuário não encontrado!");
		}

		return apostaRepository.carregarApostaPorUsuario(idUsuario);
	}

	public ApostaPartida calcularApostasPorPartida(Long partida) {

		return apostaRepository.calcularApostasPorPartida(partida);
	}

	@Transactional
	public void atualizarPontuacaoDiaria() {

		// primeiro verifica a situação do bolão, se está DURANTE
		Situacao situacao = configuracaoService.situacaoAtiva();

		if (situacao != null && situacao.getId() == Constants.SITUACAO_DURANTE) {

			List<Ranking> listaRanking = rankingRepository.carregarRanking();
			int i = 1;
			for (Ranking ranking : listaRanking) {
				RankingHistorico rh = new RankingHistorico();
				rh.setUsuario(ranking.getUsuario());
				rh.setPosicao(i++);
				rh.setPontuacao(ranking.getPontuacao());
				rh.setDataRegistro(LocalDate.now());
				rhRepository.save(rh);
			}
		}
	}

	public HomeUsuario carregarDadosUsuario(Long idUsuario) {

		HomeUsuario hu = new HomeUsuario();

		try {
			if (idUsuario == null) {
				throw new Bolao10Exception("Usuário não encontrado!");
			}
			// Dados de usuario
			hu = dadosUsuario(idUsuario, hu);

			// Dados do bolão
			hu = dadosBolaoPorUsuario(idUsuario, hu);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hu;
	}

	private HomeUsuario dadosBolaoPorUsuario(Long idUsuario, HomeUsuario hu) {

		Long pontuacao = rankingRepository.obterPontuacaoPorUsuario(idUsuario);
		Long posicao = rankingRepository.obterPosicaoPorUsuario(pontuacao);

		RankingHistorico rk = rhRepository.obterMelhorPosicaoPorUsuario(idUsuario);

		Long placarExato = apostaRepository.obterPlacarExatoPorUsuario(idUsuario);
		Long totalPartida = partidaRepository.obterQntdPartidasRealizadas();

		hu.setPontuacao(pontuacao);
		hu.setPosicao(posicao);

		if (rk != null) {
			hu.setMelhorPosicao(rk.getPosicao().longValue());
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
			hu.setMelhorPosicaoData(rk.getDataRegistro().format(fmt));
		}
		hu.setPlacarExato(placarExato);
		hu.setTotalPartida(totalPartida);

		return hu;
	}

	private HomeUsuario dadosUsuario(Long idUsuario, HomeUsuario hu) {
		Usuario usuario = userRepository.findById(idUsuario);

		if (usuario == null) {
			throw new Bolao10Exception("Usuário não encontrado!");
		}
		hu.setUsuario(usuario);

		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm:ss");
		if (usuario.getDataHoraPgto()!=null)
			hu.setDataPagamento(usuario.getDataHoraPgto().format(fmt));
		if (usuario.getDataHoraAposta()!=null)
			hu.setDataAposta(usuario.getDataHoraAposta().format(fmt));
		return hu;
	}

	public HomeUsuarioGrafico carregarDadosUsuarioGrafico(Long idUsuario) {

		HomeUsuarioGrafico hug = new HomeUsuarioGrafico();

		// primeiro verifica a situação do bolão, se está DURANTE
		Situacao situacao = configuracaoService.situacaoAtiva();

		if (situacao != null && situacao.getId() > Constants.SITUACAO_ANTES) {

			List<RankingHistorico> listaRank = rhRepository.carregarRankingHistoricoPorUsuario(idUsuario);

			for (RankingHistorico rh : listaRank) {

				DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
				hug.getListaDatas().add(rh.getDataRegistro().format(fmt));
				hug.getListaPontuacao().add(rh.getPontuacao());
				hug.getListaPosicao().add(rh.getPosicao());
			}

			// verificar o Lider
			Usuario usuario = rankingRepository.obterLiderRanking();

			List<RankingHistorico> listaRankLider = rhRepository.carregarRankingHistoricoPorUsuario(usuario.getId());

			for (RankingHistorico rh : listaRankLider) {
				hug.getListaPontuacaoLider().add(rh.getPontuacao());
			}
		}
		return hug;
	}

	public ApostaColocacao carregarApostaColocacaoPorUsuario(Long idUsuario) {

		return apostaColocacaoRepository.findByUsuario(idUsuario);
	}

	public List<PontuacaoUsuarioPartida> carregarPontuacaoPartidas() {

		List<PontuacaoUsuarioPartida> listaPontuacao = new ArrayList<PontuacaoUsuarioPartida>();

		if (configuracaoService.situacaoAtiva().getId() == Constants.SITUACAO_ANTES) {

			List<Usuario> listaUsuario = userRepository.carregarParticipantes();

			for (Usuario usuario : listaUsuario) {
				PontuacaoUsuarioPartida pup = new PontuacaoUsuarioPartida();
				pup.setUsuario(usuario);

				pup.setPontuacao(0L);
				listaPontuacao.add(pup);
			}
		} else {

			List<Usuario> listaUsuario = userRepository.carregarParticipantesAtivos();

			for (Usuario usuario : listaUsuario) {

				PontuacaoUsuarioPartida pup = new PontuacaoUsuarioPartida();
				pup.setUsuario(usuario);

				Long pontuacao = rankingRepository.obterPontuacaoPorUsuario(usuario.getId());
				pup.setPontuacao(pontuacao);

				List<Aposta> listaApostas = apostaRepository.carregarApostaPorUsuario(usuario.getId());
				pup.setListaApostas(listaApostas);

				ApostaColocacao apostaColocacao = apostaColocacaoRepository.findByUsuario(usuario.getId());
				pup.setApostaColocacao(apostaColocacao);

				listaPontuacao.add(pup);
			}
		}
		return listaPontuacao;
	}

	public List<RankingUsuario> carregarRankingAtivo() {

		List<Ranking> listaRanking = rankingRepository.carregarRanking();

		List<RankingUsuario> listaUR = new ArrayList<RankingUsuario>();

		for (Ranking ranking : listaRanking) {
			RankingUsuario ur = new RankingUsuario();
			ur.setRanking(ranking);
			listaUR.add(ur);
		}
		return listaUR;
	}

	@Transactional
	public void salvarRankingCustomizado(Long idUsuario, RankingCustomizadoRequest ranking) {

		validarRankingCustomizado(idUsuario, ranking);

		try {

			Usuario usuario = userRepository.findById(idUsuario);

			List<Ranking> listaRanking = new ArrayList<Ranking>();

			RankingCustomizado rc;
			if (ranking.getId() == null) {
				rc = new RankingCustomizado();
				rc.setUsuario(usuario);

				// adicionar a própria pessoa no ranking na criação do ranking se ela já apostou
				if (usuario.getAposta()) {
					listaRanking.add(rankingRepository.findById(idUsuario));
				}
			} else {
				rc = rcRepository.findById(ranking.getId());
			}
			rc.setNome(ranking.getNome());

			for (RankingUsuario rnkUsuario : ranking.getListaRankingUsuario()) {

				if (rnkUsuario.getSelecionado()) {
					Ranking rnk = rankingRepository.findById(rnkUsuario.getRanking().getUsuario().getId());
					listaRanking.add(rnk);
				}
			}
			Collections.sort(listaRanking, Comparator.comparing(Ranking::getPontuacao));

			rc.setListaRanking(listaRanking);

			rcRepository.save(rc);

		} catch (Exception e) {
			throw new Bolao10Exception("Erro ao salvar um Ranking!");
		}
	}

	private void validarRankingCustomizado(Long idUsuario, RankingCustomizadoRequest ranking) {

		if (idUsuario == null) {
			throw new Bolao10Exception("Usuário não encontrado!");
		}
		if (ranking == null || ranking.getListaRankingUsuario().isEmpty()) {
			throw new Bolao10Exception("Nenhum participante selecionado!");
		}
	}

	public List<RankingCustomizado> carregarRankingCustomizado(Long idUsuario) {

		if (idUsuario == null) {
			throw new Bolao10Exception("Usuário não encontrado!");
		}
		List<RankingCustomizado> listaRnkCu = rcRepository.carregarRankingCustomizado(idUsuario);

		// não sei se precisa ordenar novamente, já que no salvar já ordena
//		for (RankingCustomizado rc : listaRnkCu) {
//			Collections.sort(rc.getListaRanking(), Comparator.comparing(Ranking::getPontuacao));
//		}
		return listaRnkCu;
	}

	@Transactional
	public void apagarRankingCustomizado(Long idRanking) {

		if (idRanking == null) {
			throw new Bolao10Exception("Ranking não encontrado!");
		}
		RankingCustomizado rankingCustomizado = rcRepository.findById(idRanking);

		rcRepository.delete(rankingCustomizado);
	}

}
