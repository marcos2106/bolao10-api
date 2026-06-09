package br.com.bolao.bolao10.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Selecao;

/**
 * Query SQL NATIVA para carregar Partidas com Seleções em UMA ÚNICA query.
 * Bypassa o Hibernate para evitar N+1 queries (104 partidas × 2 seleções = 208 queries extras!).
 */
@Component
public class PartidasNativeQuery {

	@Autowired
	private EntityManager em;
	
	/**
	 * Converte valor do banco para Boolean de forma segura.
	 */
	private Boolean toBoolean(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		}
		if (value instanceof Character) {
			char c = (Character) value;
			return c == '1' || c == 'Y' || c == 'y' || c == 'T' || c == 't';
		}
		if (value instanceof String) {
			String s = (String) value;
			return "1".equals(s) || "Y".equalsIgnoreCase(s) || "T".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s);
		}
		return false;
	}

	/**
	 * Carrega todas as partidas com suas seleções em UMA query SQL nativa.
	 * Não usa JPA/Hibernate para evitar N+1 queries.
	 */
	public List<Partida> carregarPartidasComSelecoes() {
		long inicio = System.currentTimeMillis();
		
		// SQL NATIVO: JOIN entre partida e ambas as seleções em uma única query
		String sql = 
			"SELECT " +
			"  p.idpartida, p.placarA, p.placarB, p.data_hora, p.fase, p.rodada, " +
			"  p.iniciada, p.finalizada, " +
			"  sa.idselecao as idselecao_a, sa.nome as nome_a, sa.imagem as imagem_a, sa.grupo as grupo_a, " +
			"  sb.idselecao as idselecao_b, sb.nome as nome_b, sb.imagem as imagem_b, sb.grupo as grupo_b " +
			"FROM partida p " +
			"INNER JOIN selecao sa ON p.idselecaoA = sa.idselecao " +
			"INNER JOIN selecao sb ON p.idselecaoB = sb.idselecao " +
			"ORDER BY p.fase, p.rodada, p.data_hora, p.idpartida";

		Query query = em.createNativeQuery(sql);
		
		long t1 = System.currentTimeMillis();
		System.out.println(">>> [NATIVE PARTIDAS] Query preparada em: " + (t1-inicio) + "ms");
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		long t2 = System.currentTimeMillis();
		System.out.println(">>> [NATIVE PARTIDAS] Query executada em: " + (t2-t1) + "ms - Retornou " + resultados.size() + " registros");
		
		// Mapear manualmente para objetos Partida + Selecoes
		List<Partida> partidas = new ArrayList<>();
		for (Object[] row : resultados) {
			// Criar Selecao A
			Selecao selecaoA = new Selecao();
			selecaoA.setId(((Number) row[8]).longValue());
			selecaoA.setNome((String) row[9]);
			selecaoA.setImagem((String) row[10]);
			selecaoA.setGrupo((String) row[11]);
			
			// Criar Selecao B
			Selecao selecaoB = new Selecao();
			selecaoB.setId(((Number) row[12]).longValue());
			selecaoB.setNome((String) row[13]);
			selecaoB.setImagem((String) row[14]);
			selecaoB.setGrupo((String) row[15]);
			
			// Criar Partida
			Partida partida = new Partida();
			partida.setId(((Number) row[0]).longValue());
			
			if (row[1] != null) {
				partida.setPlacarA(((Number) row[1]).intValue());
			}
			if (row[2] != null) {
				partida.setPlacarB(((Number) row[2]).intValue());
			}
			
			// Converter Timestamp para LocalDateTime
			if (row[3] != null) {
				Timestamp ts = (Timestamp) row[3];
				partida.setDataHora(ts.toLocalDateTime());
			}
			
			// Converter String para int fase
			if (row[4] != null) {
				partida.setFase(((Number) row[4]).intValue());
			}
			
			if (row[5] != null) {
				partida.setRodada(((Number) row[5]).intValue());
			}
			
			partida.setIniciada(toBoolean(row[6]));
			partida.setFinalizada(toBoolean(row[7]));
			
			partida.setSelecaoA(selecaoA);
			partida.setSelecaoB(selecaoB);
			
			partidas.add(partida);
		}
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [NATIVE PARTIDAS] Mapeamento levou: " + (fim-t2) + "ms");
		System.out.println(">>> [NATIVE PARTIDAS] TOTAL: " + (fim-inicio) + "ms");
		
		return partidas;
	}
}
