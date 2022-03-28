package br.com.bolao.bolao10.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.bolao.bolao10.model.StandardResponse;

@ControllerAdvice("br.com.bolao.bolao10.rest")
public class ResourceExceptionHandlerRest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceExceptionHandlerRest.class);
	
	@ExceptionHandler(Bolao10Exception.class)
	public ResponseEntity<StandardResponse> doutorVirtualExceptions(Bolao10Exception e, HttpServletRequest request){
		StandardResponse error = new StandardResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), System.currentTimeMillis());
		esreverErroNoLog("Bolao10Exception", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
	
	@ExceptionHandler(SecurityException.class)
	public ResponseEntity<StandardResponse> securityExceptions(SecurityException e, HttpServletRequest request){
		StandardResponse error = new StandardResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), System.currentTimeMillis());
		esreverErroNoLog("SecurityException", e);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}
	
	@ExceptionHandler(TransactionException.class)
	public ResponseEntity<StandardResponse> transactionnalExceptions(TransactionException e, HttpServletRequest request){
		StandardResponse error = new StandardResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro inesperado e isso não é nada bom. Tente novamente se o problema persistir nos avise. (Erro Club099)", System.currentTimeMillis());
		esreverErroNoLog("TransactionalException", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardResponse> allExceptions(Exception e, HttpServletRequest request){
		StandardResponse error = new StandardResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro inesperado. Tente novamente, se o problema persistir, nos avise. (Erro Club100)", System.currentTimeMillis());
		esreverErroNoLog("Exception", e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	private void esreverErroNoLog(String exceptionType, Throwable  e) {
		if(e != null && e.getMessage() != null) {
			LOGGER.error("***ERRO ".concat(exceptionType).concat(" ===> ".concat(e.getMessage())));
		}
	}
}
