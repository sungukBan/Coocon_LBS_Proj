package com.coocon.lbs.monitor;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.handler.SSAgentHandler;
import com.coocon.lbs.handler.SSJobListHandler;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class SSJobListMonitorThread extends Thread {
	
	private String tName = this.getClass().getSimpleName();
	
	public SSJobListMonitorThread(){
	}
	
	public void run() {
		
		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName+ " is now starting...........");
		
		while(true){
			try {
				String sShowAllEntities = SSJobListHandler.getInstance().showAllEntity(this.getClass().getName());
				
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " :::::: linkedHashMapSSJobList 에 할당되어 있는 SSJobs ::::::");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), sShowAllEntities);
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				
				QueueEntity qEntity = SSJobListHandler.getInstance().removeExpiredEntity(this.getClass().getName());
				
				if(qEntity != null) {
					SSAgentHandler.getInstance().updateNowRunningCnt(qEntity.getEntityMsgCommon(), -1, this.getClass().getName());
				} else {
					Thread.sleep(1000*Integer.parseInt(UtilConfig.getValue(ConstConfig.MSG_SS_JOB_TTL_CHECK_INTERVAL)));
				}
				
			} catch(Exception e) {
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
			}
		}
	}
}
