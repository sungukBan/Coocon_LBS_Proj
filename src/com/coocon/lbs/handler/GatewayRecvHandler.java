package com.coocon.lbs.handler;

import com.coocon.lbs.agent.GatewayAgent;
import com.coocon.lbs.agent.SSAgent;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.entity.EntitySSAgentGroup;
import com.coocon.lbs.entity.EntityTxCode;
import com.coocon.lbs.msg.MsgCommon;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.queue.QueueHandler;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;


public class GatewayRecvHandler extends Thread {
	
	public  static QueueHandler gatewayRecvQueueHandler = null;
	private String              tName                   = this.getClass().getSimpleName();

	public GatewayRecvHandler(){
		gatewayRecvQueueHandler = new QueueHandler();
	}

	public void run() {

		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+ " is now starting...........");

		while (true) {

			try {
				//--01.dequeue..for 3 seconds waiting..
				QueueEntity qEntity = GatewayRecvHandler.gatewayRecvQueueHandler.recvDequeue();

				//--02.요청받은 전문이 없을 때..
				if(qEntity == null) {
					Thread.sleep(Integer.parseInt(UtilConfig.getValue(ConstConfig.QUE_DEQUEUE_SLEEP_SECOND)));
					continue;
				}
				
				//--03.사용가능한 SS Sender EntitySSAgent 검색
				EntitySSAgentGroup ssAgentGroup = new EntitySSAgentGroup();
				EntityMsgCommon cEntity = qEntity.getEntityMsgCommon();
				SSAgent ssAgent = null;
				
				//--04. SS서버 전체구간으로 검색
				if ( Constant.SS_SYS_NO_ALL.equals(cEntity.ss_sys_no) || "".equals(cEntity.ss_sys_no.trim()) ) {
					
					//--04.거래코드의 그룹코드 검색...
					EntityTxCode txCodeEntity = TxCodeHandler.getInstance().getEntity(cEntity.tx_cd, tName);
					String sTxCode = null;

					if(txCodeEntity != null) {
						ssAgentGroup.GROUP_NO  = txCodeEntity.MSG_GROUP_CODE;
						ssAgentGroup.SECT_FROM = Integer.parseInt(txCodeEntity.MSG_SECT_START.trim());
						ssAgentGroup.SECT_TO   = Integer.parseInt(txCodeEntity.MSG_SECT_END.trim());
						sTxCode = txCodeEntity.TX_CODE;
					}else{
						ssAgentGroup.GROUP_NO  = cEntity.group_gb;
						ssAgentGroup.SECT_FROM = Integer.parseInt(cEntity.sect_start.trim());
						ssAgentGroup.SECT_TO   = Integer.parseInt(cEntity.sect_end.trim());
						sTxCode = null;
					}
					
					ssAgentGroup.SS_SYS_NO = cEntity.ss_sys_no;
					ssAgentGroup.IDX_LAST_SSAGENT = ssAgentGroup.SECT_FROM -1;
					ssAgentGroup.LAST_UPDATE_DTM = UtilCommon.getDate()+UtilCommon.getHHmmss();
					
					//--04-02. SSAgentGroupHandler 그룹정보 갱신(SECT_FROM, SECT_TO 변경시에만..) SSAgentGroup 가져오기 
					ssAgentGroup = SSAgentGroupHandler.getInstance().upsert(ssAgentGroup, tName);

					//--04-03. 사용가능한 SS Sender를 찾는다
					ssAgent = SSAgentHandler.getInstance().getAvailableAgent(ssAgentGroup, sTxCode, tName);
				}

				//--05. 지정 SS Sender를 찾는다
				else {
					
					ssAgent = SSAgentHandler.getInstance().getTheAgent(cEntity.ss_sys_no, tName);
				}

				//--06.사용가능한 SS Sender SSAgent 없을 때..
				if ( ssAgent == null ) {

					byte[] bRecv = qEntity.getMessage();
					MsgCommon mCommon = new MsgCommon();
					mCommon.fromByteArray(bRecv);

					UtilCommon.assignString(Constant.TX_TYPE_0610      , mCommon.tx_type);
					UtilCommon.assignString(Constant.ERR_CD_LBS_00001  , mCommon.resp_cd);
					UtilCommon.assignString(UtilCommon.getDate()       , mCommon.send_dt);
					UtilCommon.assignString(UtilCommon.getTime_HHmmss(), mCommon.send_tm);
					
					byte[] bCommon = mCommon.toByteArray();
					System.arraycopy(bCommon, 0, bRecv, 0, bCommon.length);
					qEntity.setMessage(bRecv);
					
					GatewayAgent gwAgent = GatewayAgentHandler.getInstance().getEntity(Constant.PREFIX_SEND + qEntity.getEntityMsgCommon().gw_sys_no, tName);
					
					if(gwAgent != null) gwAgent.qHandler.sendEnqueue(qEntity);
					
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), "[LBS00001]처리가능한 SSAgent가 없습니다. [cMsg]::["  + new String(bCommon) + "]" );
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), "전체 Agent => \n" + SSAgentHandler.getInstance().showAllEntity(tName));
					
					continue;
				}

				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ###################(사용가능한 SS Sender SSAgent 있을때)#####################################");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), ssAgent.toString());
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ########################################################");
				
				qEntity.setSsSysNo(ssAgent.SS_SYS_NO.replaceAll(Constant.PREFIX_SEND, ""));
				
				//--05. QueueEntity를 hashtable 에 put 함.
				//-- 일정시간 응답이   오면, SSRecvHandler          에서 remove 처리함
				//-- 일정시간 응답이 안오면, SSJobListMonitorThread 에서 remove 처리하고, running_cnt -1 처리함.
				SSJobListHandler.getInstance().put(qEntity.getMsgKey(), qEntity);
				
				//--06. 전문 SEND QUEUE 에 Enqueue ..
				ssAgent.qHandler.sendEnqueue(qEntity);
				ssAgent.REFRESHED_DTM = UtilCommon.getDate() + UtilCommon.getHHmmss();
				
			} catch(Exception e) {
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
			}
		}
	}


}
