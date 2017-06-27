package com.c2point.tools.utils;

public class IncrementalInt {
	private int value = 1; 
	
	public void increment() { ++value;      }
	public int  get()       { return value; }
	public String toString() { return Integer.toString( value ); }

}
