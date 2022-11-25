package com.coocon.lbs.handler;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.coocon.lbs.agent.SSAgent;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.consts.Constant;
import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.entity.EntityMsgPolling;
import com.coocon.lbs.entity.EntitySSAgentGroup;
import com.coocon.lbs.entity.EntityTxCode;
import com.coocon.lbs.msg.MsgPolling;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class SSAgentHandler {

	private static SSAgentHandler instance;
	private static Map<String, SSAgent> hashTableSSAgentStatus = new Hashtable<String, SSAgent>();

	private SSAgentHandler(){
	}

	public static SSAgentHandler getInstance() {
		if( instance == null ) {
			synchronized( SSAgentHandler.class ) {
				if( instance == null ) {
					instance = new SSAgentHandler();
				}
			}
		}
		return instance;
	}


	public void setEntity(SSAgent entity, String tName) throws Exception {

		synchronized( SSAgentHandler.class ) {

			SSAgent oldEntity = hashTableSSAgentStatus.remove(entity.SS_SYS_NO);
			if(oldEntity != null) {
				if( oldEntity.utilSocket != null) {
					oldEntity.utilSocket.finalize();
					oldEntity.utilSocket = null;
				}
				if(oldEntity.qHandler != null) {
					oldEntity.qHandler.finalize();
					oldEntity.qHandler = null;
				}
				oldEntity = null;
			}
			hashTableSSAgentStatus.put(entity.SS_SYS_NO, entity);
		}
	}

	public void removeEntity(SSAgent entity, String tName) throws Exception {
		synchronized( SSAgentHandler.class ) {
			SSAgent oldEntity = hashTableSSAgentStatus.remove(entity.SS_SYS_NO);
			if(oldEntity != null) {
				oldEntity.finalize();
				oldEntity = null;
			}
		}
	}

	public SSAgent getEntity(String sMid, String tName) throws Exception {
		synchronized( SSAgentHandler.class ) {
			return getEntity2(sMid);
		}
	}

	public SSAgent getEntity2(String sMid) throws Exception {
		return hashTableSSAgentStatus.get(sMid);
	}

	public SSAgent cloneEntity(String sMid, String tName) throws Exception {

		synchronized( SSAgentHandler.class ) {
			SSAgent rEntity = new SSAgent();
			SSAgent entity  = getEntity2(sMid);

			rEntity.SS_SYS_NO = entity.SS_SYS_NO;
			rEntity.SS_STS_CD = entity.SS_STS_CD;
			rEntity.SS_MAX_RUN_CNT = entity.SS_MAX_RUN_CNT;
			rEntity.SS_NOW_RUN_CNT = entity.SS_NOW_RUN_CNT;

			return rEntity;
		}
	}

	public String showAllEntity(String tName) throws Exception {

		synchronized( SSAgentHandler.class ) {
			Collection<SSAgent> map = hashTableSSAgentStatus.values();
			Iterator<SSAgent> itr = map.iterator();

			StringBuffer sb = new StringBuffer();
			//sb.append("--------[ALL_Entities begin]--------\n");
			while(itr.hasNext()){
				SSAgent entity = itr.next();
				
				if( entity.SS_SYS_NO.substring(0, 5).equals(Constant.PREFIX_RECV)) {
					continue;
				}
				
				sb.append(entity.toString() + "\n");
			}
			//sb.append("--------[ALL_Entities end]--------\n");		
			return sb.toString();
		}
	}	

	public SSAgent updateNowRunningCnt(EntityMsgCommon entity, int iCnt, String tName) throws Exception {

		synchronized( SSAgentHandler.class ) {
			String sMid  = entity.ss_sys_no;
			SSAgent agent = getEntity2(Constant.PREFIX_SEND + UtilCommon.fillZeros(4, sMid));

			if(agent != null) {
				EntityTxCode txCodeEntity = agent.htTxCodeList.get(entity.tx_cd);

				if(txCodeEntity != null) {
					txCodeEntity.SS_NOW_RUN_CNT += iCnt;
					agent.SS_NOW_RUN_CNT += iCnt;

					if(txCodeEntity.SS_NOW_RUN_CNT < 0) { txCodeEntity.SS_NOW_RUN_CNT = 0; }
					if(agent.SS_NOW_RUN_CNT < 0)        { agent.SS_NOW_RUN_CNT        = 0; }

					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ALERT), "SSAgentHandler :: agent_minus=["+agent.SS_SYS_NO+"]["+agent.SS_MAX_RUN_CNT+"]["+agent.SS_NOW_RUN_CNT+"]-["+entity.tx_cd+"]["+txCodeEntity.SS_MAX_RUN_CNT+"]["+txCodeEntity.SS_NOW_RUN_CNT+"]");
				}else{
					agent.SS_NOW_RUN_CNT += iCnt;

					if(agent.SS_NOW_RUN_CNT < 0) { agent.SS_NOW_RUN_CNT = 0; }

					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ALERT), "SSAgentHandler :: agent_minus=["+agent.SS_SYS_NO+"]["+agent.SS_MAX_RUN_CNT+"]["+agent.SS_NOW_RUN_CNT+"]");
				}
			}
			return agent;
		}
	}

	public SSAgent getAvailableAgent(EntitySSAgentGroup group, String sTxCode, String tName) throws Exception {

		synchronized( SSAgentHandler.class ) {
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + "");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + "");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " had entered getAvailableAgent()...");

			SSAgent agent = null;
			int iTo      = group.SECT_TO;
			int iLastIdx = group.IDX_LAST_SSAGENT;
			int iCurIdx  = group.IDX_LAST_SSAGENT;
			int iGap     = group.SECT_TO - group.SECT_FROM + 1;

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),"------------------------------");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),"검색 SSAgentGroup 정보=>\n" + group);
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),"------------------------------");

			boolean isFirst = true;
			for(int i=0; i < iGap; i++)
			{
				iCurIdx = iCurIdx + 1;

				//현재 시점이 TO 까지 도달했을때, FROM 으로 돌아가게 처리. 
				if(iCurIdx > iTo) iCurIdx = iCurIdx - iGap;

				//라운드 로빈 한바퀴 다 돌았을때(맨 첫번째는 제외)
				if(isFirst == false && iCurIdx == iLastIdx+1) {
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + "----------------------------------------------------------------------------------------------");
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + "......한바뀌 ("+iGap+")번 다돌아도 가능한 SSAgent가 없네요....iCurIdx=("+iCurIdx+")..iLastIdx=("+iLastIdx+")");
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + showAllEntity(tName) );
					UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + "----------------------------------------------------------------------------------------------");
					break;
				}
				isFirst = false;

				//Agent 가져오기
				agent = getEntity2(Constant.PREFIX_SEND + UtilCommon.fillZeros(4, iCurIdx+""));

				//SSAgent 소켓접속 이전일때...
				if( agent == null ) continue;

				//SSAgent 소켓접속 되었고, MAX JOB CNT 초과하지 않았을때...
				if( agent.SS_STS_CD.equals("Y") && (agent.SS_MAX_RUN_CNT > agent.SS_NOW_RUN_CNT) ) {
					
					if( sTxCode == null ) {
						agent.SS_NOW_RUN_CNT++;
						group.IDX_LAST_SSAGENT = iCurIdx;
						UtilLogger.logTemp(UtilConfig.getValue(ConstConfig.LOG_PATH_ALERT), "SSAgentHandler :: agent_plus =["+agent.SS_SYS_NO+"]["+agent.SS_MAX_RUN_CNT+"]["+agent.SS_NOW_RUN_CNT+"]");
						break;
					}
					else {
						EntityTxCode txCodeEntity = agent.htTxCodeList.get(sTxCode);

						if( txCodeEntity.SS_MAX_RUN_CNT > txCodeEntity.SS_NOW_RUN_CNT ) {
							txCodeEntity.SS_NOW_RUN_CNT++;
							agent.SS_NOW_RUN_CNT++;
							group.IDX_LAST_SSAGENT = iCurIdx;
							UtilLogger.logTemp(UtilConfig.getValue(ConstConfig.LOG_PATH_ALERT), "SSAgentHandler :: agent_plus =["+agent.SS_SYS_NO+"]["+agent.SS_MAX_RUN_CNT+"]["+agent.SS_NOW_RUN_CNT+"]-["+sTxCode+"]["+txCodeEntity.SS_MAX_RUN_CNT+"]["+txCodeEntity.SS_NOW_RUN_CNT+"]");
							break;
						}
					}
				}

				//--looping 처리로 이전값 세팅 방지
				agent = null;
			}
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),tName + "-------------------------------");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),tName + "...검색된 결과 agent 정보=>\n" + agent);
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),tName + "------------------------------");

			return agent;
		}
	}

	public SSAgent getTheAgent(String sSsSysNo, String tName) throws Exception {

		synchronized( SSAgentHandler.class ) {
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), tName + " had entered getTheAgentPlain()...");

			SSAgent agent = null;

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),"---------------------------------");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),"검색 sSsSysNo 정보=>\n" + sSsSysNo);
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),"---------------------------------");

			// Agent 가져오기
			agent = getEntity2(Constant.PREFIX_SEND + UtilCommon.fillZeros(4, sSsSysNo));

			// SSAgent 소켓접속 이전일때...
			if( agent == null ) return null;
			
			if ( agent.SS_STS_CD.equals("Y") && (agent.SS_MAX_RUN_CNT > agent.SS_NOW_RUN_CNT) ) {
				agent.SS_NOW_RUN_CNT++;
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_ALERT), "SSAgentHandler :: agent_plus =["+agent.SS_SYS_NO+"]["+agent.SS_MAX_RUN_CNT+"]["+agent.SS_NOW_RUN_CNT+"]");
				
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),tName + "-------------------------------------");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),tName + "...검색된 결과 agent 정보=>\n" + agent);
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ),tName + "-------------------------------------");

				return agent;
			}
			else {
				return null;
			}
		}
	}

	public SSAgent reOpenBizTimeoutSSAgent(String tName) throws Exception {

		synchronized( SSAgentHandler.class ) {
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "=========================================");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "......reOpenBizTimeoutSSAgent...starting.");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "=========================================");

			SSAgent rAgent = null;

			String sToDtm = UtilCommon.getDate() + UtilCommon.getHHmmss();

			for (String key : hashTableSSAgentStatus.keySet()) {

				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "=========================================");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " key=["+key+"]...........................");
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "=========================================");

				SSAgent agent = hashTableSSAgentStatus.get(key);


				if( false == agent.SS_SYS_NO.substring(0, 5).equals(Constant.PREFIX_SEND)) {
					continue;
				}

				//--running 은행 스크래핑 건수 >= max 은행 스크래핑 건수 일때, 업무개시를 보내서 SSAgent 로부터 직접 처리건수를 받는다.
				if( agent.SS_NOW_RUN_CNT > 0 && agent.SS_NOW_RUN_CNT >= agent.SS_MAX_RUN_CNT ) {

					//--120 초 이상 갱신이 안되었을때(config.properties 값 참조)
					long lTimeGapSec = UtilCommon.getTimeDiffSec(agent.REFRESHED_DTM, sToDtm);
					if( lTimeGapSec > Long.parseLong(UtilConfig.getValue(ConstConfig.SS_REFRESH_TIMEOUT)) ) {

						UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ################################################");
						UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " lTimeGapSec=["+lTimeGapSec+"]...SS_TIMEOUT_CHECK_INTERVAL=["+Long.parseLong(UtilConfig.getValue(ConstConfig.SS_REFRESH_TIMEOUT))+"] ");
						UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " running 은행 스크래핑 건수 >= max 은행 스크래핑 건수 일때, 폴핑 전문를 보내서 SSAgent에 정보를 요청한다... ");
						UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " " + agent.toString());
						UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ################################################");

						rAgent = agent;

						//--1개만 찾고 중단...
						break;
					}
				}

			}

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "=========================================");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "......reOpenBizTimeoutSSAgent...ending...");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "=========================================");

			return rAgent;
		}
	}

	public void refreshSSAgent(MsgPolling mPolling, String tName) throws Exception {

		synchronized( SSAgentHandler.class ) {
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ################################################");
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " SSAgent 정보를 refresh 합니다... ");

			EntityMsgCommon  eCommon  = new EntityMsgCommon();
			EntityMsgPolling ePolling = new EntityMsgPolling();

			mPolling.cMsg.setEntity(eCommon);
			mPolling.setEntity(ePolling);

			SSAgent agent = getEntity2(eCommon.ss_sys_no);

			agent.SS_MAX_RUN_CNT      = ePolling.tot_cnt;
			agent.SS_NOW_RUN_CNT      = ePolling.nw_cnt;
			agent.REFRESHED_DTM       = UtilCommon.getDate()+UtilCommon.getHHmmss();

			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " " + agent.toString());
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " ################################################");
		}
	}



}
