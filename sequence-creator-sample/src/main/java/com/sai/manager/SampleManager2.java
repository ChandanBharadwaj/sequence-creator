package com.sai.manager;

import com.tmobile.annotations.core.component.TmoComponent;
import com.tmobile.annotations.core.component.TmoComponentFlow;

public class SampleManager2 {

	@TmoComponent(componentFlows={
			@TmoComponentFlow(component="SampleManager3",componentOperation="method1")
			})
	public void method1(){
		
	}
	@TmoComponent(componentFlows={})
	public void method2(){
		
	}
}
