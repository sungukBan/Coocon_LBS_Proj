package com.coocon.lbs.msg;

import java.io.*;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class MsgCommon {

	public int iMsgLen = 100;
	
	public byte [] tot_len   ; // 6  N  길이        
	public byte [] trn_cd1   ; // 4  C  식별코드1   
	public byte [] trn_cd2   ; // 8  C  식별코드2   
	public byte [] send_flag ; // 1  C  송신자FLAG  
	public byte [] gw_sys_no ; // 3  N  GW시스템번호
	public byte [] ss_sys_no ; // 4  N  SS시스템번호
	public byte [] tx_type   ; // 4  C  전문코드    
	public byte [] tx_cd     ; // 4  C  업무구분    
	public byte [] send_dt   ; // 8  C  전송일자    
	public byte [] send_tm   ; // 6  C  전송시간    
	public byte [] resp_cd   ; // 8  C  응답코드    
	public byte [] prod_key  ; // 20 C  제품고유번호
	public byte [] tr_seq    ; // 7  C  거래고유번호
	public byte [] group_gb  ; // 2  N  그룹 구분   
	public byte [] sect_start; // 4  N  구간시작번호
	public byte [] sect_end  ; // 4  N  구간종료번호
	public byte [] filler    ; // 7  C  예비필드 
	
    public MsgCommon() {
        
    	tot_len    = new byte[ 6]; // 6  N  길이        
    	trn_cd1    = new byte[ 4]; // 4  C  식별코드1   
    	trn_cd2    = new byte[ 8]; // 8  C  식별코드2   
    	send_flag  = new byte[ 1]; // 1  C  송신자FLAG  
    	gw_sys_no  = new byte[ 3]; // 3  N  GW시스템번호
    	ss_sys_no  = new byte[ 4]; // 4  N  SS시스템번호
    	tx_type    = new byte[ 4]; // 4  C  전문코드    
    	tx_cd      = new byte[ 4]; // 4  C  업무구분    
    	send_dt    = new byte[ 8]; // 8  C  전송일자    
    	send_tm    = new byte[ 6]; // 6  C  전송시간    
    	resp_cd    = new byte[ 8]; // 8  C  응답코드    
    	prod_key   = new byte[20]; // 20 C  제품고유번호
    	tr_seq     = new byte[ 7]; // 7  C  거래고유번호
    	group_gb   = new byte[ 2]; // 2  N  그룹 구분   
    	sect_start = new byte[ 4]; // 4  N  구간시작번호
    	sect_end   = new byte[ 4]; // 4  N  구간종료번호
    	filler     = new byte[ 7]; // 7  C  예비필드                                
    }

    public void fromByteArray(byte in[]) {
    	System.arraycopy(   in,   0 ,   tot_len    ,     0,  6);
    	System.arraycopy(   in,   6 ,   trn_cd1    ,     0,  4);
    	System.arraycopy(   in,   10,   trn_cd2    ,     0,  8);
    	System.arraycopy(   in,   18,   send_flag  ,     0,  1);
    	System.arraycopy(   in,   19,   gw_sys_no  ,     0,  3);
    	System.arraycopy(   in,   22,   ss_sys_no  ,     0,  4);
    	System.arraycopy(   in,   26,   tx_type    ,     0,  4);
    	System.arraycopy(   in,   30,   tx_cd      ,     0,  4);
    	System.arraycopy(   in,   34,   send_dt    ,     0,  8);
    	System.arraycopy(   in,   42,   send_tm    ,     0,  6);
    	System.arraycopy(   in,   48,   resp_cd    ,     0,  8);
    	System.arraycopy(   in,   56,   prod_key   ,     0, 20);
    	System.arraycopy(   in,   76,   tr_seq     ,     0,  7);
    	System.arraycopy(   in,   83,   group_gb   ,     0,  2);
    	System.arraycopy(   in,   85,   sect_start ,     0,  4);
    	System.arraycopy(   in,   89,   sect_end   ,     0,  4);
    	System.arraycopy(   in,   93,   filler     ,     0,  7);
    }

    public byte[] toByteArray() {

        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        try {
        	ret.write(tot_len   );
        	ret.write(trn_cd1   );
        	ret.write(trn_cd2   );
        	ret.write(send_flag );
        	ret.write(gw_sys_no );
        	ret.write(ss_sys_no );
        	ret.write(tx_type   );
        	ret.write(tx_cd     );
        	ret.write(send_dt   );
        	ret.write(send_tm   );
        	ret.write(resp_cd   );
        	ret.write(prod_key  );
        	ret.write(tr_seq    );
        	ret.write(group_gb  );
        	ret.write(sect_start);
        	ret.write(sect_end  );
        	ret.write(filler    );
        } catch(Exception e) {
        	UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);   
        }            
    	return ret.toByteArray();
    }
    
    public void setEntity(EntityMsgCommon entity) {

        entity.tot_len    = Integer.parseInt(new String(tot_len    ).trim());   // 6  N  길이
        entity.trn_cd1    =                  new String(trn_cd1    );           // 4  C  식별코드1
        entity.trn_cd2    =                  new String(trn_cd2    );           // 8  C  식별코드2
        entity.send_flag  =                  new String(send_flag  );           // 1  C  송신자FLAG
        entity.gw_sys_no  =                  new String(gw_sys_no  );           // 3  N  GW시스템번호
        entity.ss_sys_no  =                  new String(ss_sys_no  );           // 4  N  SS시스템번호
        entity.tx_type    =                  new String(tx_type    );           // 4  C  전문코드
        entity.tx_cd      =                  new String(tx_cd      );           // 4  C  업무구분
        entity.send_dt    =                  new String(send_dt    );           // 8  C  전송일자
        entity.send_tm    =                  new String(send_tm    );           // 6  C  전송시간
        entity.resp_cd    =                  new String(resp_cd    );           // 8  C  응답코드
        entity.prod_key   =                  new String(prod_key   );           // 20 C  제품고유번호
        entity.tr_seq     =                  new String(tr_seq     );           // 7  C  거래고유번호
        entity.group_gb   =                  new String(group_gb   );   		// 2  N  그룹 구분
        entity.sect_start = 				 new String(sect_start );   		// 4  N  구간시작번호
        entity.sect_end   =                  new String(sect_end   );   		// 4  N  구간종료번호
        entity.filler     =                  new String(filler     );          	// 7  C  예비필드 
    }  
    
    public String toString() {
        
        return new StringBuffer()
            .append("-----------------------------[MsgCommon]---------------------------\n" )
            .append("6  N  길이          = ["+tot_len   .length      +"][" + new String(tot_len   )+"]\n" )  
            .append("4  C  식별코드1   = ["+trn_cd1   .length      +"][" + new String(trn_cd1   )+"]\n" )  
            .append("8  C  식별코드2   = ["+trn_cd2   .length      +"][" + new String(trn_cd2   )+"]\n" )  
            .append("1  C  송신자FLAG = ["+send_flag .length      +"][" + new String(send_flag )+"]\n" )  
            .append("3  N  GW시스템번호 = ["+gw_sys_no .length      +"][" + new String(gw_sys_no )+"]\n" )  
            .append("4  N  SS시스템번호 = ["+ss_sys_no .length      +"][" + new String(ss_sys_no )+"]\n" )  
            .append("4  C  전문코드      = ["+tx_type   .length      +"][" + new String(tx_type   )+"]\n" )  
            .append("4  C  업무구분      = ["+tx_cd     .length      +"][" + new String(tx_cd     )+"]\n" )  
            .append("8  C  전송일자      = ["+send_dt   .length      +"][" + new String(send_dt   )+"]\n" )  
            .append("6  C  전송시간      = ["+send_tm   .length      +"][" + new String(send_tm   )+"]\n" )  
            .append("8  C  응답코드      = ["+resp_cd   .length      +"][" + new String(resp_cd   )+"]\n" )  
            .append("20 C  제품고유번호  = ["+prod_key  .length      +"][" + new String(prod_key  )+"]\n" )  
            .append("7  C  거래고유번호  = ["+tr_seq    .length      +"][" + new String(tr_seq    )+"]\n" )  
            .append("2  N  그룹 구분     = ["+group_gb  .length      +"][" + new String(group_gb  )+"]\n" )  
            .append("4  N  구간시작번호  = ["+sect_start.length      +"][" + new String(sect_start)+"]\n" )  
            .append("4  N  구간종료번호  = ["+sect_end  .length      +"][" + new String(sect_end  )+"]\n" )  
            .append("7  C  예비필드      = ["+filler    .length      +"][" + new String(filler    )+"]\n" )  
            .toString();
        
    }

}