package com.coocon.lbs.consts;

public class Constant {
    final static public String  SUCCESS				= "00000000"; // �������� ����
    final static public String  ERR_CD_LBS_00001    = "LBS00001"; // �̿밡���� SSAgent ����
    
    final static public int     WK_INVALID          = 0;    	// �۾�ó�� ������
    final static public int     WK_VALID            = 1;    	// �۾�ó��   ����
                                                            	
    final static public int     DB_SQL_FAILED       = 0;    	// DB �۾� ������   (INSERT, UPDATE, DELETE)
    final static public int     DB_SQL_SUCCESS      = 1;    	// DB �۾�   ����   (INSERT, UPDATE, DELETE)
    final static public int     DB_SQL_EXCEPTION    = 2;    	// DB �۾�   ����
    final static public int     DB_PGM_EXCEPTION    = 3;    	// DB �۾��� PGM ����
                                                            	
    final static public int     DB_SQL_NOTFOUND     = 0;    	// DB SELECT ���� �������� ����
    final static public int     DB_SQL_FOUND        = 1;    	// DB SELECT ���� ������
                                                            	
    final static public int     SOCK_DISCONNECTED   = 0;    	// ��Ż��� ���� ������
    final static public int     SOCK_CONNECTED      = 1;    	// ��Ż��� ����   ����   
    
    final static public int     MSG_COMMON_LEN      = 100;    	// ���� ����� ����
    final static public String  SS_SYS_NO_ALL       = "0000";   // SS������ȣ(��ü)
                     
    // ���� ����� ����
    final static public String  TRN_CD1             = "LBS";      // �ĺ��ڵ�1
    final static public String  TRN_CD2             = "LBS00001"; // �ĺ��ڵ�2
    final static public String  SEND_FLAG           = "1";      // �۽���FLAG_�䱸����:1
    final static public String  SEND_FLAG_JUNMUN    = "2";      // �۽���FLAG_��������:2(�Ϲ�)
    final static public String  SEND_FLAG_FILE      = "3";      // �۽���FLAG_��������:3(����)
    final static public String  TX_TYPE_0800        = "0800";   // ����Ÿ�� - ����� - ��û
    final static public String  TX_TYPE_0810        = "0810";   // ����Ÿ�� - ����� - ����
    final static public String  TX_TYPE_0900        = "0900";   // ����Ÿ�� - ����� - ��û
    final static public String  TX_TYPE_0910        = "0910";   // ����Ÿ�� - ����� - ����
    final static public String  TX_CODE_0001        = "0001";   // �����ڵ� - ��������
    final static public String  TX_CODE_0002        = "0002";   // �����ڵ� - ����
    final static public String  TX_TYPE_0600        = "0600";   // ����Ÿ�� - �������� - ��û
    final static public String  TX_TYPE_0610        = "0610";   // ����Ÿ�� - �������� - ����
    final static public String  TX_CODE_0102        = "0102";   // �����ڵ� - �ŷ�������ȸ - ID/PWD
    final static public String  TX_CODE_0203        = "0203";   // �����ڵ� - �ŷ�������ȸ - ������
    
    final static public String  POLLING_REQ_MSG     = "000016REQPOLLING";   // �����޽��� - ���� REQ
    final static public String  POLLING_RES_MSG     = "000016RESPOLLING";   // �����޽��� - ���� RES
    final static public String  PREFIX_SEND         = "SEND_";   // LBS -> GW/SS ���� Prefix
    final static public String  PREFIX_RECV         = "RECV_";   // LBS <- GW/SS ���� Prefix
    final static public String  SYNCH_GW_NUM        = "990"  ;   // GW Synch     ����϶� gw ��ȣ
    final static public String  ASYNCH_GW_NUM       = "911"  ;   // �츮ī�� EAI Asynch ����϶� gw ��ȣ
    
	// bank agent �۾�ó������                                             	
    final static public String	STS_BANK_AGENT_BUSY = "6";		// �۾���
    final static public String	STS_BANK_AGENT_IDLE = "0";		// �����

    final static public String  SOCK_TYPE_SSL       = "SSL"  ;  // ����-SSL
    final static public String  SOCK_TYPE_PLAIN     = "PLAIN";  // ����-PLAIN
    
    final static public String  SOCK_TYPE_SYNCH     = "SYNCH" ; // ����-Synch
    final static public String  SOCK_TYPE_ASYNCH    = "ASYNCH"; // ����-Asynch
    
}


