package br.com.bolao.bolao10.support;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {

	private JsonUtils() {
		super();
	}

	public static <T> T jsonStringToObject(String str, Class<T> clazz) {
		try {
			ObjectMapper mapper = mapper();
			return mapper.readValue(str, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> List<T> jsonStringToList(String str, Class<T> clazz) {
		try {
			ObjectMapper mapper = mapper();
			return mapper.readValue(str, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
		} catch (Exception e) {
			return null;
		}
	}

	public static String objectToJsonString(Object object) {
		try {
			ObjectMapper mapper = mapper();
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> T readInputStream(InputStream input, Class<T> clazz) {
		try {
			ObjectMapper mapper = mapper();
			return mapper.readValue(input, clazz);
		} catch (Exception e) {
			return null;
		}
	}

	public static ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		return mapper;
	}
}