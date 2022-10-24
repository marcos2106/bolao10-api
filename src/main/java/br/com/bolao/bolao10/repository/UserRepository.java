
package br.com.bolao.bolao10.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.com.bolao.bolao10.domain.Aposta;
import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.model.HomeDepoisPlacarExato;
import br.com.bolao.bolao10.support.Constants;
import br.com.bolao.bolao10.support.Cryptography;

@Repository
public class UserRepository extends GenericRepository {

	@Autowired
	private EntityManager em;

	public Usuario getUserByLoginAndPasswd(String login, String passwd) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select u ");
		sql.append(" from Usuario u");
		sql.append(" where u.email = :login");
		sql.append("   and u.senha = :passwd");

		TypedQuery<Usuario> query = em.createQuery(sql.toString(), Usuario.class);
		query.setParameter("login", login);
		query.setParameter("passwd", passwd);

		try {
			return query.getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}

	public Usuario getUserByLoginAndPasswd(String login, String passwd, boolean isInternalLogin) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select u ");
		sql.append(" from User u");
		sql.append(" where u.login = :login");
		sql.append("   and u.password = :passwd");
		sql.append("   and u.isActive = :active");

		if (isInternalLogin) {
			sql.append("  and u.profile IN ('MANAGER', 'ATTENDANT', 'BROKER', 'SUPERVISOR') ");
		}

		TypedQuery<Usuario> query = em.createQuery(sql.toString(), Usuario.class);
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

	public Usuario save(Usuario user) {
		if (user.getId() == null) {
			super.persist(user);
		}
		else {
			super.update(user);
		}
		return user;
	}

	public Usuario getUserByLoginAndPasswdAndProfile(String login, String passwd, UserProfile profile) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select u ");
		sql.append(" from User u");
		sql.append(" where u.login = :login");
		sql.append("   and u.password = :passwd");
		sql.append("   and u.isActive = :active");
		sql.append("   and u.profile = :profile");

		TypedQuery<Usuario> query = em.createQuery(sql.toString(), Usuario.class);
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

	public List<Usuario> getUserByLogin(String login) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select u ");
		sql.append(" from User u");
		sql.append(" where u.login = :login");
		sql.append("   and u.isActive = :active");

		TypedQuery<Usuario> query = em.createQuery(sql.toString(), Usuario.class);
		query.setParameter("login", login);
		query.setParameter("active", Boolean.TRUE);

		try {
			return query.getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public Usuario findById(Long id) {
		return super.find(Usuario.class, id);
	}

	public List<Usuario> carregarUsuarios() {

		String sql = " select u from Usuario u order by u.nome	";
		try {
			return em.createQuery(sql, Usuario.class).getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public Usuario findByLogin(String login) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select u                       ");
		sql.append(" from User u 					");
		sql.append(" where u.login = :login		    ");
		sql.append(" order by u.name     			");

		try {
			return em.createQuery(sql.toString(), Usuario.class).setParameter("login", login).getSingleResult();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Usuario> carregarApostadores() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select u from Usuario u 				");
		sql.append(" where u.aposta = true 					");
		sql.append(" and u.perfil = 'USER' 					");
		sql.append(" and u.ativo = true 					");
		sql.append(" order by u.dataHoraAposta desc			");

		try {
			return em.createQuery(sql.toString(), Usuario.class).setMaxResults(5).getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Usuario> carregarFaltamApostar() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select u from Usuario u 	");
		sql.append(" where u.aposta = false 	");
		sql.append(" and u.perfil = 'USER' 		");
		sql.append(" and u.ativo = true 		");
		sql.append(" order by rand()			");

		try {
			return em.createQuery(sql.toString(), Usuario.class).setMaxResults(5).getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	public List<Usuario> carregarParticipantes() {

		StringBuilder sql = new StringBuilder();
		sql.append(" select u from Usuario u 				");
		sql.append(" where u.perfil = 'USER' 				");
		//sql.append(" and u.pagamento = true 				");
		//sql.append(" and u.aposta = true 					");
		sql.append(" and u.ativo = true 					");
		sql.append(" order by u.dataHoraPgto desc, u.nome	");

		try {
			return em.createQuery(sql.toString(), Usuario.class).getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public List<Usuario> carregarParticipantesAtivosHome() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select u from Usuario u 				");
		sql.append(" where u.perfil = 'USER' 				");
		sql.append(" and u.pagamento = true 				");
		sql.append(" and u.aposta = true 					");
		sql.append(" and u.ativo = true 					");
		sql.append(" order by u.dataHoraPgto desc			");
		
		try {
			return em.createQuery(sql.toString(), Usuario.class).setMaxResults(5).getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public List<Usuario> carregarParticipantesAtivos() {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select u from Usuario u 	");
		sql.append(" where u.perfil = 'USER' 	");
		sql.append(" and u.pagamento = true 	");
		sql.append(" and u.aposta = true 		");
		sql.append(" and u.ativo = true 		");
		sql.append(" order by u.nome			");
		
		try {
			return em.createQuery(sql.toString(), Usuario.class).getResultList();
		}
		catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<HomeDepoisPlacarExato> carregarPlacarExato() {

		List<HomeDepoisPlacarExato> listaPlacarExato = new ArrayList<HomeDepoisPlacarExato>();

		StringBuilder sql = new StringBuilder();
		sql.append(" select a.idusuario, count(*) as total from Aposta a ");
		sql.append(" where a.pontuacao = 5 ");
		sql.append(" group by a.idusuario  ");
		sql.append(" order by total desc ");

		try {
			Query query = em.createNativeQuery(sql.toString()).setMaxResults(5);

			List<Object[]> objects = query.getResultList();
			objects.forEach(o -> {
				HomeDepoisPlacarExato placarExato = new HomeDepoisPlacarExato();
				placarExato.setUsuario(findById(getLongValue(o, 1)));
				placarExato.setQuantidade(getIntegerValue(o, 2));
				listaPlacarExato.add(placarExato);
			});
		}
		catch (Exception e) {}
		return listaPlacarExato;
	}

	public List<Usuario> carregarNenhumPlacar() {

		List<Usuario> listaNenhumPlacar = new ArrayList<Usuario>();

		List<Usuario> participantes = carregarParticipantes();
		int qntd = 0;
		for (Usuario usuario : participantes) {

			StringBuilder sql = new StringBuilder();
			sql.append(" select a from Aposta a ");
			sql.append(" where a.pontuacao = 5 ");
			sql.append(" and a.usuario.id = :idusuario ");

			try {
				TypedQuery<Aposta> query = em.createQuery(sql.toString(), Aposta.class);
				query.setParameter("idusuario", usuario.getId());

				if (query.getResultList() != null && query.getResultList().isEmpty()) {
					listaNenhumPlacar.add(usuario);
					qntd++;
				}
				if (qntd==5) {
					break;
				}
			}
			catch (Exception e) {
				return null;
			}
		}
		return listaNenhumPlacar;
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> carregarColocado() {

		List<Usuario> listaColocado = new ArrayList<Usuario>();

		StringBuilder sql = new StringBuilder();
		sql.append(" select a.idusuario from aposta_colocacao a ");
		sql.append(" where a.pontoscampeao = "+ Constants.APOSTA_CAMPEAO +"  ");
		sql.append(" and a.pontosvice = "+ Constants.APOSTA_VICE +" ");
		sql.append(" and a.pontosterceiro = "+ Constants.APOSTA_TERCEIRO +" ");
		sql.append(" and a.pontosquarto = "+ Constants.APOSTA_QUARTO +" ");
		sql.append(" group by a.idusuario   ");

		try {
			Query query = em.createNativeQuery(sql.toString()).setMaxResults(5);

			List<Integer> objects = query.getResultList();
			objects.forEach(id -> {
				listaColocado.add(findById(id.longValue()));
			});
		}
		catch (Exception e) {
			return null;
		}
		return listaColocado;
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> carregarNenhumColocado() {

		List<Usuario> listaColocado = new ArrayList<Usuario>();

		StringBuilder sql = new StringBuilder();
		sql.append(" select a.idusuario from aposta_colocacao a ");
		sql.append(" where a.pontoscampeao = 0  ");
		sql.append(" and a.pontosvice = 0 ");
		sql.append(" and a.pontosterceiro = 0 ");
		sql.append(" and a.pontosquarto = 0 ");
		sql.append(" group by a.idusuario   ");

		try {
			Query query = em.createNativeQuery(sql.toString()).setMaxResults(5);

			List<Integer> objects = query.getResultList();
			objects.forEach(id -> {
				listaColocado.add(findById(id.longValue()));
			});
		}
		catch (Exception e) {
			return null;
		}
		return listaColocado;
	}

}
