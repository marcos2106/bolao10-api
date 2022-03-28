
package br.com.segmedic.clubflex.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.ParamsSubscription;
import br.com.segmedic.clubflex.domain.SmsParams;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.SystemParamsService;

@RestController
@RequestMapping("/params")
public class SystemParamsRest extends BaseRest {

   @Autowired
   private SystemParamsService ssp;

   @GetMapping(value = "/obter_campos_tela", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> obterCamposTela() {
      return createObjectReturn(ssp.obterCamposTela());
   }

   @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> save(@RequestBody SmsParams params) {
      ssp.updateParam(params);
      return createObjectReturn(Boolean.TRUE);
   }

   @PostMapping(value = "/send", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> send(@RequestBody SmsParams params) {
      ssp.updateParam(params);
      // ssp.sendMessage(params);
      return createObjectReturn(Boolean.TRUE);
   }

   @GetMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getParamsSubscription() {
      return createObjectReturn(ssp.getParamsSubscription());
   }

   @PostMapping(value = "/subscription/save", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> saveParamsSubscription(@RequestBody ParamsSubscription params) {
      ssp.saveParamsSubscription(params);
      return createObjectReturn(Boolean.TRUE);
   }

}
