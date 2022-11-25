package com.coocon.lbs.net.gw;

import java.net.Socket;
import javax.net.ssl.SSLSocket;
import com.coocon.lbs.agent.GatewayAgent;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.queue.QueueHandler;
import com.coocon.lbs.handler.GatewayAgentHandler;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;
import com.coocon.lbs.util.sock.IUtilSocket;
import com.coocon.lbs.util.sock.UtilSocketImpl;
import com.coocon.lbs.util.sock.UtilSocketImplSSL;

public class LbsSenderToGWThread extends Thread {

	private IUtilSocket utilSocket = null;
	private int	 iMsgLen	       = Integer.parseInt(UtilConfig.getValue(ConstConfig.MSG_LEN_COLUMN_SIZE));
	private int	 iSocketTimeOut    = Integer.parseInt(UtilConfig.getValue(ConstConfig.SOCKET_GW_TIMEOUT));
	private String tName           = this.getClass().getSimpleName();
	private String sMid            = "";
	private GatewayAgent gwEntity  = null;
	private static int LOG_JUNMUN_PRINT_MAX_LEN = Integer.parseInt(UtilConfig.getValue(ConstConfig.LOG_JUNMUN_PRINT_MAX_LEN));
	
	private long lStartTime = 0;
	private int	 nPolling	= 0;
	private long lPollingSsInterval = Long.parseLong(UtilConfig.getValue(ConstConfig.POLLING_GW_INTERVAL)) * 1000;
	
	public LbsSenderToGWThread(Socket m_socket) throws Exception {
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now starting...........");
		utilSocket = new UtilSocketImpl(m_socket);
		utilSocket.setTimeout(1000*iSocketTimeOut);
	}

	public LbsSenderToGWThread(SSLSocket m_socket) throws Exception {
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now starting...........");
		utilSocket = new UtilSocketImplSSL(m_socket);
		utilSocket.setTimeout(1000*iSocketTimeOut);
	}

	public void run() {
		
		int rV = Constant.WK_INVALID;
		
		try {
			//--01.업무개시 및 MID 등록처리
			rV = doBizOpen();

			//--02.전문전송 처리
			if(rV == Constant.WK_VALID) doJob();

		} catch(Exception e) {
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		} finally {
			//--03.전송소켓 삭제처리
			doBizClose();
		}
	}

	private void doBizClose() {
		try {
			if(gwEntity != null) GatewayAgentHandler.getInstance().removeEntity(gwEntity, tName);
		}catch(Exception e){
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}
	}
	
	
	private void doJob() {
		
		
		lStartTime = System.currentTimeMillis();
		
		//--재접속으로 인한 기존의 소켓이 다시 연결되지 않으면 반복 진행.
		while( gwEntity != null && gwEntity.SOCKET_STS == Constant.SOCK_CONNECTED) {

			try {
				//--01. polling 처리
				long lTimeGaps = System.currentTimeMillis() - lStartTime;

				if ( lTimeGaps > lPollingSsInterval ) {
					
					// polling 전문생성 & 전송
					doSendMsg(Constant.POLLING_REQ_MSG.getBytes());
					nPolling++;
					
					// polling 전문수신
					byte[] bRecv = doRecvMsg();
					
					//--폴링 전문일때는 다음 진행.
					if(Constant.POLLING_RES_MSG.equals(new String(bRecv))) {
						nPolling = 0;
						lStartTime = System.currentTimeMillis();
						continue;
					}
					//--폴링 전문의 응답이 없으면..종료함.
					if ( nPolling > 0 ) break;
				}

				//--02.GW_SEND_QUE Queue 에서 Dequeue 처리
				//--Queue read time-out n 초...세팅..
				QueueEntity qEntity = gwEntity.qHandler.sendDequeue2();

				//--03.Queue 에서 Dequeue entity 없으면 looping 처리.
				if( qEntity == null ) {
					Thread.sleep(Integer.parseInt(UtilConfig.getValue(ConstConfig.QUE_DEQUEUE_SLEEP_SECOND)));
					continue;
				}
				
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "08. LbsSenderToGWThread.sendDequeue2 !! [MSG_KEY]::[" + qEntity.getMsgKey() + "]");

				//--04.GW 로 전문 전송
				doSendMsg(qEntity.getMessage());
				
				//--05.LBS <-> G/W 거래 완료시, polling 생략할 수 있도록
				lStartTime = System.currentTimeMillis();

			} catch(Exception e) {
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
				gwEntity.SOCKET_STS = Constant.SOCK_DISCONNECTED;
			}
		} //--while( gwEntity.GW_SOCKET_RECV_STS == Constant.SOCK_CONNECTED ) {
		
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " :: is now finishing...BATCH 에서 연결을 종료 했습니다.......");

	}

	private byte[] doRecvMsg() throws Exception {
		byte[]	bRecv  	= null;
		try {
			byte[] bSize = utilSocket.read(iMsgLen);
			int iSize = Integer.parseInt(new String(bSize));

			bRecv = new byte[iSize];
			byte[] bMid = utilSocket.read(iSize-iMsgLen);

			System.arraycopy(bSize, 0, bRecv, 0, bSize.length);
			System.arraycopy(bMid , 0, bRecv, bSize.length, bMid.length);

			if(new String(bRecv).length() >= LOG_JUNMUN_PRINT_MAX_LEN){
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS <-- GW) ==>" + new String(bRecv).substring(0, LOG_JUNMUN_PRINT_MAX_LEN) + "$");
			}else{
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS <-- GW) ==>" + new String(bRecv) + "$");
			}

		} catch(Exception e){
			//--L4 Health Check 프로세스가 소켓으로 접속만하고 ... null 값을 send 함.
			//--관련해서 에러 로그가 많이 찍히고 있음
			throw e;
		}
		return bRecv;
	}

	private int doBizOpen() throws Exception {
		
		int rV = Constant.WK_INVALID;
		byte[]	bRecv  	= null;

		try {
			//--01.업무개시 전문수신(GW <-> LBS 구간은 약식 업무개시 전문 사용)
			//--000009101
			bRecv = doRecvMsg();

			//--02.GatewayAgent 등록 -> GatewayAgentHandler
			sMid = Constant.PREFIX_SEND + UtilCommon.fillZeros(3, new String(bRecv).substring(6, 9));
			tName = tName + "_" + sMid;
			gwEntity = new GatewayAgent();
			gwEntity.GW_SYS_NO   = sMid;
			gwEntity.CREATED_DTM = UtilCommon.getDate() + UtilCommon.getHHmmss();
			gwEntity.SOCKET_STS  = Constant.SOCK_CONNECTED;
			gwEntity.utilSocket  = utilSocket;
			gwEntity.qHandler    = new QueueHandler();
			

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " :: gwEntity=[\n"+gwEntity.toString()+"\n]....");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			
			//--03.신규 생성된 Agent를 AgentHandler 에 등록(기등록 삭제)
			GatewayAgentHandler.getInstance().setEntity(gwEntity, tName);
			
			//--04.업무개시전문 ECHO 응답
			doSendMsg(bRecv);

			rV = Constant.WK_VALID;
			
		} catch(Exception e){
			if(utilSocket != null) {
				utilSocket.finalize();
			}
			//--2017.02.16 
			throw e;
		}
		return rV;
	}


	private void doSendMsg(byte[] bSend) throws Exception {

		try {

			if(new String(bSend).length() >= LOG_JUNMUN_PRINT_MAX_LEN){
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS --> GW) ==>" + new String(bSend).substring(0, LOG_JUNMUN_PRINT_MAX_LEN) + "$");
			}else{
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS --> GW) ==>" + new String(bSend) + "$");
			}

			utilSocket.send(bSend);
		} catch(Exception e){
			throw e;
		}
	}


}

