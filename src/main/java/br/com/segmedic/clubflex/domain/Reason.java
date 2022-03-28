
package br.com.segmedic.clubflex.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "reason", indexes = @Index(columnList = "description"))
public class Reason implements Serializable {

   private static final long serialVersionUID = -3576336733482L;

   @Id
   @Column(name = "idreason", nullable = false, columnDefinition = "BIGINT(20)")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "description", nullable = false, columnDefinition = "VARCHAR(200)")
   private String description;

   @Column(name = "flag_active", nullable = false, columnDefinition = "CHAR(1) DEFAULT 1")
   private Boolean isActive;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Boolean getIsActive() {
      return isActive;
   }

   public void setIsActive(Boolean isActive) {
      this.isActive = isActive;
   }

}
