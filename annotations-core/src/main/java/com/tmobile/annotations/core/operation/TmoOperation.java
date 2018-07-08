package com.tmobile.annotations.core.operation;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * TmoOperation annotation is used on methods in Service.operations.
 * 
 * <p>
 * operationFlow list.
 *
 * @author Chandan Bharadwaj(schanda9)
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface TmoOperation {
	TmoOperationFlow[] operationFlows();
	String operationInput() default "";
	String operationOutput() default "";
	String operationDescription() default "";
 }