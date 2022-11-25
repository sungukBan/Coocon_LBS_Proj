package com.coocon.lbs.handler;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.coocon.lbs.entity.EntitySSAgentGroup;

public class SSAgentGroupHandler {

	private static SSAgentGroupHandler instance;
	private static Map<String, EntitySSAgentGroup> hashtableSSAgentGroup  = null;

	private SSAgentGroupHandler(){
		hashtableSSAgentGroup = new Hashtable<String, EntitySSAgentGroup>(); 
	}

	public static SSAgentGroupHandler getInstance() {
		if( instance == null ){
			synchronized( SSAgentGroupHandler.class ){
				if( instance == null ){
					instance = new SSAgentGroupHandler();
				}
			}
		}
		return instance;
	}

	public synchronized void setEntity(EntitySSAgentGroup entity, String tName) throws Exception {
		hashtableSSAgentGroup.put(entity.GROUP_NO, entity);
	}

	public synchronized void removeEntity(EntitySSAgentGroup entity, String tName) throws Exception {
		hashtableSSAgentGroup.remove(entity.GROUP_NO);
	}

	private synchronized EntitySSAgentGroup getEntity(String sMid, String tName) throws Exception {
		return hashtableSSAgentGroup.get(sMid);
	}

	public synchronized EntitySSAgentGroup cloneEntity(String sMid, String tName) throws Exception {
		EntitySSAgentGroup rEntity = new EntitySSAgentGroup();
		EntitySSAgentGroup entity  = getEntity(sMid, tName);

		rEntity.GROUP_NO        = entity.GROUP_NO;
		rEntity.SECT_FROM       = entity.SECT_FROM;
		rEntity.SECT_TO         = entity.SECT_TO;
		rEntity.LAST_UPDATE_DTM = entity.LAST_UPDATE_DTM;

		return rEntity;
	}


	public String showAllEntity(String tName) throws Exception {
		Collection<EntitySSAgentGroup> map = hashtableSSAgentGroup.values();
		Iterator<EntitySSAgentGroup> itr = map.iterator();

		StringBuffer sb = new StringBuffer();
		sb.append("--------[ALL_Entities begin]--------");
		while(itr.hasNext()){
			EntitySSAgentGroup entity = itr.next();
			sb.append(entity.toString() + "\n");
		}
		sb.append("--------[ALL_Entities end]--------");		
		return sb.toString();
	}	

	public synchronized EntitySSAgentGroup upsert(EntitySSAgentGroup ssAgentGroupNew, String tName) {

		EntitySSAgentGroup ssAgentGroupOld = hashtableSSAgentGroup.get(ssAgentGroupNew.GROUP_NO);

		if(ssAgentGroupOld != null) {
			if(  ssAgentGroupNew.SECT_FROM != ssAgentGroupOld.SECT_FROM
			  || ssAgentGroupNew.SECT_TO   != ssAgentGroupOld.SECT_TO   ){
				hashtableSSAgentGroup.put(ssAgentGroupNew.GROUP_NO, ssAgentGroupNew);
			}
		} else {
			hashtableSSAgentGroup.put(ssAgentGroupNew.GROUP_NO, ssAgentGroupNew);
		}
		return hashtableSSAgentGroup.get(ssAgentGroupNew.GROUP_NO);
	}

	public static void main(String args[]){
		SSAgentGroupHandler handler = SSAgentGroupHandler.getInstance();
		//EntitySSAgent entity = new EntitySSAgent();

		try {
			//--01.추가하기
			EntitySSAgentGroup entity01 = new EntitySSAgentGroup();
			entity01.GROUP_NO        = "001";
			entity01.SECT_FROM       = 1;
			entity01.SECT_TO         = 10;
			entity01.LAST_UPDATE_DTM = "20160122133000";
			entity01.IDX_LAST_SSAGENT= 0;
			handler.setEntity(entity01, "THREAD_01");

			EntitySSAgentGroup entity02 = new EntitySSAgentGroup();
			entity02.GROUP_NO        = "002";
			entity02.SECT_FROM       = 1;
			entity02.SECT_TO         = 10;
			entity02.LAST_UPDATE_DTM = "20160122133000";
			entity02.IDX_LAST_SSAGENT= 0;
			handler.setEntity(entity02, "THREAD_01");

			EntitySSAgentGroup entity03 = new EntitySSAgentGroup();
			entity03.GROUP_NO        = "003";
			entity03.SECT_FROM       = 1;
			entity03.SECT_TO         = 10;
			entity03.LAST_UPDATE_DTM = "20160122133000";
			entity03.IDX_LAST_SSAGENT= 0;
			handler.setEntity(entity03, "THREAD_01");

			//--02.복제하기
			EntitySSAgentGroup cEntity = handler.cloneEntity("001", "THREAD_01");
			System.out.println("cEntity..타객체 수정전=" + cEntity.GROUP_NO);

			//--03.원본가져오기
			EntitySSAgentGroup rEntity = handler.getEntity("001", "THREAD_01");
			System.out.println("rEntity..타객체 수정후=" + rEntity.GROUP_NO);

			//--04.출력하기
			String sAllEntities = handler.showAllEntity("THREAD_01");
			System.out.println(sAllEntities);

			//--05.삭제하기
			handler.removeEntity(entity03, "THREAD_01");
			sAllEntities = handler.showAllEntity("THREAD_01");
			System.out.println(sAllEntities);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





}
