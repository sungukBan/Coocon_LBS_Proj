package com.coocon.lbs.handler;

import com.coocon.lbs.agent.GatewayAgent;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.queue.QueueHandler;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;


public class SSRecvHandler extends Thread {

	public static QueueHandler ssRecvHandler = null;
	private String          tName            = this.getClass().getSimpleName();

	public SSRecvHandler(){
		ssRecvHandler = new QueueHandler();
	}

	public void run() {

		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + "is now starting...........");

		while (true) {

			try {
				//--01.dequeue..for 3 seconds waiting..
				QueueEntity qEntity = ssRecvHandler.recvDequeue();

				//--02.��û���� ������ ���� ��
				if(qEntity == null) {
					Thread.sleep(Integer.parseInt(UtilConfig.getValue(ConstConfig.QUE_DEQUEUE_SLEEP_SECOND)));
					continue;
				}
				
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "06. SSRecvHandler.recvDequeue !! [MSG_KEY]::[" + qEntity.getMsgKey() + "]");
				
				//--03.������� ������ ���� ��..linkedHashMapSSJobList..���� �ش� ����� entity ����..
				SSJobListHandler.getInstance().remove(qEntity.getMsgKey());
				
				//--04.������� ������ ���� ��..SSAgent SS_NOW_RUN_CNT -1 ... 
				SSAgentHandler.getInstance().updateNowRunningCnt(qEntity.getEntityMsgCommon(), -1, tName);
				
				//--05.��û�� GatewayAgent Get...
				GatewayAgent gwAgent = GatewayAgentHandler.getInstance().getEntity(Constant.PREFIX_SEND + qEntity.getEntityMsgCommon().gw_sys_no, tName);
				
				//--06.��û�� EntityGatewayAgent ���� ��..
				if(gwAgent == null) {
					GatewayAgentHandler.getInstance().showAllEntity(tName);
					
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ��û�� GatewayAgent ���� ��...qEntity .. ����...qEntity=[\n"+qEntity.toString()+"\n]");
					continue;
				}
				
				//--06. ���� ���� EnQueue
				gwAgent.qHandler.sendEnqueue(qEntity);
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "07. SSRecvHandler.sendEnqueue !! [MSG_KEY]::[" + qEntity.getMsgKey() + "]");
				
			} catch(Exception e){
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
			}
		}
	}


}
