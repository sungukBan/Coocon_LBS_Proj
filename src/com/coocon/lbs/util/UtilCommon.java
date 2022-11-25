package com.coocon.lbs.util;

import java.io.*;
import java.text.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.util.sock.AUtilSocket;

public class UtilCommon
{
    public static final String FILLER        = " ";
    public static final int    FILLER_OPTION = 0;
    public static final String ZERP          = "0";
    public static final int    ZERP_OPTION   = 1;

	public static String getSeqNo() {
		return "0" + UtilCommon.getTime_HHmmss();
	}
	
  	public static long doCvrtStrToLong(String sSrc)
  	{
		long rV = 0L;

    	try
    	{
			if( sSrc.trim().length() == 0 )
				rV = 0L;
			else
				rV = Long.parseLong(sSrc.trim());
    	}
    	catch(Exception e)
    	{
    	  	e.printStackTrace();
    	}
    	return rV;
  	}




  	public static String strAppendZero(String getAmount, int intLen)
  	{
    	String   imsiString  = "";
    	int      intLength   = 0;
    	try
    	{
    		imsiString = getAmount;
    	   	if (getAmount.length() < intLen)
    	   	{
    	      	intLength = intLen - getAmount.length();
    	      	for (int i = 0 ; i < intLength ; i++)
    	      	{
    	         	imsiString = "0" + imsiString ;
    	      	}
    	   	}
    	}
    	catch(Exception e)
    	{
    	  	e.printStackTrace();
    	}
    	return imsiString;
  	}


    public static String getNullToBlank(String src)
    {
        if ( src == null )
            return " ";
        else
            return src;
    }

    public static String getNullToStr(String src, String str)
    {
        if ( src == null || src.length() == 0 )
            return str;
        else
            return src;
    }

    public static String getFillStr(String src, String fillStr, int size, int opt)
    {
        if ( src.length() >= size )
            return src;

        int cnt = size - src.length();
        for ( int i = 0 ; i < cnt ; i++ )
        {
            if ( opt == 1 )
                src = fillStr + src;
            else
                src = src + fillStr;
        }

        return src;
    }

    // 주어진 길이만큼 ' '를 채워 리턴하는 메소드
    public static String fillSpace(int count)
    {
        String sReturn = "";

        for(int i=0; i < count; i++)
            sReturn += " ";

        return sReturn;
    }

    // 주어진 길이 만큼 '0'를 채운 문자를 리턴하는 메소드
    public static String fillZeros( int count)
    {
        String sReturn = "";

        for(int i=0; i < count; i++)
            sReturn += "0";

        return sReturn;
    }

    // 주어진 길이에서 문자의 길이를 뺀 수만큼 '0'를 앞에 채워 문자를 리턴하는 메소드
    public static String fillZeros( int tLen, String source)
    {
        if (source.equals("null") || (source == null) )
            source = "";

        int mLen = source.trim().length();

        for ( int i=0 ; i < ( tLen - mLen ) ; i++ )
            source = "0" + source;

        return source;
    }

    // 앞의 0 제거하고 리턴하는 메소드
    // 예) 00000 ==> 0 , 00001 ==> 1 , 00100 ==> 100
    public static String deleteZero(String amt)
    {
        String sReturn = "";

        if(amt.equals("000000000000") || amt.equals(""))
        {
            amt = "0";
        }
        else
        {
            int count = 0;
            while(amt.substring(0,1).equals("0"))
            {
                count = amt.length();
                amt = amt.substring(1,count);
            }
        }

        return amt;
    }

    // String ==> char[] 에 채워 넣어 char[]를 리턴하는 메소드
    public static char[] getCharArray(String sSource, char[] cDestination)
    {
        sSource.getChars(0, sSource.length(), cDestination, 0);
        return cDestination;
    }

    // char[] ==> byte[] 에 채워 넣어 byte[]를 리턴하는 메소드
    public static byte[] cstobs(char cs[])
    {
        byte ret[] = new byte[cs.length];
        for(int i = 0; i < cs.length; i++)
            ret[i] = (byte)cs[i];

        return ret;
    }

    //byte[] ==> String 으로 변환하여 리턴하는 메소드
    public static String bstostr(byte bs[], int position, int length)
    {
        byte[] bReturn = new byte[length];
        System.arraycopy(bs, position, bReturn, 0, length);

        return new String(bReturn);
    }

    //잘못 표현된 스트링을 유니코드 한글 코드값의 스트링으로 변환해주는 메소드
    public static String Uni2Ksc(String str) throws UnsupportedEncodingException
    {
        if(str == null)
            return null;

        return new String(str.getBytes("ISO-8859-1"),"euc-kr");
    }

    //유니코드 한글 코드값으로 표현된 스트링을 잘못 표현된 스트링으로 변환해주는 메소드
    public static String Ksc2Uni(String str) throws UnsupportedEncodingException
    {
        if(str == null)
            return null;

        return new String(str.getBytes("euc-kr"),"ISO-8859-1");
    }

    // 실행되는 시스템에 따른 이스케이프 문자(새로운 행)를 얻는 메소드
    public static String getEsc()
    {
        String strOsName = System.getProperty("os.name").toLowerCase();
        String m_strLF   = "";

        if( strOsName.indexOf("window") != -1 )
            m_strLF = "\r\n";
        else
            m_strLF = "\n";

		return m_strLF;
    }

    // 시스템의 현재 날짜를 얻는 메소드(yyyyMMdd)
    public static String getDate()
    {
        final int  millisPerHour = 60 * 60 * 1000;
        SimpleDateFormat sdf     = new SimpleDateFormat("yyyyMMdd");
        SimpleTimeZone timeZone  = new SimpleTimeZone( 9 * millisPerHour, "KST");
        sdf.setTimeZone(timeZone);

        Date             d         = new Date();
        String           sYYYYMMDD = sdf.format(d);

        return sYYYYMMDD;
    }

    //시스템의 현재 시간을 얻는 메소드(HH:mm:ss:SSS)
    public static String getTime()
    {
        final int  millisPerHour = 60 * 60 * 1000;
        SimpleDateFormat sdf     = new SimpleDateFormat("HH:mm:ss:SSS");
        SimpleTimeZone timeZone  = new SimpleTimeZone( 9 * millisPerHour, "KST");
        sdf.setTimeZone(timeZone);

        Date             d       = new Date();
        String           sHHmmss = sdf.format(d);

        return sHHmmss;
    }

    //시스템의 현재시간을 얻는 메소드(HHmmss)
    public static String getTime_HHmmss()
    {
        final int  millisPerHour = 60 * 60 * 1000;
        SimpleDateFormat sdf     = new SimpleDateFormat("HHmmss");
        SimpleTimeZone timeZone  = new SimpleTimeZone( 9 * millisPerHour, "KST");
        sdf.setTimeZone(timeZone);

        Date             d       = new Date();
        String           sHHmmss = sdf.format(d);

        return sHHmmss;
    }

    //시스템의 현재시간을 얻는 메소드(HHmmss)
    public static String getHHmmss()
    {
        final int  millisPerHour = 60 * 60 * 1000;
        SimpleDateFormat sdf     = new SimpleDateFormat("HHmmss");
        SimpleTimeZone timeZone  = new SimpleTimeZone( 9 * millisPerHour, "KST");
        sdf.setTimeZone(timeZone);

        Date             d       = new Date();
        String           sHHmmss = sdf.format(d);

        return sHHmmss;
    }


   /**
    * String date를 Date객체로 parsing하여 return하는 method.
    *
    * @param date       날짜를 나타내는 String 객체
    * @param formate    Date의 format
    *
    * @return String    String 날짜를 parsing한 Date객체.<br>
    */
    public static Date dateParse(String date, String format)
    {
        if ( date == null )
            return null;

        try {
            return new SimpleDateFormat(format).parse(date);
        } catch(Exception e) {
            return null;
        }
    }

   /**
    * String date의 validate를 check하는 method.
    *
    * @param date       날짜를 나타내는 String 객체
    * @param formate    Date의 format
    *
    * @return boolean   유효성 여부.<br>
    */
    public static boolean isDate(String date, String format)
    {
        if ( date == null )
            return false;

        try {
            Date tmpDate = new SimpleDateFormat(format).parse(date);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

	public static String addDate( String format, String date, String field,  int amount) {

		SimpleDateFormat fmt = new SimpleDateFormat(format);
		Calendar cal = new GregorianCalendar();
		cal.setTime(dateParse(date, "yyyyMMdd"));

		if (field.equals("YEAR"))
			cal.add(Calendar.YEAR, amount);
		if (field.equals("MONTH"))
			cal.add(Calendar.MONTH, amount);
		if (field.equals("DAY"))
			cal.add(Calendar.DATE, amount);

		String strTime =
			fmt.format(new java.util.Date(cal.getTime().getTime()));

		return strTime;
	}
	
	public static long getTimeDiffSec( String fromDate, String toDate) {

		Calendar cal1 = new GregorianCalendar();
		Calendar cal2 = new GregorianCalendar();
		
		int yyyy1 = Integer.parseInt(fromDate.substring( 0,  4));
		int momth1= Integer.parseInt(fromDate.substring( 4,  6));
		int dd1   = Integer.parseInt(fromDate.substring( 6,  8));
		int hh1   = Integer.parseInt(fromDate.substring( 8, 10));
		int mm1   = Integer.parseInt(fromDate.substring(10, 12));
		int ss1   = Integer.parseInt(fromDate.substring(12, 14));
		
		int yyyy2 = Integer.parseInt(toDate.substring( 0,  4));
		int momth2= Integer.parseInt(toDate.substring( 4,  6));
		int dd2   = Integer.parseInt(toDate.substring( 6,  8));
		int hh2   = Integer.parseInt(toDate.substring( 8, 10));
		int mm2   = Integer.parseInt(toDate.substring(10, 12));
		int ss2   = Integer.parseInt(toDate.substring(12, 14));
		
		cal1.set(yyyy1, momth1, dd1, hh1, mm1, ss1);
		cal2.set(yyyy2, momth2, dd2, hh2, mm2, ss2);
		
		return (cal2.getTimeInMillis() - cal1.getTimeInMillis())/1000;
	}
	

    public static void assignString(String in, byte out[]) {

   	    byte tin[] = in.getBytes();
        int tlen = tin.length;

        if(tlen <= out.length) {
 	        System.arraycopy(tin, 0, out, 0, tlen);
        	for(int i = tlen; i < out.length; i++)
        	    out[i] = 32;
    	}
    	else {
    		int j = 0;
    		for (int i = 0 ; i < in.length() && j < out.length ; i++ ) {
    			if ( in.substring(i, i + 1).getBytes().length == 1 ) {
            		System.arraycopy(in.substring(i, i + 1).getBytes(), 0, out, j, 1);
            		j++;
            	} else if ( in.substring(i, i + 1).getBytes().length > 1 && j + 1 == out.length )  {
            		out[j] = 32;
            		break;
            	} else {
            		System.arraycopy(in.substring(i, i + 1).getBytes(), 0, out, j, 2);
            		j++;
            		j++;
            	}
            }
        }
    }

    public static void assignBytes(byte[] in, byte out[]) {

        int len = in.length;

        if(len <= out.length) {
 	        System.arraycopy(in, 0, out, 0, len);
        	for(int i = len; i < out.length; i++)
        	    out[i] = 32;
    	}
    	else {
            System.arraycopy(in, 0, out, 0, out.length);
        }
    }


    // DB 에서 Sequence 생성해서 Number fetching.....
    public static String getHostSerialNumFromDB() {

        String strRetVal = null;

        try {

        } catch(Exception e) {

        } finally {

        }
        return strRetVal;
    }

	// 객체를 생성한다.
	public static Object getObject(String strClassName, Class[] agumentClass, Object[] arguments) throws Exception
	{
		Constructor constr;
		Class objClass;
//		Class[] agumentClass;
		
		Object obj = null;
		try {
			// constructor 구하기
			objClass  = Class.forName(strClassName);
			constr = objClass.getConstructor(agumentClass);
		
			// object 생성
			obj = constr.newInstance(arguments);

		} catch (ClassNotFoundException e) {
			System.out.println(e);
			throw e;
		} catch (NoSuchMethodException e) {
			System.out.println(e);			
			throw e;
		} catch (Exception e) {
			System.out.println(e);			
			throw e;
	    }
		
		return obj;
	}
	
}