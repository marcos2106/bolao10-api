
package br.com.bolao.bolao10.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Classificacao;
import br.com.bolao.bolao10.domain.Colocacao;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Ranking;
import br.com.bolao.bolao10.model.ApostaPartida;
import br.com.bolao.bolao10.model.ClassificacaoGrupo;
import br.com.bolao.bolao10.model.HomeAntesDadosIniciais;
import br.com.bolao.bolao10.model.HomeDepoisCuriosidade;
import br.com.bolao.bolao10.model.HomeDuranteProximasPartidas;
import br.com.bolao.bolao10.model.UltimosUsuarios;
import br.com.bolao.bolao10.repository.ApostaRepository;
import br.com.bolao.bolao10.repository.ClassificacaoRepository;
import br.com.bolao.bolao10.repository.ColocacaoRepository;
import br.com.bolao.bolao10.repository.PartidaRepository;
import br.com.bolao.bolao10.repository.RankingRepository;
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
	private ClassificacaoRepository classificacaoRepository;
	
	@Autowired
	private ColocacaoRepository colocacaoRepository;


	public Partida carregarEstreia() {

		return partidaRepository.carregarPartidaEstreia();
	}

	public HomeAntesDadosIniciais carregarDadosInciais() {

		HomeAntesDadosIniciais dados = new HomeAntesDadosIniciais();

		Integer qntdParticipantes = usuarioRepository.carregarParticipantes().size();

		BigDecimal valorTotal = new BigDecimal(qntdParticipantes).multiply(Constants.VALOR_APOSTA);
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
		if (listaPartidas != null) {
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
		Collections.sort(listaPartida, Comparator.comparing(Partida::getId));
		return listaPartida;
	}

	public List<Ranking> carregarRanking() {
		
		return rankingRepository.carregarRanking();
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

}
