package com.coocon.lbs.monitor;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public class MemoryCheckThread extends Thread{

	static long totalMemory = 0;
	static long freeMemory = 0;
	static long maxMemory = 0;

	public MemoryCheckThread() {
	}

	public void run() {
		while (true) {

			try	{
				totalMemory = Runtime.getRuntime().totalMemory()/1024/1024;
				freeMemory = Runtime.getRuntime().freeMemory()/1024/1024;
				maxMemory = Runtime.getRuntime().maxMemory()/1024/1024;
				
				UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_MONITOR), "use:[" + (totalMemory - freeMemory) + "M], total:[" + totalMemory + " M], free:[" + freeMemory + " M], max:[" + maxMemory + " M]" );
				Thread.sleep(1000*10);

			} catch (Exception e) {
				UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
			}
		}
	}

	public static void showStatus() {
		try	{
			totalMemory = Runtime.getRuntime().totalMemory()/1024/1024;
			freeMemory = Runtime.getRuntime().freeMemory()/1024/1024;
			maxMemory = Runtime.getRuntime().maxMemory()/1024/1024;
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "use:[" + (totalMemory - freeMemory) + "M], total:[" + totalMemory + " M], free:[" + freeMemory + " M], max:[" + maxMemory + " M]" );
		} catch (Exception e) {
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}

	}
}