package br.com.segmedic.clubflex.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.segmedic.clubflex.domain.ParamsSubscription;
import br.com.segmedic.clubflex.domain.SmsParams;


@Repository
public class SystemParamsRepository extends GenericRepository {

	/**
	 * Obtem parametros do sistema para criacao de formulario
	 * 
	 * @return - Parametros
	 */
	public List<SmsParams> obterCamposTela() {
		return super.list(SmsParams.class);
	}

	/**
	 * Atualiza parametro
	 * 
	 * @param params
	 */
	public void updateParam(SmsParams params) {
		super.update(params);
	}

	/**
	 * Obtem parametros da assinatura para exibição do formulario
	 * 
	 * @return - Parametros
	 */
	public ParamsSubscription getParamsSubscription() {
		return super.list(ParamsSubscription.class).get(0);
	}

}
