package com.coocon.lbs.test;

import java.io.File;

import com.coocon.lbs.util.FileUtil;

public class FileMergeTest {

	public static void main(String [] args){
		FileMergeTest cs = new FileMergeTest();
		cs.start();
	}

	private void start() {
		
		String srcFile1 = "C:\\temp\\log_TestJunmunLogBy10Minutes\\gateway.20171204.104000";
		String srcFile2 = "C:\\temp\\log_TestJunmunLogBy10Minutes\\gateway.20171204.105000";
		String srcFile3 = "C:\\temp\\log_TestJunmunLogBy10Minutes\\gateway.20171204.110000";
		
		String sDestFile = "C:\\temp\\log_TestJunmunLogBy10Minutes\\gateway.20171204";
		
		try {
			
			File destFile = new File(sDestFile);
			if(destFile.exists() == false) {
				destFile.createNewFile();
			}
			
			FileUtil.fileMerge(new File(srcFile1), destFile);
			
			FileUtil.fileMerge(new File(srcFile2), destFile);
			
			FileUtil.fileMerge(new File(srcFile3), destFile);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
