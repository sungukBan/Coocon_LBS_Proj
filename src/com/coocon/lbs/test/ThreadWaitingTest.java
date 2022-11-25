package com.coocon.lbs.test;

import com.coocon.lbs.handler.GatewayRecvHandler;

public class ThreadWaitingTest {

	public static void main(String[] args){
		ThreadWaitingTest cs = new ThreadWaitingTest();
		cs.start();
	}

	private void start() {
		try {
			new GatewayRecvHandler().start();

			//new EnqueueTest().start();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
