package br.com.segmedic.clubflex.support;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class EmailValidator {
	private static final Pattern PADRAO_EMAIL_VALIDO = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	public static boolean isValido(String email){
		if(StringUtils.isBlank(email)) {
			return false;
		}
	   return PADRAO_EMAIL_VALIDO.matcher(email).matches();
	 }
}
