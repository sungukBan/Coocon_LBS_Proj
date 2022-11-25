package com.coocon.lbs.entity;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;


public class EntityTxCode {

	public String TX_CODE        = "";
	public int    SS_MAX_RUN_CNT =  0;
	public int    SS_NOW_RUN_CNT =  0;
	
	public String MSG_GROUP_CODE = "";
	public String MSG_SECT_START = "";
	public String MSG_SECT_END   = "";
	
	public String CREATED_DTM     = "";
	public String REFRESHED_DTM   = "";

	public EntityTxCode(){
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		//sb.append("----------------------------------------------\n");
		sb.append("TX_CODE= ["        + TX_CODE         + "]..");
		sb.append("SS_MAX_RUN_CNT= [" + SS_MAX_RUN_CNT  + "]..");
		sb.append("SS_NOW_RUN_CNT= [" + SS_NOW_RUN_CNT  + "]..");
		sb.append("MSG_GROUP_CODE= [" + MSG_GROUP_CODE  + "]..");
		sb.append("MSG_SECT_START= [" + MSG_SECT_START  + "]..");
		sb.append("MSG_SECT_END= ["   + MSG_SECT_END    + "]..");
		sb.append("CREATED_DTM= ["    + CREATED_DTM     + "]..");
		sb.append("REFRESHED_DTM= ["  + REFRESHED_DTM   + "]..");
		//sb.append("----------------------------------------------\n");

		return sb.toString();
	}

	public void finalize(){
		try {
	        TX_CODE         = "";
	        SS_MAX_RUN_CNT  =  0;
	        SS_NOW_RUN_CNT  =  0;
	        MSG_GROUP_CODE  = "";
	        MSG_SECT_START  = "";
	        MSG_SECT_END    = "";
	        CREATED_DTM     = "";
	        REFRESHED_DTM   = "";

		} catch(Exception e){
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}
	}
	
}
