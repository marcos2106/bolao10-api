package br.com.bolao.bolao10.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Badge;
import br.com.bolao.bolao10.domain.UsuarioBadge;

@Repository
public class UsuarioBadgeRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	/** Retorna os badges ATIVOS de um usuário específico */
	public List<Badge> carregarBadgesAtivos(Long idUsuario) {
		String sql = "select ub from UsuarioBadge ub "
				+ "where ub.usuario.id = :idUsuario and (ub.atual = true or ub.atual = '1')";
		try {
			List<UsuarioBadge> lista = em.createQuery(sql, UsuarioBadge.class)
					.setParameter("idUsuario", idUsuario)
					.getResultList();
			List<Badge> badges = new ArrayList<>();
			for (UsuarioBadge ub : lista) {
				if (Boolean.TRUE.equals(ub.getAtual())) {
					badges.add(ub.getBadge());
				}
			}
			return badges;
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	/** Retorna TODOS os UsuarioBadge (histórico completo, incluindo inativos) de um usuário */
	public List<UsuarioBadge> carregarHistoricoBadges(Long idUsuario) {
		String sql = "select ub from UsuarioBadge ub "
				+ "where ub.usuario.id = :idUsuario "
				+ "order by ub.badge.id, ub.dataConquista desc";
		try {
			return em.createQuery(sql, UsuarioBadge.class)
					.setParameter("idUsuario", idUsuario)
					.getResultList();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	/**
	 * Carrega um mapa idUsuario → lista de badges ativos.
	 * Usado para carregar os badges de todo o ranking em uma única query.
	 */
	public Map<Long, List<Badge>> carregarMapaBadgesAtivos() {
		Map<Long, List<Badge>> mapa = new HashMap<>();
		String sql = "select ub from UsuarioBadge ub where ub.atual = true or ub.atual = '1'";
		try {
			List<UsuarioBadge> lista = em.createQuery(sql, UsuarioBadge.class).getResultList();
			for (UsuarioBadge ub : lista) {
				// Filtro Java para segurança contra o bug de Boolean no CHAR(1)
				if (Boolean.TRUE.equals(ub.getAtual())) {
					Long idUsuario = ub.getUsuario().getId();
					mapa.computeIfAbsent(idUsuario, k -> new ArrayList<>()).add(ub.getBadge());
				}
			}
		} catch (Exception e) {
			// retorna mapa vazio em caso de erro
		}
		return mapa;
	}

	/** Inativa todos os registros de usuario_badge para um badge específico */
	@Transactional
	public void inativarPorBadge(Long idBadge) {
		em.createQuery("update UsuarioBadge ub set ub.atual = false where ub.badge.id = :idBadge and ub.atual = true")
				.setParameter("idBadge", idBadge)
				.executeUpdate();
		em.flush();
	}

	/** Persiste um novo UsuarioBadge */
	public UsuarioBadge salvar(UsuarioBadge usuarioBadge) {
		return super.persist(usuarioBadge);
	}
}
