/*
* Rankme Informática S.A.
* Criação : 25 de jan. de 2021
*/

package br.com.segmedic.clubflex.domain.enums;

/**
 * Tipos de Email
 * 
 * @author Janeiro/2021: Roberto de Souza Rodrigues
 */
public enum EmailType {

      EMAIL_FINANCE("EMAIL_FINANCE"),
      EMAIL_WELCOME("EMAIL_WELCOME"),
      EMAIL_GERAL("EMAIL_GERAL");

   private String describe;

   private EmailType(String describe) {
      this.describe = describe;
   }

   public String getDescribe() {
      return this.describe;
   }
}
