package com.coocon.lbs.entity;

public class EntityMsgCommon {

	public int     tot_len    ; // 6  N  길이        
	public String  trn_cd1    ; // 4  C  식별코드1   
	public String  trn_cd2    ; // 8  C  식별코드2   
	public String  send_flag  ; // 1  C  송신자FLAG  
	public String  gw_sys_no  ; // 3  N  GW시스템번호
	public String  ss_sys_no  ; // 4  N  SS시스템번호
	public String  tx_type    ; // 4  C  전문코드    
	public String  tx_cd      ; // 4  C  업무구분    
	public String  send_dt    ; // 8  C  전송일자    
	public String  send_tm    ; // 6  C  전송시간    
	public String  resp_cd    ; // 8  C  응답코드    
	public String  prod_key   ; // 20 C  제품고유번호
	public String  tr_seq     ; // 7  C  거래고유번호
	public String  group_gb   ; // 2  N  그룹 구분   
	public String  sect_start ; // 4  N  구간시작번호
	public String  sect_end   ; // 4  N  구간종료번호
	public String  filler     ; // 7  C  예비필드    

    public EntityMsgCommon() {
        
    	tot_len    =   0; // 6  N  길이        
    	trn_cd1    = " "; // 4  C  식별코드1   
    	trn_cd2    = " "; // 8  C  식별코드2   
    	send_flag  = " "; // 1  C  송신자FLAG  
    	gw_sys_no  = " "; // 3  N  GW시스템번호
    	ss_sys_no  = " "; // 4  N  SS시스템번호
    	tx_type    = " "; // 4  C  전문코드    
    	tx_cd      = " "; // 4  C  업무구분    
    	send_dt    = " "; // 8  C  전송일자    
    	send_tm    = " "; // 6  C  전송시간    
    	resp_cd    = " "; // 8  C  응답코드    
    	prod_key   = " "; // 20 C  제품고유번호
    	tr_seq     = " "; // 7  C  거래고유번호
    	group_gb   = " "; // 2  N  그룹 구분   
    	sect_start = " "; // 4  N  구간시작번호
    	sect_end   = " "; // 4  N  구간종료번호
    	filler     = " "; // 7  C  예비필드    
    }        
    
    public String toString() {
        
        return new StringBuffer()
	        .append("-----------------------------[CommonMsgEntity]---------------------------\n" )
	        .append("6  N  길이          = [" + tot_len      +             "]\n" )  
	        .append("4  C  식별코드1   = [" + trn_cd1      +             "]\n" )  
	        .append("8  C  식별코드2   = [" + trn_cd2      +             "]\n" )  
	        .append("1  C  송신자FLAG = [" + send_flag    +             "]\n" )  
	        .append("3  N  GW시스템번호 = [" + gw_sys_no    +             "]\n" )  
	        .append("4  N  SS시스템번호 = [" + ss_sys_no    +             "]\n" )  
	        .append("4  C  전문코드      = [" + tx_type      +             "]\n" )  
	        .append("4  C  업무구분      = [" + tx_cd        +             "]\n" )  
	        .append("8  C  전송일자      = [" + send_dt      +             "]\n" )  
	        .append("6  C  전송시간      = [" + send_tm      +             "]\n" )  
	        .append("8  C  응답코드      = [" + resp_cd      +             "]\n" )  
	        .append("20 C  제품고유번호  = [" + prod_key     +             "]\n" )  
	        .append("7  C  거래고유번호  = [" + tr_seq       +             "]\n" )  
	        .append("2  N  그룹 구분     = [" + group_gb     +             "]\n" )  
	        .append("4  N  구간시작번호  = [" + sect_start   +             "]\n" )  
	        .append("4  N  구간종료번호  = [" + sect_end     +             "]\n" )  
	        .append("7  C  예비필드      = [" + filler       +             "]\n" )  
	        .toString();
    }

}