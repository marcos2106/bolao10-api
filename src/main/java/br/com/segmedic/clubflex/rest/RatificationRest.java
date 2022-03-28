
package br.com.segmedic.clubflex.rest;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.User;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.model.CreateRatification;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.RatificationService;
import br.com.segmedic.clubflex.service.UserService;

@RestController
@RequestMapping("/ratification")
public class RatificationRest extends BaseRest {

   @Autowired
   private RatificationService ratificationService;

   @Autowired
   private UserService userService;

   @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> listRatification() {
      return createObjectReturn(ratificationService.listRatification());
   }

   @PutMapping(value = "/approval", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> approval(HttpServletRequest request, @RequestBody CreateRatification ratification) {
      ratificationService.approval(ratification, getUserToken(request));
      return createObjectReturn(Boolean.TRUE);
   }

   @PutMapping(value = "/deny", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> deny(HttpServletRequest request, @RequestBody CreateRatification ratification) {
      ratificationService.deny(ratification, getUserToken(request));
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/lista", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT})
   public @ResponseBody ResponseEntity<?> listRatification(HttpServletRequest request) {
      User user = userService.findUserById(getUserToken(request).getId());
      return createObjectReturn(ratificationService.listRatification(user));
   }
}
