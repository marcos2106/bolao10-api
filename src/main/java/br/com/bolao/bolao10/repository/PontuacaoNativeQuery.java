package br.com.bolao.bolao10.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.domain.Aposta;
import br.com.bolao.bolao10.domain.ApostaColocacao;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Selecao;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.NivelUsuarioEnum;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.model.PontuacaoUsuarioPartida;

/**
 * Query SQL NATIVA para carregar pontuação de TODOS os usuários com TODAS as apostas.
 * Bypassa Hibernate para evitar dezenas de milhares de N+1 queries.
 */
@Component
public class PontuacaoNativeQuery {

	@Autowired
	private EntityManager em;
	
	private Boolean toBoolean(Object value) {
		if (value == null) return false;
		if (value instanceof Boolean) return (Boolean) value;
		if (value instanceof Number) return ((Number) value).intValue() != 0;
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
	 * Carrega pontuação completa de todos os usuários com apostas.
	 * OTIMIZADO: 4 queries ao invés de dezenas de milhares!
	 */
	public List<PontuacaoUsuarioPartida> carregarPontuacaoCompleta() {
		long inicio = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE PONTUACAO] Iniciando carregarPontuacaoCompleta...");
		
		// PASSO 1: Carregar todos os usuários ativos com pontuação
		long t1 = System.currentTimeMillis();
		Map<Long, PontuacaoUsuarioPartida> mapaPontuacao = carregarUsuariosComPontuacao();
		long t2 = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE PONTUACAO] Usuários + pontuação: " + (t2-t1) + "ms - " + mapaPontuacao.size() + " usuários");
		
		if (mapaPontuacao.isEmpty()) {
			System.out.println(">>> [PERFORMANCE PONTUACAO] Nenhum usuário ativo encontrado");
			return new ArrayList<>();
		}
		
		// PASSO 2: Carregar TODAS as apostas de TODOS os usuários de uma vez
		long t3 = System.currentTimeMillis();
		carregarTodasApostas(mapaPontuacao);
		long t4 = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE PONTUACAO] Apostas carregadas: " + (t4-t3) + "ms");
		
		// PASSO 3: Carregar TODAS as apostas de colocação de TODOS os usuários
		long t5 = System.currentTimeMillis();
		carregarTodasApostasColocacao(mapaPontuacao);
		long t6 = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE PONTUACAO] Apostas colocação carregadas: " + (t6-t5) + "ms");
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE PONTUACAO] TOTAL: " + (fim-inicio) + "ms");
		
		return new ArrayList<>(mapaPontuacao.values());
	}

	/**
	 * QUERY 1: Carregar usuários ativos com pontuação do ranking.
	 */
	private Map<Long, PontuacaoUsuarioPartida> carregarUsuariosComPontuacao() {
		String sql = 
			"SELECT " +
			"  u.idusuario, u.nome, u.avatar, u.nivel, u.aposta, u.ativo, u.pagamento, u.perfil, u.primeiro, " +
			"  COALESCE(r.pontuacao, 0) as pontuacao " +
			"FROM usuario u " +
			"LEFT JOIN ranking r ON u.idusuario = r.idusuario " +
			"WHERE u.ativo = 1 " +
			"ORDER BY u.nome";

		Query query = em.createNativeQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		Map<Long, PontuacaoUsuarioPartida> mapa = new HashMap<>();
		for (Object[] row : resultados) {
			Usuario usuario = new Usuario();
			usuario.setId(((Number) row[0]).longValue());
			usuario.setNome((String) row[1]);
			usuario.setAvatar((String) row[2]);
			
			String nivelStr = (String) row[3];
			if (nivelStr != null) {
				usuario.setNivel(NivelUsuarioEnum.valueOf(nivelStr));
			}
			
			usuario.setAposta(toBoolean(row[4]));
			usuario.setAtivo(toBoolean(row[5]));
			usuario.setPagamento(toBoolean(row[6]));
			
			String perfilStr = (String) row[7];
			if (perfilStr != null) {
				usuario.setPerfil(UserProfile.valueOf(perfilStr));
			}
			
			usuario.setPrimeiro(toBoolean(row[8]));
			
			PontuacaoUsuarioPartida pup = new PontuacaoUsuarioPartida();
			pup.setUsuario(usuario);
			pup.setPontuacao(((Number) row[9]).longValue());
			pup.setListaApostas(new ArrayList<>());
			
			mapa.put(usuario.getId(), pup);
		}
		
		return mapa;
	}

	/**
	 * QUERY 2: Carregar TODAS as apostas de TODOS os usuários de uma vez.
	 */
	private void carregarTodasApostas(Map<Long, PontuacaoUsuarioPartida> mapaPontuacao) {
		String sql = 
			"SELECT " +
			"  a.idusuario, a.idpartida, a.placar_a, a.placar_b, a.pontuacao, a.pontuacao_provisoria, " +
			"  p.placar_a as p_placar_a, p.placar_b as p_placar_b, p.data_hora, p.fase, p.rodada, " +
			"  p.iniciada, p.finalizada, " +
			"  sa.idselecao as sa_id, sa.nome as sa_nome, sa.imagem as sa_img, sa.grupo as sa_grupo, " +
			"  sb.idselecao as sb_id, sb.nome as sb_nome, sb.imagem as sb_img, sb.grupo as sb_grupo " +
			"FROM aposta a " +
			"INNER JOIN partida p ON a.idpartida = p.idpartida " +
			"INNER JOIN selecao sa ON p.idselecao_a = sa.idselecao " +
			"INNER JOIN selecao sb ON p.idselecao_b = sb.idselecao " +
			"WHERE a.idusuario IN (" + String.join(",", mapaPontuacao.keySet().stream().map(String::valueOf).toArray(String[]::new)) + ") " +
			"ORDER BY a.idusuario, p.data_hora";

		Query query = em.createNativeQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		for (Object[] row : resultados) {
			Long idUsuario = ((Number) row[0]).longValue();
			
			// Criar Selecoes
			Selecao selecaoA = new Selecao();
			selecaoA.setId(((Number) row[13]).longValue());
			selecaoA.setNome((String) row[14]);
			selecaoA.setImagem((String) row[15]);
			selecaoA.setGrupo((String) row[16]);
			
			Selecao selecaoB = new Selecao();
			selecaoB.setId(((Number) row[17]).longValue());
			selecaoB.setNome((String) row[18]);
			selecaoB.setImagem((String) row[19]);
			selecaoB.setGrupo((String) row[20]);
			
			// Criar Partida
			Partida partida = new Partida();
			partida.setId(((Number) row[1]).longValue());
			if (row[6] != null) partida.setPlacarA(((Number) row[6]).intValue());
			if (row[7] != null) partida.setPlacarB(((Number) row[7]).intValue());
			if (row[8] != null) {
				Timestamp ts = (Timestamp) row[8];
				partida.setDataHora(ts.toLocalDateTime());
			}
			if (row[9] != null) partida.setFase(((Number) row[9]).intValue());
			if (row[10] != null) partida.setRodada(((Number) row[10]).intValue());
			partida.setIniciada(toBoolean(row[11]));
			partida.setFinalizada(toBoolean(row[12]));
			partida.setSelecaoA(selecaoA);
			partida.setSelecaoB(selecaoB);
			
			// Criar Aposta
			Aposta aposta = new Aposta();
			aposta.setPlacarA(((Number) row[2]).intValue());
			aposta.setPlacarB(((Number) row[3]).intValue());
			if (row[4] != null) aposta.setPontuacao(((Number) row[4]).intValue());
			if (row[5] != null) aposta.setPontuacaoProvisoria(((Number) row[5]).intValue());
			aposta.setPartida(partida);
			
			// Adicionar aposta ao usuário
			PontuacaoUsuarioPartida pup = mapaPontuacao.get(idUsuario);
			if (pup != null) {
				pup.getListaApostas().add(aposta);
			}
		}
	}

	/**
	 * QUERY 3: Carregar TODAS as apostas de colocação de TODOS os usuários.
	 * NOTA: artilharia é do tipo Selecao (não Jogador) conforme estrutura do banco.
	 */
	private void carregarTodasApostasColocacao(Map<Long, PontuacaoUsuarioPartida> mapaPontuacao) {
		String sql = 
			"SELECT " +
			"  ac.idusuario, " +
			"  ac.pontos_campeao, ac.pontos_vice, ac.pontos_terceiro, ac.pontos_quarto, ac.pontos_artilharia, " +
			"  s_cam.idselecao as cam_id, s_cam.nome as cam_nome, s_cam.imagem as cam_img, s_cam.grupo as cam_grupo, " +
			"  s_vic.idselecao as vic_id, s_vic.nome as vic_nome, s_vic.imagem as vic_img, s_vic.grupo as vic_grupo, " +
			"  s_ter.idselecao as ter_id, s_ter.nome as ter_nome, s_ter.imagem as ter_img, s_ter.grupo as ter_grupo, " +
			"  s_qua.idselecao as qua_id, s_qua.nome as qua_nome, s_qua.imagem as qua_img, s_qua.grupo as qua_grupo, " +
			"  s_art.idselecao as art_id, s_art.nome as art_nome, s_art.imagem as art_img, s_art.grupo as art_grupo " +
			"FROM aposta_colocacao ac " +
			"LEFT JOIN selecao s_cam ON ac.campeao_id = s_cam.idselecao " +
			"LEFT JOIN selecao s_vic ON ac.vice_id = s_vic.idselecao " +
			"LEFT JOIN selecao s_ter ON ac.terceiro_id = s_ter.idselecao " +
			"LEFT JOIN selecao s_qua ON ac.quarto_id = s_qua.idselecao " +
			"LEFT JOIN selecao s_art ON ac.artilharia_id = s_art.idselecao " +
			"WHERE ac.idusuario IN (" + String.join(",", mapaPontuacao.keySet().stream().map(String::valueOf).toArray(String[]::new)) + ")";

		Query query = em.createNativeQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		for (Object[] row : resultados) {
			Long idUsuario = ((Number) row[0]).longValue();
			
			ApostaColocacao ac = new ApostaColocacao();
			
			if (row[1] != null) ac.setPontosCampeao(((Number) row[1]).intValue());
			if (row[2] != null) ac.setPontosVice(((Number) row[2]).intValue());
			if (row[3] != null) ac.setPontosTerceiro(((Number) row[3]).intValue());
			if (row[4] != null) ac.setPontosQuarto(((Number) row[4]).intValue());
			if (row[5] != null) ac.setPontosArtilharia(((Number) row[5]).intValue());
			
			// Campeão
			if (row[6] != null) {
				Selecao campeao = new Selecao();
				campeao.setId(((Number) row[6]).longValue());
				campeao.setNome((String) row[7]);
				campeao.setImagem((String) row[8]);
				campeao.setGrupo((String) row[9]);
				ac.setCampeao(campeao);
			}
			
			// Vice
			if (row[10] != null) {
				Selecao vice = new Selecao();
				vice.setId(((Number) row[10]).longValue());
				vice.setNome((String) row[11]);
				vice.setImagem((String) row[12]);
				vice.setGrupo((String) row[13]);
				ac.setVice(vice);
			}
			
			// Terceiro
			if (row[14] != null) {
				Selecao terceiro = new Selecao();
				terceiro.setId(((Number) row[14]).longValue());
				terceiro.setNome((String) row[15]);
				terceiro.setImagem((String) row[16]);
				terceiro.setGrupo((String) row[17]);
				ac.setTerceiro(terceiro);
			}
			
			// Quarto
			if (row[18] != null) {
				Selecao quarto = new Selecao();
				quarto.setId(((Number) row[18]).longValue());
				quarto.setNome((String) row[19]);
				quarto.setImagem((String) row[20]);
				quarto.setGrupo((String) row[21]);
				ac.setQuarto(quarto);
			}
			
			// Artilharia (é Selecao conforme estrutura do banco)
			if (row[22] != null) {
				Selecao artilharia = new Selecao();
				artilharia.setId(((Number) row[22]).longValue());
				artilharia.setNome((String) row[23]);
				artilharia.setImagem((String) row[24]);
				artilharia.setGrupo((String) row[25]);
				ac.setArtilharia(artilharia);
			}
			
			// Adicionar aposta colocação ao usuário
			PontuacaoUsuarioPartida pup = mapaPontuacao.get(idUsuario);
			if (pup != null) {
				pup.setApostaColocacao(ac);
			}
		}
	}
}
