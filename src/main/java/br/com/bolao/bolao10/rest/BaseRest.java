
package br.com.bolao.bolao10.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.model.StandardResponse;
import br.com.bolao.bolao10.service.JWTService;

@Component
public class BaseRest {

   @Autowired
   private JWTService jwtService;

   protected ResponseEntity<?> createObjectReturn(Object object) {
      return ResponseEntity.ok().body(new StandardResponse(HttpStatus.OK.value(), null, null, object));
   }

   protected Usuario getUserToken(HttpServletRequest request) {
      return jwtService.readJwtToken(request);
   }

//   protected void generateLog(Long userId, Long subscriptionId, SubscriptionLogAction action) {
//      subscriptionLogService.generateLog(userId, subscriptionId, action);
//   }
//
//   protected void generateLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action) {
//      subscriptionLogService.generateLog(userId, subscriptionId, obs, action);
//   }
//
//   protected void generateLog(Long userId, Long subscriptionId, String obs, SubscriptionLogAction action, Long idreason) {
//      subscriptionLogService.generateLog(userId, subscriptionId, obs, action, idreason);
//   }

}
