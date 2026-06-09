package br.com.bolao.bolao10.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.NivelUsuarioEnum;
import br.com.bolao.bolao10.domain.enums.UserProfile;

/**
 * Query SQL NATIVA para carregar Ranking + Usuario em UMA ÚNICA query.
 * Bypassa o Hibernate para garantir performance máxima.
 */
@Component
public class RankingNativeQuery {

	@Autowired
	private EntityManager em;

	/**
	 * Carrega ranking com usuários em UMA query SQL nativa.
	 * Não usa JPA/Hibernate para evitar N+1 queries.
	 */
	public List<Ranking> carregarRankingComUsuarios() {
		long inicio = System.currentTimeMillis();
		
		// SQL NATIVO: JOIN entre ranking e usuario em uma única query
		String sql = 
			"SELECT " +
			"  r.idusuario, r.pontuacao, r.pontuacao_provisoria, r.posicaoanterior, " +
			"  u.nome, u.avatar, u.nivel, u.aposta, u.ativo, u.pagamento, u.perfil, u.primeiro " +
			"FROM ranking r " +
			"INNER JOIN usuario u ON r.idusuario = u.idusuario " +
			"ORDER BY r.pontuacao DESC, u.nome";

		Query query = em.createNativeQuery(sql);
		
		long t1 = System.currentTimeMillis();
		System.out.println(">>> [NATIVE] Query preparada em: " + (t1-inicio) + "ms");
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		long t2 = System.currentTimeMillis();
		System.out.println(">>> [NATIVE] Query executada em: " + (t2-t1) + "ms - Retornou " + resultados.size() + " registros");
		
		// Mapear manualmente para objetos Ranking + Usuario
		List<Ranking> rankings = new ArrayList<>();
		for (Object[] row : resultados) {
			// Criar Usuario
			Usuario usuario = new Usuario();
			usuario.setId(((Number) row[0]).longValue());
			usuario.setNome((String) row[4]);
			usuario.setAvatar((String) row[5]);
			
			String nivelStr = (String) row[6];
			if (nivelStr != null) {
				usuario.setNivel(NivelUsuarioEnum.valueOf(nivelStr));
			}
			
			usuario.setAposta((Boolean) row[7]);
			usuario.setAtivo((Boolean) row[8]);
			usuario.setPagamento((Boolean) row[9]);
			
			// Converter String do banco para enum UserProfile
			String perfilStr = (String) row[10];
			if (perfilStr != null) {
				usuario.setPerfil(UserProfile.valueOf(perfilStr));
			}
			
			usuario.setPrimeiro((Boolean) row[11]);
			
			// Criar Ranking
			Ranking ranking = new Ranking();
			ranking.setUsuario(usuario);
			ranking.setPontuacao((Integer) row[1]);
			ranking.setPontuacaoProvisoria((Integer) row[2]);
			
			Object posicaoAnt = row[3];
			if (posicaoAnt != null) {
				ranking.setPosicaoAnterior(((Number) posicaoAnt).intValue());
			}
			
			rankings.add(ranking);
		}
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [NATIVE] Mapeamento levou: " + (fim-t2) + "ms");
		System.out.println(">>> [NATIVE] TOTAL: " + (fim-inicio) + "ms");
		
		return rankings;
	}
}
