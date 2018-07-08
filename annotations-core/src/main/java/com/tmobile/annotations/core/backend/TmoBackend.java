package com.tmobile.annotations.core.backend;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 
 * <p>
 * A RspBackend meta-annotation contains the information of Dao's and the
 * backends invoked.
 *
 *
 *
 *
 * @author Chandan Bharadwaj (schanda9)
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface TmoBackend {

	/**
	 * Holds the backend name.
	 * 
	 * <p>
	 * It is case-sensitive
	 * 
	 * @return the backend name
	 */
	String backend();

	/**
	 * Holds the backend method name.
	 * 
	 * <p>
	 * It is case-sensitive
	 * 
	 * @return the backend method name
	 */
	String backendOperation();
}