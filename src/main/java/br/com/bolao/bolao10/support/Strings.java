package br.com.bolao.bolao10.support;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.swing.text.MaskFormatter;

import org.apache.commons.lang3.StringUtils;

/**
 * Metodos estaticos utilitarios para trabalhar com Strings ou colecoes de Strings
 * 
 */
public class Strings {
	
	
	/**
	 * Normaliza o CEP colocando tamanho 8 e preenchendo com zeros a esquerda se necessario
	 * 
	 * @param cep CEP original
	 * @return CEP normalizado
	 */
	public static String normalizeCep(String cep) {
		if (StringUtils.isBlank(cep)) {
			return null;
		}
		return StringUtils.leftPad(removeNoNumericChars(cep), 8, '0');
	}

	/**
	 * unnormaliza o CPF removendo toda a formatacao, mantendo apenas os numeros
	 * 
	 * @param cpf CPF original
	 * @return CPF normalizado
	 */
	public static String unnormalizeCPF(String cpf) {
		if (StringUtils.isNotBlank(cpf)) {
			return cpfComDigitosCompletos(cpf);
		}
		return null;
	}

	/**
	 * unnormaliza o CPF removendo toda a formatacao, mantendo apenas os numeros
	 * 
	 * @param cpf CPF original
	 * @return CPF normalizado
	 */
	public static Long unnormalizeCPFParaNumero(String cpf) {
		if (StringUtils.isNotBlank(cpf)) {
			return Long.valueOf(removeNoNumericChars(cpf));
		}
		return null;
	}

	/**
	 * Remove os caracteres nao numericos do cpf e preenche com zeros a esquerda se necessario
	 * 
	 * @param cpf CPF original
	 * @return CPF apenas com caracteres numéricos e com zeros a esquerda se necessário
	 */
	protected static String cpfComDigitosCompletos(String cpf) {
		return StringUtils.leftPad(removeNoNumericChars(cpf), 11, '0');
	}

	/**
	 * Normaliza o CNPJ mantendo-o com tamanho 14 e com zeros a esquerda se necessario
	 * 
	 * @param cnpj cnpj original
	 * @return cnpj normalizado
	 */
	public static String normalizeCNPJ(String cnpj) {
		if (cnpj == null) {
			return null;
		}
		return StringUtils.leftPad(removeNoNumericChars(cnpj), 14, '0');
	}

	public static String removeNoNumericChars(String texto) {
		if(texto != null) {
			return texto.replaceAll("[^\\d]", "");
		}
		return null;
	}

	/**
	 * A partir de uma lista de Strings retorna um Set com os valores duplicados unsa lista.
	 * 
	 * @param lista Lista completa com as Strings
	 * @return Set com as strings duplicadas
	 */
	public static Set<String> encontrarDuplicados(List<String> lista) {
		List<String> copia = new ArrayList<String>(lista);
		for (String value : new HashSet<String>(lista)) {
			copia.remove(value);
		}
		return new HashSet<String>(copia);
	}

	public static String normalizeTelefone(String telefone) {
		if (StringUtils.isBlank(telefone)) {
			return null;
		}
		return removeNoNumericChars(telefone);
	}

	public static String maiusculas(String texto) {
		if (StringUtils.isBlank(texto)) {
			return null;
		}
		return texto.toUpperCase();
	}

	/**
	 * Metodo responsavel por verificar se a String e vazia ou nula.
	 * 
	 * @param string - objeto a ser verificado.
	 * @return boolean - true/false
	 */
	public static boolean isBlankOrNull(String string) {
		return StringUtils.isBlank(string);
	}

	public static boolean isNotBlankOrNull(String string) {
		return !isBlankOrNull(string);
	}

	/**
	 * Retorna uma String vazia com o tamanho informado
	 * 
	 * @param tamanho - referente tamanho da string
	 * @return String vazia com o tamanho informado
	 */
	public static String spaces(int tamanho) {
		return StringUtils.leftPad("", tamanho, " ");
	}
	
	public static String formatString(String value, String pattern) {
        MaskFormatter mf;
        try {
            mf = new MaskFormatter(pattern);
            mf.setValueContainsLiteralCharacters(false);
            return mf.valueToString(value);
        } catch (ParseException ex) {
            return value;
        }
    }

	public static String formatCPF(String cpfCnpj) {
		if(cpfCnpj != null) {
			return formatString(cpfCnpj, "###.###.###-##");
		}
		return "";
	}
	
	public static String formatCNPJ(String cpfCnpj) {
		if(cpfCnpj != null) {
			return formatString(cpfCnpj, "##.###.###/####-##");
		}
		return "";
	}
	
	public static String formatClubCardNumber(String numberCard) {
		if(numberCard != null) {
			return formatString(numberCard, "#### #### #### ####");
		}
		return "";
	}

	public static String generateUniqueToken() {
		return UUID.randomUUID().toString().replace("-","");
	}

	public static String unnormalizeTelefone(String telefoneCelular) {
		if(telefoneCelular != null) {
			return telefoneCelular.replace("(","").replace(")","").replace("-","");
		}
		return null;
	}
	
	public static String limparTagsHtml(String text){
		if(text == null){
			return null;
		}
		return text.replaceAll("<[^>]*>"," ").replaceAll("&nbsp;"," ").replaceAll("\n", "").replaceAll("\t", "");
	}

	public static String generateTempPassword() {
		return generateUniqueToken().substring(0, 6);
	}

	public static String unNormalizeCEP(String cep) {
		if(cep != null) {
			return cep.replace("-","").replace(".","");
		}
		return null;
	}

	public static String removerEspacosEmBranco(String str) {
		return str.replaceAll("\\s+","");
	}

	public static String generateKeyTransactionRede() {
		return StringUtils.left(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd")).concat(generateUniqueToken()), 16);
	}

}