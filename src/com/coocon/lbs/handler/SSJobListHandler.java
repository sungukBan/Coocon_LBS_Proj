package com.coocon.lbs.handler;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class SSJobListHandler {

	private static SSJobListHandler instance;
	private static Map<String, QueueEntity> linkedHashMapSSJobList = new LinkedHashMap<String, QueueEntity>();
	
	private SSJobListHandler(){
	}
	
	public static SSJobListHandler getInstance(){
		if( instance == null ){
			synchronized( SSJobListHandler.class ){
				if( instance == null ){
					instance = new SSJobListHandler();
				}
			}
		}
		return instance;
	}
	

	public synchronized QueueEntity put(String sMsgKey, QueueEntity qEntity){
		return linkedHashMapSSJobList.put(sMsgKey, qEntity);
	}
	
	public synchronized QueueEntity get(String sMsgKey){
		return linkedHashMapSSJobList.get(sMsgKey);
	}
	
	public synchronized QueueEntity remove(String sMsgKey){
		return linkedHashMapSSJobList.remove(sMsgKey);
	}
	
	public String showAllEntity(String tName) throws Exception {
		Collection<QueueEntity> map = linkedHashMapSSJobList.values();
		if( map.size() < 1){
			return this.getClass().getSimpleName() + " :: linkedHashMapSSJobList 에 할당된 SSJob 이 없습니다....";
		}
		Iterator<QueueEntity> itr = map.iterator();

		StringBuffer sb = new StringBuffer();
		sb.append("\n--------[ALL_Entities begin]--------\n");
		while(itr.hasNext()){
			QueueEntity qEntity = itr.next();
			sb.append(qEntity.getRecvTime()                   + "\n");
			sb.append(qEntity.getEntityMsgCommon().toString() + "\n");
		}
		sb.append("\n--------[ALL_Entities end]--------\n");		
		return sb.toString();
	}	
	
	public synchronized QueueEntity removeExpiredEntity(String tName) throws Exception {

		QueueEntity rQEntity = null;
		
		String sToDtm = UtilCommon.getDate() + UtilCommon.getHHmmss();
		
		for (String key : linkedHashMapSSJobList.keySet()) {
			QueueEntity qEntity = linkedHashMapSSJobList.get(key);
			long lTimeGapSec = UtilCommon.getTimeDiffSec(qEntity.getRecvTime(), sToDtm);
			
			if( lTimeGapSec > Long.parseLong(UtilConfig.getValue(ConstConfig.MSG_SS_JOB_TTL))){
				rQEntity = linkedHashMapSSJobList.remove(key);
				
				String sRemovedEntity = "TIME-OUT MSG FOR TTL("+UtilConfig.getValue(ConstConfig.MSG_SS_JOB_TTL)+")초..["+qEntity.getRecvTime()+"]["+sToDtm+"]["+lTimeGapSec+"]\n"
						              + "RemovedEntity=["+ rQEntity.getEntityMsgCommon().toString()+"]";
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), sRemovedEntity);
			}
			//--1개만 지우고 중단...
			break;
		}
		return rQEntity;
	}

}
