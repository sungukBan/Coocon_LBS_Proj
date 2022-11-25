package com.coocon.lbs.test;

import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilLogger;


public class TestJunmunLogBy10Minutes {
	public static void main(String[] args){
		TestJunmunLogBy10Minutes cs = new TestJunmunLogBy10Minutes();
		cs.start();
	}

	private void start() {
		
		String dir      = "C:\\temp\\log_TestJunmunLogBy10Minutes\\";
		String fileName = "gateway";
		String msg      = "msg~~~~~~~~";
		int iLoop       =  0;
		
		try {
			
			while(iLoop < 10000 ){
				UtilLogger.junmunLogBy10Minutes(dir, fileName, UtilCommon.fillZeros(10, (iLoop+1)+"")+msg);
				System.out.print(".");
				if(iLoop%100 == 0 ) System.out.print("\n");
				Thread.sleep(1000*5);
				iLoop++;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
