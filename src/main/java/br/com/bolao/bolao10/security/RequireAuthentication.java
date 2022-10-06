package br.com.bolao.bolao10.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.bolao.bolao10.domain.enums.UserProfile;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuthentication {
	UserProfile[] value() default UserProfile.USER;
}
