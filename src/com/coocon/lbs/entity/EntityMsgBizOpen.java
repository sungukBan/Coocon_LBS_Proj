package com.coocon.lbs.entity;

public class EntityMsgBizOpen {

	public String  sts_gb   ; // C  1   가용상태구분 
	public int     tot_cnt  ; // N  3   총가용수     
	public int     nw_cnt   ; // N  3   현재 실행수  
	public String  filler   ; // C  43  예비필드     
 
    public EntityMsgBizOpen() {
        
    	sts_gb   = " "; // C  1   가용상태구분   
    	tot_cnt  =   0; // N  3   총가용수       
    	nw_cnt   =   0; // N  3   현재 실행수    
    	filler   = " "; // C  43  예비필드       
 
    }        
    
    public String toString() {
        
        return new StringBuffer()
	        .append("-----------------------------[CommonMsgEntity]---------------------------\n" )
	        .append("C  1   가용상태구분 = [" + sts_gb    +             "]\n" )  
	        .append("N  3   총가용수     = [" + tot_cnt   +             "]\n" )  
	        .append("N  3   현재 실행수  = [" + nw_cnt    +             "]\n" )  
	        .append("C  43  예비필드     = [" + filler    +             "]\n" )  
	        .toString();
    }

}