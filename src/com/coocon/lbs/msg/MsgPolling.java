package com.coocon.lbs.msg;

import java.io.*;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.entity.EntityMsgPolling;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class MsgPolling {

	public MsgCommon   cMsg        ;
	
	public int iMsgLen = 50;
	
	public byte [] sts_gb ; // C  1   가용상태구분
	public byte [] tot_cnt; // N  3   총가용수    
	public byte [] nw_cnt ; // N  3   현재 실행수 
	public byte [] filler ; // C  43  예비필드    

	
    public MsgPolling() {
        
    	cMsg    = new MsgCommon();
    	
    	sts_gb  = new byte[ 1]; // C  1   가용상태구분 
    	tot_cnt = new byte[ 3]; // N  3   총가용수     
    	nw_cnt  = new byte[ 3]; // N  3   현재 실행수  
    	filler  = new byte[43]; // C  43  예비필드     
    }

    public void fromByteArray(byte in[]) {
    	
    	cMsg.fromByteArray(in);
    	
    	System.arraycopy(   in,   100,   sts_gb  ,     0,  1);
    	System.arraycopy(   in,   101,   tot_cnt ,     0,  3);
    	System.arraycopy(   in,   104,   nw_cnt  ,     0,  3);
    	System.arraycopy(   in,   107,   filler  ,     0, 43);

    }

    public byte[] toByteArray() {

        ByteArrayOutputStream ret = new ByteArrayOutputStream();

        try {
        	ret.write(cMsg.toByteArray());
        	
        	ret.write(sts_gb );
        	ret.write(tot_cnt);
        	ret.write(nw_cnt );
        	ret.write(filler );

        } catch(Exception e) {
        	UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);   
        }            
    	return ret.toByteArray();
    }
    
    public void setEntity(EntityMsgPolling entity) {

        entity.sts_gb   =                  new String(sts_gb   );        // C  1   가용상태구분    
        entity.tot_cnt  = Integer.parseInt(new String(tot_cnt ).trim()); // N  3   총가용수        
        entity.nw_cnt   = Integer.parseInt(new String(nw_cnt  ).trim()); // N  3   현재 실행수     
        entity.filler   =                  new String(filler   ); 		 // C  43  예비필드        
      
    }  
    
    public String toString() {
        
        return new StringBuffer()
            .append("-----------------------------[MsgPolling]---------------------------\n" )
            .append("C  1   가용상태구분 = [" + sts_gb .length      +"][" + new String(sts_gb )+"]\n" )  
            .append("N  3   총가용수     = [" + tot_cnt.length      +"][" + new String(tot_cnt)+"]\n" )  
            .append("N  3   현재 실행수  = [" + nw_cnt .length      +"][" + new String(nw_cnt )+"]\n" )  
            .append("C  43  예비필드     = [" + filler .length      +"][" + new String(filler )+"]\n" )  
            .toString();
        
    }

}