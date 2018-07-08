package com.sai.manager;

import com.tmobile.annotations.core.component.TmoComponent;
import com.tmobile.annotations.core.component.TmoComponentFlow;

public class SampleManager1 {

	@TmoComponent(componentFlows={
			@TmoComponentFlow(component="SampleManager1",componentOperation="method2"),
			@TmoComponentFlow(component="SampleManager2",componentOperation="method1"),
			@TmoComponentFlow(component="SampleDao1",componentOperation="method2")
			})
	public void method1(){
		
	}
	
	@TmoComponent(componentFlows={
			@TmoComponentFlow(component="SampleManager2",componentOperation="method2")
			})
	public void method2(){
		
	}
	
	@TmoComponent(componentFlows={})
	public void method3(){
		
	}
}
