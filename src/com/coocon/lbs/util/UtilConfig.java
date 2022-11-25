package com.coocon.lbs.util;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * UtilConfig.java
 * Descriptions
 * -----------
 * 환경설정 Data를 관리하는 클래스
 *
 */

public class UtilConfig extends Properties { 
	public static String strFileName = "";

	private static UtilConfig conf;


	/**
	* Constractor
	*/
	private UtilConfig() {
	}

	/**
	* UtilConfig 설정내용을 재로딩한다.
	*/
	public synchronized static void reload() {
		conf = null;
		try {
			load(strFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	* UtilConfig Singlton Instance를 리턴
	* @return conf UtilConfig
	*/
	public static UtilConfig getInstance() {
		if (conf == null) {
			reload();
		}
		return conf;
	}

	/**
	* config.properties의 설정내용을 메모리에 올린다
	* @param strConfigDir String
	*/
	public synchronized static void load(String filename) throws Exception {
		if (conf == null) {
			conf = new UtilConfig();

			FileInputStream fin = null;

			try {
				fin = new FileInputStream(filename);
				conf.load(fin);
				
			} catch (Exception e) {
				System.out.println( "UtilConfig::load(String strConfigDir) ERROR");
				System.out.println(e.getMessage());
				throw new Exception("UtilConfig::load(String strConfigDir) ERROR");
			} finally {
				if (fin != null)
					fin.close();
			}
		}
	}

	/**
	* boolean type으로 설정내용을 리턴
	* @param key String
	* @return boolean
	*/
	public static String getValue(String key) {
		if (conf == null) {
			reload();
		}
		if ( conf.getProperty(key) == null )
			return null;
		else
			return (String)conf.getProperty(key);
	}
	
	/**
	* boolean type으로 설정내용을 리턴
	* @param key String
	* @return boolean
	*/
	public static String[] getValues(int iCnt, String sInfoCnt)
	{
		String[] sArray = null;

		if (conf == null) {
			reload();
		}
		
		sArray = new String[iCnt];
		
		//--svpc.con.info.0005=https://127.0.0.5:35751/SCRAP;0005
		for(int i=0; i<iCnt; i++)
		{
			System.out.println(UtilConfig.getValue(sInfoCnt + "." + UtilCommon.fillZeros(4, (i+1)+"")));
			sArray[i] = UtilConfig.getValue(sInfoCnt + "." + UtilCommon.fillZeros(4, (i+1)+""));
		}
		return sArray;
	}	
}
