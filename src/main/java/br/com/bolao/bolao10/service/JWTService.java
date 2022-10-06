
package br.com.bolao.bolao10.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import br.com.bolao.bolao10.domain.Usuario;
import br.com.bolao.bolao10.domain.enums.UserProfile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class JWTService {

   private static final Logger LOGGER = LoggerFactory.getLogger(JWTService.class);

   private static final int EXPIRATION_TIME_MINUTES = 1440; // 24 horas

   @Value("${custom.jwtkey}")
   private String jwtkey;


   public String createJwtToken(Usuario user) {

      Map<String, Object> dados = Maps.newHashMap();
      dados.put("id", user.getId());
      dados.put("nome", user.getNome());
      dados.put("email", user.getEmail());
      dados.put("perfil", user.getPerfil());

      Date expiresAt = Date.from(LocalDateTime.now().plusMinutes(EXPIRATION_TIME_MINUTES).atZone(ZoneId.systemDefault()).toInstant());

      try {
         JwtBuilder builder = Jwts.builder()
                  .compressWith(CompressionCodecs.DEFLATE)
                  .setClaims(dados)
                  .setExpiration(expiresAt)
                  .signWith(SignatureAlgorithm.HS256, jwtkey);
         return builder.compact();
      }
      catch (Exception e) {
         LOGGER.error("Erro GRAVE gerando token...", e);
      }

      return null;
   }

   public Usuario readJwtTokenUser(String jwtToken) throws SecurityException {
      try {
         Claims claims = Jwts.parser()
                  .setSigningKey(DatatypeConverter.parseBase64Binary(jwtkey))
                  .parseClaimsJws(jwtToken).getBody();

         Usuario user = new Usuario();
         user.setId(claims.get("id", Long.class));
         user.setNome(claims.get("nome", String.class));
         user.setEmail(claims.get("email", String.class));
         user.setPerfil(UserProfile.valueOf(claims.get("perfil", String.class)));

         return user;
      }
      catch (Exception e) {
         LOGGER.error("Erro de segurança.", e);
         throw new SecurityException("Token inválido ou expirado! INVALID_TOKEN");
      }
   }

   public Usuario readJwtToken(HttpServletRequest request) throws SecurityException {
      if (request == null) {
         throw new SecurityException("Token não informado. INVALID_TOKEN");
      }

      String token = request.getHeader("token");

      if (token == null || "".equals(token) || "null".equals(token)) {
         throw new SecurityException("Token inválido ou não informado. INVALID_TOKEN");
      }

      return readJwtTokenUser(token);
   }

   public void readJwtTokenAndValidateProfile(HttpServletRequest request, UserProfile[] profile) {
      Usuario user = readJwtToken(request);
      AtomicBoolean hasAccess = new AtomicBoolean(false);
      Lists.newArrayList(profile).forEach(p -> {
         if (user.getPerfil().equals(p)) {
            hasAccess.set(true);
         }
      });
      if (!hasAccess.get()) {
         throw new SecurityException("Desculpe, acesso não autorizado!");
      }
   }
}