
package br.com.bolao.bolao10.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Classificacao;
import br.com.bolao.bolao10.domain.Partida;
import br.com.bolao.bolao10.domain.Selecao;
import br.com.bolao.bolao10.exception.Bolao10Exception;
import br.com.bolao.bolao10.repository.ClassificacaoRepository;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ClassificacaoService {

	@Autowired
	private ClassificacaoRepository classificacaoRepository;

	
	@Transactional
	public void contabilizarClassificacao(Partida partida) {
		
		try {
			classificacaoRepository.zerarAnteriorClassificacao(partida.getSelecaoA());
			classificacaoRepository.zerarAnteriorClassificacao(partida.getSelecaoB());
			
			// Vitoria Selecao A
			if (partida.getPlacarA() > partida.getPlacarB()) {
				somarVitoriaClassificacao(partida, partida.getSelecaoA(), partida.getSelecaoB(), partida.getPlacarA(), partida.getPlacarB());
			}
			// Vitoria Selecao B
			if (partida.getPlacarB() > partida.getPlacarA()) {
				somarVitoriaClassificacao(partida, partida.getSelecaoB(), partida.getSelecaoA(), partida.getPlacarB(), partida.getPlacarA());
			}
			// Empate
			if (partida.getPlacarA() == partida.getPlacarB()) {
				somarEmpateClassificacao(partida);
			}
			
		} catch (Exception e) {
			throw new Bolao10Exception("Erro ao atualizar a classificação do Mundial.");
		}
	}

	private void somarVitoriaClassificacao(Partida partida, Selecao selecaoVitoria, Selecao selecaoDerrota, 
			Integer placarVitoria, Integer placarDerrota) {
		
		Classificacao cSelecaoVitoria = classificacaoRepository.findBySelecao(selecaoVitoria.getId());
		cSelecaoVitoria.setPontos(cSelecaoVitoria.getPontos()+3);
		cSelecaoVitoria.setVitoria(cSelecaoVitoria.getVitoria()+1);
		cSelecaoVitoria.setGolspro(cSelecaoVitoria.getGolspro() + placarVitoria);
		cSelecaoVitoria.setGolscontra(cSelecaoVitoria.getGolscontra() + placarDerrota);
		cSelecaoVitoria.setSaldogols(cSelecaoVitoria.getGolspro() - cSelecaoVitoria.getGolscontra());
		classificacaoRepository.save(cSelecaoVitoria);

		Classificacao cSelecaoDerrota = classificacaoRepository.findBySelecao(selecaoDerrota.getId());
		cSelecaoDerrota.setDerrota(cSelecaoDerrota.getDerrota()+1);
		cSelecaoDerrota.setGolspro(cSelecaoDerrota.getGolspro() + placarDerrota);
		cSelecaoDerrota.setGolscontra(cSelecaoDerrota.getGolscontra() + placarVitoria);
		cSelecaoDerrota.setSaldogols(cSelecaoDerrota.getGolspro() - cSelecaoDerrota.getGolscontra());
		classificacaoRepository.save(cSelecaoDerrota);
	}

	private void somarEmpateClassificacao(Partida partida) {
		
		Classificacao cSelecaoEmpateA = classificacaoRepository.findBySelecao(partida.getSelecaoA().getId());
		cSelecaoEmpateA.setPontos(cSelecaoEmpateA.getPontos() + 1);
		cSelecaoEmpateA.setEmpate(cSelecaoEmpateA.getEmpate() + 1);
		cSelecaoEmpateA.setGolspro(cSelecaoEmpateA.getGolspro() + partida.getPlacarA());
		cSelecaoEmpateA.setGolscontra(cSelecaoEmpateA.getGolscontra() + partida.getPlacarB());
		cSelecaoEmpateA.setSaldogols(cSelecaoEmpateA.getGolspro() - cSelecaoEmpateA.getGolscontra());
		classificacaoRepository.save(cSelecaoEmpateA);
		
		Classificacao cSelecaoEmpateB = classificacaoRepository.findBySelecao(partida.getSelecaoB().getId());
		cSelecaoEmpateB.setPontos(cSelecaoEmpateB.getPontos() + 1);
		cSelecaoEmpateB.setEmpate(cSelecaoEmpateB.getEmpate() + 1);
		cSelecaoEmpateB.setGolspro(cSelecaoEmpateB.getGolspro() + partida.getPlacarB());
		cSelecaoEmpateB.setGolscontra(cSelecaoEmpateB.getGolscontra() + partida.getPlacarA());
		cSelecaoEmpateB.setSaldogols(cSelecaoEmpateB.getGolspro() - cSelecaoEmpateB.getGolscontra());
		classificacaoRepository.save(cSelecaoEmpateB);
	}
	
	@Transactional
	public void finalizarClassificacao(Partida partida) {
		
		classificacaoRepository.atualizarAnteriorClassificacao(partida.getSelecaoA());
		classificacaoRepository.atualizarAnteriorClassificacao(partida.getSelecaoB());
	}

}
