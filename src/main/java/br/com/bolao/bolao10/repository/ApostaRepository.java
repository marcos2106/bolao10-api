
package br.com.bolao.bolao10.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Aposta;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Selecao;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.NivelUsuarioEnum;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.model.ApostaPartida;
import br.com.bolao.bolao10.support.Constants;

@Repository
public class ApostaRepository extends GenericRepository {

	private int toInt(Object value) {
		if (value == null) {
			return 0;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		try {
			return Integer.parseInt(value.toString());
		} catch (Exception e) {
			return 0;
		}
	}

	private Integer toInteger(Object value) {
		return value == null ? null : Integer.valueOf(((Number) value).intValue());
	}

	private Long toLong(Object value) {
		return value == null ? null : Long.valueOf(((Number) value).longValue());
	}

	private Boolean toBoolean(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue() != 0;
		}
		if (value instanceof byte[]) {
			byte[] bytes = (byte[]) value;
			return bytes.length > 0 && bytes[0] != 0;
		}
		return "1".equals(value.toString()) || Boolean.valueOf(value.toString());
	}

	private LocalDateTime toLocalDateTime(Object value) {
		return value == null ? null : ((Timestamp) value).toLocalDateTime();
	}

	@Autowired
	private EntityManager em;

	@Autowired
	private PartidaRepository partidaRepository;

	@Autowired
	private UserRepository usuarioRepository;

	public Aposta save(Aposta aposta) {
		return super.persist(aposta);
	}

	public Aposta findById(Long idPartida, Long idUsuario) {

		Aposta apostaRetorno = null;

		StringBuilder sql = new StringBuilder();
		sql.append(" select a from Aposta a	 			");
		sql.append(" where a.partida.id = :idPartida	");
		sql.append(" and a.usuario.id = :idUsuario		");

		TypedQuery<Aposta> query = em.createQuery(sql.toString(), Aposta.class);
		query.setParameter("idPartida", idPartida);
		query.setParameter("idUsuario", idUsuario);

		try {
			apostaRetorno = query.getSingleResult();
		}
		catch (Exception e) {}

		// se achar, retorna o objeto
		if (apostaRetorno != null) {
			return apostaRetorno;

		} else { // se não achar cria um novo registro
			Aposta aposta = new Aposta();
			aposta.setPartida(partidaRepository.findById(idPartida));
			aposta.setUsuario(usuarioRepository.findById(idUsuario));
			aposta.setPlacarA(null);
			aposta.setPlacarB(null);
			return aposta;
		}
	}

	public ApostaPartida calcularApostasPorPartida(Long idPartida) {

		ApostaPartida ap = new ApostaPartida();
		StringBuilder sql = new StringBuilder();
		sql.append(" select                   ");
		sql.append("   sum(case when a.placarA > a.placarB then 1 else 0 end) as numA, ");
		sql.append("   sum(case when a.placarA = a.placarB then 1 else 0 end) as numE, ");
		sql.append("   sum(case when a.placarA < a.placarB then 1 else 0 end) as numB ");
		sql.append(" from aposta a                  ");
		sql.append(" where a.idpartida = :idPartida            ");

		try {
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter("idPartida", idPartida);
			Object[] result = (Object[]) query.getSingleResult();

			ap.setNumSelecaoA(toInt(result[0]));
			ap.setNumEmpate(toInt(result[1]));
			ap.setNumSelecaoB(toInt(result[2]));
		}
		catch (Exception e) {
			ap.setNumSelecaoA(0);
			ap.setNumEmpate(0);
			ap.setNumSelecaoB(0);
		}
		return ap;
	}

	public List<Aposta> carregarApostaPorPartida(Long idPartida) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select a from Aposta a                ");
		sql.append(" join fetch a.usuario                ");
		sql.append(" join fetch a.partida p              ");
		sql.append(" join fetch p.selecaoA              ");
		sql.append(" join fetch p.selecaoB              ");
		sql.append(" where p.id = :idPartida              ");
		sql.append(" order by                  ");
		sql.append("   case when p.iniciada = true and p.finalizada = true then a.pontuacao end desc, ");
		sql.append("   case when p.iniciada = true and p.finalizada = false then a.pontuacaoProvisoria end desc, ");
		sql.append("   case when p.iniciada = false then a.placarA end asc, ");
		sql.append("   case when p.iniciada = false then a.placarB end asc   ");

		TypedQuery<Aposta> query = em.createQuery(sql.toString(), Aposta.class);
		query.setParameter("idPartida", idPartida);

		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return new java.util.ArrayList<>();
		}
	}

	public List<Aposta> carregarApostaPorUsuario(Long idUsuario) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select ");
		sql.append("   a.idpartida as a_idpartida, a.idusuario as a_idusuario, ");
		sql.append("   a.placarA as a_placarA, a.placarB as a_placarB, ");
		sql.append("   a.pontuacao as a_pontuacao, ");
		sql.append("   a.pontuacao_provisoria as a_pontuacao_provisoria, ");
		sql.append("   p.placarA as p_placarA, p.placarB as p_placarB, ");
		sql.append("   p.iniciada as p_iniciada, p.finalizada as p_finalizada, ");
		sql.append("   p.datahora as p_datahora, p.fase as p_fase, ");
		sql.append("   p.rodada as p_rodada, p.local as p_local, ");
		sql.append("   sa.idselecao as sa_idselecao, sa.nome as sa_nome, ");
		sql.append("   sa.imagem as sa_imagem, sa.ativo as sa_ativo, ");
		sql.append("   sa.grupo as sa_grupo, sa.cor as sa_cor, ");
		sql.append("   sb.idselecao as sb_idselecao, sb.nome as sb_nome, ");
		sql.append("   sb.imagem as sb_imagem, sb.ativo as sb_ativo, ");
		sql.append("   sb.grupo as sb_grupo, sb.cor as sb_cor, ");
		sql.append("   u.idusuario as u_idusuario, u.nome as u_nome, ");
		sql.append("   u.cidade as u_cidade, u.telefone as u_telefone, ");
		sql.append("   u.email as u_email, u.senha as u_senha, ");
		sql.append("   u.perfil as u_perfil, u.ativo as u_ativo, ");
		sql.append("   u.aposta as u_aposta, u.pagamento as u_pagamento, ");
		sql.append("   u.primeiro as u_primeiro, u.nivel as u_nivel, ");
		sql.append("   u.avatar as u_avatar, u.datahoraaposta as u_datahoraaposta, ");
		sql.append("   u.datahorapgto as u_datahorapgto ");
		sql.append(" from aposta a ");
		sql.append(" join partida p on p.idpartida = a.idpartida ");
		sql.append(" join selecao sa on sa.idselecao = p.idselecaoA ");
		sql.append(" join selecao sb on sb.idselecao = p.idselecaoB ");
		sql.append(" join usuario u on u.idusuario = a.idusuario ");
		sql.append(" where a.idusuario = :idUsuario ");
		sql.append(" order by p.datahora ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("idUsuario", idUsuario);

		try {
			List<Object[]> resultados = query.getResultList();
			List<Aposta> apostas = new ArrayList<>(resultados.size());
			Map<Long, Selecao> selecoes = new HashMap<>();
			Usuario usuario = null;

			for (Object[] resultado : resultados) {
				if (usuario == null) {
					usuario = mapearUsuario(resultado);
				}

				Partida partida = new Partida();
				partida.setId(toLong(resultado[0]));
				partida.setPlacarA(toInteger(resultado[6]));
				partida.setPlacarB(toInteger(resultado[7]));
				partida.setIniciada(toBoolean(resultado[8]));
				partida.setFinalizada(toBoolean(resultado[9]));
				partida.setDataHora(toLocalDateTime(resultado[10]));
				partida.setFase(toInt(resultado[11]));
				partida.setRodada(toInteger(resultado[12]));
				partida.setLocal((String) resultado[13]);
				partida.setSelecaoA(mapearSelecao(resultado, 14, selecoes));
				partida.setSelecaoB(mapearSelecao(resultado, 20, selecoes));

				Aposta aposta = new Aposta();
				aposta.setPartida(partida);
				aposta.setUsuario(usuario);
				aposta.setPlacarA(toInteger(resultado[2]));
				aposta.setPlacarB(toInteger(resultado[3]));
				aposta.setPontuacao(toInteger(resultado[4]));
				aposta.setPontuacaoProvisoria(toInteger(resultado[5]));
				apostas.add(aposta);
			}

			return apostas;
		}
		catch (Exception e) {
			return null;
		}
	}

	private Selecao mapearSelecao(Object[] resultado, int inicio, Map<Long, Selecao> selecoes) {
		Long idSelecao = toLong(resultado[inicio]);
		Selecao selecao = selecoes.get(idSelecao);
		if (selecao != null) {
			return selecao;
		}

		selecao = new Selecao();
		selecao.setId(idSelecao);
		selecao.setNome((String) resultado[inicio + 1]);
		selecao.setImagem((String) resultado[inicio + 2]);
		selecao.setAtivo(toBoolean(resultado[inicio + 3]));
		selecao.setGrupo((String) resultado[inicio + 4]);
		selecao.setCor((String) resultado[inicio + 5]);
		selecoes.put(idSelecao, selecao);
		return selecao;
	}

	private Usuario mapearUsuario(Object[] resultado) {
		Usuario usuario = new Usuario();
		usuario.setId(toLong(resultado[26]));
		usuario.setNome((String) resultado[27]);
		usuario.setCidade((String) resultado[28]);
		usuario.setTelefone((String) resultado[29]);
		usuario.setEmail((String) resultado[30]);
		usuario.setSenha((String) resultado[31]);
		usuario.setPerfil(UserProfile.valueOf((String) resultado[32]));
		usuario.setAtivo(toBoolean(resultado[33]));
		usuario.setAposta(toBoolean(resultado[34]));
		usuario.setPagamento(toBoolean(resultado[35]));
		usuario.setPrimeiro(toBoolean(resultado[36]));
		usuario.setNivel(resultado[37] == null
				? null
				: NivelUsuarioEnum.valueOf((String) resultado[37]));
		usuario.setAvatar((String) resultado[38]);
		usuario.setDataHoraAposta(toLocalDateTime(resultado[39]));
		usuario.setDataHoraPgto(toLocalDateTime(resultado[40]));
		return usuario;
	}

	public Long obterPlacarExatoPorUsuario(Long idUsuario) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select count(a) as total from Aposta a	");
		sql.append(" where a.usuario.id = :idUsuario		");
		sql.append(" and a.pontuacao = :pontuacao			");

		TypedQuery<Long> query = em.createQuery(sql.toString(), Long.class);
		query.setParameter("idUsuario", idUsuario);
		query.setParameter("pontuacao", Constants.APOSTA_CORRETA);

		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
			return 0L;
		}
	}

	public Integer calcularPontuacaoProvisoria(Usuario usuario) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select sum(a.pontuacao_provisoria) as somaPontos ");
		sql.append(" from aposta a where a.idusuario = :idUsuario ");
		sql.append(" and a.pontuacao_provisoria is not NULL ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("idUsuario", usuario.getId());

		BigDecimal pontuacaoBD = (BigDecimal) query.getSingleResult();

		return (pontuacaoBD == null) ? 0 : pontuacaoBD.intValue();		
	}

	/** Retorna o ID do usuário com mais placares exatos (pontuacao = 5) — Badge Beteiro */
	public Long carregarIdUsuarioMaisPlacarExato() {
		try {
			String sql = "select a.usuario.id from Aposta a where a.pontuacao = 5 "
					+ "group by a.usuario.id order by count(a) desc";
			List<Long> result = em.createQuery(sql, Long.class).setMaxResults(1).getResultList();
			return result.isEmpty() ? null : result.get(0);
		} catch (Exception e) { return null; }
	}

	/** Retorna o ID do usuário com mais placares zerados (pontuacao = 0) — Badge Gato Preto */
	public Long carregarIdUsuarioMaisZerou() {
		try {
			String sql = "select a.usuario.id from Aposta a where a.pontuacao = 0 "
					+ "group by a.usuario.id order by count(a) desc";
			List<Long> result = em.createQuery(sql, Long.class).setMaxResults(1).getResultList();
			return result.isEmpty() ? null : result.get(0);
		} catch (Exception e) { return null; }
	}

	/** Retorna o ID do usuário com mais empates acertados — Badge Meia Boca */
	public Long carregarIdUsuarioMaisEmpate() {
		try {
			// Empate acertado: apostou empate E a partida empatou (placarA = placarB e pontuacao > 0)
			String sql = "select a.usuario.id from Aposta a "
					+ "where a.placarA = a.placarB and a.pontuacao is not null and a.pontuacao > 0 "
					+ "group by a.usuario.id order by count(a) desc";
			List<Long> result = em.createQuery(sql, Long.class).setMaxResults(1).getResultList();
			return result.isEmpty() ? null : result.get(0);
		} catch (Exception e) { return null; }
	}

	/**
	 * Retorna IDs dos usuários que zeraram pontos na ÚLTIMA partida calculada — Badge Empacado.
	 * Considera a última partida que teve pontuação calculada (pontuacao not null).
	 */
	public java.util.List<Long> carregarIdsUsuariosEmpacados() {
		try {
			// Encontra o ID da última partida que teve pontuação calculada
			String sqlUltimaPartida = "select a.partida.id from Aposta a "
					+ "where a.pontuacao is not null "
					+ "order by a.partida.dataHora desc";
			List<Long> idsUltimaPartida = em.createQuery(sqlUltimaPartida, Long.class).setMaxResults(1).getResultList();
			if (idsUltimaPartida.isEmpty()) return new java.util.ArrayList<>();
			Long idUltimaPartida = idsUltimaPartida.get(0);

			// Busca usuários que zeraram nessa partida
			String sql = "select a.usuario.id from Aposta a "
					+ "where a.partida.id = :idPartida and a.pontuacao = 0";
			return em.createQuery(sql, Long.class)
					.setParameter("idPartida", idUltimaPartida)
					.getResultList();
		} catch (Exception e) { return new java.util.ArrayList<>(); }
	}

}
