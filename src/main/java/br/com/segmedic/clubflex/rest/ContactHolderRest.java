
package br.com.segmedic.clubflex.rest;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.ContactHolder;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.ContactHolderService;

@RestController
public class ContactHolderRest extends BaseRest {

   @Autowired
   private ContactHolderService contactHolderService;

   @PutMapping(value = "/contact-holder", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> saveConcatHolder(HttpServletRequest request, @RequestBody ContactHolder contact) {
      contact.setUser(getUserToken(request));
      return createObjectReturn(contactHolderService.save(contact));
   }

   @GetMapping(value = "/contact-holder/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getConcatHolder(HttpServletRequest request, @PathVariable Long id) {
      return createObjectReturn(contactHolderService.findById(id));
   }

   @GetMapping(value = "/contact-holder/holder/{holderId}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getConcatHolderId(HttpServletRequest request, @PathVariable Long holderId) {
      return createObjectReturn(contactHolderService.listByHolderId(holderId));
   }

}
