package com.coocon.lbs.main;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.handler.GatewayRecvHandler;
import com.coocon.lbs.handler.SSRecvHandler;
import com.coocon.lbs.monitor.MemoryCheckThread;
import com.coocon.lbs.monitor.SSAgentTimeOutMonitorThread;
import com.coocon.lbs.monitor.SSJobListMonitorThread;
import com.coocon.lbs.net.gw.LbsReceiverFromGWServer;
import com.coocon.lbs.net.gw.LbsReceiverFromGWServerSSL;
import com.coocon.lbs.net.gw.LbsSenderToGWServer;
import com.coocon.lbs.net.gw.LbsSenderToGWServerSSL;
import com.coocon.lbs.net.ss.LbsReceiverFromSSServer;
import com.coocon.lbs.net.ss.LbsReceiverFromSSServerSSL;
import com.coocon.lbs.net.ss.LbsSenderToSSServer;
import com.coocon.lbs.net.ss.LbsSenderToSSServerSSL;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class LbsMainApp {
	
	private static boolean IS_SSL_USE = false;
	
	public static void main(String[] args) {
		if ( args == null || args.length < 1 ) {
    		System.out.println("Argument�� �����մϴ�.");
    		return;
    	}
    	UtilConfig.strFileName = args[0];
    	LbsMainApp mainApp = new LbsMainApp();
    	mainApp.start();        
    }
	
	public void start() {
		
		try {
			System.out.println("MASTER ���� �׽�Ʈ");

			//-----------------------------------------
			//--001. ȯ�漳��
			//-----------------------------------------
			init();
			
			//-----------------------------------------
			//--001. �޸� üũ ������ �⵿
			//-----------------------------------------
			new MemoryCheckThread().start();
			new SSJobListMonitorThread().start();
			new SSAgentTimeOutMonitorThread().start();
			
			//-----------------------------------------
			//--002. GW --> LBS ���� �������� �⵿
			//--003. GW <-- LBS �۽� �������� �⵿
			//-----------------------------------------
			//--GW Asynch ��� - SSL - PLAIN
			if( UtilConfig.getValue(ConstConfig.SOCK_PLAIN_SSL_GW).equalsIgnoreCase(Constant.SOCK_TYPE_SSL) ) {
				new LbsReceiverFromGWServerSSL().start();
				new LbsSenderToGWServerSSL().start();
			} else {
				new LbsReceiverFromGWServer().start();
				new LbsSenderToGWServer().start();
			}
			new GatewayRecvHandler().start();
						
			
			//-----------------------------------------
			//--004. SS --> LBS ���� �������� �⵿
			//--005. SS <-- LBS �۽� �������� �⵿
			//-----------------------------------------
			//--SS Asynch ��� - SSL - PLAIN
			if( UtilConfig.getValue(ConstConfig.SOCK_PLAIN_SSL_SS).equalsIgnoreCase(Constant.SOCK_TYPE_SSL) ) {
				new LbsReceiverFromSSServerSSL().start();
				new LbsSenderToSSServerSSL().start();
			} else {
				new LbsReceiverFromSSServer().start();
				new LbsSenderToSSServer().start();
			}
			new SSRecvHandler().start();
			
		} catch(Exception e) {
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}
	}

	private void init() {

		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "#####################################################################");
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "SSL_USE_YN          =["+UtilConfig.getValue(ConstConfig.SSL_USE_YN)+"]");
		
		if(   UtilConfig.getValue(ConstConfig.SSL_USE_YN) != null 
		   && UtilConfig.getValue(ConstConfig.SSL_USE_YN).equalsIgnoreCase("Y")) {
			IS_SSL_USE = true;
		}

		//-----------------------------------------
		//--001. SSL ��弳��
		//-----------------------------------------
		if( IS_SSL_USE == true) {

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "SSL_KEYSTORE        =["+UtilConfig.getValue(ConstConfig.SSL_KEYSTORE)+"]");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "SSL_KEYSTOREPASSWORD=["+UtilConfig.getValue(ConstConfig.SSL_KEYSTOREPASSWORD)+"]");
				
			System.setProperty("javax.net.ssl.keyStore"        , UtilConfig.getValue(ConstConfig.SSL_KEYSTORE        ));
	        System.setProperty("javax.net.ssl.keyStorePassword", UtilConfig.getValue(ConstConfig.SSL_KEYSTOREPASSWORD));

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "SOCK_TYPE_GW        =["+UtilConfig.getValue(ConstConfig.SOCK_PLAIN_SSL_GW)+"]");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "SOCK_TYPE_SS        =["+UtilConfig.getValue(ConstConfig.SOCK_PLAIN_SSL_SS)+"]");
		}

		//-----------------------------------------
		//--002. �α� debug ��弳��
		//-----------------------------------------
        UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "DEBUG_MODE=["+UtilConfig.getValue(ConstConfig.DEBUG_MODE)+"]");
		if(UtilConfig.getValue(ConstConfig.DEBUG_MODE).equalsIgnoreCase("FALSE")) {
			UtilLogger.setMode(false);
		}
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "LOG_JUNMUN_10MIN=["+UtilConfig.getValue(ConstConfig.LOG_JUNMUN_10MIN)+"]");
		if(UtilConfig.getValue(ConstConfig.LOG_JUNMUN_10MIN).equalsIgnoreCase("TRUE")) {
			UtilLogger.setModeJunmun10min(true);
		}
		
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "#####################################################################");
		
	}
}
