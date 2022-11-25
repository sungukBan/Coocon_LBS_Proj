package com.coocon.lbs.msg;

import java.io.*;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class MsgCommon {

	public int iMsgLen = 100;
	
	public byte [] tot_len   ; // 6  N  ����        
	public byte [] trn_cd1   ; // 4  C  �ĺ��ڵ�1   
	public byte [] trn_cd2   ; // 8  C  �ĺ��ڵ�2   
	public byte [] send_flag ; // 1  C  �۽���FLAG  
	public byte [] gw_sys_no ; // 3  N  GW�ý��۹�ȣ
	public byte [] ss_sys_no ; // 4  N  SS�ý��۹�ȣ
	public byte [] tx_type   ; // 4  C  �����ڵ�    
	public byte [] tx_cd     ; // 4  C  ��������    
	public byte [] send_dt   ; // 8  C  ��������    
	public byte [] send_tm   ; // 6  C  ���۽ð�    
	public byte [] resp_cd   ; // 8  C  �����ڵ�    
	public byte [] prod_key  ; // 20 C  ��ǰ������ȣ
	public byte [] tr_seq    ; // 7  C  �ŷ�������ȣ
	public byte [] group_gb  ; // 2  N  �׷� ����   
	public byte [] sect_start; // 4  N  �������۹�ȣ
	public byte [] sect_end  ; // 4  N  ���������ȣ
	public byte [] filler    ; // 7  C  �����ʵ� 
	
    public MsgCommon() {
        
    	tot_len    = new byte[ 6]; // 6  N  ����        
    	trn_cd1    = new byte[ 4]; // 4  C  �ĺ��ڵ�1   
    	trn_cd2    = new byte[ 8]; // 8  C  �ĺ��ڵ�2   
    	send_flag  = new byte[ 1]; // 1  C  �۽���FLAG  
    	gw_sys_no  = new byte[ 3]; // 3  N  GW�ý��۹�ȣ
    	ss_sys_no  = new byte[ 4]; // 4  N  SS�ý��۹�ȣ
    	tx_type    = new byte[ 4]; // 4  C  �����ڵ�    
    	tx_cd      = new byte[ 4]; // 4  C  ��������    
    	send_dt    = new byte[ 8]; // 8  C  ��������    
    	send_tm    = new byte[ 6]; // 6  C  ���۽ð�    
    	resp_cd    = new byte[ 8]; // 8  C  �����ڵ�    
    	prod_key   = new byte[20]; // 20 C  ��ǰ������ȣ
    	tr_seq     = new byte[ 7]; // 7  C  �ŷ�������ȣ
    	group_gb   = new byte[ 2]; // 2  N  �׷� ����   
    	sect_start = new byte[ 4]; // 4  N  �������۹�ȣ
    	sect_end   = new byte[ 4]; // 4  N  ���������ȣ
    	filler     = new byte[ 7]; // 7  C  �����ʵ�                                
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

        entity.tot_len    = Integer.parseInt(new String(tot_len    ).trim());   // 6  N  ����
        entity.trn_cd1    =                  new String(trn_cd1    );           // 4  C  �ĺ��ڵ�1
        entity.trn_cd2    =                  new String(trn_cd2    );           // 8  C  �ĺ��ڵ�2
        entity.send_flag  =                  new String(send_flag  );           // 1  C  �۽���FLAG
        entity.gw_sys_no  =                  new String(gw_sys_no  );           // 3  N  GW�ý��۹�ȣ
        entity.ss_sys_no  =                  new String(ss_sys_no  );           // 4  N  SS�ý��۹�ȣ
        entity.tx_type    =                  new String(tx_type    );           // 4  C  �����ڵ�
        entity.tx_cd      =                  new String(tx_cd      );           // 4  C  ��������
        entity.send_dt    =                  new String(send_dt    );           // 8  C  ��������
        entity.send_tm    =                  new String(send_tm    );           // 6  C  ���۽ð�
        entity.resp_cd    =                  new String(resp_cd    );           // 8  C  �����ڵ�
        entity.prod_key   =                  new String(prod_key   );           // 20 C  ��ǰ������ȣ
        entity.tr_seq     =                  new String(tr_seq     );           // 7  C  �ŷ�������ȣ
        entity.group_gb   =                  new String(group_gb   );   		// 2  N  �׷� ����
        entity.sect_start = 				 new String(sect_start );   		// 4  N  �������۹�ȣ
        entity.sect_end   =                  new String(sect_end   );   		// 4  N  ���������ȣ
        entity.filler     =                  new String(filler     );          	// 7  C  �����ʵ� 
    }  
    
    public String toString() {
        
        return new StringBuffer()
            .append("-----------------------------[MsgCommon]---------------------------\n" )
            .append("6  N  ����          = ["+tot_len   .length      +"][" + new String(tot_len   )+"]\n" )  
            .append("4  C  �ĺ��ڵ�1   = ["+trn_cd1   .length      +"][" + new String(trn_cd1   )+"]\n" )  
            .append("8  C  �ĺ��ڵ�2   = ["+trn_cd2   .length      +"][" + new String(trn_cd2   )+"]\n" )  
            .append("1  C  �۽���FLAG = ["+send_flag .length      +"][" + new String(send_flag )+"]\n" )  
            .append("3  N  GW�ý��۹�ȣ = ["+gw_sys_no .length      +"][" + new String(gw_sys_no )+"]\n" )  
            .append("4  N  SS�ý��۹�ȣ = ["+ss_sys_no .length      +"][" + new String(ss_sys_no )+"]\n" )  
            .append("4  C  �����ڵ�      = ["+tx_type   .length      +"][" + new String(tx_type   )+"]\n" )  
            .append("4  C  ��������      = ["+tx_cd     .length      +"][" + new String(tx_cd     )+"]\n" )  
            .append("8  C  ��������      = ["+send_dt   .length      +"][" + new String(send_dt   )+"]\n" )  
            .append("6  C  ���۽ð�      = ["+send_tm   .length      +"][" + new String(send_tm   )+"]\n" )  
            .append("8  C  �����ڵ�      = ["+resp_cd   .length      +"][" + new String(resp_cd   )+"]\n" )  
            .append("20 C  ��ǰ������ȣ  = ["+prod_key  .length      +"][" + new String(prod_key  )+"]\n" )  
            .append("7  C  �ŷ�������ȣ  = ["+tr_seq    .length      +"][" + new String(tr_seq    )+"]\n" )  
            .append("2  N  �׷� ����     = ["+group_gb  .length      +"][" + new String(group_gb  )+"]\n" )  
            .append("4  N  �������۹�ȣ  = ["+sect_start.length      +"][" + new String(sect_start)+"]\n" )  
            .append("4  N  ���������ȣ  = ["+sect_end  .length      +"][" + new String(sect_end  )+"]\n" )  
            .append("7  C  �����ʵ�      = ["+filler    .length      +"][" + new String(filler    )+"]\n" )  
            .toString();
        
    }

}