
package br.com.bolao.bolao10.domain;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.bolao.bolao10.domain.enums.Sex;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.support.Strings;

@Table(name = "user", indexes = @Index(columnList = "login,password,profile,flag_active"))
public class User implements Serializable {

   private static final long serialVersionUID = -3576336738296104582L;

   @Id
   @Column(name = "iduser", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(80)")
   private String name;

   @Column(name = "login", nullable = false, columnDefinition = "VARCHAR(14)")
   private String login;

   @JsonIgnore
   @Column(name = "password", nullable = false, columnDefinition = "VARCHAR(60)")
   private String password;

   @Enumerated(EnumType.STRING)
   @Column(name = "profile", nullable = false, columnDefinition = "ENUM('MANAGER','ATTENDANT','BROKER','HOLDER','DEPENDENT','ALL')")
   private UserProfile profile;

   @Column(name = "flag_active", nullable = false, columnDefinition = "CHAR(1) DEFAULT 1")
   private Boolean isActive;

   @ManyToOne(cascade = CascadeType.MERGE, optional = true, fetch = FetchType.EAGER)
   @JoinColumn(name = "idholder", nullable = true)
   private Holder holder;

   @Column(name = "email", nullable = true, columnDefinition = "VARCHAR(60)")
   private String email;

   @Enumerated(EnumType.STRING)
   @Column(name = "sex", nullable = true, columnDefinition = "ENUM('MALE','FEMALE') default 'MALE'")
   private Sex sex;

   @Transient
   private Long idSubscription;

   public Sex getSex() {
      return sex;
   }

   public void setSex(Sex sex) {
      this.sex = sex;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      if (this.name != null) {
         this.name.toUpperCase();
      }
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getLogin() {
      return Strings.removeNoNumericChars(this.login);
   }

   public void setLogin(String login) {
      this.login = login;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public UserProfile getProfile() {
      return profile;
   }

   public void setProfile(UserProfile profile) {
      this.profile = profile;
   }

   public Boolean getIsActive() {
      return isActive;
   }

   public void setIsActive(Boolean isActive) {
      this.isActive = isActive;
   }

   public Holder getHolder() {
      return holder;
   }

   public void setHolder(Holder holder) {
      this.holder = holder;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getEmail() {
	   return email;
   }

@Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      User other = (User) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      }
      else if (!id.equals(other.id))
         return false;
      return true;
   }

   public Long getIdSubscription() {
      return idSubscription;
   }

   public void setIdSubscription(Long idSubscription) {
      this.idSubscription = idSubscription;
   }

}
