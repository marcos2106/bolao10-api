
package br.com.bolao.bolao10.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import br.com.bolao.bolao10.service.HolderService;
import br.com.bolao.bolao10.service.InvoiceService;
import br.com.bolao.bolao10.service.SubscriptionService;
import br.com.bolao.bolao10.service.UserService;

@RestController
public class SubscriptionRest extends BaseRest {

   @Autowired
   private SubscriptionService subscriptionService;

   @Autowired
   private InvoiceService invoiceService;

   @Autowired
   private UserService userService;

   @Autowired
   private HolderService holderService;

}
