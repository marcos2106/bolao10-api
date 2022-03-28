
package br.com.segmedic.clubflex.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import br.com.segmedic.clubflex.domain.Company;
import br.com.segmedic.clubflex.domain.enums.UserProfile;
import br.com.segmedic.clubflex.security.RequireAuthentication;
import br.com.segmedic.clubflex.service.CompanyService;

@RestController
public class CompanyRest extends BaseRest {

   @Autowired
   private CompanyService companyService;

   @GetMapping(value = "/company", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.MANAGER, UserProfile.SUPERVISOR, UserProfile.BROKER})
   public @ResponseBody ResponseEntity<?> listCompanies() {

      List<Company> listCompany = companyService.listAll();
      if (listCompany != null && !listCompany.isEmpty())
         listCompany.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));
      return createObjectReturn(listCompany);
   }
}
