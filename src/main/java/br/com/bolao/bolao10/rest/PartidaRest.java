
package br.com.bolao.bolao10.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.service.PartidaService;

@RestController
@RequestMapping("/partida")
public class PartidaRest extends BaseRest {

	@Autowired
	private PartidaService partidaService;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartidas() {
		return createObjectReturn(partidaService.carregarPartidas());
	}
	
	/**
	 * Endpoint OTIMIZADO para carregar partidas com seleções.
	 * USA SQL NATIVO: 1 query ao invés de 312 queries!
	 */
	@GetMapping(value = "/otimizado", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> carregarPartidasOtimizado() {
		long inicio = System.currentTimeMillis();
		System.out.println(">>> [REST PARTIDAS] Iniciando /partida/otimizado...");
		
		long t1 = System.currentTimeMillis();
		Object result = partidaService.carregarPartidasOtimizado();
		long t2 = System.currentTimeMillis();
		System.out.println(">>> [REST PARTIDAS] Service levou: " + (t2-t1) + "ms");
		
		long t3 = System.currentTimeMillis();
		ResponseEntity<?> response = createObjectReturn(result);
		long t4 = System.currentTimeMillis();
		System.out.println(">>> [REST PARTIDAS] createObjectReturn/serialização levou: " + (t4-t3) + "ms");
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [REST PARTIDAS] TOTAL endpoint: " + (fim-inicio) + "ms");
		
		return response;
	}

}
