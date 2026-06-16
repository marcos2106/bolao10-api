package br.com.bolao.bolao10.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Badge;
import br.com.bolao.bolao10.domain.Colocacao;
import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.domain.RankingHistorico;
import br.com.bolao.bolao10.domain.UsuarioBadge;
import br.com.bolao.bolao10.repository.ApostaColocacaoRepository;
import br.com.bolao.bolao10.repository.ApostaRepository;
import br.com.bolao.bolao10.repository.BadgeRepository;
import br.com.bolao.bolao10.repository.ColocacaoRepository;
import br.com.bolao.bolao10.repository.PartidaRepository;
import br.com.bolao.bolao10.repository.RankingHistoricoRepository;
import br.com.bolao.bolao10.repository.RankingRepository;
import br.com.bolao.bolao10.repository.UsuarioBadgeRepository;
import br.com.bolao.bolao10.repository.UserRepository;
import br.com.bolao.bolao10.domain.enums.TipoNotificacaoEnum;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.domain.Usuario;

/**
 * Serviço responsável por calcular e atualizar os Badges (selos de gamificação).
 * Cada badge tem um método privado dedicado com sua lógica de negócio.
 */
@Service
public class BadgeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BadgeService.class);

	// IDs dos badges conforme inseridos na tabela (mesma ordem do INSERT)
	private static final long BADGE_SEGUE_LIDER   = 1L;
	private static final long BADGE_LANTERNA      = 2L;
	private static final long BADGE_BETEIRO       = 3L;
	private static final long BADGE_GATO_PRETO    = 4L;
	private static final long BADGE_FOGUETE       = 5L;
	private static final long BADGE_MESTRE_EMPATE = 6L;
	private static final long BADGE_EMPACADO      = 7L;
	private static final long BADGE_GOLEADOR      = 8L;
	private static final ZoneId TIME_ZONE = ZoneId.of("America/Sao_Paulo");

	@Autowired private BadgeRepository badgeRepository;
	@Autowired private UsuarioBadgeRepository usuarioBadgeRepository;
	@Autowired private RankingRepository rankingRepository;
	@Autowired private RankingHistoricoRepository rankingHistoricoRepository;
	@Autowired private ApostaRepository apostaRepository;
	@Autowired private ApostaColocacaoRepository apostaColocacaoRepository;
	@Autowired private ColocacaoRepository colocacaoRepository;
	@Autowired private PartidaRepository partidaRepository;
	@Autowired private UserRepository userRepository;
	@Autowired private UserService userService;
	@Autowired private NotificacaoService notificacaoService;

	// ─────────────────────────────────────────────
	// API pública
	// ─────────────────────────────────────────────

	/** Carrega badges ativos de um usuário específico (para o endpoint REST). */
	public List<Badge> carregarBadgesDoUsuario(Long idUsuario) {
		return usuarioBadgeRepository.carregarBadgesAtivos(idUsuario);
	}

	/**
	 * Retorna o histórico de badges agrupado. Cada item é um badge que a pessoa ganhou.
	 * Ele contém uma lista de "conquistas" (com a data de cada vez que ganhou) e
	 * um indicativo se o usuário possui ele ativamente hoje ou não.
	 */
	public List<java.util.Map<String, Object>> carregarHistoricoBadgesDoUsuario(Long idUsuario) {
		List<UsuarioBadge> historico = usuarioBadgeRepository.carregarHistoricoBadges(idUsuario);
		
		java.util.Map<Long, java.util.Map<String, Object>> agrupado = new java.util.LinkedHashMap<>();
		for (UsuarioBadge ub : historico) {
			Long idBadge = ub.getBadge().getId();
			if (!agrupado.containsKey(idBadge)) {
				java.util.Map<String, Object> item = new java.util.LinkedHashMap<>();
				item.put("badge", ub.getBadge());
				item.put("ativo", false);
				item.put("conquistas", new java.util.ArrayList<java.util.Map<String, Object>>());
				agrupado.put(idBadge, item);
			}
			java.util.Map<String, Object> item = agrupado.get(idBadge);
			
			// Se o badge atual for verdadeiro (1), marca como ativo geral
			if (Boolean.TRUE.equals(ub.getAtual())) {
				item.put("ativo", true);
			}
			
			// Adiciona conquista como mapa de data e atual
			@SuppressWarnings("unchecked")
			List<java.util.Map<String, Object>> conquistas = (List<java.util.Map<String, Object>>) item.get("conquistas");
			java.util.Map<String, Object> conquista = new java.util.HashMap<>();
			if (ub.getDataConquista() != null) {
				java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
				conquista.put("data", ub.getDataConquista().format(fmt));
			}
			conquista.put("atual", Boolean.TRUE.equals(ub.getAtual()));
			// Insere na posição 0 para inverter a ordem (mais antigos à esquerda e mais recentes/atual à direita)
			conquistas.add(0, conquista);
		}
		
		return new java.util.ArrayList<>(agrupado.values());
	}

	/**
	 * Carrega mapa idUsuario → badges ativos (para rankings — evita N+1 queries).
	 */
	public Map<Long, List<Badge>> carregarMapaBadgesAtivos() {
		return usuarioBadgeRepository.carregarMapaBadgesAtivos();
	}

	/**
	 * Carrega badges SOMENTE de usuários específicos (ainda mais otimizado).
	 * Usado quando já sabemos a lista de IDs dos usuários.
	 */
	public Map<Long, List<Badge>> carregarMapaBadgesDeUsuarios(List<Long> idsUsuarios) {
		if (idsUsuarios == null || idsUsuarios.isEmpty()) {
			return new java.util.HashMap<>();
		}
		return usuarioBadgeRepository.carregarMapaBadgesDeUsuarios(idsUsuarios);
	}

	/**
	 * Ponto de entrada principal — atualiza os badges ativos considerando as
	 * partidas finalizadas no dia anterior à execução.
	 * Chamado pelo Scheduled job de madrugada e executado somente quando houve jogos.
	 */
	@Transactional
	public void atualizarTodosBadges() {
		String executionId = java.util.UUID.randomUUID().toString().substring(0, 8);
		LOGGER.info("[{}] Iniciando atualização de todos os Badges...", executionId);

		LocalDate dataReferencia = LocalDate.now(TIME_ZONE).minusDays(1);
		LocalDateTime inicio = dataReferencia.atStartOfDay();
		LocalDateTime fim = inicio.plusDays(1);

		if (!partidaRepository.existePartidaFinalizadaEntre(inicio, fim)) {
			LOGGER.info("[{}] Nenhuma partida finalizada em {}. Badges não serão atualizados.",
					executionId, dataReferencia);
			return;
		}

		try { aplicarBadgeSegueLider(); }    catch (Exception e) { LOGGER.error("[{}] Erro badge SegueLider", executionId, e); }
		try { aplicarBadgeLanterna(); }      catch (Exception e) { LOGGER.error("[{}] Erro badge Lanterna", executionId, e); }
		try { aplicarBadgeBeteiro(inicio, fim); }       catch (Exception e) { LOGGER.error("[{}] Erro badge Beteiro", executionId, e); }
		try { aplicarBadgeGatoPreto(inicio, fim); }     catch (Exception e) { LOGGER.error("[{}] Erro badge GatoPreto", executionId, e); }
		try { aplicarBadgeFoguete(dataReferencia); }    catch (Exception e) { LOGGER.error("[{}] Erro badge Foguete", executionId, e); }
		try { aplicarBadgeMestreEmpate(inicio, fim); }  catch (Exception e) { LOGGER.error("[{}] Erro badge MestreEmpate", executionId, e); }
		// Badge Empacado desabilitado: na prática duplica o Gato Preto.
		// try { aplicarBadgeEmpacado(inicio, fim); }      catch (Exception e) { LOGGER.error("[{}] Erro badge Empacado", executionId, e); }
		try { aplicarBadgeGoleador(); }      catch (Exception e) { LOGGER.error("[{}] Erro badge Goleador", executionId, e); }

		// Atualizar nível de todos os usuários com base na pontuação atual do Ranking
		try {
			List<Ranking> todosRanking = rankingRepository.carregarRanking();
			if (todosRanking != null) {
				for (Ranking r : todosRanking) {
					if (isUsuarioPerfilUser(r.getUsuario())) {
						userService.atualizarNivel(r.getUsuario().getId(), r.getPontuacao());
					}
				}
			}
		} catch (Exception e) { LOGGER.error("[{}] Erro ao atualizar níveis", executionId, e); }

		LOGGER.info("[{}] Atualização de Badges concluída.", executionId);
	}

	// ─────────────────────────────────────────────
	// Métodos privados — um por badge
	// ─────────────────────────────────────────────

	/**
	 * 🏆 SEGUE O LÍDER — todos os usuários USER empatados na maior pontuação atual.
	 */
	private void aplicarBadgeSegueLider() {
		sincronizarBadge(BADGE_SEGUE_LIDER, carregarIdsPorPontuacaoExtrema(true));
	}

	/**
	 * 💩 LANTERNA — todos os usuários USER empatados na menor pontuação atual.
	 */
	private void aplicarBadgeLanterna() {
		sincronizarBadge(BADGE_LANTERNA, carregarIdsPorPontuacaoExtrema(false));
	}

	/**
	 * 🎯 BETEIRO — usuários USER com mais palpites exatos de placar (pontuação = 5)
	 * nas partidas finalizadas do dia anterior.
	 */
	private void aplicarBadgeBeteiro(LocalDateTime inicio, LocalDateTime fim) {
		sincronizarBadge(BADGE_BETEIRO,
				apostaRepository.carregarIdsUsuariosMaisPlacarExato(inicio, fim));
	}

	/**
	 * 🐈 GATO PRETO — usuários USER com mais apostas zeradas (pontuação = 0)
	 * nas partidas finalizadas do dia anterior.
	 */
	private void aplicarBadgeGatoPreto(LocalDateTime inicio, LocalDateTime fim) {
		sincronizarBadge(BADGE_GATO_PRETO,
				apostaRepository.carregarIdsUsuariosMaisZerou(inicio, fim));
	}

	/**
	 * 🚀 FOGUETE — usuários USER com a maior subida positiva de posições.
	 * Compara o histórico do dia dos jogos com o histórico criado na execução atual.
	 */
	private void aplicarBadgeFoguete(LocalDate dataReferencia) {
		List<RankingHistorico> rankingAnterior =
				rankingHistoricoRepository.carregarRankingHistoricoPorData(dataReferencia);
		List<RankingHistorico> rankingAtual =
				rankingHistoricoRepository.carregarRankingHistoricoPorData(dataReferencia.plusDays(1));

		Map<Long, Integer> posicoesAnteriores = carregarPosicoesUsuarios(rankingAnterior);
		Map<Long, Integer> posicoesAtuais = carregarPosicoesUsuarios(rankingAtual);

		List<Long> idsFoguete = new ArrayList<>();
		int maiorSubida = 0;
		for (Map.Entry<Long, Integer> posicaoAtual : posicoesAtuais.entrySet()) {
				Integer posicaoAnterior = posicoesAnteriores.get(posicaoAtual.getKey());
				if (posicaoAnterior == null) continue;

				int subida = posicaoAnterior - posicaoAtual.getValue();
				if (subida > maiorSubida) {
					maiorSubida = subida;
					idsFoguete.clear();
					idsFoguete.add(posicaoAtual.getKey());
				} else if (subida > 0 && subida == maiorSubida) {
					idsFoguete.add(posicaoAtual.getKey());
				}
		}
		sincronizarBadge(BADGE_FOGUETE, idsFoguete);
	}

	/**
	 * MESTRE DO EMPATE — usuários USER com mais palpites de empate acertados
	 * nas partidas finalizadas do dia anterior.
	 */
	private void aplicarBadgeMestreEmpate(LocalDateTime inicio, LocalDateTime fim) {
		sincronizarBadge(BADGE_MESTRE_EMPATE,
				apostaRepository.carregarIdsUsuariosMaisEmpate(inicio, fim));
	}

	/**
	 * 🔋 EMPACADO — usuários USER cuja soma das pontuações foi zero em todas
	 * as partidas finalizadas do dia anterior.
	 */
	@Transactional
	private void aplicarBadgeEmpacado(LocalDateTime inicio, LocalDateTime fim) {
		sincronizarBadge(BADGE_EMPACADO,
				apostaRepository.carregarIdsUsuariosEmpacados(inicio, fim));
	}

	/**
	 * ⚽ GOLEADOR — todos os usuários USER que apostaram na seleção do
	 * artilheiro provisório da copa.
	 */
	private void aplicarBadgeGoleador() {
		// Carrega o artilheiro definido na colocação oficial
		Colocacao colocacaoReal = colocacaoRepository.carregarColocacao();
		if (colocacaoReal == null || colocacaoReal.getArtilharia() == null) {
			sincronizarBadge(BADGE_GOLEADOR, new ArrayList<>());
			return;
		}

		Long idArtilheiro = colocacaoReal.getArtilharia().getId();
		sincronizarBadge(BADGE_GOLEADOR,
				apostaColocacaoRepository.carregarIdsUsuariosAcertaramArtilheiro(idArtilheiro));
	}

	private List<Long> carregarIdsPorPontuacaoExtrema(boolean maiorPontuacao) {
		List<Ranking> ranking = rankingRepository.carregarRanking();
		List<Long> idsUsuarios = new ArrayList<>();
		Integer pontuacaoExtrema = null;

		if (ranking == null) return idsUsuarios;
		for (Ranking item : ranking) {
			if (!isUsuarioPerfilUser(item.getUsuario()) || item.getPontuacao() == null) continue;
			if (pontuacaoExtrema == null
					|| (maiorPontuacao && item.getPontuacao() > pontuacaoExtrema)
					|| (!maiorPontuacao && item.getPontuacao() < pontuacaoExtrema)) {
				pontuacaoExtrema = item.getPontuacao();
				idsUsuarios.clear();
				idsUsuarios.add(item.getUsuario().getId());
			} else if (item.getPontuacao().equals(pontuacaoExtrema)) {
				idsUsuarios.add(item.getUsuario().getId());
			}
		}
		return idsUsuarios;
	}

	private boolean isUsuarioPerfilUser(Usuario usuario) {
		return usuario != null && UserProfile.USER.equals(usuario.getPerfil());
	}

	private Map<Long, Integer> carregarPosicoesUsuarios(List<RankingHistorico> historicos) {
		Map<Long, Integer> posicoes = new HashMap<>();
		if (historicos == null) return posicoes;

		List<RankingHistorico> historicosUsuarios = new ArrayList<>();
		Set<Long> idsAdicionados = new HashSet<>();
		for (RankingHistorico historico : historicos) {
			if (historico.getPosicao() != null
					&& isUsuarioPerfilUser(historico.getUsuario())
					&& idsAdicionados.add(historico.getUsuario().getId())) {
				historicosUsuarios.add(historico);
			}
		}
		historicosUsuarios.sort(Comparator.comparing(RankingHistorico::getPosicao));

		int posicao = 1;
		for (RankingHistorico historico : historicosUsuarios) {
			posicoes.put(historico.getUsuario().getId(), posicao++);
		}
		return posicoes;
	}

	/**
	 * Sincroniza todos os vencedores de um badge.
	 * Mantém ativos os usuários que continuam atendendo ao critério, inativa quem
	 * deixou de atender e cria uma conquista somente para os novos vencedores.
	 */
	private void sincronizarBadge(Long idBadge, List<Long> idsUsuarios) {
		Set<Long> idsVencedores = new HashSet<>();
		if (idsUsuarios != null) {
			for (Long idUsuario : idsUsuarios) {
				if (idUsuario != null) idsVencedores.add(idUsuario);
			}
		}

		List<Long> idsAtivos = usuarioBadgeRepository.carregarIdsUsuariosAtivosPorBadge(idBadge);
		usuarioBadgeRepository.inativarPorBadgeExceto(idBadge, new ArrayList<>(idsVencedores));

		if (idsVencedores.isEmpty()) return;
		Badge badge = badgeRepository.findById(idBadge);
		if (badge == null) return;

		Set<Long> idsJaAtivos = new HashSet<>();
		if (idsAtivos != null) idsJaAtivos.addAll(idsAtivos);

		for (Long idUsuario : idsVencedores) {
			if (idsJaAtivos.contains(idUsuario)) continue;
			Usuario usuario = userRepository.findById(idUsuario);
			if (!isUsuarioPerfilUser(usuario)) continue;
			UsuarioBadge ub = new UsuarioBadge();
			ub.setUsuario(usuario);
			ub.setBadge(badge);
			ub.setDataConquista(LocalDateTime.now(TIME_ZONE));
			ub.setAtual(Boolean.TRUE);
			usuarioBadgeRepository.salvar(ub);

			String msg = usuario.getNome() + " conquistou um novo selo de qualidade: " + badge.getNome();
			notificacaoService.salvarNotificacao(TipoNotificacaoEnum.NOVO_BADGE, msg);

			LOGGER.info("Badge '{}' atribuído ao usuário ID {}", badge.getNome(), idUsuario);
		}
	}
}
