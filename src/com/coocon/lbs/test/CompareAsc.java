package com.coocon.lbs.test;

import java.io.File;
import java.util.Comparator;

public class CompareAsc implements Comparator<File> {

	private String order = "asc";

	public CompareAsc(String order){
		this.order = order;
	}
	
	/**
	 * 오름차순(ASC)
	 */
	@Override
	public int compare(File arg0, File arg1) {
		
		int iRetVal = -1;

		String sArg0FileName = arg0.getName();
		int iLastIndex0 = sArg0FileName.indexOf(".");
		String sArg0YYYYMMDDHHS = sArg0FileName.substring(iLastIndex0+1).replaceAll("\\.", "");
		
		String sArg1FileName = arg1.getName();
		int iLastIndex1 = sArg1FileName.indexOf(".");
		String sArg1YYYYMMDDHHS = sArg1FileName.substring(iLastIndex1+1).replaceAll("\\.", "");

		if(order.equalsIgnoreCase("asc")){
			iRetVal = sArg0YYYYMMDDHHS.compareTo(sArg1YYYYMMDDHHS);
		}else{
			iRetVal = sArg1YYYYMMDDHHS.compareTo(sArg0YYYYMMDDHHS);
		}
		System.out.println("CompareAsc.compare  iRetVal = [" + iRetVal +"]..sArg0YYYYMMDDHHS = [" + sArg0YYYYMMDDHHS +"]..sArg1YYYYMMDDHHS = [" + sArg1YYYYMMDDHHS +"]");
		return iRetVal;
	}

}