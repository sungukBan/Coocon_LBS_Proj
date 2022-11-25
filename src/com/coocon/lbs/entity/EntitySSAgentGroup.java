package com.coocon.lbs.entity;

public class EntitySSAgentGroup {

	public String GROUP_NO         = "";
	public int    SECT_FROM        =  0;
	public int    SECT_TO          =  0;
	public String LAST_UPDATE_DTM  = "";
	public int    IDX_LAST_SSAGENT =  0;
	public String SS_SYS_NO        = "";
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------\n");
		sb.append("GROUP_NO         =["+GROUP_NO         +"]\n");
		sb.append("SECT_FROM        =["+SECT_FROM        +"]\n");
		sb.append("SECT_TO          =["+SECT_TO          +"]\n");
		sb.append("IDX_LAST_SSAGENT =["+IDX_LAST_SSAGENT +"]\n");
		sb.append("LAST_UPDATE_DTM  =["+LAST_UPDATE_DTM  +"]\n");
		sb.append("SS_SYS_NO        =["+SS_SYS_NO        +"]\n");
		return sb.toString();
	}
	
}
