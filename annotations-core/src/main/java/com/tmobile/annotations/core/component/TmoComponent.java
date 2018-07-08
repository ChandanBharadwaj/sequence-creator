package com.tmobile.annotations.core.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TmoComponent annotation is used on methods in managers implementations.
 * 
 * <p>
 * 
 *
 * @author Chandan Bharadwaj(schanda9)
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface TmoComponent {

	/**
	 * Holds the list of {@code @TmoComponentFlow}.
	 * <p>
	 * The {@code @TmoComponentFlow}s should be in an order in which they where
	 * invoked.
	 * 
	 * @return {@code @TmoComponentFlow[]}
	 */
	TmoComponentFlow[] componentFlows() default {};
}
