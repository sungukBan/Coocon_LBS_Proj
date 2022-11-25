package com.coocon.lbs.handler;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.entity.EntityTxCode;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;

public class TxCodeHandler {

	private static TxCodeHandler instance;
	private static String [] sArrayTxCode = null;
	private static Map<String, EntityTxCode> hashMapTxCodeEntity = new Hashtable<String, EntityTxCode>();
	private static boolean TXCODE_INFO_USE_YN = false;
	
	private TxCodeHandler(){
		init();
	}

	public static TxCodeHandler getInstance(){
		if( instance == null ){
			synchronized( TxCodeHandler.class ){
				if( instance == null ){
					instance = new TxCodeHandler();
				}
			}
		}
		return instance;
	}
	
	/*
	 * Config파일에 등록된 SS서버 정보 세팅
	 */
	private void init()
	{
		if(   (UtilConfig.getValue(ConstConfig.TXCODE_INFO_USE_YN) != null) 
		   && UtilConfig.getValue(ConstConfig.TXCODE_INFO_USE_YN).equalsIgnoreCase("Y") ){
			TXCODE_INFO_USE_YN = true;	
			setTxCodeAgentValues(hashMapTxCodeEntity);
		}else{
			TXCODE_INFO_USE_YN = false;
		}
	}

	public Map<String, EntityTxCode> createNewTxCodeMap() {
		
		Map<String, EntityTxCode> newHashMapTxCodeEntity = new Hashtable<String, EntityTxCode>();
		if(TXCODE_INFO_USE_YN == false) return newHashMapTxCodeEntity;
		
		setTxCodeAgentValues(newHashMapTxCodeEntity);
		return newHashMapTxCodeEntity;
	}
		
	/*
	 * Config파일에 등록된 SS서버 정보 세팅
	 */
	private static void setTxCodeAgentValues(Map<String, EntityTxCode> hashMapTxCodeAgent)
	{
		try
		{
			if(TXCODE_INFO_USE_YN == false) return;
			
			sArrayTxCode = UtilConfig.getValues(Integer.parseInt(UtilConfig.getValue(ConstConfig.TXCODE_INFO_CNT)), UtilConfig.getValue(ConstConfig.TXCODE_INFO));

			for(int i=0; i<sArrayTxCode.length; i++){
	
				String []sArrayBizInfoSplit = sArrayTxCode[i].split(";");
				
				EntityTxCode txCodeEntity = new EntityTxCode();
				
				txCodeEntity.TX_CODE         = sArrayBizInfoSplit[0];
				txCodeEntity.SS_MAX_RUN_CNT  = Integer.parseInt(sArrayBizInfoSplit[1].trim());
				txCodeEntity.SS_NOW_RUN_CNT  = 0;
				txCodeEntity.MSG_GROUP_CODE  = sArrayBizInfoSplit[2];
				txCodeEntity.MSG_SECT_START  = sArrayBizInfoSplit[3];
				txCodeEntity.MSG_SECT_END    = sArrayBizInfoSplit[4];
				txCodeEntity.CREATED_DTM     = UtilCommon.getDate() + UtilCommon.getHHmmss();
				txCodeEntity.REFRESHED_DTM   = txCodeEntity.CREATED_DTM;
			
				hashMapTxCodeAgent.put(txCodeEntity.TX_CODE, txCodeEntity);
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	
	public synchronized void setEntity(EntityTxCode entity, String tName) throws Exception {
		
		if(TXCODE_INFO_USE_YN == false) return;
		
		EntityTxCode oldEntity = hashMapTxCodeEntity.remove(entity.TX_CODE);
		if(oldEntity != null) {
			oldEntity.finalize();
			oldEntity = null;
		}
		hashMapTxCodeEntity.put(entity.TX_CODE, entity);
	}	

	public synchronized void removeEntity(EntityTxCode entity, String tName) throws Exception {
		
		if(TXCODE_INFO_USE_YN == false) return;
		
		EntityTxCode oldEntity = hashMapTxCodeEntity.remove(entity.TX_CODE);
		if(oldEntity != null) {
			oldEntity.finalize();
			oldEntity = null;
		}
	}	
	
	public synchronized EntityTxCode getEntity(String sTxCode, String tName) throws Exception {
		if(TXCODE_INFO_USE_YN == false) return null;
		return hashMapTxCodeEntity.get(sTxCode);
	}

	public synchronized EntityTxCode cloneEntity(String sTxCode, String tName) throws Exception {

		if(TXCODE_INFO_USE_YN == false) return null;
		
		EntityTxCode rEntity = new EntityTxCode();
		EntityTxCode entity  = getEntity(sTxCode, tName);
		
		rEntity.TX_CODE        = entity.TX_CODE        ;
		rEntity.SS_MAX_RUN_CNT = entity.SS_MAX_RUN_CNT ;
		rEntity.SS_NOW_RUN_CNT = entity.SS_NOW_RUN_CNT ;
		rEntity.MSG_GROUP_CODE = entity.MSG_GROUP_CODE ;
		rEntity.MSG_SECT_START = entity.MSG_SECT_START ;
		rEntity.MSG_SECT_END   = entity.MSG_SECT_END   ;
		rEntity.CREATED_DTM    = entity.CREATED_DTM    ;
		rEntity.REFRESHED_DTM  = entity.REFRESHED_DTM  ;
		
		return rEntity;
	}
	
	public String showAllEntity(String tName) throws Exception {
		
		if(TXCODE_INFO_USE_YN == false) return null;
		
		Collection<EntityTxCode> map = hashMapTxCodeEntity.values();
		Iterator<EntityTxCode> itr = map.iterator();

		StringBuffer sb = new StringBuffer();
		sb.append("--------[ALL_Entities begin]--------\n");
		while(itr.hasNext()){
			EntityTxCode entity = itr.next();
			sb.append(entity.toString() + "\n");
		}
		sb.append("--------[ALL_Entities end]--------");		
		return sb.toString();
	}		


	public static void main(String args[]){

		//--테스트를 위해서 config 파일정보 하드코딩 처리함.
		UtilConfig.strFileName = "E:\\COOCON_SVN\\Coocon_LBS_Proj\\config\\config.properties";
		
		TxCodeHandler handler = TxCodeHandler.getInstance();
		

		try {

			handler.showAllEntity("THREAD_01");
			String sAllEntities = handler.showAllEntity("THREAD_01");
			System.out.println(sAllEntities);
			
			Thread.sleep(1000*1);
			
			
			//--01.추가하기 biz.info.0001=MULT;N;10;100;0001;0050
			EntityTxCode entity01 = new EntityTxCode();
			entity01.TX_CODE        = "MULT";
			entity01.SS_MAX_RUN_CNT = 10;
			entity01.SS_NOW_RUN_CNT = 0;
			entity01.MSG_GROUP_CODE = "100";
			entity01.MSG_SECT_START = "001";
			entity01.MSG_SECT_END   = "050";
			entity01.CREATED_DTM    = "20180227103000";
			entity01.REFRESHED_DTM  = "20180227103000";
			handler.setEntity(entity01, "THREAD_01");

			//--02.추가하기 biz.info.0002=9913;Y; 1;200;0001;0050
			EntityTxCode entity02 = new EntityTxCode();
			entity02.TX_CODE        = "5555";
			entity02.SS_MAX_RUN_CNT = 1;
			entity02.SS_NOW_RUN_CNT = 0;
			entity02.MSG_GROUP_CODE = "200";
			entity02.MSG_SECT_START = "001";
			entity02.MSG_SECT_END   = "050";
			entity02.CREATED_DTM    = "20180227103000";
			entity02.REFRESHED_DTM  = "20180227103000";
			handler.setEntity(entity02, "THREAD_01");

			//--03.추가하기 biz.info.0003=9902;N; 1;300;0001;0050
			EntityTxCode entity03 = new EntityTxCode();
			entity03.TX_CODE        = "4444";
			entity03.SS_MAX_RUN_CNT = 1;
			entity03.SS_NOW_RUN_CNT = 0;
			entity03.MSG_GROUP_CODE = "300";
			entity03.MSG_SECT_START = "001";
			entity03.MSG_SECT_END   = "050";
			entity03.CREATED_DTM    = "20180227103000";
			entity03.REFRESHED_DTM  = "20180227103000";
			handler.setEntity(entity03, "THREAD_01");

			//--04.추가하기	biz.info.0004=xxxx;N; 1;400;0001;0050
			EntityTxCode entity04 = new EntityTxCode();
			entity04.TX_CODE        = "3333";
			entity04.SS_MAX_RUN_CNT = 10;
			entity04.SS_NOW_RUN_CNT = 0;
			entity04.MSG_GROUP_CODE = "400";
			entity04.MSG_SECT_START = "001";
			entity04.MSG_SECT_END   = "050";
			entity04.CREATED_DTM    = "20180227103000";
			entity04.REFRESHED_DTM  = "20180227103000";
			handler.setEntity(entity04, "THREAD_01");

			
			//--04.출력하기
			sAllEntities = handler.showAllEntity("THREAD_01");
			System.out.println(sAllEntities);

			//--03.원본가져오기
			EntityTxCode rEntity = handler.getEntity("3333", "THREAD_01");
			System.out.println("rEntity..타객체 수정후=" + rEntity);

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


