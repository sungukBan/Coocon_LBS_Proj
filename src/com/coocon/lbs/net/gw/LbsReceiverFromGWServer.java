package com.coocon.lbs.net.gw;

import java.net.ServerSocket;
import java.net.Socket;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;
import com.coocon.lbs.util.sock.AUtilSocket;
import com.coocon.lbs.util.sock.IUtilSocket;
import com.coocon.lbs.util.sock.UtilSocketImpl;

public class LbsReceiverFromGWServer extends Thread {
    private ServerSocket	m_serverSocket	= null;
    private Socket			m_socket		= null;
    private static Object	mutex			= null;  
    private String          tName           = this.getClass().getSimpleName();
    
    public LbsReceiverFromGWServer() {
        try {
	        UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is starting...........");
            createBizToGWSenderServer();
        } catch(Exception e) {
            UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
        }
    }

    public void createBizToGWSenderServer() {
        try {
        	m_serverSocket = new ServerSocket( Integer.parseInt(UtilCommon.getNullToStr(UtilConfig.getValue(ConstConfig.PORT_GW_RECV), "0")) );
        } catch (Exception e) {
        	UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
        }
    }

    public void run() {
        UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " run() is starting...........");

        if(mutex == null) mutex = new Object();

        try {
            if ( m_serverSocket == null ) 
            	createBizToGWSenderServer();
        } catch(Exception e) {
        	UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
        }

        while (true) {

            try {
                synchronized(mutex) {
                    m_socket = m_serverSocket.accept();
                }
                
                //--L4 Health Check...2017.02.16
                if( AUtilSocket.isL4HealthCheck(m_socket) == true ) {
                	
                    int iMsgLen = Integer.parseInt(UtilConfig.getValue(ConstConfig.MSG_LEN_COLUMN_SIZE));
                	IUtilSocket utilSocket = new UtilSocketImpl(m_socket);
                	try {
                		utilSocket.read(iMsgLen);
                	}catch(Exception e){
                		; //--do nothing
                	}finally {
                		if(utilSocket != null) utilSocket.finalize();
                	}

                	continue;
                }
                
                LbsReceiverFromGWThread recvThread = new LbsReceiverFromGWThread(m_socket);
                recvThread.start();
            
            } catch( Exception e ) {
            	UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
            }
        }
    }



}
