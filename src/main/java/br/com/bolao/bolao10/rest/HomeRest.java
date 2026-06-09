
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

	/**
	 * Endpoint OTIMIZADO: retorna 3 próximas partidas com apostas em 2 queries (ao invés de ~40).
	 */
	@GetMapping(value = "/durante/partidas/otimizado", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartidasOtimizado() {
		long inicio = System.currentTimeMillis();
		System.out.println(">>> [REST] Iniciando /durante/partidas/otimizado...");
		
		Object resultado = homeService.carregarPartidasOtimizado();
		
		long meio = System.currentTimeMillis();
		System.out.println(">>> [REST] Service levou: " + (meio-inicio) + "ms");
		
		ResponseEntity<?> response = createObjectReturn(resultado);
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [REST] createObjectReturn/serialização levou: " + (fim-meio) + "ms");
		System.out.println(">>> [REST] TOTAL endpoint: " + (fim-inicio) + "ms");
		
		return response;
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

	@GetMapping(value = "/durante/grupo", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarGrupos() {
		return createObjectReturn(homeService.carregarGrupos());
	}

	/**
	 * Endpoint OTIMIZADO: retorna grupos de classificação em 1 query (ao invés de 32).
	 */
	@GetMapping(value = "/durante/grupo/otimizado", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarGruposOtimizado() {
		String reqId = String.format("%04d", (int)(Math.random() * 10000));
		long inicio = System.currentTimeMillis();
		System.out.println(">>> [REST:" + reqId + "] Iniciando /durante/grupo/otimizado...");
		
		Object resultado = homeService.carregarGruposOtimizado();
		
		long meio = System.currentTimeMillis();
		System.out.println(">>> [REST:" + reqId + "] Service levou: " + (meio-inicio) + "ms");
		
		ResponseEntity<?> response = createObjectReturn(resultado);
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [REST:" + reqId + "] createObjectReturn/serialização levou: " + (fim-meio) + "ms");
		System.out.println(">>> [REST:" + reqId + "] TOTAL endpoint: " + (fim-inicio) + "ms");
		
		return response;
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
