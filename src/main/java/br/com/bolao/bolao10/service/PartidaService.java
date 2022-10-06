
package br.com.bolao.bolao10.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.repository.PartidaRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PartidaService {

	@Autowired
	private PartidaRepository partidaRepository;
	
	@Transactional
	public List<Partida> carregarPartidas() {
		return partidaRepository.carregarPartidasTabela();
	}
	
}
