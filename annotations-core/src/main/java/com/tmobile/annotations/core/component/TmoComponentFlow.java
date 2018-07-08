package com.tmobile.annotations.core.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TmoComponentFlow annotation is used inside TmoComponent  *
 *
 *
 *
 * @author Chandan Bharadwaj(schanda9)
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface TmoComponentFlow {
	
	String component() default "";
	String componentOperation() default "";
	String componentCondition() default "";
	String componentInput() default "";
	String componentOutput() default "";

}
