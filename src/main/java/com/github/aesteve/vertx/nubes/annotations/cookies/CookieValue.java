package com.github.aesteve.vertx.nubes.annotations.cookies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CookieValue {
  String value();

  boolean mandatory() default false;
}
