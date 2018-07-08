package com.sai.manager.dao;

import com.tmobile.annotations.core.backend.TmoBackend;

public class SampleDao1 {

	@TmoBackend(backend = "Backend1", backendOperation = "m1")
	public void method1() {

	}
	@TmoBackend(backend = "Backend2", backendOperation = "m2")
	public void method2() {

	}
}
