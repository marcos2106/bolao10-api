
package br.com.bolao.bolao10.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.repository.PartidasNativeQuery;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PartidaService {

	@Autowired
	private PartidasNativeQuery partidasNativeQuery;
	
	@Transactional
	public List<Partida> carregarPartidas() {
		return partidasNativeQuery.carregarPartidasComSelecoes();
	}
	
	/**
	 * Carrega partidas com seleções usando SQL NATIVO (bypassa Hibernate para evitar N+1).
	 * OTIMIZADO: 1 query ao invés de 312 queries!
	 */
	@Transactional(readOnly = true)
	public List<Partida> carregarPartidasOtimizado() {
		long inicio = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE PARTIDAS] Iniciando carregarPartidasOtimizado...");
		
		List<Partida> partidas = partidasNativeQuery.carregarPartidasComSelecoes();
		
		long fim = System.currentTimeMillis();
		System.out.println(">>> [PERFORMANCE PARTIDAS] TOTAL carregarPartidasOtimizado(): " + (fim-inicio) + "ms");
		
		return partidas;
	}
	
}
