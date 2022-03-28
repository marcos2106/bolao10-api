package br.com.bolao.bolao10.support;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

	@Override
	public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		 String valorAsString = p.getText();
		 if(valorAsString != null) {
			 return new BigDecimal(valorAsString.replace("R$","").replace(".","").replace(",", ".").trim());
		 }
		 return null;
	}

//
	
}
