package com.coocon.lbs.net.ss;

import java.net.Socket;
import java.util.Hashtable;
import javax.net.ssl.SSLSocket;
import com.coocon.lbs.entity.EntityTxCode;
import com.coocon.lbs.agent.SSAgent;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.entity.EntityMsgBizOpen;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.handler.SSAgentHandler;
import com.coocon.lbs.handler.SSRecvHandler;
import com.coocon.lbs.handler.TxCodeHandler;
import com.coocon.lbs.msg.MsgBizOpen;
import com.coocon.lbs.msg.MsgCommon;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.queue.QueueHandler;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;
import com.coocon.lbs.util.sock.IUtilSocket;
import com.coocon.lbs.util.sock.UtilSocketImpl;
import com.coocon.lbs.util.sock.UtilSocketImplSSL;

public class LbsReceiverFromSSThread extends Thread {

	private IUtilSocket utilSocket = null;
	private int	 iMsgLen	       = Integer.parseInt(UtilConfig.getValue(ConstConfig.MSG_LEN_COLUMN_SIZE));
	private int	 iSocketTimeOut    = Integer.parseInt(UtilConfig.getValue(ConstConfig.SOCKET_SS_TIMEOUT));
	private String tName           = this.getClass().getSimpleName();
	private SSAgent ssAgent        = null;
    private static int LOG_JUNMUN_PRINT_MAX_LEN = Integer.parseInt(UtilConfig.getValue(ConstConfig.LOG_JUNMUN_PRINT_MAX_LEN));
    
	public LbsReceiverFromSSThread(Socket m_socket) throws Exception {
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now starting...........");
		utilSocket = new UtilSocketImpl(m_socket);
		utilSocket.setTimeout(1000*iSocketTimeOut);
	}

	public LbsReceiverFromSSThread(SSLSocket m_socket) throws Exception {
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now starting...........");
		utilSocket = new UtilSocketImplSSL(m_socket);
		utilSocket.setTimeout(1000*iSocketTimeOut);
	}

	
	public void run() {

		int rV = Constant.WK_INVALID;

		try {
			//--01.�������� �� SS ���� ���ó��
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
			if(ssAgent != null) SSAgentHandler.getInstance().removeEntity(ssAgent, tName);
		}catch(Exception e){
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}
	}


	private void doJob() {

		byte [] bRecv = null; 

		//--���������� ���� ������ ������ �ٽ� ������� ������ �ݺ� ����.
		while( ssAgent != null && ssAgent.SOCKET_STS == Constant.SOCK_CONNECTED) {

			try {

				//--01.��������
				bRecv = doRecvMsg();

				//--02.READ TIME-OUT �߻�.
				if( bRecv == null ) continue;

				//--03.���� ����� �Ľ�
				MsgCommon cMsg = new MsgCommon();
				EntityMsgCommon eCommon = new EntityMsgCommon(); 
				cMsg.fromByteArray(bRecv);
				cMsg.setEntity(eCommon); 

				//--04.polling ��û ó��(SS --> LBS)
				if( eCommon.tx_type.equals(Constant.TX_TYPE_0800) && eCommon.tx_cd.equals  (Constant.TX_CODE_0002) ) {

					MsgBizOpen mBizOpen = new MsgBizOpen();
					mBizOpen.fromByteArray(bRecv);
					UtilCommon.assignString(Constant.TX_TYPE_0810      , mBizOpen.cMsg.tx_type);
					UtilCommon.assignString(Constant.SUCCESS           , mBizOpen.cMsg.resp_cd);
					UtilCommon.assignString(UtilCommon.getDate()       , mBizOpen.cMsg.send_dt);
					UtilCommon.assignString(UtilCommon.getTime_HHmmss(), mBizOpen.cMsg.send_tm);

					doSendMsg(mBizOpen.toByteArray());
					continue;
				} 

				//--05.Queue Entity ����
				QueueEntity qEntity = new QueueEntity();
				qEntity.setMessage(bRecv);
				qEntity.setRecvTime(UtilCommon.getDate()+UtilCommon.getTime_HHmmss());
				//qEntity.setMsgKey  (eCommon.trn_cd1.trim() + eCommon.trn_cd2.trim() + eCommon.tr_seq.trim());
				qEntity.setMsgKey  (eCommon);
				qEntity.setEntityMsgCommon(eCommon    );					

				SSRecvHandler.ssRecvHandler.recvEnqueue(qEntity);

			} catch(Exception e) {
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), tName + " [SS_NO]::[" + ssAgent.SS_SYS_NO + "] [SS_IP]::[" + ssAgent.remoteServerIp + "] ���� ������ ���� �߽��ϴ�. \n" + e);
				//UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
				ssAgent.SOCKET_STS = Constant.SOCK_DISCONNECTED;
			}
		} //--while( gwEntity.GW_SOCKET_RECV_STS == Constant.SOCK_CONNECTED ) {

		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " ###################################################");
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " :: is now finishing [" + ssAgent.SS_SYS_NO + "] [" + ssAgent.remoteServerIp + "] ���� ������ ���� �߽��ϴ�.......");
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " ###################################################");
	}

	private byte[] doRecvMsg() throws Exception {

		byte[]	bRecv  	= null;
		try {

			byte[] bSize = utilSocket.read(iMsgLen);
			
			int iSize = Integer.parseInt(new String(bSize));
			
			bRecv = new byte[iSize];
			
			byte[] bMid = utilSocket.read(iSize-iMsgLen);

			System.arraycopy(bSize, 0, bRecv, 0,            bSize.length);
			System.arraycopy(bMid , 0, bRecv, bSize.length, bMid.length);

			if(new String(bRecv).length() >= LOG_JUNMUN_PRINT_MAX_LEN){
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS <-- SS) ==>" + new String(bRecv).substring(0, LOG_JUNMUN_PRINT_MAX_LEN) + "$");
			} else {
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS <-- SS) ==>" + new String(bRecv) + "$");
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

		try {
			//--01.�������� ��������
			byte [] bRecv = doRecvMsg();

			//--02.���� ����� READ
			MsgCommon mCommon = new MsgCommon();
			EntityMsgCommon eCommon = new EntityMsgCommon();
			mCommon.fromByteArray(bRecv);
			mCommon.setEntity(eCommon);
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + ":: eCommon=[\n"+eCommon.toString()+"\n]....");

			//--03.�������� ������ �ƴϸ� ����..
			if ( true == eCommon.tx_type.equals(Constant.TX_TYPE_0800) && true == eCommon.tx_cd.equals  (Constant.TX_CODE_0001) ) {

				//--04.SS ���� ���� SSAgentHandler �� ���
				MsgBizOpen       mBizOpen = new MsgBizOpen();
				EntityMsgBizOpen eBizOpen = new EntityMsgBizOpen();
				mBizOpen.fromByteArray(bRecv);
				mBizOpen.setEntity(eBizOpen);

				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + ":: eBizOpen=[\n"+eBizOpen.toString()+"\n]....");

				UtilCommon.assignString(Constant.TX_TYPE_0810      , mBizOpen.cMsg.tx_type);
				UtilCommon.assignString(Constant.SUCCESS           , mBizOpen.cMsg.resp_cd);
				UtilCommon.assignString(UtilCommon.getDate()       , mBizOpen.cMsg.send_dt);
				UtilCommon.assignString(UtilCommon.getTime_HHmmss(), mBizOpen.cMsg.send_tm);

				ssAgent = new SSAgent();
				ssAgent.SS_SYS_NO      = Constant.PREFIX_RECV + UtilCommon.fillZeros(4, eCommon.ss_sys_no);
				ssAgent.SS_STS_CD      = eBizOpen.sts_gb;
				ssAgent.SS_MAX_RUN_CNT = eBizOpen.tot_cnt;
				ssAgent.SS_NOW_RUN_CNT = eBizOpen.nw_cnt;
				ssAgent.SOCKET_STS     = Constant.SOCK_CONNECTED;
				ssAgent.CREATED_DTM    = UtilCommon.getDate() + UtilCommon.getHHmmss();
				ssAgent.REFRESHED_DTM  = UtilCommon.getDate() + UtilCommon.getHHmmss();
				ssAgent.utilSocket     = utilSocket;
				ssAgent.qHandler       = new QueueHandler();
				ssAgent.remoteServerIp = utilSocket.getRemoteSocketIP();
				
				ssAgent.SS_MAX_RUN_CNT = Integer.parseInt(UtilConfig.getValue(ConstConfig.SS_MAX_RUN_CNT));
				ssAgent.SS_NOW_RUN_CNT = 0;
				
				ssAgent.htTxCodeList = (Hashtable<String, EntityTxCode>) TxCodeHandler.getInstance().createNewTxCodeMap();

				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " :: ssAgent=[\n"+ssAgent.toString()+"\n]....");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

				SSAgentHandler.getInstance().setEntity(ssAgent, tName);

				//--04.������������ ����
				doSendMsg(mBizOpen.toByteArray());

				rV = Constant.WK_VALID;
			}

		} catch(Exception e){
			if(utilSocket != null) {
				utilSocket.finalize();
			}
			//--2017.02.16 TOMISGOOD
			throw e;
		}
		return rV;
	}

	private void doSendMsg(byte[] bSend) throws Exception
	{
		try
		{
			if(new String(bSend).length() >= LOG_JUNMUN_PRINT_MAX_LEN){
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS --> SS) ==>" + new String(bSend).substring(0, LOG_JUNMUN_PRINT_MAX_LEN) + "$");
			}else{
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS --> SS) ==>" + new String(bSend) + "$");
			}

			utilSocket.send(bSend);
		} catch(Exception e){
			throw e;
		}
	}


}

