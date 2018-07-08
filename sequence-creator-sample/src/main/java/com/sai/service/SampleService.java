package com.sai.service;

import com.tmobile.annotations.core.operation.TmoOperation;
import com.tmobile.annotations.core.operation.TmoOperationFlow;

public class SampleService {

	@TmoOperation(operationFlows = {
			@TmoOperationFlow(component="SampleValidator",componentOperation="validateRequest"),
			@TmoOperationFlow(component="SampleManager1",componentOperation="method1"),
			@TmoOperationFlow(component="SampleDao1",componentOperation="method1"),
			})
	public void getSampleService(){
		
	}
	
	@TmoOperation(operationFlows = { @TmoOperationFlow(component="SampleManager1",componentOperation="method1") })
	public void getDummySampleService(){
		
	}
}
