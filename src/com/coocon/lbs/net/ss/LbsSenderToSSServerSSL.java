package com.coocon.lbs.net.ss;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;
import com.coocon.lbs.util.sock.AUtilSocket;
import com.coocon.lbs.util.sock.IUtilSocket;
import com.coocon.lbs.util.sock.UtilSocketImpl;
import com.coocon.lbs.util.sock.UtilSocketImplSSL;

public class LbsSenderToSSServerSSL extends Thread {
    
	private SSLServerSocket			m_serverSocket	= null;
    private SSLSocket			    m_socket		= null;
    private static Object			mutex			= null;  
    private String                  tName           = this.getClass().getSimpleName();

    public LbsSenderToSSServerSSL() {
    	try {
    		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "[" + tName + "] 积己磊() is calling...........");
            createLbsSenderToSSServer();
        } catch(Exception e) {
            UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
        }
    }

    public void createLbsSenderToSSServer() {
        try {
        	// 辑滚 家南 蒲配府 积己.
            SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            
            // 辑滚 家南 积己. 
            m_serverSocket = (SSLServerSocket)sslserversocketfactory.createServerSocket(Integer.parseInt(UtilCommon.getNullToStr(UtilConfig.getValue(ConstConfig.PORT_SS_SEND), "0")) );
        } catch (Exception e) {
        	UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
        }
    }

    public void run() {
        UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "[" + tName + "] run() is starting...........");

        if(mutex == null) mutex = new Object();

        try {
            if ( m_serverSocket == null ) 
            	createLbsSenderToSSServer();
        } catch(Exception e) {
        	UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
        }

        while (true) {

            try {
                synchronized(mutex) {
                    m_socket = (SSLSocket) m_serverSocket.accept();
                }
                
                //--L4 Health Check...2017.02.16 
                if( AUtilSocket.isL4HealthCheck(m_socket) == true ) {
                	
                    int iMsgLen = Integer.parseInt(UtilConfig.getValue(ConstConfig.MSG_LEN_COLUMN_SIZE));
                	IUtilSocket utilSocket = new UtilSocketImplSSL(m_socket);
                	try {
                		utilSocket.read(iMsgLen);
                	}catch(Exception e){
                		; //--do nothing
                	}finally {
                		if(utilSocket != null) utilSocket.finalize();
                	}

                	continue;
                }


                LbsSenderToSSThread tThread = new LbsSenderToSSThread(m_socket);
                tThread.start();
            
            } catch( Exception e ) {
            	UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
            }
        }
    }

}
