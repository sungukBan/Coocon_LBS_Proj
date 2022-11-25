package com.coocon.lbs.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.coocon.lbs.entity.EntityMsgCommon;
import com.coocon.lbs.msg.MsgCommon;
import com.coocon.lbs.util.FileUtil;
import com.coocon.lbs.util.UtilCommon;

public class TestJunmunLoaderBy10Minutes {
	public static void main(String[] args){
		TestJunmunLoaderBy10Minutes cs = new TestJunmunLoaderBy10Minutes();
		cs.start();
	}

	private void start() {
		
		String dir      = "C:\\temp\\log_TestJunmunLogBy10Minutes\\";
		String order    = "asc";
		
		try {
			List<File> voList = getSortedFormattedFiles(dir, order);
			
			Iterator<File> itr = voList.iterator();
			while(itr.hasNext()){
				File file = itr.next();
				System.out.println("##################################################");
				System.out.println(file.getAbsolutePath());
				System.out.println("##################################################");
			
				if(file.isFile() != true){
					System.out.println("file 이 아니라 skip 처리합니다.");
					continue;
				}
				
				//--01.파일 1개을 DB 에 넣기...
				handleOneFile(file);
			
				if(true) break;
				
				//--02.파일을 전체 파일로 합쳐기
				mergeOneFile(file);
				
				//--03.파일 1개를 백업하기
				backupOneFile(file);
				
				//--99.5초 sleep 처리(임시로...)
				System.out.println("\n~~~1초 sleep 이후 다시 진행합니다.~~~~~\n");
				Thread.sleep(1000*1);
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	private void mergeOneFile(File fSrcFile) throws Exception {

		System.out.println("1.file.getParent()=["+fSrcFile.getParent()+"]");
		
		File destDir = new File(fSrcFile.getParent()+File.separator+"merge");
		
		System.out.println("dest=["+destDir.getAbsolutePath()+"]");
		
		//--dest=[C:\temp\log_TestJunmunLogBy10Minutes\merge]
		//if( !destDir.exists() || !destDir.isDirectory()){
		//	destDir.mkdirs();//생성
		//}
		
		//--
		//C:\temp\log_TestJunmunLogBy10Minutes\gateway.20171204.104000
		//--
		
		String sFileName = fSrcFile.getName().substring(0, fSrcFile.getName().lastIndexOf("."));
		System.out.println("sFileName=["+sFileName+"]");

		File fMergeFile = new File(destDir.getAbsolutePath()+File.separator+sFileName);
		FileUtil.fileMerge(fSrcFile, fMergeFile);

	}

	private void backupOneFile(File file) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("1.file.getParent()=["+file.getParent()+"]");
	
		File dest = new File(file.getParent()+File.separator+"backup");
		
		System.out.println("dest=["+dest.getAbsolutePath()+"]");
		dest.mkdirs();//생성
		
		if( !dest.exists() || !dest.isDirectory()){
			throw new Exception("DEST 디렉토리 생성안됐음");
		}
		
		
		FileUtil.renamefile(file
				          , dest.getAbsolutePath()+File.separator+file.getName()+"."+UtilCommon.getDate()+UtilCommon.getHHmmss());
	}

	private void handleOneFile(File file) {
		// TODO Auto-generated method stub
		/*
17:03:59:303 (LBS --> SS) SEND==>000150BIGDSLB00001100000000800000220171204170359        REAL                01703590000010010       Y000000                                           $
17:03:59:360 (LBS <-- SS) SEND==>000150BIGDSLB00001200000870810000220171204170400        REAL                01703590000010010       Y008000                                           $
17:03:59:498 (LBS --> GW) ==>000016REQPOLLING$
17:03:59:500 (LBS <-- GW) ==>000016RESPOLLING$
17:04:04:515 (LBS --> SS) SEND==>000150BIGDSLB00001100000000800000220171204170404        REAL                01704040000010010       Y000000                                           $
17:04:04:519 (LBS <-- GW) ==>000251SGWCSGW      002    0600132020171204170404        gXl7tPnAn2Pwds941UTu02351920606010660       004오희경                                            5710072351810                                                                             20000114$
17:04:04:605 (LBS --> SS) SEND==>000251SGWCSGW      00206090600132020171204170404        gXl7tPnAn2Pwds941UTu02351920606010660       004오희경                                            5710072351810                                                                             20000114$
		 */
		String sOneLine = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
			while((sOneLine = bufferedReader.readLine()) != null){
				System.out.println("1.sOneLine=["+sOneLine.getBytes().length+"]["+sOneLine+"]");
				int iStartIndex = sOneLine.indexOf("==>") + "==>".length();
				int iMinLen     = iStartIndex + 100;
				
				if( sOneLine.getBytes().length < iMinLen ){
					System.out.println("skipping.....");
					Thread.sleep(1000*1);
					continue;
				}
				
				String sTrimedOneLine = sOneLine.substring(iStartIndex);
				System.out.println("2.sTrimedOneLine=["+sTrimedOneLine.getBytes().length+"]["+sTrimedOneLine+"]\n");
				
				
				
				//--공통부 영문/숫자만 있음....한글 있다면 
				byte bCommon[] = new byte[100];
				System.arraycopy(sTrimedOneLine.getBytes(), 0, bCommon, 0, bCommon.length);
				sTrimedOneLine = new String(bCommon);
				System.out.println("3.sTrimedOneLine=["+sTrimedOneLine.getBytes().length+"]["+sTrimedOneLine+"]\n");
				
				MsgCommon mCommon = new MsgCommon();
				mCommon.fromByteArray(bCommon);
				EntityMsgCommon eCommon = new EntityMsgCommon();
				mCommon.setEntity(eCommon);
				
				System.out.println("3.eCommon.tx_type=["+eCommon.tx_type+"]["+eCommon.tx_cd+"]\n");
				
				if(eCommon.tx_type.equals("0800") || eCommon.tx_type.equals("0810")){
					System.out.println("4.운영전문....");
				}else{
					System.out.println(mCommon.toString());	
					System.out.println("doing next soon.....");
					Thread.sleep(1000*1);
				}
				
				//break;
				//Thread.sleep(1000*2);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try { if(bufferedReader != null) bufferedReader.close(); } catch(Exception e){}
		}
	}

	private List<File> getSortedFormattedFiles(String dir, String order){
		
		//String dir      = "C:\\temp\\log_TestJunmunLogBy10Minutes\\";
		List<File> voList = null;
		
		try {
			System.out.println("1----------------------------------");
			File[] fileList = FileUtil.readFilesFromDir(dir);
			voList = new ArrayList<File>();

			System.out.println("2----------------------------------");
			
			String sYYYYMMDDHHMMSS = UtilCommon.getDate() + UtilCommon.getHHmmss();
			//String sYYYYMMDDHHMMSS = "20171204104000";		
			System.out.println("sYYYYMMDDHHMMSS=["+sYYYYMMDDHHMMSS+"]");
			for(int i=0; i<fileList.length; i++){
				System.out.println("..getSortedFormattedFiles..getName()=["+fileList[i].getName() +"]");
				
				if(fileList[i].isFile() != true) continue;
				
				String sName = fileList[i].getName();
				int iIndex = sName.indexOf(".");
				String sYYYYMMDDHHM000 = sName.substring(iIndex+1).replaceAll("\\.", "")+"000";
				
				if( sYYYYMMDDHHM000.compareTo(sYYYYMMDDHHMMSS) < 0 ){
					voList.add(fileList[i]);
					System.out.println("..getSortedFormattedFiles..getName()=["+fileList[i].getName() +"]...added...");
				}
			}
			
			System.out.println("3----------------------------------");
			Collections.sort(voList, new CompareAsc(order));			
			//Collections.sort(voList, new CompareAsc(order));
			
			System.out.println("4-----------sorted display-----------------------");
			display(voList);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return voList;
	}
	

	private void display(List<File> voList) {
		// TODO Auto-generated method stub
		System.out.println("4----------------------------------");
		Iterator<File> itr = voList.iterator();
		while(itr.hasNext()){
			File file = itr.next();
			System.out.println(file.getAbsolutePath());
		}
		System.out.println("5----------------------------------");
		
	}

}
