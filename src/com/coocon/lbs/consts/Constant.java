package com.coocon.lbs.consts;

public class Constant {
    final static public String  SUCCESS				= "00000000"; // 전문응답 정상
    final static public String  ERR_CD_LBS_00001    = "LBS00001"; // 이용가능한 SSAgent 없음
    
    final static public int     WK_INVALID          = 0;    	// 작업처리 비정상
    final static public int     WK_VALID            = 1;    	// 작업처리   정상
                                                            	
    final static public int     DB_SQL_FAILED       = 0;    	// DB 작업 비정상   (INSERT, UPDATE, DELETE)
    final static public int     DB_SQL_SUCCESS      = 1;    	// DB 작업   정상   (INSERT, UPDATE, DELETE)
    final static public int     DB_SQL_EXCEPTION    = 2;    	// DB 작업   오류
    final static public int     DB_PGM_EXCEPTION    = 3;    	// DB 작업중 PGM 오류
                                                            	
    final static public int     DB_SQL_NOTFOUND     = 0;    	// DB SELECT 건이 존재하지 않음
    final static public int     DB_SQL_FOUND        = 1;    	// DB SELECT 건이 존재함
                                                            	
    final static public int     SOCK_DISCONNECTED   = 0;    	// 통신상태 연결 비정상
    final static public int     SOCK_CONNECTED      = 1;    	// 통신상태 연결   정상   
    
    final static public int     MSG_COMMON_LEN      = 100;    	// 전문 공통부 길이
    final static public String  SS_SYS_NO_ALL       = "0000";   // SS서버번호(전체)
                     
    // 전문 공통부 관련
    final static public String  TRN_CD1             = "LBS";      // 식별코드1
    final static public String  TRN_CD2             = "LBS00001"; // 식별코드2
    final static public String  SEND_FLAG           = "1";      // 송신자FLAG_요구전문:1
    final static public String  SEND_FLAG_JUNMUN    = "2";      // 송신자FLAG_응답전문:2(일반)
    final static public String  SEND_FLAG_FILE      = "3";      // 송신자FLAG_응답전문:3(파일)
    final static public String  TX_TYPE_0800        = "0800";   // 전문타입 - 운영전문 - 요청
    final static public String  TX_TYPE_0810        = "0810";   // 전문타입 - 운영전문 - 응답
    final static public String  TX_TYPE_0900        = "0900";   // 전문타입 - 운영전문 - 요청
    final static public String  TX_TYPE_0910        = "0910";   // 전문타입 - 운영전문 - 응답
    final static public String  TX_CODE_0001        = "0001";   // 전문코드 - 업무개시
    final static public String  TX_CODE_0002        = "0002";   // 전문코드 - 폴링
    final static public String  TX_TYPE_0600        = "0600";   // 전문타입 - 업무전문 - 요청
    final static public String  TX_TYPE_0610        = "0610";   // 전문타입 - 업무전문 - 응답
    final static public String  TX_CODE_0102        = "0102";   // 전문코드 - 거래내역조회 - ID/PWD
    final static public String  TX_CODE_0203        = "0203";   // 전문코드 - 거래내역조회 - 인증서
    
    final static public String  POLLING_REQ_MSG     = "000016REQPOLLING";   // 전문메시지 - 폴링 REQ
    final static public String  POLLING_RES_MSG     = "000016RESPOLLING";   // 전문메시지 - 폴링 RES
    final static public String  PREFIX_SEND         = "SEND_";   // LBS -> GW/SS 전송 Prefix
    final static public String  PREFIX_RECV         = "RECV_";   // LBS <- GW/SS 수신 Prefix
    final static public String  SYNCH_GW_NUM        = "990"  ;   // GW Synch     방식일때 gw 번호
    final static public String  ASYNCH_GW_NUM       = "911"  ;   // 우리카드 EAI Asynch 방식일때 gw 번호
    
	// bank agent 작업처리상태                                             	
    final static public String	STS_BANK_AGENT_BUSY = "6";		// 작업중
    final static public String	STS_BANK_AGENT_IDLE = "0";		// 대기중

    final static public String  SOCK_TYPE_SSL       = "SSL"  ;  // 소켓-SSL
    final static public String  SOCK_TYPE_PLAIN     = "PLAIN";  // 소켓-PLAIN
    
    final static public String  SOCK_TYPE_SYNCH     = "SYNCH" ; // 소켓-Synch
    final static public String  SOCK_TYPE_ASYNCH    = "ASYNCH"; // 소켓-Asynch
    
}


