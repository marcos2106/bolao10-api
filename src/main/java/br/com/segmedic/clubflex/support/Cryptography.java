package br.com.segmedic.clubflex.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.segmedic.clubflex.exception.ClubFlexException;

public class Cryptography {

	private Cryptography() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Cryptography.class);

	private static final String DEFAULT_KEY_PART = "%CLUBFLX29898330989HJShhjK#2019#";
	private static final String ALGORITHM_SHA1 = "SHA-1";
	private static final String ALGORITHM_AES = "AES";

	public static String encrypt(String valor) {
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
			cipher.init(Cipher.ENCRYPT_MODE, gerarChave());
			byte[] dadoCriptografadoBytes = cipher.doFinal(valor.getBytes());
			Base64.Encoder encoder = Base64.getEncoder();
			return encoder.encodeToString(dadoCriptografadoBytes);
		} catch (Exception e) {
			LOGGER.error("Erro ao tentar criptografar o valor informado para a chave especificada.", e);
			throw new ClubFlexException("Ocorreu um erro inesperado. Favor tentar novamente mais tarde.", e);
		}
	}

	public static String decrypt(String valor) {
		try {
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] dadoCriptografadaBytes = decoder.decode(valor);

			Cipher cipher = Cipher.getInstance(ALGORITHM_AES);
			cipher.init(Cipher.DECRYPT_MODE, gerarChave());
			byte[] dadoDecriptado = cipher.doFinal(dadoCriptografadaBytes);
			return new String(dadoDecriptado);
		} catch (Exception e) {
			LOGGER.error("Erro ao tentar descriptografar o valor informado para a chave especificada.", e);
			throw new ClubFlexException("Ocorreu um erro inesperado. Favor tentar novamente mais tarde.", e);
		}
	}

	private static SecretKeySpec gerarChave() throws NoSuchAlgorithmException {
		byte[] chave = DEFAULT_KEY_PART.getBytes();
		MessageDigest sha = MessageDigest.getInstance(ALGORITHM_SHA1);
		chave = sha.digest(chave);
		chave = Arrays.copyOf(chave, 16);
		return new SecretKeySpec(chave, ALGORITHM_AES);
	}

}
