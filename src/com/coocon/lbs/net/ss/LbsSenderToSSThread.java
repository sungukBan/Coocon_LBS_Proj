package com.coocon.lbs.net.ss;

import java.net.Socket;
import java.util.Hashtable;
import javax.net.ssl.SSLSocket;
import com.coocon.lbs.agent.SSAgent;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.entity.EntityMsgBizOpen;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.entity.EntityTxCode;
import com.coocon.lbs.msg.MsgBizOpen;
import com.coocon.lbs.msg.MsgCommon;
import com.coocon.lbs.msg.MsgPolling;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.queue.QueueHandler;
import com.coocon.lbs.handler.SSAgentHandler;
import com.coocon.lbs.handler.TxCodeHandler;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;
import com.coocon.lbs.util.sock.IUtilSocket;
import com.coocon.lbs.util.sock.UtilSocketImpl;
import com.coocon.lbs.util.sock.UtilSocketImplSSL;

public class LbsSenderToSSThread extends Thread {

	private IUtilSocket utilSocket = null;
	private int	 iMsgLen	       = Integer.parseInt(UtilConfig.getValue(ConstConfig.MSG_LEN_COLUMN_SIZE));
	private int	 iSocketTimeOut    = Integer.parseInt(UtilConfig.getValue(ConstConfig.SOCKET_SS_TIMEOUT));
	private String tName           = this.getClass().getSimpleName();
	private SSAgent ssAgent  = null;
	private long lStartTime = 0;
	private int	 nPolling	= 0;
	private long lPollingSsInterval = Long.parseLong(UtilConfig.getValue(ConstConfig.POLLING_SS_INTERVAL)) * 1000;
	private static int LOG_JUNMUN_PRINT_MAX_LEN = Integer.parseInt(UtilConfig.getValue(ConstConfig.LOG_JUNMUN_PRINT_MAX_LEN));
	
	public LbsSenderToSSThread(Socket m_socket) throws Exception {
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now starting...........");
		utilSocket = new UtilSocketImpl(m_socket);
		utilSocket.setTimeout(1000*iSocketTimeOut);
	}
	
	public LbsSenderToSSThread(SSLSocket m_socket) throws Exception {
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now starting...........");
		utilSocket = new UtilSocketImplSSL(m_socket);
		utilSocket.setTimeout(1000*iSocketTimeOut);
	}	

	public void run() {

		int rV = Constant.WK_INVALID;
		
		try {
			//--01.업무개시 및 SS 등록처리
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
			if(ssAgent != null) SSAgentHandler.getInstance().removeEntity(ssAgent, tName);
		}catch(Exception e){
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}
	}

	private void doJob() {

		int rV = Constant.WK_INVALID;
		lStartTime = System.currentTimeMillis();

		//--재접속으로 인한 기존의 소켓이 다시 연결되지 않으면 반복 진행.
		while( ssAgent != null && ssAgent.SOCKET_STS == Constant.SOCK_CONNECTED ) {

			try {
				//--01. polling 처리
				long lTimeGaps = System.currentTimeMillis() - lStartTime; 
				
				if ( lTimeGaps > lPollingSsInterval ) {

					// polling 전문생성 & 전송
					rV = doSendPollingMsg();
					
					lStartTime = System.currentTimeMillis();
				}

				//--02.GW_SEND_QUE Queue 에서 Dequeue 처리
				//--Queue read time-out n 초...세팅..
				QueueEntity qEntity = ssAgent.qHandler.sendDequeue2();
				
				//--2016.04.04
				//--03.Queue 에서 Dequeue entity 없으면 looping 처리.
				if( qEntity == null ) {
					Thread.sleep(Integer.parseInt(UtilConfig.getValue(ConstConfig.QUE_DEQUEUE_SLEEP_SECOND)));
					continue;
				}

				//--04.SS 로 전문 전송
				doSendMsg(qEntity.getMessage());
				
				//--2016.03.23
				lStartTime = System.currentTimeMillis();

			} catch(Exception e) {
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), tName + " [SS_NO]::[" + ssAgent.SS_SYS_NO + "] [SS_IP]::[" + ssAgent.remoteServerIp + "] 에서 연결을 종료 했습니다. \n" + e);
				//UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
				ssAgent.SOCKET_STS = Constant.SOCK_DISCONNECTED;
			}
		} //--while( ... ) {
		
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " ###################################################");
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " :: is now finishing [" + ssAgent.SS_SYS_NO + "] [" + ssAgent.remoteServerIp + "] 에서 연결을 종료 했습니다.......");
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " ###################################################");
	}

	private int doSendPollingMsg() throws Exception {
		
		int rV = Constant.WK_INVALID;
		MsgPolling mRecvPolling = new MsgPolling();
		MsgPolling mSendPolling = null;
		
		try {
			//UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " is now Sending PollingMsg to SS.....");
			
			//--01.polling 전문 생성
			mSendPolling = doSetPollingMsg();
			
			//--02.polling 전문 전송
			doSendMsg(mSendPolling.toByteArray());
			
			nPolling++;
			
			//--03.polling 전문수신
			byte[] bRecv = doRecvMsg();
			
			mRecvPolling.fromByteArray(bRecv);
			
			if(Constant.SUCCESS.equals(mRecvPolling.cMsg.resp_cd)){
				nPolling = 0;
				lStartTime = System.currentTimeMillis();
				
				//--SSAgent 에서 받은 정보 update ...
				SSAgentHandler.getInstance().refreshSSAgent(mRecvPolling, tName);
			}
			
			//--폴링 전문의 응답이 없으면..종료함.
			if ( nPolling > 0 ) {
				rV = Constant.WK_INVALID;
			}else {
				rV = Constant.WK_VALID;
			}
			
		}catch(Exception e){
			throw e;
		}
		return rV;
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
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS <-- SS) ==>" + new String(bRecv).substring(0, LOG_JUNMUN_PRINT_MAX_LEN) + "$");
			}else{
				UtilLogger.junmunLog(UtilConfig.getValue(ConstConfig.LOG_PATH_JUN), "gateway", "(LBS <-- SS) ==>" + new String(bRecv) + "$");
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

		try {
			//--01.업무개시 전문수신
			//--공통부(100) + 개별부(50) : 전문으로 올라옴
			byte[]	bRecv = doRecvMsg();

			//--02.전문 공통부 READ
			MsgCommon mCommon = new MsgCommon();
			EntityMsgCommon eCommon = new EntityMsgCommon();
			mCommon.fromByteArray(bRecv);
			mCommon.setEntity(eCommon);
			
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + ":: eCommon=[\n"+eCommon.toString()+"\n]....");
			
			//--03.업무개시 전문이 아니면 종료..
			if ( true == eCommon.tx_type.equals(Constant.TX_TYPE_0800) && true == eCommon.tx_cd.equals  (Constant.TX_CODE_0001) ) {

				//--04.SS 접속 정보 SSAgentHandler 에 등록
				MsgBizOpen       mBizOpen = new MsgBizOpen();
				EntityMsgBizOpen eBizOpen = new EntityMsgBizOpen();
				mBizOpen.fromByteArray(bRecv);
				mBizOpen.setEntity(eBizOpen);

				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + ":: eBizOpen=[\n"+eBizOpen.toString()+"\n]....");
				
				UtilCommon.assignString(Constant.TX_TYPE_0810      , mBizOpen.cMsg.tx_type);
				UtilCommon.assignString(Constant.SUCCESS           , mBizOpen.cMsg.resp_cd);
				UtilCommon.assignString(UtilCommon.getDate()       , mBizOpen.cMsg.send_dt);
				UtilCommon.assignString(UtilCommon.getTime_HHmmss(), mBizOpen.cMsg.send_tm);

				//--04.SS 접속 정보 EntitySSAgent 생성
				ssAgent                = new SSAgent();
				ssAgent.SS_SYS_NO      = Constant.PREFIX_SEND + UtilCommon.fillZeros(4, eCommon.ss_sys_no);
				ssAgent.SS_STS_CD      = eBizOpen.sts_gb;
				ssAgent.SS_MAX_RUN_CNT = eBizOpen.tot_cnt;
				ssAgent.SS_NOW_RUN_CNT = eBizOpen.nw_cnt;
				ssAgent.SOCKET_STS     = Constant.SOCK_CONNECTED;
				ssAgent.CREATED_DTM    = UtilCommon.getDate() + UtilCommon.getHHmmss();
				ssAgent.REFRESHED_DTM  = UtilCommon.getDate() + UtilCommon.getHHmmss();
				ssAgent.utilSocket     = utilSocket;
				ssAgent.remoteServerIp = utilSocket.getRemoteSocketIP();
				ssAgent.qHandler       = new QueueHandler();

				ssAgent.SS_MAX_RUN_CNT = Integer.parseInt(UtilConfig.getValue(ConstConfig.SS_MAX_RUN_CNT));
				ssAgent.SS_NOW_RUN_CNT = 0;
				
				ssAgent.htTxCodeList = (Hashtable<String, EntityTxCode>) TxCodeHandler.getInstance().createNewTxCodeMap();
				
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " :: ssAgent=[\n"+ssAgent.toString()+"\n]....");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");

				//--05.신규 생성된 SSAgent를   SSAgentHandler 에 등록 (기등록된게 있으면 삭제후 등록)
				SSAgentHandler.getInstance().setEntity(ssAgent, tName);

				//--06.업무개시전문 정상 응답
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
	
	private MsgPolling doSetPollingMsg() {

		MsgPolling mPolling = new MsgPolling();
		MsgCommon cMsg = mPolling.cMsg;
		
		UtilCommon.assignString( UtilCommon.fillZeros(6, (cMsg.iMsgLen+ mPolling.iMsgLen) +"" )   
																				, cMsg.tot_len    );
		UtilCommon.assignString( Constant.TRN_CD1            					, cMsg.trn_cd1    );
		UtilCommon.assignString( Constant.TRN_CD2+"00001"               		, cMsg.trn_cd2    );
		UtilCommon.assignString( Constant.SEND_FLAG          					, cMsg.send_flag  );
		UtilCommon.assignString( UtilConfig.getValue(ConstConfig.MSG_GW_SYS_NO) , cMsg.gw_sys_no  );
		UtilCommon.assignString( UtilConfig.getValue(ConstConfig.MSG_SS_SYS_NO) , cMsg.ss_sys_no  );
		UtilCommon.assignString( Constant.TX_TYPE_0800       					, cMsg.tx_type    );
		UtilCommon.assignString( Constant.TX_CODE_0002       					, cMsg.tx_cd      );
		UtilCommon.assignString( UtilCommon.getDate()        					, cMsg.send_dt    );
		UtilCommon.assignString( UtilCommon.getTime_HHmmss() 					, cMsg.send_tm    );
		UtilCommon.assignString( UtilCommon.fillSpace(8)     					, cMsg.resp_cd    );
		UtilCommon.assignString( UtilConfig.getValue(ConstConfig.MSG_PROD_KEY)  , cMsg.prod_key   );
		UtilCommon.assignString( UtilCommon.getSeqNo()       					, cMsg.tr_seq     );
		UtilCommon.assignString( UtilConfig.getValue(ConstConfig.MSG_GROUP_GB)  , cMsg.group_gb   );  
		UtilCommon.assignString( UtilConfig.getValue(ConstConfig.MSG_SECT_START), cMsg.sect_start );
		UtilCommon.assignString( UtilConfig.getValue(ConstConfig.MSG_SECT_END)  , cMsg.sect_end   );
		UtilCommon.assignString( UtilCommon.fillSpace(7)                       	, cMsg.filler     );
		
		UtilCommon.assignString( "Y"                       					   	, mPolling.sts_gb );
		UtilCommon.assignString( UtilCommon.fillZeros(3)                       	, mPolling.tot_cnt);
		UtilCommon.assignString( UtilCommon.fillZeros(3)                       	, mPolling.nw_cnt );
		UtilCommon.assignString( UtilCommon.fillSpace(43)                      	, mPolling.filler );

		return mPolling;
		
	}

}

