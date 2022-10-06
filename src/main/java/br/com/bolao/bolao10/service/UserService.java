
package br.com.bolao.bolao10.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.exception.Bolao10Exception;
import br.com.bolao.bolao10.repository.UserRepository;
import br.com.bolao.bolao10.support.EmailValidator;
import br.com.bolao.bolao10.support.Strings;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JWTService jwtService;

	// @Autowired
	// private ClubFlexCardService clubFlexCardService;

	// @Autowired
	// private SubscriptionRepository subscriptionRepository;

	public String login(String login, String passwd) {
		if (StringUtils.isBlank(login) || StringUtils.isBlank(passwd)) {
			throw new Bolao10Exception("Login ou Senha não informado.");
		}

		Usuario user = userRepository.getUserByLoginAndPasswd(login, passwd);

		if (user == null) {
			throw new Bolao10Exception("Login ou Senha inválido.");
		}
		return jwtService.createJwtToken(user);
	}

	@Transactional
	public String backofficeLogin(String login, String password) {

		if (StringUtils.isBlank(login) || StringUtils.isBlank(password)) {
			throw new Bolao10Exception("Login ou Senha não informado.");
		}

		//User user = userRepository.getUserByLoginAndPasswd(login, password, true);
		Usuario user = new Usuario();
		user.setId(123L);
		user.setNome("Teste");
		user.setPerfil(UserProfile.ADMIN);

		return jwtService.createJwtToken(user);
	}

	@Transactional
	public Usuario save(Usuario user) {
		String tempPASS = Strings.generateTempPassword();
		if (StringUtils.isBlank(user.getSenha())) {
			user.setSenha(tempPASS);
		}
		user.setAtivo(Boolean.TRUE);

		// Validar existencia para Atendente e Gerente
		if (UserProfile.ADMIN.equals(user.getPerfil())) {

			Usuario registeredUser = userRepository.findByLogin(user.getEmail());

			if (registeredUser != null && registeredUser.getPerfil().equals(UserProfile.USER)) {
				throw new Bolao10Exception("Usuário já cadastrado!");
			}
		}
		return userRepository.save(user);
	}

	@Transactional
	public Usuario update(Usuario user) {
		Usuario userObj = userRepository.findById(user.getId());
		userObj.setNome(user.getNome());
		userObj.setAtivo(user.getAtivo());
		userObj.setPerfil(user.getPerfil());
		userObj.setEmail(user.getEmail());
		return userRepository.save(userObj);
	}

	public Usuario findUserById(Long id) {
		return userRepository.findById(id);
	}

	/*
   private void validarPreenchimento(String login) {
      if (StringUtils.isBlank(login)) {
         throw new Bolao10Exception("Preencha seu CPF/CNPJ.");
      }
   }
   private void validarCPFCNPJValido(String login) {
      if (login.length() > 14) {
         if (!CNPJValidator.isValido(login)) {
            throw new Bolao10Exception("CNPJ não está correto.");
         }
      }
      else {
         if (!CPFValidator.isValido(login)) {
            throw new Bolao10Exception("CPF não está correto.");
         }
      }
   }

   private void validarUsuarioExistente(Usuario user) {
      if (user == null) {
         throw new Bolao10Exception("Usuário não encontrado.");
      }
   }
	 */

	@SuppressWarnings("unused")
	private void validaEmailUsuario(String email) {
		if (StringUtils.isBlank(email)) {
			throw new Bolao10Exception("E-mail não está cadastrado.");
		}
	}

	@Transactional
	public void updateMail(Long userId, String mail) {
		if (StringUtils.isBlank(mail)) {
			throw new Bolao10Exception("E-mail não informado.");
		}
		if (!EmailValidator.isValido(mail)) {
			throw new Bolao10Exception("E-mail inválido. Informe um e-mail válido.");
		}
		Usuario user = findUserById(userId);
		if (user == null)
			throw new Bolao10Exception("Usuário não encontrado.");

		user.setEmail(mail);
		userRepository.merge(user);
	}

	@Transactional
	public void updatePasswd(Long userId, String passwd) {
		if (StringUtils.isBlank(passwd)) {
			throw new Bolao10Exception("Nova senha não informada.");
		}
		if (passwd.length() < 6) {
			throw new Bolao10Exception("Senha deve ter ao menos 6 caracteres.");
		}
		Usuario user = findUserById(userId);
		user.setSenha(passwd);
		userRepository.merge(user);
	}

	@Transactional
	public void trocarSenha(Usuario user) {
		
		if (StringUtils.isBlank(user.getSenha())) {
			throw new Bolao10Exception("Senha não preenchida.");
		}
		if (StringUtils.isBlank(user.getSenhaanterior())) {
			throw new Bolao10Exception("Senha anterior não preenchida.");
		}
		if (StringUtils.isBlank(user.getConfirmarsenha())) {
			throw new Bolao10Exception("Confirmação de senha não preenchida.");
		}
		if (!user.getConfirmarsenha().equalsIgnoreCase(user.getSenha())) {
			throw new Bolao10Exception("Nova senha diferente da confirmação!");
		}
		if (user.getSenhaanterior().length() != 8) {
			throw new Bolao10Exception("Senha precisa ter 8 dígitos.");
		}
		if (user.getSenha().length() != 8) {
			throw new Bolao10Exception("Nova senha precisa ter 8 dígitos.");
		}
		Usuario userPersistente = findUserById(user.getId());
		if (userPersistente == null)
			throw new Bolao10Exception("Usuário não encontrado.");
		
		Usuario userLogado = userRepository.getUserByLoginAndPasswd(userPersistente.getEmail(), user.getSenhaanterior());
		if (userLogado == null) {
			throw new Bolao10Exception("Senha inválida.");
		}
		userPersistente.setSenha(user.getSenha());
		userRepository.save(userPersistente);
	}

}
