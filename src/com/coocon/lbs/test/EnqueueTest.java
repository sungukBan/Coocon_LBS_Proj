package com.coocon.lbs.test;

import com.coocon.lbs.handler.GatewayRecvHandler;
import com.coocon.lbs.queue.QueueEntity;
import com.coocon.lbs.util.UtilCommon;

public class EnqueueTest extends Thread {

	public EnqueueTest(){

	}

	public void run() {
		QueueEntity entity = null;
		while(true) {
			try {
				
				System.out.println(UtilCommon.getHHmmss() + " EnqueueTest...enQueuing...");
				entity = new QueueEntity();
				GatewayRecvHandler.gatewayRecvQueueHandler.recvEnqueue(entity);
				System.out.println(UtilCommon.getHHmmss() + " EnqueueTest...Sleeping...5√ ");
				Thread.sleep(1000*5);
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}