package com.coocon.lbs.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.coocon.lbs.consts.ConstConfig;


public class FileUtil {

	public static void main(String[] args) {
		FileUtil s = new FileUtil();

		String sampleFile = "C:\\file_test\\src";
		String outSrc     = "C:\\file_test\\dst";

		try {

			s.copy(sampleFile, outSrc);

			if(true) return;

			s.mkdir("C:\\file_test\\tempDir");

			s.mkfile("C:\\file_test\\tempDir\\aaa.txt");

			Thread.sleep(1000*5);

			s.renamefile("C:\\file_test\\tempDir\\aaa.txt", "C:\\file_test\\tempDir\\aaa.bak");

			Thread.sleep(1000*5);

			s.delfile("C:\\file_test\\tempDir\\aaa.bak");


			s.copy("C:\\file_test\\src", "C:\\file_test\\dst222");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void mkfile(String sFileName) throws Exception{
		File m_file = new File(sFileName);
		m_file.createNewFile();
	}

	public static void delfile(String sFileName) throws Exception{
		File m_file = new File(sFileName);
		m_file.delete();
	}

	public static void renamefile(String srcFile, String dstFile) throws Exception{
		File m_file_src = new File(srcFile);
		File m_file_dst = new File(dstFile);
		m_file_src.renameTo(m_file_dst);
	}

	public static void renamefile(File srcFile, String dstFile) throws Exception{
		File m_file_dst = new File(dstFile);
		srcFile.renameTo(m_file_dst);
	}

	public static void renamefile(File srcFile, File dstFile) throws Exception{
		srcFile.renameTo(dstFile);
	}


	public static void mkdir(String sPath) throws Exception{
		File m_dir = new File(sPath);
		m_dir.mkdir();
	}

	public static int copy(String srcPath, String destPath) throws FileNotFoundException
	{
		int count = 0;
		File srcFile  = new File(srcPath);
		File destFile = new File(destPath);

		//���� ���� ����
		if(!srcFile.exists())
		{
			throw new FileNotFoundException("������ �������� �ʽ��ϴ�.");
		}

		//����üũ �� ����
		if(srcFile.isFile()){
			copyFile(srcFile,destFile);
			//���丮 üũ �� ����
		}else if(srcFile.isDirectory()){
			copyDir(srcFile,destFile);
		}

		return count;  
	}

	//���Ϻ���
	public static void copyFile(File source,File dest)
	{
		long startTime = System.currentTimeMillis();

		int count = 0;
		long totalSize = 0;
		byte[] b = new byte[128];

		FileInputStream  in = null;
		FileOutputStream out = null; 

		//��������� ���� ���� ��Ʈ�� ���
		BufferedInputStream  bin  = null;
		BufferedOutputStream bout = null;

		try { 
			in  = new FileInputStream(source);
			bin = new BufferedInputStream(in);

			out  = new FileOutputStream(dest);
			bout = new BufferedOutputStream(out);
			while((count = bin.read(b))!= -1){
				bout.write(b,0,count);
				totalSize += count;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally{// ��Ʈ�� close �ʼ�
			try {
				if(bout!=null){
					bout.close();
				}    
				if (out != null){
					out.close();
				}
				if(bin!=null){
					bin.close();
				}
				if (in != null){
					in.close();
				}

			} catch (IOException r) {
				// TODO: handle exception
				System.out.println("close ���� ���� �߻�.");
			}
		}
		//���� �ð� üũ 
		StringBuffer time = new StringBuffer("�ҿ�ð� : ");
		time.append(System.currentTimeMillis() - startTime);
		time.append(",FileSize : " + totalSize);
		System.out.println(time);
	}


	//�����б� - ����/���丮 ����...
	public static File[] readFilesFromDir(String path){

		File source = new File(path);

		if( !source.exists()){
			throw new IllegalArgumentException("SRC ���丮 ����");
		}

		File[] fileList = source.listFiles();//������ ���ϸ���Ʈ ��������

		for(int i=0;i<fileList.length;i++){
			File sourceFile = fileList[i];
			System.out.println("..readFilesFromDir..sourceFile=["+sourceFile.getName()+"]");
			//File destFile = new File(dest,sourceFile.getName());
			//copyFile(sourceFile,destFile);//copyFile�޼ҵ� ����
		}
		return fileList;

	}

	//���丮 ���� -> ���Ϻ���
	public static void copyDir(File source, File dest){
		long startTime = System.currentTimeMillis();

		dest.mkdirs();//����

		if( !source.exists() || !dest.isDirectory()){
			throw new IllegalArgumentException("SRC ���丮 ����");
		}

		File[] fileList = source.listFiles();//������ ���ϸ���Ʈ ��������

		for(int i=0;i<fileList.length;i++){
			File sourceFile = fileList[i];

			File destFile = new File(dest,sourceFile.getName());
			copyFile(sourceFile,destFile);//copyFile�޼ҵ� ����
		}

		//���� �ð� üũ 
		StringBuffer time = new StringBuffer("�ҿ�ð� : ");
		time.append(System.currentTimeMillis() - startTime);
		time.append(",File Total List : " +  fileList.length);
		System.out.println(time);
	}

	public static void writeFileFromByteArray(String sRootPath, String sFileName,
			byte[] file_contents) throws Exception {

		File fRoot = new File(sRootPath);
		if(fRoot.exists() == false) fRoot.mkdir();

		File fDstFile = new File(sRootPath + fRoot.separator + sFileName);
		if(fDstFile.exists() == true) {
			File fBackupFile = new File(fDstFile.getAbsoluteFile()+ "." + UtilCommon.getDate()+ "." + UtilCommon.getHHmmss());
			fDstFile.renameTo(fBackupFile);
		}
		fDstFile = new File(sRootPath + fRoot.separator + sFileName);
		fDstFile.createNewFile();

		UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), " :: 99.���������ϱ� fDstFile=["+fDstFile.getAbsolutePath()+"]");

		FileOutputStream     out  = new FileOutputStream(fDstFile);
		BufferedOutputStream bout = new BufferedOutputStream(out);
		bout.write(file_contents);

		if(bout != null) bout.close();
		if(out != null) out.close();

	}

	
	/**
	 * ���� �����۾��� �����Ѵ�.
	 */
	public static void fileMerge(File srcFile, File destFile) {

		System.out.println("������ �����մϴ�...srcFile=["+srcFile.getAbsolutePath()+"]..destFile=["+destFile.getAbsolutePath()+"]");
		long startTime = System.currentTimeMillis();
	
		FileWriter outfile = null;
		BufferedReader bufferedReader = null;
		String msg = "";
		
		try {
			File destDir = new File(destFile.getParent());
			
			if( !destDir.exists() || !destDir.isDirectory()){
				destDir.mkdirs();//����
			}
			
			if(destFile.exists() == false) {
				destFile.createNewFile();
			}
			
			outfile = new FileWriter( destFile.getAbsolutePath(), true );
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
			
			while((msg = bufferedReader.readLine()) != null){
	            outfile.write(msg + UtilCommon.getEsc());
	            outfile.flush();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try{
				if( bufferedReader != null ) bufferedReader.close();
				if( outfile        != null ) outfile.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		//���� �ð� üũ 
		StringBuffer time = new StringBuffer("�ҿ�ð� : ");
		time.append(System.currentTimeMillis() - startTime + " ms");
		System.out.println(time);
	} 
 
}


