
package br.com.bolao.bolao10.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Classificacao;
import br.com.bolao.bolao10.domain.Colocacao;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.domain.Badge;
import br.com.bolao.bolao10.model.ApostaPartida;
import br.com.bolao.bolao10.model.ClassificacaoGrupo;
import br.com.bolao.bolao10.model.HomeAntesDadosIniciais;
import br.com.bolao.bolao10.model.HomeDepoisCuriosidade;
import br.com.bolao.bolao10.model.HomeDuranteProximasPartidas;
import br.com.bolao.bolao10.model.RankingComBadges;
import br.com.bolao.bolao10.model.UltimosUsuarios;
import br.com.bolao.bolao10.model.UsuarioDTO;
import br.com.bolao.bolao10.repository.ApostaRepository;
import br.com.bolao.bolao10.repository.ClassificacaoRepository;
import br.com.bolao.bolao10.repository.ClassificacaoNativeQuery;
import br.com.bolao.bolao10.repository.ColocacaoRepository;
import br.com.bolao.bolao10.repository.PartidaRepository;
import br.com.bolao.bolao10.repository.PartidasNativeQuery;
import br.com.bolao.bolao10.repository.RankingRepository;
import br.com.bolao.bolao10.repository.RankingNativeQuery;
import br.com.bolao.bolao10.repository.UserRepository;
import br.com.bolao.bolao10.support.Constants;
import br.com.bolao.bolao10.support.NumberUtils;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class HomeService {

	@Autowired
	private PartidaRepository partidaRepository;

	@Autowired
	private UserRepository usuarioRepository;

	@Autowired
	private ApostaRepository apostaRepository;
	
	@Autowired
	private RankingRepository rankingRepository;
	
	@Autowired
	private RankingNativeQuery rankingNativeQuery;
	
	@Autowired
	private PartidasNativeQuery partidasNativeQuery;
	
	@Autowired
	private ClassificacaoNativeQuery classificacaoNativeQuery;
	
	@Autowired
	private BadgeService badgeService;
	
	@Autowired
	private ClassificacaoRepository classificacaoRepository;
	
	@Autowired
	private ColocacaoRepository colocacaoRepository;


	public Partida carregarEstreia() {

		return partidaRepository.carregarPartidaEstreia();
	}

	public HomeAntesDadosIniciais carregarDadosInciais() {

		HomeAntesDadosIniciais dados = new HomeAntesDadosIniciais();

		// Desconta 1 usuário de IA (não entra no cálculo do prêmio nem na contagem de jogadores)
		Integer qntdParticipantes = usuarioRepository.carregarParticipantes().size() - 1;

		BigDecimal valorTotal = new BigDecimal(qntdParticipantes).multiply(Constants.VALOR_APOSTA);
		
		// tirar 5%
		BigDecimal taxasAdmin = valorTotal.multiply(Constants.PORC_ADMIN).divide(new BigDecimal(100));
		valorTotal = valorTotal.subtract(taxasAdmin);
		
		dados.setValorTotal(NumberUtils.formatMoney(valorTotal));

		BigDecimal valor1 = valorTotal.multiply(Constants.PORC_PRIMEIRO).divide(new BigDecimal(100));
		BigDecimal valor2 = valorTotal.multiply(Constants.PORC_SEGUNDO).divide(new BigDecimal(100));
		BigDecimal valor3 = valorTotal.multiply(Constants.PORC_TERCEIRO).divide(new BigDecimal(100));
		BigDecimal valor4 = valorTotal.multiply(Constants.PORC_QUARTO).divide(new BigDecimal(100));
		BigDecimal valor5 = valorTotal.multiply(Constants.PORC_QUINTO).divide(new BigDecimal(100));
		BigDecimal valor6 = valorTotal.multiply(Constants.PORC_SEXTO).divide(new BigDecimal(100));
		dados.setValor1(NumberUtils.formatMoney(valor1));
		dados.setValor2(NumberUtils.formatMoney(valor2));
		dados.setValor3(NumberUtils.formatMoney(valor3));
		dados.setValor4(NumberUtils.formatMoney(valor4));
		dados.setValor5(NumberUtils.formatMoney(valor5));
		dados.setValor6(NumberUtils.formatMoney(valor6));

		dados.setQntdJogadores(qntdParticipantes);

		return dados;
	}

	public UltimosUsuarios carregarParticipantes() {

		UltimosUsuarios uu = new UltimosUsuarios();

		uu.setListaApostadores(usuarioRepository.carregarApostadores());
		uu.setListaFaltam(usuarioRepository.carregarFaltamApostar());
		uu.setListaParticipantes(usuarioRepository.carregarParticipantesAtivosHome());

		return uu;
	}

	public HomeDuranteProximasPartidas carregarPartidas() {

		HomeDuranteProximasPartidas pp = new HomeDuranteProximasPartidas();

		List<Partida> listaPartidas = partidaRepository.carregarProximasPartidas();

		for (Partida partida : listaPartidas) {
			ApostaPartida aposta = apostaRepository.calcularApostasPorPartida(partida.getId());
			partida.setAposta(aposta);
		}
		if (listaPartidas != null && listaPartidas.size() > 0) {
			pp.setPartida1(listaPartidas.get(0));
			if (listaPartidas.size() > 1) {
				pp.setPartida2(listaPartidas.get(1));
			}
			if (listaPartidas.size() > 2) {
				pp.setPartida3(listaPartidas.get(2));
			}
		}
		return pp;
	}
	
	public List<Partida> carregarPartidasAnteriores() {
		
		List<Partida> listaPartida = partidaRepository.carregarPartidasAnteriores();
		Collections.sort(listaPartida, Comparator.comparing(Partida::getDataHora));
		return listaPartida;
	}

	/**
	 * Carrega ranking simples (pode estar lento se não otimizado).
	 * Considere usar carregarRankingCompleto() para melhor performance.
	 */
	public List<Ranking> carregarRanking() {
		return rankingRepository.carregarRanking();
	}

	/**
	 * Carrega ranking JÁ com badges usando SQL NATIVO (bypassa Hibernate para evitar N+1).
	 */
	@Transactional(readOnly = true)
	public List<RankingComBadges> carregarRankingCompleto() {
		long inicio = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE] Iniciando carregarRankingCompleto...");
		
		// USAR QUERY NATIVA: Uma única query SQL que traz ranking + usuario
		long t1 = System.currentTimeMillis();
		List<Ranking> ranking = rankingNativeQuery.carregarRankingComUsuarios();
		long t2 = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE] carregarRankingComUsuarios() levou: " + (t2-t1) + "ms - " + ranking.size() + " registros");
		
		// Coletar IDs dos usuários
		List<Long> idsUsuarios = ranking.stream()
				.map(r -> r.getUsuario().getId())
				.collect(Collectors.toList());
		
		long t3 = System.currentTimeMillis();
		// Buscar badges SOMENTE desses usuários
		Map<Long, List<Badge>> badgesMap = badgeService.carregarMapaBadgesDeUsuarios(idsUsuarios);
		long t4 = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE] carregarMapaBadgesDeUsuarios() levou: " + (t4-t3) + "ms");
		
		// Montar resultado com DTOs
		long t5 = System.currentTimeMillis();
		List<RankingComBadges> resultado = new ArrayList<>();
		for (Ranking r : ranking) {
			Long idUsuario = r.getUsuario().getId();
			List<Badge> badges = badgesMap.getOrDefault(idUsuario, new ArrayList<>());
			
			// Converter Usuario para DTO
			UsuarioDTO usuarioDTO = new UsuarioDTO(r.getUsuario());
			
			RankingComBadges item = new RankingComBadges(
				usuarioDTO,
				r.getPontuacao(),
				r.getPontuacaoProvisoria(),
				r.getPosicaoAnterior(),
				badges
			);
			resultado.add(item);
		}
		long t6 = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE] Montar DTOs levou: " + (t6-t5) + "ms");
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE] TOTAL carregarRankingCompleto(): " + (fim-inicio) + "ms");
		
		return resultado;
	}

	public List<ClassificacaoGrupo> carregarGrupos() {
		
		List<ClassificacaoGrupo> listaGrupo = new ArrayList<ClassificacaoGrupo>();
		List<Classificacao> listaClassificacao = classificacaoRepository.carregarClassificacao();
		
		for (String grupo : Constants.GRUPOS) {
			ClassificacaoGrupo cgA = new ClassificacaoGrupo();
			cgA.setGrupo(grupo);
			cgA.setListaClassificacao(listaClassificacao.stream().filter( (c) -> {
				return c.getSelecao().getGrupo().equalsIgnoreCase(grupo);
			}).collect(Collectors.toList()));
			listaGrupo.add(cgA);
		}
		return listaGrupo;
	}

	public Colocacao carregarColocacao() {
		
		return colocacaoRepository.carregarColocacao();
	}

	public HomeDepoisCuriosidade carregarCuriosidade() {
		
		HomeDepoisCuriosidade cu = new HomeDepoisCuriosidade();
		
		cu.setListaPlacarExato(usuarioRepository.carregarPlacarExato());
		cu.setListaNenhumPlacar(usuarioRepository.carregarNenhumPlacar());
		cu.setListaColocado(usuarioRepository.carregarColocado());
		cu.setListaNenhumColocado(usuarioRepository.carregarNenhumColocado());

		return cu;
	}

	/**
	 * OTIMIZADO: Carrega as 3 próximas partidas com apostas em 2 queries (ao invés de ~40 queries).
	 * Usa PartidasNativeQuery para evitar N+1.
	 */
	@Transactional(readOnly = true)
	public HomeDuranteProximasPartidas carregarPartidasOtimizado() {
		
		HomeDuranteProximasPartidas pp = new HomeDuranteProximasPartidas();
		
		// USAR QUERY NATIVA: Busca 3 partidas + seleções + apostas em 2 queries
		List<Partida> listaPartidas = partidasNativeQuery.carregarProximasPartidasComApostas();
		
		if (listaPartidas != null && listaPartidas.size() > 0) {
			pp.setPartida1(listaPartidas.get(0));
			if (listaPartidas.size() > 1) {
				pp.setPartida2(listaPartidas.get(1));
			}
			if (listaPartidas.size() > 2) {
				pp.setPartida3(listaPartidas.get(2));
			}
		}
		return pp;
	}

	/**
	 * OTIMIZADO: Carrega classificação dos grupos em 1 query (ao invés de 32 queries).
	 * Usa ClassificacaoNativeQuery para evitar N+1.
	 */
	@Transactional(readOnly = true)
	public List<ClassificacaoGrupo> carregarGruposOtimizado() {
		
		// USAR QUERY NATIVA: Busca classificações + seleções em 1 query
		List<Classificacao> listaClassificacao = classificacaoNativeQuery.carregarClassificacaoComSelecoes();
		
		// Agrupar por grupo (mantém lógica original)
		List<ClassificacaoGrupo> listaGrupo = new ArrayList<ClassificacaoGrupo>();
		for (String grupo : Constants.GRUPOS) {
			ClassificacaoGrupo cgA = new ClassificacaoGrupo();
			cgA.setGrupo(grupo);
			cgA.setListaClassificacao(listaClassificacao.stream().filter( (c) -> {
				return c.getSelecao().getGrupo().equalsIgnoreCase(grupo);
			}).collect(Collectors.toList()));
			listaGrupo.add(cgA);
		}
		return listaGrupo;
	}

}

