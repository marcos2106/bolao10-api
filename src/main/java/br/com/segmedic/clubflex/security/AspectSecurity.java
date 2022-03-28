package br.com.segmedic.clubflex.security;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.service.JWTService;

@Aspect
@Component
public class AspectSecurity {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(AspectSecurity.class);
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private JWTService jwtService;
	
	@Before("execution(public * *(..)) && @annotation(br.com.segmedic.clubflex.security.RequireAuthentication)")
	public void callBeforeToken(JoinPoint joinPoint) {
		//Validação de token
		Annotation[] annotations = MethodSignature.class.cast(joinPoint.getSignature()).getMethod().getAnnotations();
	    for (Annotation annotation : annotations) {
    		if(annotation instanceof RequireAuthentication) {
    			RequireAuthentication a = (RequireAuthentication) annotation;
    			jwtService.readJwtTokenAndValidateProfile(request, a.value());
    		}
	    }

	    //Audit (envia ação para fila de auditoria)
//	    try {
//	    	ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//	    	HttpServletRequest requestObj = attributes.getRequest();
//	    	//@formatter:off
//		    	Audit audit = new AuditBuilder(jwtService.readJwtToken(requestObj))
//				    			  .withUrl(requestObj.getRequestURL().toString())
//				    			  .withMethod(requestObj.getMethod())
//				    			  .withIp(requestObj.getRemoteAddr())
//				    			  .withClassMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName())
//				    			  .withArgs(Arrays.asList(joinPoint.getArgs()))
//				    			  .build();
//		    	auditService.audit(audit);
//	    	//@formatter:on
//		} catch (Exception e) {
//			LOGGER.info("Erro no audit", e);
//		}
	}
}
