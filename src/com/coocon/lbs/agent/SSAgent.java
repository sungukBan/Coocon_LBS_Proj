package com.coocon.lbs.agent;

import java.util.Enumeration;
import java.util.Hashtable;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.entity.EntityTxCode;
import com.coocon.lbs.queue.QueueHandler;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;
import com.coocon.lbs.util.sock.IUtilSocket;

public class SSAgent {

	public String SS_SYS_NO      = "";
	public String SS_STS_CD      = "";
	public int    SS_MAX_RUN_CNT =  0;
	public int    SS_NOW_RUN_CNT =  0;
 	public int    SOCKET_STS     = Constant.SOCK_DISCONNECTED;

 	public IUtilSocket utilSocket = null;
 	public String remoteServerIp = "";
	public QueueHandler qHandler = null;
	public String CREATED_DTM    = "";
	public String REFRESHED_DTM  = "";
	public Hashtable<String, EntityMsgCommon> htJobList = new Hashtable<String, EntityMsgCommon>();
	
	//--20180227 TOMISGOOD
	public Hashtable<String, EntityTxCode> htTxCodeList = new Hashtable<String, EntityTxCode>();
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		//sb.append("----------------------------------------------\n");
		sb.append("SS_SYS_NO=["          +SS_SYS_NO          +"]..");
		sb.append("SS_STS_CD=["          +SS_STS_CD          +"]..");
		sb.append("SS_MAX_RUN_CNT=["     +SS_MAX_RUN_CNT     +"]..");
		sb.append("SS_NOW_RUN_CNT=["     +SS_NOW_RUN_CNT     +"]..");
		sb.append("SOCKET_STS=["         +SOCKET_STS         +"]..");
		sb.append("IP=["                 +remoteServerIp     +"]..");
		sb.append("CREATED_DTM=["        +CREATED_DTM        +"]..");
		sb.append("REFRESHED_DTM=["      +REFRESHED_DTM      +"]..");
		//sb.append("----------------------------------------------\n");

		return sb.toString();
	}
	
	public String toString_back(){
		StringBuffer sb = new StringBuffer();
		//sb.append("----------------------------------------------\n");
		sb.append("SS_SYS_NO=["          +SS_SYS_NO          +"]..");
		sb.append("SS_STS_CD=["          +SS_STS_CD          +"]..");
		sb.append("SS_MAX_RUN_CNT=["     +SS_MAX_RUN_CNT     +"]..");
		sb.append("SS_NOW_RUN_CNT=["     +SS_NOW_RUN_CNT     +"]..");
		sb.append("SOCKET_STS=["         +SOCKET_STS         +"]..");
		sb.append("remoteServerIp=["     +remoteServerIp     +"]..");
		sb.append("CREATED_DTM=["        +CREATED_DTM        +"]..");
		sb.append("REFRESHED_DTM=["      +REFRESHED_DTM      +"]..");
		sb.append("htTxCodeList=["       +htTxCodeList       +"]..");
		//sb.append("----------------------------------------------\n");

		return sb.toString();
	}
	
	public void finalize(){
		try {
			SS_SYS_NO   	= "";
			SS_STS_CD   	= "";
			SS_MAX_RUN_CNT 	= 0;
			SS_NOW_RUN_CNT 	= 0;
			SOCKET_STS  	= Constant.SOCK_DISCONNECTED;
			remoteServerIp  = "";
			CREATED_DTM 	= "";
			REFRESHED_DTM   = "";

			if(qHandler != null) {
				qHandler.finalize();
				qHandler 	= null;
			}
			
			if(utilSocket != null) {
				utilSocket.finalize();
				utilSocket = null;
			}
			
			if(htJobList != null){
				Enumeration<EntityMsgCommon> enumVals = htJobList.elements();
				while(enumVals.hasMoreElements()){
					EntityMsgCommon entityCommon = enumVals.nextElement();
					entityCommon = null;
				}
			}
			
			if(htTxCodeList != null){
				Enumeration<EntityTxCode> enumVals = htTxCodeList.elements();
				while(enumVals.hasMoreElements()){
					EntityTxCode txCodeAgent = enumVals.nextElement();
					txCodeAgent = null;
				}
			}
			
			
		} catch(Exception e){
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}
	}
	
}
