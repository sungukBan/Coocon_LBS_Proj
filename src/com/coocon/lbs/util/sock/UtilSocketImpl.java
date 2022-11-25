package com.coocon.lbs.util.sock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class UtilSocketImpl extends AUtilSocket {

	private Socket m_sock = null;
	
	public UtilSocketImpl( String strHost, int nPort ) throws Exception 
	{
		try 
  	  	{
  	  	  	m_sock = new Socket( strHost, nPort );
  	  	  	m_dis  = new DataInputStream(  m_sock.getInputStream() );
  	  	  	m_dos  = new DataOutputStream( m_sock.getOutputStream() );
  	  	} 
  	  	catch( Exception e ) 
  	  	{
  	  	  	throw e;
  	  	}
	}


	public UtilSocketImpl( Socket sock ) throws Exception 
	{
		try 
		{
			m_sock = sock;
			m_dis = new DataInputStream( m_sock.getInputStream() );
			m_dos = new DataOutputStream( m_sock.getOutputStream() );
		} 
		catch( Exception e ) 
		{
			throw e;
		}
	}

	
	public String getRemoteSocketIP() throws Exception 
	{
		String IP = "";
		try {
			SocketAddress socketAddress = m_sock.getRemoteSocketAddress();
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

	public void setTimeout( int nSec ) throws Exception 
	{
		try 
		{
			m_sock.setSoTimeout( nSec );
		} 
		catch( Exception e ) 
		{
			throw e;
		}
	}


	public void finalize() throws Exception  
	{
		try 
		{
			super.finalize();
			if( m_sock!= null ) m_sock.close();
		} 
		catch( Exception e ) 
		{
			throw e;
		}
	}

}
