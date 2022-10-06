package br.com.bolao.bolao10.support;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

	private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

	public static String formatMoney(BigDecimal valor) {
		if (valor == null) {
			return NumberFormat.getCurrencyInstance(LOCALE_PT_BR).format(BigDecimal.ZERO);
		}
		return NumberFormat.getCurrencyInstance(LOCALE_PT_BR).format(valor);
	}
	
}
