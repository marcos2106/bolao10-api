package br.com.segmedic.clubflex.support;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

public class Ibge {
	public static final String city(String name) {
		Map<String, String> maps = Maps.newConcurrentMap();
		maps.put("Angra dos Reis","3300100");
		maps.put("Aperibé","3300159");
		maps.put("Araruama","3300209");
		maps.put("Areal","3300225");
		maps.put("Armação dos Búzios","3300233");
		maps.put("Arraial do Cabo","3300258");
		maps.put("Barra Mansa","3300407");
		maps.put("Barra do Piraí","3300308");
		maps.put("Belford Roxo","3300456");
		maps.put("Bom Jardim","3300506");
		maps.put("Bom Jesus do Itabapoana","3300605");
		maps.put("Cabo Frio","3300704");
		maps.put("Cachoeiras de Macacu","3300803");
		maps.put("Cambuci","3300902");
		maps.put("Campos dos Goytacazes","3301009");
		maps.put("Cantagalo","3301108");
		maps.put("Carapebus","3300936");
		maps.put("Cardoso Moreira","3301157");
		maps.put("Carmo","3301207");
		maps.put("Casimiro de Abreu","3301306");
		maps.put("Comendador Levy Gasparian","3300951");
		maps.put("Conceição de Macabu","3301405");
		maps.put("Cordeiro","3301504");
		maps.put("Duas Barras","3301603");
		maps.put("Duque de Caxias","3301702");
		maps.put("Engenheiro Paulo de Frontin","3301801");
		maps.put("Guapimirim","3301850");
		maps.put("Iguaba Grande","3301876");
		maps.put("Itaboraí","3301900");
		maps.put("Itaguaí","3302007");
		maps.put("Italva","3302056");
		maps.put("Itaocara","3302106");
		maps.put("Itaperuna","3302205");
		maps.put("Itatiaia","3302254");
		maps.put("Japeri","3302270");
		maps.put("Laje do Muriaé","3302304");
		maps.put("Macaé","3302403");
		maps.put("Macuco","3302452");
		maps.put("Magé","3302502");
		maps.put("Mangaratiba","3302601");
		maps.put("Maricá","3302700");
		maps.put("Mendes","3302809");
		maps.put("Mesquita","3302858");
		maps.put("Miguel Pereira","3302908");
		maps.put("Miracema","3303005");
		maps.put("Natividade","3303104");
		maps.put("Nilópolis","3303203");
		maps.put("Niterói","3303302");
		maps.put("Nova Friburgo","3303401");
		maps.put("Nova Iguaçu","3303500");
		maps.put("Paracambi","3303609");
		maps.put("Paraíba do Sul","3303708");
		maps.put("Paraty","3303807");
		maps.put("Paty do Alferes","3303856");
		maps.put("Petrópolis","3303906");
		maps.put("Pinheiral","3303955");
		maps.put("Piraí","3304003");
		maps.put("Porciúncula","3304102");
		maps.put("Porto Real","3304110");
		maps.put("Quatis","3304128");
		maps.put("Queimados","3304144");
		maps.put("Quissamã","3304151");
		maps.put("Resende","3304201");
		maps.put("Rio Bonito","3304300");
		maps.put("Rio Claro","3304409");
		maps.put("Rio das Flores","3304508");
		maps.put("Rio das Ostras","3304524");
		maps.put("Rio de Janeiro","3304557");
		maps.put("Santa Maria Madalena","3304607");
		maps.put("Santo Antônio de Pádua","3304706");
		maps.put("São Fidélis","3304805");
		maps.put("São Francisco de Itabapoana","3304755");
		maps.put("São Gonçalo","3304904");
		maps.put("São João da Barra","3305000");
		maps.put("São João de Meriti","3305109");
		maps.put("São José de Ubá","3305133");
		maps.put("São José do Vale do Rio Preto","3305158");
		maps.put("São Pedro da Aldeia","3305208");
		maps.put("São Sebastião do Alto","3305307");
		maps.put("Sapucaia","3305406");
		maps.put("Saquarema","3305505");
		maps.put("Seropédica","3305554");
		maps.put("Silva Jardim","3305604");
		maps.put("Sumidouro","3305703");
		maps.put("Tanguá","3305752");
		maps.put("Teresópolis","3305802");
		maps.put("Trajano de Moraes","3305901");
		maps.put("Três Rios","3306008");
		maps.put("Valença","3306107");
		maps.put("Varre-Sai","3306156");
		maps.put("Vassouras","3306206");
		maps.put("Volta Redonda","3306305");
		
		if(StringUtils.isNotBlank(name)) {
			return maps.get(name);
		}
		return null;
	}
}
