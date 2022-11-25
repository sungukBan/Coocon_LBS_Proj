package com.coocon.lbs.util.sock;

public interface IUtilSocket {
	
	int send(byte[] bSend) throws Exception;

   	byte[] read(int nSize) throws Exception; 

  	byte[] readFully() throws Exception;

  	void setTimeout(int nSec) throws Exception; 

  	String getRemoteSocketIP() throws Exception;
  	
  	void finalize() throws Exception;
}
