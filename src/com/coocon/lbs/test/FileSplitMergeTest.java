package com.coocon.lbs.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSplitMergeTest {
	public static void main(String[] args) throws Exception {
		split("c:\\test.log", 3 * 1024 * 1024, false);

		String test[] = {"c:\\test.log_1", "c:\\test.log_2", "c:\\test.log_3", "c:\\test.log_4"};
		merge("c:\\test2.log", test, false);
	}

	/**
	 * If file size is more than flleSpliteSize parameter, the file will be divided to seperated files.
	 * @param filename : File name with full path
	 * @param fileSplitSize
	 * @param deleteOrgFile : weather delete original file or not
	 */
	public static void split(String filename, long fileSplitSize, boolean deleteOrgFile) throws Exception{
		try {
			File file = new File(filename);
			String path = file.getParent();
			FileInputStream in = new FileInputStream(file);

			long originTotalFileLength = file.length();
			long jobProcess = fileSplitSize;
			long chunkCnt = (long)Math.ceil((double)originTotalFileLength / (double)fileSplitSize);

			for (int i = 1; i <= chunkCnt; i++) {
				FileOutputStream fout = new FileOutputStream(filename + "_" + i);
				int len = 0;
				byte[] buf = new byte[1024];

				while ((len = in.read(buf, 0, 1024)) != -1) {
					fout.write(buf, 0, len);
					jobProcess = jobProcess + len;

					if (fileSplitSize * (i + 1) == jobProcess) break;
				}
				fout.flush();
				fout.close();
			}
			in.close();

			if (deleteOrgFile && new File(filename).exists()) new File(path, filename).delete();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Merge Files
	 * @param newFileName
	 * @param filenames
	 * @param removeSplittedFiles
	 * @throws Exception
	 */
	public static void merge(String newFileName, String[] filenames, boolean removeSplittedFiles) throws Exception {
		FileOutputStream fout = null;
		FileInputStream in = null;
		BufferedInputStream bis = null;
		try {
			File newFile = new File(newFileName);
			fout = new FileOutputStream(newFile);

			for (String fileName : filenames) {
				File splittedFile = new File(fileName);
				in = new FileInputStream(splittedFile);
				bis = new BufferedInputStream(in);
				int len = 0;
				byte[] buf = new byte[1024];
				while ((len = bis.read(buf, 0, 1024)) != -1) {
					fout.write(buf, 0, len);
				}

				if(removeSplittedFiles && splittedFile.exists()) splittedFile.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				fout.close();
				in.close();
				bis.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
