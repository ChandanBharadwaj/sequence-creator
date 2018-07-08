package com.tmobile.annotations.processor;

import java.util.List;

import com.tmobile.annotations.core.component.TmoComponent;

public class TmoComponentCollection {

	List<String> componetCollection;
	List<String> daoCollection;
	
	public List<String> getComponetCollection() {
		return componetCollection;
	}
	public void setComponetCollection(List<String> componetCollection) {
		this.componetCollection = componetCollection;
	}
	public List<String> getDaoCollection() {
		return daoCollection;
	}
	public void setDaoCollection(List<String> daoCollection) {
		this.daoCollection = daoCollection;
	}
	@Override
	public String toString() {
		return "TmoComponentCollection [componetCollection=" + componetCollection + ", daoCollection=" + daoCollection
				+ "]";
	}
	
}
