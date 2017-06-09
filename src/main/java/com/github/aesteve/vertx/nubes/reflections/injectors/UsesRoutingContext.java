package com.github.aesteve.vertx.nubes.reflections.injectors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Defines if the response is async or not. This annotation is useful if the
 * response should be written in an async manner, but
 * {@link io.vertx.ext.web.RoutingContext} is not on the method parameters. This annotation should be 
 * 
 * @author rpax
 *
 */
public @interface UsesRoutingContext {
}
