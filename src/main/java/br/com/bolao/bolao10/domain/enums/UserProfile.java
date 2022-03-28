
package br.com.segmedic.clubflex.domain.enums;

public enum UserProfile {
      MANAGER("Gerente"),
      ATTENDANT("Atendente"),
      BROKER("Corretor"),
      HOLDER("Titular"),
      DEPENDENT("Dependente"),
      SUPERVISOR("Supervisor"),
      ALL("Todos");

   private String describe;

   private UserProfile(String describe) {
      this.describe = describe;
   }

   public String getDescribe() {
      return describe;
   }

}
