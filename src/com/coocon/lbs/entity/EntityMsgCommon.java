package com.coocon.lbs.entity;

public class EntityMsgCommon {

	public int     tot_len    ; // 6  N  ����        
	public String  trn_cd1    ; // 4  C  �ĺ��ڵ�1   
	public String  trn_cd2    ; // 8  C  �ĺ��ڵ�2   
	public String  send_flag  ; // 1  C  �۽���FLAG  
	public String  gw_sys_no  ; // 3  N  GW�ý��۹�ȣ
	public String  ss_sys_no  ; // 4  N  SS�ý��۹�ȣ
	public String  tx_type    ; // 4  C  �����ڵ�    
	public String  tx_cd      ; // 4  C  ��������    
	public String  send_dt    ; // 8  C  ��������    
	public String  send_tm    ; // 6  C  ���۽ð�    
	public String  resp_cd    ; // 8  C  �����ڵ�    
	public String  prod_key   ; // 20 C  ��ǰ������ȣ
	public String  tr_seq     ; // 7  C  �ŷ�������ȣ
	public String  group_gb   ; // 2  N  �׷� ����   
	public String  sect_start ; // 4  N  �������۹�ȣ
	public String  sect_end   ; // 4  N  ���������ȣ
	public String  filler     ; // 7  C  �����ʵ�    

    public EntityMsgCommon() {
        
    	tot_len    =   0; // 6  N  ����        
    	trn_cd1    = " "; // 4  C  �ĺ��ڵ�1   
    	trn_cd2    = " "; // 8  C  �ĺ��ڵ�2   
    	send_flag  = " "; // 1  C  �۽���FLAG  
    	gw_sys_no  = " "; // 3  N  GW�ý��۹�ȣ
    	ss_sys_no  = " "; // 4  N  SS�ý��۹�ȣ
    	tx_type    = " "; // 4  C  �����ڵ�    
    	tx_cd      = " "; // 4  C  ��������    
    	send_dt    = " "; // 8  C  ��������    
    	send_tm    = " "; // 6  C  ���۽ð�    
    	resp_cd    = " "; // 8  C  �����ڵ�    
    	prod_key   = " "; // 20 C  ��ǰ������ȣ
    	tr_seq     = " "; // 7  C  �ŷ�������ȣ
    	group_gb   = " "; // 2  N  �׷� ����   
    	sect_start = " "; // 4  N  �������۹�ȣ
    	sect_end   = " "; // 4  N  ���������ȣ
    	filler     = " "; // 7  C  �����ʵ�    
    }        
    
    public String toString() {
        
        return new StringBuffer()
	        .append("-----------------------------[CommonMsgEntity]---------------------------\n" )
	        .append("6  N  ����          = [" + tot_len      +             "]\n" )  
	        .append("4  C  �ĺ��ڵ�1   = [" + trn_cd1      +             "]\n" )  
	        .append("8  C  �ĺ��ڵ�2   = [" + trn_cd2      +             "]\n" )  
	        .append("1  C  �۽���FLAG = [" + send_flag    +             "]\n" )  
	        .append("3  N  GW�ý��۹�ȣ = [" + gw_sys_no    +             "]\n" )  
	        .append("4  N  SS�ý��۹�ȣ = [" + ss_sys_no    +             "]\n" )  
	        .append("4  C  �����ڵ�      = [" + tx_type      +             "]\n" )  
	        .append("4  C  ��������      = [" + tx_cd        +             "]\n" )  
	        .append("8  C  ��������      = [" + send_dt      +             "]\n" )  
	        .append("6  C  ���۽ð�      = [" + send_tm      +             "]\n" )  
	        .append("8  C  �����ڵ�      = [" + resp_cd      +             "]\n" )  
	        .append("20 C  ��ǰ������ȣ  = [" + prod_key     +             "]\n" )  
	        .append("7  C  �ŷ�������ȣ  = [" + tr_seq       +             "]\n" )  
	        .append("2  N  �׷� ����     = [" + group_gb     +             "]\n" )  
	        .append("4  N  �������۹�ȣ  = [" + sect_start   +             "]\n" )  
	        .append("4  N  ���������ȣ  = [" + sect_end     +             "]\n" )  
	        .append("7  C  �����ʵ�      = [" + filler       +             "]\n" )  
	        .toString();
    }

}