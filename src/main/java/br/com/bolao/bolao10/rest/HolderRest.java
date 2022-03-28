
package br.com.bolao.bolao10.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.domain.Holder;
import br.com.bolao.bolao10.domain.User;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import br.com.bolao.bolao10.model.HolderFarma;
import br.com.bolao.bolao10.model.HolderFilter;
import br.com.bolao.bolao10.model.HolderStatus;
import br.com.bolao.bolao10.security.RequireAuthentication;
import br.com.bolao.bolao10.service.HolderService;
import br.com.bolao.bolao10.service.UserService;
import br.com.bolao.bolao10.support.Cryptography;

@RestController
public class HolderRest extends BaseRest {

   @Autowired
   private HolderService holderService;

   @Autowired
   private UserService userService;

   @PostMapping(value = "/holder/filter", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> filterHolder(@RequestBody HolderFilter filter) {

      List<HolderStatus> holders = new ArrayList<>();
      holders = holderService.filter(filter, true);
      return createObjectReturn(holders);
   }

   @PostMapping(value = "/holder/filter/inactive", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> filterHolderInactives(@RequestBody HolderFilter filter) {

      List<HolderStatus> holders = new ArrayList<>();
      holders = holderService.filter(filter, false);
      return createObjectReturn(holders);
   }

   @PostMapping(value = "/holder/parceria/farma", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> filterHolderBasic(@RequestBody HolderFilter filter) {

      List<HolderFarma> holders = new ArrayList<>();
      holders = holderService.filterBasic(filter);
      return createObjectReturn(holders);
   }

   @GetMapping(value = "/holder/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> getHolder(@PathVariable Long id) {
      return createObjectReturn(holderService.findById(id));
   }

   @PostMapping(value = "/holder", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
   @RequireAuthentication({UserProfile.ATTENDANT, UserProfile.BROKER, UserProfile.MANAGER, UserProfile.SUPERVISOR})
   public @ResponseBody ResponseEntity<?> updateHolder(@RequestBody Holder holder) {

      User user = userService.findUserByHolderId(holder.getId());
      holder.setCpfCnpj(holder.getCpfCnpj());
      holderService.update(holder);

      // Atualizo os dados de usuario de acordo com os dados de holder.
      if (user != null) {
         user.setEmail(holder.getEmail());
         user.setName(holder.getName());
         user.setSex(holder.getSex());
         user.setLogin(holder.getCpfCnpj());
         user.setPassword(Cryptography.decrypt(user.getPassword()));
         user.setHolder(holder);
         userService.save(user);
      }

      // CRIAR LOG NESSE PONTO

      return createObjectReturn(Boolean.TRUE);
   }
}
