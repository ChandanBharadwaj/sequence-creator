package com.tmobile.annotations.core.operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface TmoOperationFlow {
	String component() default "";
	String componentOperation() default "";
	String componentCondition() default "";
	String componentInput() default "";
	String componentOutput() default "";
	boolean isEndOfFlow() default false;
	String endOfFlowCondition() default "";
 }