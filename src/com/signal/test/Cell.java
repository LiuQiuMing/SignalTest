package com.signal.test;

public class Cell {
	public int _id;
	public String CI;
	public int PN;
	public String cell_name;
	
	public Cell() 
	{
	}
	
	public Cell(String CI, String cell_name,int PN) 
	{
		this.CI = CI;
		this.PN = PN;
		this.cell_name = cell_name;
	}
}