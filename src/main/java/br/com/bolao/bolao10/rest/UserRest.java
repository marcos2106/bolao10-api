
package br.com.bolao.bolao10.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.model.UserLoginRequest;
import br.com.bolao.bolao10.security.RequireAuthentication;
import br.com.bolao.bolao10.service.UserService;

@RestController
public class UserRest extends BaseRest {

   @Autowired
   private UserService userService;


   @PostMapping(value = "/user/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
      return createObjectReturn(userService.login(request.getLogin(), request.getPassword()));
   }
   
   @PostMapping(value = "/user/novasenha", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> novaSenha(@RequestBody UserLoginRequest usuarioRequest) {
	   return createObjectReturn(userService.novaSenha(usuarioRequest));
   }

   @GetMapping(value = "/user/data", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> getData(HttpServletRequest request) {
      return createObjectReturn(getUserToken(request));
   }

   @GetMapping(value = "/user/data/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> getData(HttpServletRequest request, @PathVariable Long id) {
      return createObjectReturn(userService.findUserById(id));
   }

   @GetMapping(value = "/user/data/complete", produces = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> getDataComplete(HttpServletRequest request) {
      return createObjectReturn(userService.findUserById(getUserToken(request).getId()));
   }

   @PostMapping(value = "/user/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ADMIN})
   public @ResponseBody ResponseEntity<?> update(@RequestBody Usuario user) {
      userService.update(user);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/user/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ADMIN})
   public @ResponseBody ResponseEntity<?> save(@RequestBody Usuario user) {
      userService.save(user);
      return createObjectReturn(Boolean.TRUE);
   }
   
   @PostMapping(value = "/user/senha", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   public @ResponseBody ResponseEntity<?> trocarSenha(@RequestBody Usuario user) {
	   userService.trocarSenha(user);
	   return createObjectReturn(Boolean.TRUE);
   }
}
