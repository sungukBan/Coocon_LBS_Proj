package com.coocon.lbs.consts;

/**
 * ConstConfig.java
 * Descriptions
 * -----------
 * ��� �����͸� �����ϴ� Ŭ����
 *
 */

public class ConstConfig {
	
	//--���� length ����
	public final static String MSG_LEN_COLUMN_SIZE  	= "msg.len.column.size";

	// �������� - polling ���������� �����.
	public final static String MSG_GW_SYS_NO            = "msg.gw.sys.no";   // GW�ý��۹�ȣ
	public final static String MSG_SS_SYS_NO            = "msg.ss.sys.no";   // SS�ý��۹�ȣ
	public final static String MSG_PROD_KEY             = "msg.prod.key";    // ��ǰ������ȣ
	public final static String MSG_GROUP_GB             = "msg.group.gb";    // �׷� ����
	public final static String MSG_SECT_START           = "msg.sect.start";  // �������۹�ȣ
	public final static String MSG_SECT_END             = "msg.sect.end";    // ���������ȣ
	
	//--SSAgent Job TTL
	public final static String MSG_SS_JOB_TTL    	         = "msg.ss.job.ttl";
	public final static String MSG_SS_JOB_TTL_CHECK_INTERVAL = "msg.ss.job.ttl.check.interval";
	
	//--SS ��������
	public final static String PORT_SS_SEND				= "port.ss.send";
	public final static String PORT_SS_RECV				= "port.ss.recv";	
	public final static String SOCKET_SS_TIMEOUT 		= "socket.ss.timeout";
	public final static String POLLING_SS_INTERVAL 		= "polling.ss.interval";
	public final static String POLLING_SS_TIMEOUT 		= "polling.ss.timeout";
	
	//--G/W ��������
	public final static String PORT_GW_SEND				= "port.gw.send";
	public final static String PORT_GW_RECV				= "port.gw.recv";
	public final static String SOCKET_GW_TIMEOUT 		= "socket.gw.timeout";
	public final static String POLLING_GW_INTERVAL 		= "polling.gw.interval";
	public final static String POLLING_GW_TIMEOUT 		= "polling.gw.timeout";
	
	//--QUEUE MAX SIZE
	public final static String QUE_MAX_SIZE 			= "que.max.size";
	public final static String QUE_DEQUEUE_SLEEP_SECOND	= "que.dequeue.sleep.second";
	
	//--Log File Path
	public static final String LOG_JUNMUN_PRINT_MAX_LEN = "log.junmun.print.max.len";
	public final static String DEBUG_MODE				= "log.debug";
	//--2018.02.26 TOMISGOOD �����α����� 10�� ������ file write ó������
	public final static String LOG_JUNMUN_10MIN	        = "log.junmun.10min";
	public final static String LOG_PATH_ERR				= "log.path.err";
	public final static String LOG_PATH_JUN				= "log.path.jun";
	public final static String LOG_PATH_BIZ				= "log.path.biz";
	public final static String LOG_PATH_QUERY			= "log.path.query";
	public final static String LOG_PATH_ALERT			= "log.path.alert";
	public final static String LOG_PATH_MONITOR			= "log.path.monitor";
	public final static String LOG_PATH_AGENT           = "log.path.agent";
	
	public final static String SS_MAX_RUN_CNT   	      = "ss.max.run.cnt";
	public final static String SS_REFRESH_TIMEOUT         = "ss.refresh.timeout";
	public final static String SS_REFRESH_TIMEOUT_INTERVAL= "ss.refresh.timeout.interval";
	
	public static final String SSL_USE_YN           = "ssl.use.yn";
	public static final String SSL_KEYSTORE         = "ssl.keystore";
	public static final String SSL_KEYSTOREPASSWORD = "ssl.keystorepassword";
	
	public static final String SOCK_PLAIN_SSL_GW    = "sock.plain.ssl.gw";
	public static final String SOCK_PLAIN_SSL_SS    = "sock.plain.ssl.ss";
	
	public static final String IP_L4_LIST           = "ip.l4.list";
	
	//--2018.02.26 TOMISGOOD �ŷ��ڵ庰 �׷��ڵ�, start, end ���ð���
	public static final String TXCODE_INFO_USE_YN   = "txcode.info.use.yn";
	public static final String TXCODE_INFO          = "txcode.info";
	public static final String TXCODE_INFO_CNT      = "txcode.info.cnt";

}
