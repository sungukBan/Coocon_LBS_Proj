package com.coocon.lbs.entity;

public class EntityMsgBizOpen {

	public String  sts_gb   ; // C  1   ������±��� 
	public int     tot_cnt  ; // N  3   �Ѱ����     
	public int     nw_cnt   ; // N  3   ���� �����  
	public String  filler   ; // C  43  �����ʵ�     
 
    public EntityMsgBizOpen() {
        
    	sts_gb   = " "; // C  1   ������±���   
    	tot_cnt  =   0; // N  3   �Ѱ����       
    	nw_cnt   =   0; // N  3   ���� �����    
    	filler   = " "; // C  43  �����ʵ�       
 
    }        
    
    public String toString() {
        
        return new StringBuffer()
	        .append("-----------------------------[CommonMsgEntity]---------------------------\n" )
	        .append("C  1   ������±��� = [" + sts_gb    +             "]\n" )  
	        .append("N  3   �Ѱ����     = [" + tot_cnt   +             "]\n" )  
	        .append("N  3   ���� �����  = [" + nw_cnt    +             "]\n" )  
	        .append("C  43  �����ʵ�     = [" + filler    +             "]\n" )  
	        .toString();
    }

}