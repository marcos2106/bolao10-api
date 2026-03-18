package br.com.bolao.bolao10.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Notificacao;
import br.com.bolao.bolao10.domain.enums.TipoNotificacaoEnum;
import br.com.bolao.bolao10.repository.NotificacaoRepository;

@Service
public class NotificacaoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoService.class);

	@Autowired
	private NotificacaoRepository notificacaoRepository;

	private final Random random = new Random();

	/**
	 * Salva um novo registro no histórico global de notificações.
	 * 
	 * @param tipo     Tipo do evento
	 * @param mensagem Texto descrevendo quem fez o quê
	 */
	@Transactional
	public void salvarNotificacao(TipoNotificacaoEnum tipo, String mensagem) {
		try {
			Notificacao notificacao = new Notificacao();
			notificacao.setTipoEvento(tipo);
			notificacao.setMensagem(mensagem);
			notificacao.setDataCriacao(LocalDateTime.now());
			
			notificacaoRepository.save(notificacao);
			LOGGER.info("Notificação registrada: [{}] {}", tipo, mensagem);
			
		} catch (Exception e) {
			LOGGER.error("Erro ao salvar notificação global [{}]: {}", tipo, e.getMessage());
		}
	}

	/**
	 * Busca as últimas 5 notificações do banco e retorna apenas UMA sorteada aleatoriamente.
	 * Utilizado para o Widget dinâmico da Home.
	 * 
	 * @return A notificação sorteada ou null se não houver registros.
	 */
	@Transactional(readOnly = true)
	public Notificacao obterNotificacaoSorteada() {
		List<Notificacao> ultimas = notificacaoRepository.obterUltimasCinco();
		
		if (ultimas == null || ultimas.isEmpty()) {
			return null;
		}
		int indiceSorteado = random.nextInt(ultimas.size());
		return ultimas.get(indiceSorteado);
	}
	
	@Transactional(readOnly = true)
	public Page<Notificacao> listar(int page, int size) {
        int offset = page * size;
        List<Notificacao> notificacoes = notificacaoRepository.obterTodasPaginado(offset, size);
        long total = notificacaoRepository.obterContagemTotal();

        // PageImpl já calcula totalPages e expõe os mesmos métodos de Page
        return new PageImpl<>(notificacoes, new PageRequest(page, size), total);
    }

	/**
	* Retorna o número total de notificações no banco, útil para montar cálculos de paginação.
	 * 
	 * @return Quantidade total de registros consolidados
	 */
	@Transactional(readOnly = true)
	public Long countTotalNotificacoes() {
		return notificacaoRepository.obterContagemTotal();
	}

}
