
package br.com.bolao.bolao10.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.service.HomeService;

@RestController
@RequestMapping("/home")
public class HomeRest extends BaseRest {

	@Autowired
	private HomeService homeService;

	@GetMapping(value = "/antes/estreia", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarEstreia() {
		return createObjectReturn(homeService.carregarEstreia());
	}

	@GetMapping(value = "/antes/inicio", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarDadosInciais() {
		return createObjectReturn(homeService.carregarDadosInciais());
	}

	@GetMapping(value = "/antes/participantes", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarParticipantes() {
		return createObjectReturn(homeService.carregarParticipantes());
	}

	@GetMapping(value = "/durante/partidas", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartidas() {
		return createObjectReturn(homeService.carregarPartidas());
	}
	
	@GetMapping(value = "/durante/partidas/anteriores", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartidasAnteriores() {
		return createObjectReturn(homeService.carregarPartidasAnteriores());
	}

	@GetMapping(value = "/durante/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarRanking() {
		return createObjectReturn(homeService.carregarRanking());
	}

	/**
	 * Endpoint OTIMIZADO: retorna ranking com badges em uma única requisição.
	 * Performance muito melhor que chamar /durante/ranking + /badge/ranking separadamente.
	 */
	@GetMapping(value = "/durante/ranking-completo", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarRankingCompleto() {
		long inicio = System.currentTimeMillis();
		System.out.println(">>> [REST] Iniciando /durante/ranking-completo...");
		
		Object resultado = homeService.carregarRankingCompleto();
		
		long meio = System.currentTimeMillis();
		System.out.println(">>> [REST] Service levou: " + (meio-inicio) + "ms");
		
		ResponseEntity<?> response = createObjectReturn(resultado);
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [REST] createObjectReturn/serialização levou: " + (fim-meio) + "ms");
		System.out.println(">>> [REST] TOTAL endpoint: " + (fim-inicio) + "ms");
		
		return response;
	}

	/**
	 * ENDPOINT DE TESTE: retorna apenas quantidade para verificar se problema é serialização
	 */
	@GetMapping(value = "/durante/ranking-completo-teste", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarRankingCompletoTeste() {
		long inicio = System.currentTimeMillis();
		List<br.com.bolao.bolao10.model.RankingComBadges> resultado = homeService.carregarRankingCompleto();
		long fim = System.currentTimeMillis();
		
		// Retorna apenas contagem - SEM serializar objetos complexos
		java.util.Map<String, Object> teste = new java.util.HashMap<>();
		teste.put("quantidade", resultado.size());
		teste.put("tempoMs", (fim-inicio));
		teste.put("mensagem", "Se este endpoint for rápido, o problema é a serialização JSON");
		
		System.out.println(">>> [TESTE] Endpoint teste levou: " + (fim-inicio) + "ms");
		return createObjectReturn(teste);
	}

	@GetMapping(value = "/durante/grupo", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarGrupos() {
		return createObjectReturn(homeService.carregarGrupos());
	}

	@GetMapping(value = "/depois/colocacao", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarColocacao() {
		return createObjectReturn(homeService.carregarColocacao());
	}

	@GetMapping(value = "/depois/curiosidade", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarCuriosidade() {
		return createObjectReturn(homeService.carregarCuriosidade());
	}

}
