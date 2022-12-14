package com.coocon.lbs.net.gw;

import java.net.Socket;
import javax.net.ssl.SSLSocket;
import com.coocon.lbs.agent.GatewayAgent;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.handler.GatewayAgentHandler;
import com.coocon.lbs.handler.GatewayRecvHandler;
import com.coocon.lbs.msg.MsgCommon;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.queue.QueueHandler;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;
import com.coocon.lbs.util.sock.IUtilSocket;
import com.coocon.lbs.util.sock.UtilSocketImpl;
import com.coocon.lbs.util.sock.UtilSocketImplSSL;


public class LbsReceiverFromGWThread extends Thread {

	private IUtilSocket utilSocket = null;
	private int	 iMsgLen	       = Integer.parseInt(UtilConfig.getValue(ConstConfig.MSG_LEN_COLUMN_SIZE));
	private int	 iSocketTimeOut    = Integer.parseInt(UtilConfig.getValue(ConstConfig.SOCKET_GW_TIMEOUT));
	private String tName           = this.getClass().getSimpleName();
	private String sMid            = "";
	private GatewayAgent gwEntity  = null;
    private static int LOG_JUNMUN_PRINT_MAX_LEN = Integer.parseInt(UtilConfig.getValue(ConstConfig.LOG_JUNMUN_PRINT_MAX_LEN));
	
	public LbsReceiverFromGWThread(Socket m_socket) throws Exception {
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now starting...........");
		utilSocket = new UtilSocketImpl(m_socket);
		utilSocket.setTimeout(1000*iSocketTimeOut);
	}
	
	public LbsReceiverFromGWThread(SSLSocket m_socket) throws Exception {
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now starting...........");
		utilSocket = new UtilSocketImplSSL(m_socket);
		utilSocket.setTimeout(1000*iSocketTimeOut);
	}

	public void run() {
		int rV = Constant.WK_INVALID;
		
		try {
			//--01.업무개시 및 MID 등록처리
			rV = doBizOpen();

			//--02.전문수신 처리
			if(rV == Constant.WK_VALID) doJob();

		} catch(Exception e) {
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		} finally {
			//--03.수신소켓 삭제처리
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
		
		//--재접속으로 인한 기존의 소켓이 다시 연결되지 않으면 반복 진행.
		while( gwEntity != null && gwEntity.SOCKET_STS == Constant.SOCK_CONNECTED ) {

			try {
				
				//--01.전문수신
				byte [] bRecv = doRecvMsg();

				//--02.READ TIME-OUT 발생시 소켓 read 재시도함.
				if( bRecv == null ) continue;
				
				//--03.polling 요청 처리
				if(Constant.POLLING_REQ_MSG.equals(new String(bRecv))) {
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now Receiveing PollingMsg.....");
					doSendMsg(Constant.POLLING_RES_MSG.getBytes());
					continue;
				}

				//--04.전문 공통부 파싱
				MsgCommon cMsg = new MsgCommon();
				EntityMsgCommon cEntity = new EntityMsgCommon(); 
				cMsg.fromByteArray(bRecv);
				cMsg.setEntity(cEntity); 

				//--05.Queue Entity 생성
				QueueEntity qEntity = new QueueEntity();
				qEntity.setMessage(bRecv);
				qEntity.setRecvTime(UtilCommon.getDate()+UtilCommon.getTime_HHmmss());
				qEntity.setMsgKey  (cEntity);
				qEntity.setEntityMsgCommon(cEntity);
				
				//--99.SS 전송 Queue 에 Enqueue 처리
				GatewayRecvHandler.gatewayRecvQueueHandler.recvEnqueue(qEntity);
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "01. LbsReceiverFromGWThread.recvEnqueue !! [MSG_KEY]::[" + qEntity.getMsgKey() + "]");
				
			} catch(Exception e) {
				e.printStackTrace();
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " Exception :: " + e.getMessage());
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
				gwEntity.SOCKET_STS = Constant.SOCK_DISCONNECTED;
			}
		} //--while( gwEntity.GW_SOCKET_RECV_STS == Constant.SOCK_CONNECTED ) {

		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " :: is now finishing...BATCH 에서 연결을 종료 했습니다.......");
	}

	private byte[] doRecvMsg() throws Exception {
		byte[]	bRecv  	= null;
		try {
			
			//UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + ":: doRecvMsg()....");
			
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
			//--000009101 : 전문길이(6) + GW시스템번호(3)
			bRecv = doRecvMsg();

			//--02.EntityGatewayAgent 등록 -> GatewayAgentHandler
			sMid = Constant.PREFIX_RECV + UtilCommon.fillZeros(3, new String(bRecv).substring(6, 9));
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
			
			//--03.신규 생성된 Agent를 AgentHandler 에 등록(기 등록된게 있으면 삭제후 등록)
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

