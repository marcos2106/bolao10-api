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
 */
@Component
public class ClassificacaoNativeQuery {

	@Autowired
	private EntityManager em;

	/**
	 * Carrega todas as classificações com suas seleções em UMA query SQL nativa.
	 * Mantém a ordenação original: pontos DESC, vitória DESC, saldo DESC, gols pró DESC, nome.
	 */
	public List<Classificacao> carregarClassificacaoComSelecoes() {
		long inicio = System.currentTimeMillis();
		
		// SQL NATIVO: JOIN entre classificacao e selecao em uma única query
		String sql = 
			"SELECT " +
			"  c.idselecao, c.pontos, c.partidas, c.vitoria, c.empate, c.derrota, " +
			"  c.golspro, c.golscontra, c.saldogols, " +
			"  c.pontosanterior, c.vitoriaanterior, c.empateanterior, c.derrotaanterior, " +
			"  c.golsproanterior, c.golscontraanterior, c.saldogolsanterior, " +
			"  s.idselecao, s.nome, s.imagem, s.grupo, s.cor " +
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
		
		// Mapear manualmente para objetos Classificacao + Selecao
		List<Classificacao> classificacoes = new ArrayList<>();
		for (Object[] row : resultados) {
			// Criar Selecao
			Selecao selecao = new Selecao();
			selecao.setId(((Number) row[17]).longValue());
			selecao.setNome((String) row[18]);
			selecao.setImagem((String) row[19]);
			selecao.setGrupo((String) row[20]);
			if (row[21] != null) {
				selecao.setCor((String) row[21]);
			}
			
			// Criar Classificacao (setters usam camelCase com "Anterior")
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
			if (row[9] != null) classificacao.setVitoriaAnterior(((Number) row[9]).intValue());
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
		
		return classificacoes;
	}
}
