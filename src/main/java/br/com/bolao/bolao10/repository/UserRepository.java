
package br.com.bolao.bolao10.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.User;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.support.Cryptography;

@Repository
public class UserRepository extends GenericRepository {

   @Autowired
   private EntityManager em;

   public User getUserByLoginAndPasswd(String login, String passwd) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u ");
      sql.append(" from User u");
      sql.append(" where u.login = :login");
      sql.append("   and u.password = :passwd");
      sql.append("   and u.isActive = :active");

      TypedQuery<User> query = em.createQuery(sql.toString(), User.class);
      query.setParameter("login", login);
      query.setParameter("passwd", Cryptography.encrypt(passwd));
      query.setParameter("active", Boolean.TRUE);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public User getUserByLoginAndPasswd(String login, String passwd, boolean isInternalLogin) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u ");
      sql.append(" from User u");
      sql.append(" where u.login = :login");
      sql.append("   and u.password = :passwd");
      sql.append("   and u.isActive = :active");

      if (isInternalLogin) {
         sql.append("  and u.profile IN ('MANAGER', 'ATTENDANT', 'BROKER', 'SUPERVISOR') ");
      }

      TypedQuery<User> query = em.createQuery(sql.toString(), User.class);
      query.setParameter("login", login);
      query.setParameter("passwd", Cryptography.encrypt(passwd));
      query.setParameter("active", Boolean.TRUE);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public User save(User user) {
      if (user.getId() == null) {
         super.persist(user);
      }
      else {
         super.update(user);
      }
      return user;
   }

   public User getUserHolderByLoginAndPasswd(String login, String passwd) {
      return getUserByLoginAndPasswdAndProfile(login, passwd, UserProfile.HOLDER);
   }

   public User getUserDependentByLoginAndPasswd(String login, String passwd) {
      return getUserByLoginAndPasswdAndProfile(login, passwd, UserProfile.DEPENDENT);
   }

   public User getUserByLoginAndPasswdAndProfile(String login, String passwd, UserProfile profile) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u ");
      sql.append(" from User u");
      sql.append(" where u.login = :login");
      sql.append("   and u.password = :passwd");
      sql.append("   and u.isActive = :active");
      sql.append("   and u.profile = :profile");

      TypedQuery<User> query = em.createQuery(sql.toString(), User.class);
      query.setParameter("login", login);
      query.setParameter("passwd", Cryptography.encrypt(passwd));
      query.setParameter("active", Boolean.TRUE);
      query.setParameter("profile", profile);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<User> getUserByLogin(String login) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u ");
      sql.append(" from User u");
      sql.append(" where u.login = :login");
      sql.append("   and u.isActive = :active");

      TypedQuery<User> query = em.createQuery(sql.toString(), User.class);
      query.setParameter("login", login);
      query.setParameter("active", Boolean.TRUE);

      try {
         return query.getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public User findById(Long id) {
      return super.find(User.class, id);
   }

   public User findByHolderId(Long id) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u                       ");
      sql.append(" from User u 					");
      sql.append(" where u.holder.id = :holderId  ");
      sql.append("   and u.profile = :profile     ");

      TypedQuery<User> query = em.createQuery(sql.toString(), User.class);
      query.setParameter("holderId", id);
      query.setParameter("profile", UserProfile.HOLDER);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public User findUserByDependentId(Long id) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u                       ");
      sql.append(" from User u               ");
      sql.append(" where u.dependent.id = :dependentId  ");
      sql.append("   and u.profile = :profile     ");

      TypedQuery<User> query = em.createQuery(sql.toString(), User.class);
      query.setParameter("dependentId", id);
      query.setParameter("profile", UserProfile.DEPENDENT);

      try {
         return query.getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<User> findByName(String name) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u                       ");
      sql.append(" from User u 					");
      sql.append(" where u.name like :name		");
      sql.append(" order by u.name     			");

      try {
         return em.createQuery(sql.toString(), User.class).setParameter("name", "%".concat(name).concat("%")).getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<User> listAll() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u                       				      ");
      sql.append(" from User u 									      ");
      sql.append(" where u.profile in ('MANAGER','ATTENDANT','BROKER')  ");
      sql.append(" order by u.name     							      ");

      try {
         return em.createQuery(sql.toString(), User.class).getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public User findByLogin(String login) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u                       ");
      sql.append(" from User u 					");
      sql.append(" where u.login = :login		    ");
      sql.append(" order by u.name     			");

      try {
         return em.createQuery(sql.toString(), User.class).setParameter("login", login).getSingleResult();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<User> findByLoginList(String login) {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u                       ");
      sql.append(" from User u 					");
      sql.append(" where u.login = :login		    ");
      sql.append(" order by u.name     			");

      try {
         return em.createQuery(sql.toString(), User.class).setParameter("login", login).getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }

   public List<User> findAttendentsAndManager() {
      StringBuilder sql = new StringBuilder();
      sql.append(" select u                      						");
      sql.append(" from User u 										");
      sql.append(" where u.profile in ('ATTENDANT', 'MANAGER')		");
      sql.append(" order by u.name     								");

      try {
         return em.createQuery(sql.toString(), User.class).getResultList();
      }
      catch (Exception e) {
         return null;
      }
   }
}
