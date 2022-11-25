package com.coocon.lbs.monitor;

import com.coocon.lbs.agent.SSAgent;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.handler.SSAgentHandler;
import com.coocon.lbs.msg.MsgCommon;
import com.coocon.lbs.msg.MsgPolling;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class SSAgentTimeOutMonitorThread extends Thread {

	private String tName = this.getClass().getSimpleName();

	public SSAgentTimeOutMonitorThread(){
	}

	public void run() {

		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+ "############################################");
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+ " is now starting...........");
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+ "############################################");

		while(true){

			try {

				SSAgent agent = SSAgentHandler.getInstance().reOpenBizTimeoutSSAgent(tName);

				if(agent != null) {

					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_AGENT), tName+  " 비정상적인 agent =>" + agent.toString());
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_AGENT), tName+  " running 스크래핑 건수 >= max 스크래핑 건수   AND  ("+UtilConfig.getValue(ConstConfig.SS_REFRESH_TIMEOUT)+") 초 이상 갱신이 안되었을때");

					//--업무개시 전문 전송
					//doSendMsgBizOpen(agent);
					//--SMS 문자 전송을 해야할듯...일단은 로그에 남김...
					
				} else {
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_AGENT), "비정상적인 Agent 는 없습니다. 전체 Agent => \n" + SSAgentHandler.getInstance().showAllEntity(tName));
				}
				
				Thread.sleep(1000 * Integer.parseInt(UtilConfig.getValue(ConstConfig.SS_REFRESH_TIMEOUT)));

			} catch(Exception e) {
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
			}
		}
	}

	private void doSendMsgBizOpen(SSAgent agent){

		try {
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+  "--------------------------------");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+  " doSendMsgBizOpen is starting..");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+  "--------------------------------");
			
			MsgPolling      mPolling = new MsgPolling();
			MsgCommon       cMsg     = mPolling.cMsg;

			//--01. Polling 전문 생성 ..
			UtilCommon.assignString( UtilCommon.fillZeros(6, (cMsg.iMsgLen+ mPolling.iMsgLen) +"" )   
					                                                                , cMsg.tot_len    );
			UtilCommon.assignString( Constant.TRN_CD1            					, cMsg.trn_cd1    );
			UtilCommon.assignString( Constant.TRN_CD2+"99999"      					, cMsg.trn_cd2    );
			UtilCommon.assignString( Constant.SEND_FLAG          					, cMsg.send_flag  );
			UtilCommon.assignString( UtilConfig.getValue(ConstConfig.MSG_GW_SYS_NO) , cMsg.gw_sys_no  );
			UtilCommon.assignString( agent.SS_SYS_NO.substring(5, 9)                , cMsg.ss_sys_no  );
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

			EntityMsgCommon cEntity  = new EntityMsgCommon();
			cMsg.setEntity(cEntity);

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+  " doSendMsgBizOpen 전문생성 완료..");
			
			QueueEntity qEntity = new QueueEntity();
			qEntity.setEntityMsgCommon(cEntity);
			qEntity.setRecvTime       (cEntity.send_tm);
			qEntity.setMsgKey         (cEntity);
			qEntity.setMessage        (mPolling.toByteArray());

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+  " doSendMsgBizOpen QueueEntity 생성 완료..");
			
			//--02. 전문 SEND QUEUE 에 Enqueue ..
			agent.qHandler.sendEnqueue(qEntity);
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+  " agent.qHandler.sendEnqueue ...QueueEntity 생성 완료..");
			
		}catch(Exception e){
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}
	}
}