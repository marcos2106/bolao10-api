package br.com.bolao.bolao10.domain.enums;

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
