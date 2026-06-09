package br.com.bolao.bolao10.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.domain.Classificacao;
import br.com.bolao.bolao10.domain.Selecao;

/**
 * Query SQL NATIVA para carregar Classificação com Seleções em UMA ÚNICA query.
 * Bypassa o Hibernate para evitar N+1 queries (32 classificações × lazy loading de seleção).
 * Inclui cache em memória com TTL de 5 minutos para evitar consultas repetidas ao banco remoto.
 */
@Component
public class ClassificacaoNativeQuery {

	@Autowired
	private EntityManager em;

	/** Cache em memória com TTL de 5 minutos. */
	private volatile List<Classificacao> cachedResult = null;
	private volatile long cacheTimestamp = 0;
	private static final long CACHE_TTL_MS = 5 * 60 * 1000L; // 5 minutos

	/** Invalida o cache (chamar ao atualizar classificação). */
	public void invalidarCache() {
		cachedResult = null;
		cacheTimestamp = 0;
		System.out.println(">>> [NATIVE CLASSIFICACAO] Cache invalidado.");
	}

	/**
	 * Carrega todas as classificações com suas seleções em UMA query SQL nativa.
	 * Mantém a ordenação original: pontos DESC, vitória DESC, saldo DESC, gols pró DESC, nome.
	 * Resultado é cacheado por 5 minutos para evitar consultas repetidas ao banco remoto.
	 */
	public List<Classificacao> carregarClassificacaoComSelecoes() {
		long now = System.currentTimeMillis();
		if (cachedResult != null && (now - cacheTimestamp) < CACHE_TTL_MS) {
			long restanteSec = (CACHE_TTL_MS - (now - cacheTimestamp)) / 1000;
			System.out.println(">>> [NATIVE CLASSIFICACAO] Retornando do cache (TTL restante: " + restanteSec + "s)");
			return cachedResult;
		}
		long inicio = System.currentTimeMillis();
		
		// SQL NATIVO: JOIN entre classificacao e selecao em uma única query
		// NOTA: partidas e aproveitamento são calculados no getter da entidade, não existem no banco
		String sql = 
			"SELECT " +
			"  c.idselecao as c_idselecao, c.pontos, c.vitoria, c.empate, c.derrota, " +
			"  c.golspro, c.golscontra, c.saldogols, " +
			"  c.pontosanterior, c.vitoriaanterior, c.empateanterior, c.derrotaanterior, " +
			"  c.golsproanterior, c.golscontraanterior, c.saldogolsanterior, " +
			"  s.idselecao as s_idselecao, s.nome, s.imagem, s.grupo, s.cor " +
			"FROM classificacao c " +
			"INNER JOIN selecao s ON c.idselecao = s.idselecao " +
			"ORDER BY c.pontos DESC, c.vitoria DESC, c.saldogols DESC, c.golspro DESC, s.nome";

		Query query = em.createNativeQuery(sql);
		
		long t1 = System.currentTimeMillis();
		System.out.println(">>> [NATIVE CLASSIFICACAO] Query preparada em: " + (t1-inicio) + "ms");
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		long t2 = System.currentTimeMillis();
		System.out.println(">>> [NATIVE CLASSIFICACAO] Query executada em: " + (t2-t1) + "ms - Retornou " + resultados.size() + " registros");
		
		// Índices:  0=idselecao, 1=pontos, 2=vitoria, 3=empate, 4=derrota,
		//           5=golspro, 6=golscontra, 7=saldogols,
		//           8=pontosant, 9=vitoriaant, 10=empateant, 11=derrotaant,
		//           12=golsproant, 13=golscontraant, 14=saldogolsant,
		//           15=s.idselecao, 16=s.nome, 17=s.imagem, 18=s.grupo, 19=s.cor
		List<Classificacao> classificacoes = new ArrayList<>();
		for (Object[] row : resultados) {
			// Criar Selecao
			Selecao selecao = new Selecao();
			selecao.setId(((Number) row[15]).longValue());
			selecao.setNome((String) row[16]);
			selecao.setImagem((String) row[17]);
			selecao.setGrupo((String) row[18]);
			if (row[19] != null) {
				selecao.setCor((String) row[19]);
			}
			
			// Criar Classificacao (partidas e aproveitamento são calculados nos getters, não precisam de setter)
			Classificacao classificacao = new Classificacao();
			classificacao.setSelecao(selecao);
			
			if (row[1] != null) classificacao.setPontos(((Number) row[1]).intValue());
			if (row[2] != null) classificacao.setVitoria(((Number) row[2]).intValue());
			if (row[3] != null) classificacao.setEmpate(((Number) row[3]).intValue());
			if (row[4] != null) classificacao.setDerrota(((Number) row[4]).intValue());
			if (row[5] != null) classificacao.setGolspro(((Number) row[5]).intValue());
			if (row[6] != null) classificacao.setGolscontra(((Number) row[6]).intValue());
			if (row[7] != null) classificacao.setSaldogols(((Number) row[7]).intValue());
			
			if (row[8] != null)  classificacao.setPontosAnterior(((Number) row[8]).intValue());
			if (row[9] != null)  classificacao.setVitoriaAnterior(((Number) row[9]).intValue());
			if (row[10] != null) classificacao.setEmpateAnterior(((Number) row[10]).intValue());
			if (row[11] != null) classificacao.setDerrotaAnterior(((Number) row[11]).intValue());
			if (row[12] != null) classificacao.setGolsproAnterior(((Number) row[12]).intValue());
			if (row[13] != null) classificacao.setGolscontraAnterior(((Number) row[13]).intValue());
			if (row[14] != null) classificacao.setSaldogolsAnterior(((Number) row[14]).intValue());
			
			classificacoes.add(classificacao);
		}
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [NATIVE CLASSIFICACAO] Mapeamento levou: " + (fim-t2) + "ms");
		System.out.println(">>> [NATIVE CLASSIFICACAO] TOTAL: " + (fim-inicio) + "ms");

		// Armazena no cache
		cachedResult = classificacoes;
		cacheTimestamp = System.currentTimeMillis();
		System.out.println(">>> [NATIVE CLASSIFICACAO] Resultado cacheado por " + (CACHE_TTL_MS/60000) + " minutos.");
		
		return classificacoes;
	}
}
