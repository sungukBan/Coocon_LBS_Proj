package com.coocon.lbs.entity;

public class EntitySSBank {

	public String SECT_NO  = "";
	public String SECT_STS = "";
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------\n");
		sb.append("SECT_NO          =["+SECT_NO          +"]\n");
		sb.append("SECT_STS         =["+SECT_STS         +"]\n");
		return sb.toString();
	}
	
}
