package com.coocon.lbs.agent;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.queue.QueueHandler;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;
import com.coocon.lbs.util.sock.IUtilSocket;

public class GatewayAgent {

	public String GW_SYS_NO      = "";
	public int SOCKET_STS        = Constant.SOCK_DISCONNECTED;
	
	public QueueHandler qHandler = null;
	public IUtilSocket utilSocket = null;
	public String CREATED_DTM    = "";
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("------------[EntityGatewayAgent]------------\n");
		sb.append("GW_SYS_NO         =[" + GW_SYS_NO        +"]\n");
		sb.append("SOCKET_STS        =[" + SOCKET_STS       +"]\n");
		return sb.toString();
	}

	public void finalize(){
		try {
			GW_SYS_NO   = "";
			SOCKET_STS  = Constant.SOCK_DISCONNECTED;
			CREATED_DTM = "";
			
			if(qHandler != null) {
				qHandler.finalize();
				qHandler 	= null;
			}
			if(utilSocket != null) {
				utilSocket.finalize();
				utilSocket = null;
			}
		} catch(Exception e){
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}
	}

}
