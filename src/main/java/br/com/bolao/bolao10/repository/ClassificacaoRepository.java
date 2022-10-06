
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Classificacao;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Selecao;

@Repository
public class ClassificacaoRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public Classificacao save(Classificacao classificacao) {
		if (classificacao.getSelecao() == null) {
			super.persist(classificacao);
		}
		else {
			super.update(classificacao);
		}
		return classificacao;
	}

	public Classificacao findBySelecao(Long idSelecao) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select c from Classificacao c	");
		sql.append(" where c.selecao.id = :idSelecao	");

		TypedQuery<Classificacao> query = em.createQuery(sql.toString(), Classificacao.class);
		query.setParameter("idSelecao", idSelecao);
		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Classificacao> carregarClassificacao() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select c from Classificacao c	");
		sql.append(" order by c.pontos desc, c.vitoria desc, c.saldogols desc, c.golspro desc, c.selecao.nome	");

		TypedQuery<Classificacao> query = em.createQuery(sql.toString(), Classificacao.class);
		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public void zerarAnteriorClassificacao(Selecao selecao) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE classificacao SET           ");
		sql.append(" pontos = pontosanterior,         	");
		sql.append(" vitoria = vitoriaanterior,         ");
		sql.append(" empate = empateanterior,         	");
		sql.append(" derrota = derrotaanterior,         ");
		sql.append(" golspro = golsproanterior,         ");
		sql.append(" golscontra = golscontraanterior,   ");
		sql.append(" saldogols = saldogolsanterior      ");
		sql.append(" WHERE idselecao = :idSelecao       ");

		em.createNativeQuery(sql.toString()).setParameter("idSelecao", selecao.getId()).executeUpdate();
	}
	
	public void atualizarAnteriorClassificacao(Selecao selecao) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE classificacao SET           ");
		sql.append(" pontosanterior = pontos,         	");
		sql.append(" vitoriaanterior = vitoria,         ");
		sql.append(" empateanterior = empate,         	");
		sql.append(" derrotaanterior = derrota,         ");
		sql.append(" golsproanterior = golspro,         ");
		sql.append(" golscontraanterior = golscontra,   ");
		sql.append(" saldogolsanterior = saldogols		");
		sql.append(" WHERE idselecao = :idSelecao       ");
		
		em.createNativeQuery(sql.toString()).setParameter("idSelecao", selecao.getId()).executeUpdate();
	}

}
