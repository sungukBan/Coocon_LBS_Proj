package com.coocon.lbs.handler;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.coocon.lbs.agent.GatewayAgent;

public class GatewayAgentHandler {

	private static GatewayAgentHandler instance;
	private static Map<String, GatewayAgent> hashMapGatewayAgentStatus = new Hashtable<String, GatewayAgent>();

	private GatewayAgentHandler(){
	}

	public static GatewayAgentHandler getInstance(){
		if( instance == null ) {
			synchronized( GatewayAgentHandler.class ) {
				if( instance == null ) {
					instance = new GatewayAgentHandler();
				}
			}
		}
		return instance;
	}

	public synchronized void setEntity(GatewayAgent entity, String tName) throws Exception {
		GatewayAgent oldEntity = hashMapGatewayAgentStatus.remove(entity.GW_SYS_NO);
		if(oldEntity != null) {
			oldEntity.finalize();
			oldEntity = null;
		}
		hashMapGatewayAgentStatus.put(entity.GW_SYS_NO, entity);
	}

	public synchronized void removeEntity(GatewayAgent entity, String tName) throws Exception {
		GatewayAgent oldEntity = hashMapGatewayAgentStatus.remove(entity.GW_SYS_NO);
		if(oldEntity != null) {
			oldEntity.finalize();
			oldEntity = null;
		}
	}

	public synchronized GatewayAgent getEntity(String sMid, String tName) throws Exception {
		return hashMapGatewayAgentStatus.get(sMid);
	}

	public synchronized GatewayAgent cloneEntity(String sMid, String tName) throws Exception {

		GatewayAgent rEntity = new GatewayAgent();
		GatewayAgent entity  = getEntity(sMid, tName);
		
		rEntity.GW_SYS_NO    = entity.GW_SYS_NO;
		rEntity.SOCKET_STS	 = entity.SOCKET_STS;	
		rEntity.CREATED_DTM  = entity.CREATED_DTM;
		rEntity.qHandler     = entity.qHandler;
		
		return rEntity;
	}

	public String showAllEntity(String tName) throws Exception {
		Collection<GatewayAgent> map = hashMapGatewayAgentStatus.values();
		Iterator<GatewayAgent> itr = map.iterator();

		StringBuffer sb = new StringBuffer();
		sb.append("--------[ALL_Entities begin]--------");
		while(itr.hasNext()){
			GatewayAgent entity = itr.next();
			sb.append(entity.toString() + "\n");
		}
		sb.append("--------[ALL_Entities end]--------");		
		return sb.toString();
	}	

	public static void main(String args[]){

		GatewayAgentHandler handler = GatewayAgentHandler.getInstance();
		//EntitySSAgent entity = new EntitySSAgent();

		try {

			//--01.추가하기
			GatewayAgent entity01 = new GatewayAgent();
			entity01.GW_SYS_NO = "001";
			handler.setEntity(entity01, "THREAD_01");

			GatewayAgent entity02 = new GatewayAgent();
			entity02.GW_SYS_NO = "002";
			handler.setEntity(entity02, "THREAD_01");

			GatewayAgent entity03 = new GatewayAgent();
			entity03.GW_SYS_NO = "003";
			handler.setEntity(entity03, "THREAD_01");			

			//--02.복제하기
			GatewayAgent cEntity = handler.cloneEntity("001", "THREAD_01");
			System.out.println("cEntity..타객체 수정전=" + cEntity);

			//--03.원본가져오기
			GatewayAgent rEntity = handler.getEntity("001", "THREAD_01");
			System.out.println("rEntity..타객체 수정후=" + rEntity);

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
