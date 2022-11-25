package com.coocon.lbs.util;

import java.io.File;
import java.io.FileWriter;

import com.coocon.lbs.consts.ConstConfig;

public class UtilLogger
{
	private static boolean debug = true;
	private static boolean isJunmun10min = false;
	
  	public static void setMode(boolean debug)
  	{
  		UtilLogger.debug = debug;
	}  		

  	public static void setModeJunmun10min(boolean isJunmun10min)
  	{
  		UtilLogger.isJunmun10min = isJunmun10min;
	}  		

  	/* 에러 메세지 & StackTrace 를 Log 로 남기기 */
  	public static void doLoggingException(String dir, Exception e)
  	{
  		//--if ( debug == false ) return;
  		
  		try {
            /********** Exception.printStackTrace() **********/
            String message = "";
            StackTraceElement[] ste = e.getStackTrace();
            message += e.getMessage() + "\n"; 

            for (int i=0;i < ste.length; i++) {
                message += ste[i].toString() + "\n";
            }

			logTemp( dir, message);
			
            /********** Exception.printStackTrace() **********/  			

  			
		} catch(Exception oe) {}  				
	}  		
    
    
    //  전문 LOG 남기기   LOG_JUNMUN_10MIN
    public static void junmunLog(String dir, String fileName, String msg)
    {
    	if ( isJunmun10min == true) {
    		junmunLogBy10Minutes(dir, fileName, msg);
    		return;
    	}
    	
    	if ( debug == false ) return;
    	
    	FileWriter outfile = null;

        try
        {
            //System.out.println(msg);
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + fileName + "." + UtilCommon.getDate(), true );
            outfile.write( UtilCommon.getTime() + " " + msg + UtilCommon.getEsc() );
            outfile.flush();
        }
        catch( Exception e )
        {
            System.out.println("log error : " + dir + fileName + "." + UtilCommon.getDate());
            System.out.println("log error : " + e.getMessage());
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }

    //  전문 LOG 남기기   
    public static void junmunLogBy10Minutes(String dir, String fileName, String msg)
    {
    	if ( debug == false ) return;
    	
    	FileWriter outfile = null;

        try
        {
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + fileName + "." + UtilCommon.getDate() + "." + UtilCommon.getHHmmss().substring(0,3), true );
            outfile.write( UtilCommon.getTime() + " " + msg + UtilCommon.getEsc() );
            outfile.flush();
        }
        catch( Exception e )
        {
            System.out.println("log error : " + dir + fileName + "." + UtilCommon.getDate());
            System.out.println("log error : " + e.getMessage());
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }
    
    //  전문 LOG 남기기
    public static void log(String dir, String fileName, String msg)
    {
    	if ( debug == false ) return;
    	
    	FileWriter outfile = null;

        try
        {
            //System.out.println(msg);
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + fileName + "." + UtilCommon.getDate(), true );
            outfile.write( UtilCommon.getTime() + " " + msg + UtilCommon.getEsc() );
            outfile.flush();
        }
        catch( Exception e )
        {
            System.out.println("log error : " + dir + fileName + "." + UtilCommon.getDate());
            System.out.println("log error : " + e.getMessage());
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }

    //  LOG 남기기
    public static void log(String dir, String msg)
    {
    	if ( debug == false ) return;
    	
    	FileWriter outfile = null;

        try
        {
            //System.out.println(msg);
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + "log." + UtilCommon.getDate(), true );
            outfile.write( UtilCommon.getTime() + " -- " + msg + UtilCommon.getEsc());
            outfile.flush();
        }
        catch( Exception e )
        {
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }

    //  LOG 남기기
    public static void logTemp(String dir, String msg)
    {
    	FileWriter outfile = null;

        try
        {
            //System.out.println(msg);
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + "log." + UtilCommon.getDate(), true );
            outfile.write( UtilCommon.getTime() + " -- " + msg + UtilCommon.getEsc());
            outfile.flush();
        }
        catch( Exception e )
        {
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }
    
    //  LOG 남기기
    public static void debug(String dir, String msg)
    {
    	if ( debug == false ) return;
    	
    	FileWriter outfile = null;

        try
        {
        	if ( !debug ) return;
        	
            //System.out.println(msg);
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + "log." + UtilCommon.getDate(), true );
            outfile.write( UtilCommon.getTime() + " -- " + msg + UtilCommon.getEsc());
            outfile.flush();
        }
        catch( Exception e )
        {
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }
 
    //  Error LOG 남기기
    public static void hostErrorLog(String dir, String strPgmName, String strErrorCode, String strMsg )
    {
    	if ( debug == false ) return;
    	
    	FileWriter outfile = null;

        try
        {
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + "sys." + UtilCommon.getDate(), true );
            outfile.write(  UtilCommon.getTime() 
                            + " -- " 
                            + strPgmName    + ",\t"   
                            + strErrorCode  + ",\t"
                            + strMsg        
                            + UtilCommon.getEsc());
            outfile.flush();
        }
        catch( Exception e )
        {
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }

    
    //  Error LOG 남기기
    public static void alertErrorLog(String dir, String strPgmName, String strMsg )
    {
    	if ( debug == false ) return;
    	
    	FileWriter outfile = null;

        try
        {
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + "log." + UtilCommon.getDate(), true );
            outfile.write(  UtilCommon.getTime() 
                            + " -- " 
                            + strPgmName    + UtilCommon.getEsc()   
                            + strMsg        
                            + UtilCommon.getEsc());
            outfile.flush();
        }
        catch( Exception e )
        {
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }

    //  Query LOG 남기기
    public static void doLoggingQuery(String dir, String sSQL, String sPgmName )
    {
    	if ( debug == false ) return;
    	
    	FileWriter outfile = null;

        try
        {
            File file = new File(dir);

            if(!file.exists())
                file.mkdir();

            outfile = new FileWriter( dir + "query." + UtilCommon.getDate() + "." + UtilCommon.getHHmmss().substring(0,2), true );
            outfile.write(  UtilCommon.getTime() 
                            + " -- " 
                            + sPgmName    + ",\t"   
                            + sSQL        
                            + UtilCommon.getEsc());
            outfile.flush();
        }
        catch( Exception e )
        {
        }
        finally
        {
            try
            {
                if ( outfile != null ) outfile.close();
            }
            catch(Exception e)
            {
            }
        }
    }


}