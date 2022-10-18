
package br.com.bolao.bolao10.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRest {

   @GetMapping(value = "/")
   public ResponseEntity<String> healthCheck() {
      return ResponseEntity.ok().build();
   }
}