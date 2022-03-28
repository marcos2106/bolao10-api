
package br.com.segmedic.clubflex.repository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GenericRepository {

   private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
   private static final String YYYY_MM_DD = "yyyy-MM-dd";

   @Autowired
   private EntityManager em;

   public void delete(Object entity) {
      em.remove(entity);
   }

   public <T> T persist(T entity) {
      em.persist(entity);
      em.flush();
      return entity;
   }

   public <T> T merge(T entity) {
      em.merge(entity);
      em.flush();
      return entity;
   }

   public <T> List<T> list(Class<T> clazz) {
      String hqlList = new StringBuilder().append(" from ").append(clazz.getSimpleName()).toString();
      return em.createQuery(hqlList, clazz).getResultList();
   }

   public <T> T update(T entity) {
      em.merge(entity);
      em.flush();
      return entity;
   }

   public <T> T find(Class<T> clazz, Serializable id) {
      return em.find(clazz, id);
   }

   protected String getStringValue(Object[] object, Integer position) {
      if (object[position - 1] != null) {
         return object[position - 1].toString();
      }
      return null;
   }

   protected Long getLongValue(Object[] object, Integer position) {
      if (object[position - 1] != null) {
         return Long.valueOf(object[position - 1].toString());
      }
      return null;
   }

   protected Integer getIntegerValue(Object[] object, Integer position) {
      if (object[position - 1] != null) {
         return Integer.valueOf(object[position - 1].toString());
      }
      return null;
   }

   protected BigDecimal getBigDecimalValue(Object[] object, Integer position) {
      if (object[position - 1] != null) {
         return new BigDecimal(object[position - 1].toString());
      }
      return null;
   }

   protected LocalDateTime getLocalDateTime(Object[] object, Integer position) {
      if (object[position - 1] != null) {
         return LocalDateTime.parse(object[position - 1].toString(), DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
      }
      return null;
   }

   protected LocalDate getLocalDate(Object[] object, Integer position) {
      if (object[position - 1] != null) {
         return LocalDate.parse(object[position - 1].toString(), DateTimeFormatter.ofPattern(YYYY_MM_DD));
      }
      return null;
   }

   protected Boolean getBooleanValue(Object[] object, Integer position) {
      if (object[position - 1] != null) {
         if (object[position - 1].toString().equals("1")) {
            return Boolean.TRUE;
         }
         else {
            return Boolean.FALSE;
         }
      }
      return false;
   }
}