package br.com.bolao.bolao10.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
import br.com.bolao.bolao10.repository.RankingHistoricoRepository;
import br.com.bolao.bolao10.repository.RankingRepository;
import br.com.bolao.bolao10.repository.UsuarioBadgeRepository;
import br.com.bolao.bolao10.repository.UserRepository;
import br.com.bolao.bolao10.domain.enums.TipoNotificacaoEnum;
import br.com.bolao.bolao10.domain.Usuario;

/**
 * Serviço responsável por calcular e atualizar os Badges (selos de gamificação).
 * Cada badge tem um método privado dedicado com sua lógica de negócio.
 */
@Service
public class BadgeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BadgeService.class);

	// IDs dos badges conforme inseridos na tabela (mesma ordem do INSERT)
	private static final long BADGE_MAIOR_CAMPEAO = 1L;
	private static final long BADGE_LANTERNA      = 2L;
	private static final long BADGE_BETEIRO       = 3L;
	private static final long BADGE_GATO_PRETO    = 4L;
	private static final long BADGE_FOGUETE       = 5L;
	private static final long BADGE_MEIA_BOCA     = 6L;
	private static final long BADGE_EMPACADO      = 7L;
	private static final long BADGE_GOLEADOR      = 8L;

	@Autowired private BadgeRepository badgeRepository;
	@Autowired private UsuarioBadgeRepository usuarioBadgeRepository;
	@Autowired private RankingRepository rankingRepository;
	@Autowired private RankingHistoricoRepository rankingHistoricoRepository;
	@Autowired private ApostaRepository apostaRepository;
	@Autowired private ApostaColocacaoRepository apostaColocacaoRepository;
	@Autowired private ColocacaoRepository colocacaoRepository;
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
				item.put("conquistas", new java.util.ArrayList<String>());
				agrupado.put(idBadge, item);
			}
			java.util.Map<String, Object> item = agrupado.get(idBadge);
			
			// Se o badge atual for verdadeiro (1), marca como ativo geral
			item.put("ativo", ub.getAtual());
			
			// Adiciona data à lista de conquistas
			@SuppressWarnings("unchecked")
			List<String> conquistas = (List<String>) item.get("conquistas");
			if (ub.getDataConquista() != null) {
				java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
				conquistas.add(ub.getDataConquista().format(fmt));
			}
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
	 * Ponto de entrada principal — atualiza todos os 8 badges.
	 * Chamado pelo Scheduled job de madrugada.
	 */
	@Transactional
	public void atualizarTodosBadges() {
		LOGGER.info("Iniciando atualização de todos os Badges...");
		try { aplicarBadgeMaiorCampeao(); } catch (Exception e) { LOGGER.error("Erro badge MaiorCampeao", e); }
		try { aplicarBadgeLanterna(); }      catch (Exception e) { LOGGER.error("Erro badge Lanterna", e); }
		try { aplicarBadgeBeteiro(); }       catch (Exception e) { LOGGER.error("Erro badge Beteiro", e); }
		try { aplicarBadgeGatoPreto(); }     catch (Exception e) { LOGGER.error("Erro badge GatoPreto", e); }
		try { aplicarBadgeFoguete(); }       catch (Exception e) { LOGGER.error("Erro badge Foguete", e); }
		try { aplicarBadgeMeiaBoca(); }      catch (Exception e) { LOGGER.error("Erro badge MeiaBoca", e); }
		try { aplicarBadgeEmpacado(); }      catch (Exception e) { LOGGER.error("Erro badge Empacado", e); }
		try { aplicarBadgeGoleador(); }      catch (Exception e) { LOGGER.error("Erro badge Goleador", e); }

		// Atualizar nível de todos os usuários com base na pontuação atual do Ranking
		try {
			List<Ranking> todosRanking = rankingRepository.carregarRanking();
			if (todosRanking != null) {
				for (Ranking r : todosRanking) {
					userService.atualizarNivel(r.getUsuario().getId(), r.getPontuacao());
				}
			}
		} catch (Exception e) { LOGGER.error("Erro ao atualizar níveis", e); }

		LOGGER.info("Atualização de Badges concluída.");
	}

	// ─────────────────────────────────────────────
	// Métodos privados — um por badge
	// ─────────────────────────────────────────────

	/**
	 * 🏆 MAIOR CAMPEÃO — usuário que ficou mais tempo na posição 1 do histórico.
	 */
	private void aplicarBadgeMaiorCampeao() {
		List<RankingHistorico> historico = rankingHistoricoRepository.carregarRankingHistorico();
		if (historico == null || historico.isEmpty()) return;

		// Conta quantos registros históricos cada usuário tem com posição = 1
		java.util.Map<Long, Long> contadorLideres = new java.util.HashMap<>();
		for (RankingHistorico rh : historico) {
			if (rh.getPosicao() != null && rh.getPosicao() == 1) {
				Long idUsuario = rh.getUsuario().getId();
				contadorLideres.merge(idUsuario, 1L, Long::sum);
			}
		}
		if (contadorLideres.isEmpty()) return;

		Long idMaiorCampeao = contadorLideres.entrySet().stream()
				.max(java.util.Map.Entry.comparingByValue())
				.map(java.util.Map.Entry::getKey)
				.orElse(null);

		atribuirBadge(BADGE_MAIOR_CAMPEAO, idMaiorCampeao);
	}

	/**
	 * 💩 LANTERNA — usuário em último lugar no ranking atual.
	 */
	private void aplicarBadgeLanterna() {
		List<Ranking> ranking = rankingRepository.carregarRanking();
		if (ranking == null || ranking.isEmpty()) return;
		Long idLanterna = ranking.get(ranking.size() - 1).getUsuario().getId();
		atribuirBadge(BADGE_LANTERNA, idLanterna);
	}

	/**
	 * 🎯 BETEIRO — usuário com mais placares exatos (pontuação = 5) em apostas.
	 */
	private void aplicarBadgeBeteiro() {
		Long idBeteiro = apostaRepository.carregarIdUsuarioMaisPlacarExato();
		atribuirBadge(BADGE_BETEIRO, idBeteiro);
	}

	/**
	 * 🐈 GATO PRETO — usuário com mais apostas zeradas (pontuação = 0).
	 */
	private void aplicarBadgeGatoPreto() {
		Long idGatoPreto = apostaRepository.carregarIdUsuarioMaisZerou();
		atribuirBadge(BADGE_GATO_PRETO, idGatoPreto);
	}

	/**
	 * 🚀 FOGUETE — usuário com maior subida de posições em um único dia.
	 * Compara posição atual com posição anterior no histórico.
	 */
	private void aplicarBadgeFoguete() {
		List<Ranking> ranking = rankingRepository.carregarRanking();
		if (ranking == null || ranking.isEmpty()) return;

		Long idFoguete = null;
		int maiorSubida = 0;
		int posicaoAtual = 1;

		for (Ranking r : ranking) {
			if (r.getPosicaoAnterior() != null && r.getPosicaoAnterior() != 999) {
				int subida = r.getPosicaoAnterior() - posicaoAtual;
				if (subida > maiorSubida) {
					maiorSubida = subida;
					idFoguete = r.getUsuario().getId();
				}
			}
			posicaoAtual++;
		}
		if (idFoguete != null) atribuirBadge(BADGE_FOGUETE, idFoguete);
	}

	/**
	 * 😐 MEIA BOCA — usuário com mais palpites de empate acertados.
	 */
	private void aplicarBadgeMeiaBoca() {
		Long idMeiaBoca = apostaRepository.carregarIdUsuarioMaisEmpate();
		atribuirBadge(BADGE_MEIA_BOCA, idMeiaBoca);
	}

	/**
	 * 🔋 EMPACADO — usuários que não pontuaram na última rodada finalizada.
	 * Pode ser multiple (vários podem zerar), por isso inativa e atribui a todos.
	 */
	@Transactional
	private void aplicarBadgeEmpacado() {
		List<Long> idsEmpacados = apostaRepository.carregarIdsUsuariosEmpacados();
		if (idsEmpacados == null || idsEmpacados.isEmpty()) return;

		Badge badge = badgeRepository.findById(BADGE_EMPACADO);
		if (badge == null) return;

		usuarioBadgeRepository.inativarPorBadge(BADGE_EMPACADO);

		for (Long idUsuario : idsEmpacados) {
			UsuarioBadge ub = new UsuarioBadge();
			ub.setUsuario(userRepository.findById(idUsuario));
			ub.setBadge(badge);
			ub.setDataConquista(LocalDateTime.now());
			ub.setAtual(Boolean.TRUE);
			usuarioBadgeRepository.salvar(ub);
		}
	}

	/**
	 * ⚽ GOLEADOR — usuário que apostou no artilheiro provisório da copa.
	 * Compara a seleção artilheira apostada com a colocação real.
	 */
	private void aplicarBadgeGoleador() {
		// Carrega o artilheiro definido na colocação oficial
		Colocacao colocacaoReal = colocacaoRepository.carregarColocacao();
		if (colocacaoReal == null || colocacaoReal.getArtilharia() == null) return;

		Long idArtilheiro = colocacaoReal.getArtilharia().getId();
		Long idGoleador = apostaColocacaoRepository.carregarIdUsuarioAcertouArtilheiro(idArtilheiro);
		if (idGoleador != null) atribuirBadge(BADGE_GOLEADOR, idGoleador);
	}

	/**
	 * Helper: inativa badge do dono anterior e atribui ao novo dono.
	 * Badges dinâmicos têm exatamente 1 dono ativo por vez.
	 */
	@Transactional
	private void atribuirBadge(Long idBadge, Long idUsuario) {
		if (idUsuario == null) return;
		Badge badge = badgeRepository.findById(idBadge);
		if (badge == null) return;

		// Inativa o dono anterior
		usuarioBadgeRepository.inativarPorBadge(idBadge);

		// Cria o novo registro
		UsuarioBadge ub = new UsuarioBadge();
		Usuario usuario = userRepository.findById(idUsuario);
		ub.setUsuario(usuario);
		ub.setBadge(badge);
		ub.setDataConquista(LocalDateTime.now());
		ub.setAtual(Boolean.TRUE);
		usuarioBadgeRepository.salvar(ub);

		// Dispara a Notificacao Global
		String msg = usuario.getNome() + " conquistou um novo selo de qualidade: " + badge.getNome();
		notificacaoService.salvarNotificacao(TipoNotificacaoEnum.NOVO_BADGE, msg);

		LOGGER.info("Badge '{}' atribuído ao usuário ID {}", badge.getNome(), idUsuario);
	}
}
