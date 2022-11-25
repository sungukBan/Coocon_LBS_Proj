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
			//--01.�������� �� MID ���ó��
			rV = doBizOpen();

			//--02.�������� ó��
			if(rV == Constant.WK_VALID) doJob();

		} catch(Exception e) {
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		} finally {
			//--03.���ż��� ����ó��
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
		
		//--���������� ���� ������ ������ �ٽ� ������� ������ �ݺ� ����.
		while( gwEntity != null && gwEntity.SOCKET_STS == Constant.SOCK_CONNECTED ) {

			try {
				
				//--01.��������
				byte [] bRecv = doRecvMsg();

				//--02.READ TIME-OUT �߻��� ���� read ��õ���.
				if( bRecv == null ) continue;
				
				//--03.polling ��û ó��
				if(Constant.POLLING_REQ_MSG.equals(new String(bRecv))) {
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now Receiveing PollingMsg.....");
					doSendMsg(Constant.POLLING_RES_MSG.getBytes());
					continue;
				}

				//--04.���� ����� �Ľ�
				MsgCommon cMsg = new MsgCommon();
				EntityMsgCommon cEntity = new EntityMsgCommon(); 
				cMsg.fromByteArray(bRecv);
				cMsg.setEntity(cEntity); 

				//--05.Queue Entity ����
				QueueEntity qEntity = new QueueEntity();
				qEntity.setMessage(bRecv);
				qEntity.setRecvTime(UtilCommon.getDate()+UtilCommon.getTime_HHmmss());
				qEntity.setMsgKey  (cEntity);
				qEntity.setEntityMsgCommon(cEntity);
				
				//--99.SS ���� Queue �� Enqueue ó��
				GatewayRecvHandler.gatewayRecvQueueHandler.recvEnqueue(qEntity);
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "01. LbsReceiverFromGWThread.recvEnqueue !! [MSG_KEY]::[" + qEntity.getMsgKey() + "]");
				
			} catch(Exception e) {
				e.printStackTrace();
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " Exception :: " + e.getMessage());
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
				gwEntity.SOCKET_STS = Constant.SOCK_DISCONNECTED;
			}
		} //--while( gwEntity.GW_SOCKET_RECV_STS == Constant.SOCK_CONNECTED ) {

		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " :: is now finishing...BATCH ���� ������ ���� �߽��ϴ�.......");
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
			//--L4 Health Check ���μ����� �������� ���Ӹ��ϰ� ... null ���� send ��.
			//--�����ؼ� ���� �αװ� ���� ������ ����
			throw e;
		}
		return bRecv;
	}

	private int doBizOpen() throws Exception {
		
		int rV = Constant.WK_INVALID;
		byte[]	bRecv  	= null;
		
		try {
			//--01.�������� ��������(GW <-> LBS ������ ��� �������� ���� ���)
			//--000009101 : ��������(6) + GW�ý��۹�ȣ(3)
			bRecv = doRecvMsg();

			//--02.EntityGatewayAgent ��� -> GatewayAgentHandler
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
			
			//--03.�ű� ������ Agent�� AgentHandler �� ���(�� ��ϵȰ� ������ ������ ���)
			GatewayAgentHandler.getInstance().setEntity(gwEntity, tName);
			
			//--04.������������ ECHO ����
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

