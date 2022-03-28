
package br.com.segmedic.clubflex.domain.enums;

public enum PetEnum {

      DOGS("Cachorros"), CATS("Gatos"), BIRDS("Pássaros"), RODENTS("Roedores"), ARACHNIDS("Aracnídeos");// , OTHERS("Outros");

   private String describe;

   private PetEnum(String describe) {
      this.describe = describe;
   }

   public String getDescribe() {
      return describe;
   }

}
