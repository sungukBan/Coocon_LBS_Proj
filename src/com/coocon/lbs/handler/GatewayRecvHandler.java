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

				//--02.��û���� ������ ���� ��..
				if(qEntity == null) {
					Thread.sleep(Integer.parseInt(UtilConfig.getValue(ConstConfig.QUE_DEQUEUE_SLEEP_SECOND)));
					continue;
				}
				
				//--03.��밡���� SS Sender EntitySSAgent �˻�
				EntitySSAgentGroup ssAgentGroup = new EntitySSAgentGroup();
				EntityMsgCommon cEntity = qEntity.getEntityMsgCommon();
				SSAgent ssAgent = null;
				
				//--04. SS���� ��ü�������� �˻�
				if ( Constant.SS_SYS_NO_ALL.equals(cEntity.ss_sys_no) || "".equals(cEntity.ss_sys_no.trim()) ) {
					
					//--04.�ŷ��ڵ��� �׷��ڵ� �˻�...
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
					
					//--04-02. SSAgentGroupHandler �׷����� ����(SECT_FROM, SECT_TO ����ÿ���..) SSAgentGroup �������� 
					ssAgentGroup = SSAgentGroupHandler.getInstance().upsert(ssAgentGroup, tName);

					//--04-03. ��밡���� SS Sender�� ã�´�
					ssAgent = SSAgentHandler.getInstance().getAvailableAgent(ssAgentGroup, sTxCode, tName);
				}

				//--05. ���� SS Sender�� ã�´�
				else {
					
					ssAgent = SSAgentHandler.getInstance().getTheAgent(cEntity.ss_sys_no, tName);
				}

				//--06.��밡���� SS Sender SSAgent ���� ��..
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
					
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), "[LBS00001]ó�������� SSAgent�� �����ϴ�. [cMsg]::["  + new String(bCommon) + "]" );
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), "��ü Agent => \n" + SSAgentHandler.getInstance().showAllEntity(tName));
					
					continue;
				}

				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ###################(��밡���� SS Sender SSAgent ������)#####################################");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), ssAgent.toString());
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ########################################################");
				
				qEntity.setSsSysNo(ssAgent.SS_SYS_NO.replaceAll(Constant.PREFIX_SEND, ""));
				
				//--05. QueueEntity�� hashtable �� put ��.
				//-- �����ð� ������   ����, SSRecvHandler          ���� remove ó����
				//-- �����ð� ������ �ȿ���, SSJobListMonitorThread ���� remove ó���ϰ�, running_cnt -1 ó����.
				SSJobListHandler.getInstance().put(qEntity.getMsgKey(), qEntity);
				
				//--06. ���� SEND QUEUE �� Enqueue ..
				ssAgent.qHandler.sendEnqueue(qEntity);
				ssAgent.REFRESHED_DTM = UtilCommon.getDate() + UtilCommon.getHHmmss();
				
			} catch(Exception e) {
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
			}
		}
	}


}
