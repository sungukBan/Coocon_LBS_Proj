package com.coocon.lbs.util.sock;

public class TestUtilSocket {

	public static void main(String []args){
		TestUtilSocket cs = new TestUtilSocket();
		cs.start();
	}

	private void start() {

		try{
			IUtilSocket sc    = new UtilSocketImpl("127.0.0.1", 15000); 
			sc.setTimeout(1000*10);
			sc.send(null);
			sc.read(0);
			sc.finalize();
			
			IUtilSocket scSSL = new UtilSocketImplSSL("127.0.0.1", 15000);
			scSSL.setTimeout(1000*10);
			scSSL.send(null);
			scSSL.read(0);
			scSSL.finalize();					
					
					
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}
