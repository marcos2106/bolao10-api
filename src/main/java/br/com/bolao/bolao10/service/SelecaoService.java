
package br.com.bolao.bolao10.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Selecao;
import br.com.bolao.bolao10.repository.SelecaoRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SelecaoService {

	@Autowired
	private SelecaoRepository selecaoRepository;
	
	@Transactional
	public List<Selecao> carregarSelecoesOrderGrupo() {
		return selecaoRepository.carregarSelecoesOrderGrupo();
	}
	
	
}
