package com.funnco.funnco.interfaces;

public interface LoadNetworkBack<T> {
	public void Suceess(T result);

	public void Failure(Object obj);
	
	public void otherData(String result);
}
