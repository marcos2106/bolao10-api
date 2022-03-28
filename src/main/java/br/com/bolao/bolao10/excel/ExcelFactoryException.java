package br.com.segmedic.clubflex.excel;


/**
 * Excecao para geracao do excel
 *
 */
public class ExcelFactoryException extends RuntimeException {

	private static final long serialVersionUID = -5875778957941024655L;

	public ExcelFactoryException(String mensagem) {
		super(mensagem);
	}
}
