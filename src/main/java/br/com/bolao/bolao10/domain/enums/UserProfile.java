package br.com.bolao.bolao10.domain.enums;

public enum UserProfile {
      ADMIN("Admin"),
      USER("Usuário");

   private String describe;

   private UserProfile(String describe) {
      this.describe = describe;
   }

   public String getDescribe() {
      return describe;
   }

}
