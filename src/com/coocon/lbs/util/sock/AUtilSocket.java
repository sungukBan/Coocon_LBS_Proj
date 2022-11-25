package com.coocon.lbs.util.sock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.ssl.SSLSocket;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;

public abstract class AUtilSocket implements IUtilSocket {

	protected DataInputStream  m_dis 	 = null;
	protected DataOutputStream m_dos 	 = null;
	
	abstract public String getRemoteSocketIP() throws Exception;
	abstract public void setTimeout(int nSec) throws Exception;
	
	public int send(byte[] bSend) throws Exception 
	{
		try 
		{
			m_dos.write( bSend, 0, bSend.length );
			m_dos.flush();
		} 
		catch( Exception e ) 
		{
			e.printStackTrace();
			throw e;
		}
		return bSend.length;
	}

	public byte[] read(int nSize) throws Exception 
	{
		byte[] bRecv = null;
		int nLen = 0;

		try 
		{
			bRecv = new byte[nSize];
			while (nLen < nSize) {
				int nReadLen = m_dis.read( bRecv, nLen, nSize-nLen);
				if ( nReadLen <= 0 ) {
					throw new Exception("data read 오류 : size(" + nSize + ")만큼 읽지 못함");
				}

				nLen = nLen + nReadLen;
			}
		} 
		catch( IOException ioe )
		{
			throw ioe;
		}
		catch( Exception e ) 
		{
			throw e;
		}

		return bRecv;
	}

  	public byte[] readFully() throws Exception 
  	{
  	  	byte[] bRecv = new byte[1024];
  	  
  	  	try 
  	  	{
  	  	  	System.out.println( "----------------->>>>>>>><<<<<<<<<<<<<<<-----------" );
  	  	  	
  	  	  	m_dis.read(bRecv);
  	  	  	
			System.out.println( "RECEIVE MESSAGE=[" + bRecv.length + "][" + new String(bRecv) + "]" );
  	  	} 
  	  	catch( IOException ioe )
  	  	{
  	  	    System.out.println( "" );
  	  	  	System.out.println( ".........ioe.getMessage()=" + ioe.getMessage() );
  	  	  	System.out.println( "" );
  	  	  	throw ioe;
  	    }
  	  	catch( Exception e ) 
  	  	{
			e.printStackTrace();
  	  	  	throw e;
  	  	}
  	  	
  	  	return bRecv;
  	}

	public void finalize() throws Exception  
	{
		try 
		{
			if( m_dis != null )	m_dis.close();
			if( m_dos != null ) m_dos.close();
		} 
		catch( Exception e ) 
		{
			throw e;
		}
	}
	
	public static String getRemoteSocketIP2(Socket _socket) throws Exception 
	{
		String IP = "";
		try {
			SocketAddress socketAddress = _socket.getRemoteSocketAddress();
			if (socketAddress instanceof InetSocketAddress) {
				InetAddress inetAddress = ((InetSocketAddress)socketAddress).getAddress();
				if (inetAddress instanceof Inet4Address)
					IP = "" + inetAddress;
				else if (inetAddress instanceof Inet6Address)
					IP = "" + inetAddress;
			} else {
				System.err.println("Not an internet protocol socket.");
			}
		} catch( Exception e ) {
			throw e;
		}
		return IP.replaceAll("[^0-9.]", "");
	}

	public static String getRemoteSocketIP2(SSLSocket _socket) throws Exception 
	{
		String IP = "";
		try {
			SocketAddress socketAddress = _socket.getRemoteSocketAddress();
			if (socketAddress instanceof InetSocketAddress) {
				InetAddress inetAddress = ((InetSocketAddress)socketAddress).getAddress();
				if (inetAddress instanceof Inet4Address)
					IP = "" + inetAddress;
				else if (inetAddress instanceof Inet6Address)
					IP = "" + inetAddress;
			} else {
				System.err.println("Not an internet protocol socket.");
			}
		} catch( Exception e ) {
			throw e;
		}
		return IP.replaceAll("[^0-9.]", "");
	}
	
	public static boolean isL4HealthCheck(Socket m_socket) {
		try {
			String sRemoteIp = getRemoteSocketIP2(m_socket).trim();
			
			String sIpL4List = UtilConfig.getValue(ConstConfig.IP_L4_LIST).trim();
			
			//UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "~~~~~~isL4HealthCheck sRemoteIp=["+sRemoteIp+"]..sIpL4List=["+sIpL4List+"]...........");
			
			if( sIpL4List != null) {
				if(sIpL4List.indexOf(sRemoteIp) != -1) { 
					//UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), "~~~~~~L4 Health Check...........");
					return true;
				} else {
					return false;
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isL4HealthCheck(SSLSocket m_socket) {
		try {
			String sRemoteIp = getRemoteSocketIP2(m_socket).trim();
			
			String sIpL4List = UtilConfig.getValue(ConstConfig.IP_L4_LIST).trim();
			
			if( sIpL4List != null) {
				if(sIpL4List.indexOf(sRemoteIp) != -1) 
					return true;
				else
					return false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
}
