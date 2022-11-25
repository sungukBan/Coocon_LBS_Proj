package com.coocon.lbs.queue;

import java.util.Vector;
import com.coocon.lbs.consts.ConstConfig;
import com.coocon.lbs.util.UtilCommon;
import com.coocon.lbs.util.UtilConfig;
import com.coocon.lbs.util.UtilLogger;


public class QueueHandler {

	private Vector recvQueue = null; // 받은   전문 저장하는 큐
	private Vector sendQueue = null; // 전송할 전문 저장하는 큐

	private int iMaxQueueSize = 1000;

	public QueueHandler() {
		iMaxQueueSize = Integer.parseInt(UtilCommon.getNullToStr(UtilConfig.getValue(ConstConfig.QUE_MAX_SIZE), "1000"));
		recvQueue = new Vector();
		sendQueue = new Vector();
	}

	/**
	 * Description: 큐에 과도한 작업이 들어오는 것을 방지하기 위한 처리를 추가해야함..
	 * @param entity BizBank04QueueEntity
	 * @throws Exception
	 */
	public synchronized void recvEnqueue(QueueEntity entity) throws Exception {

		try {
			if (recvQueue.size() >= iMaxQueueSize) {
				UtilLogger.alertErrorLog(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), "QueueHandler", "recvEnqueue     : recvQueue.size ["+ recvQueue.size() +"]");
				throw new Exception("recvEnqueue : Queue Max Size Overflow");
			}
			else recvQueue.addElement(entity);
			this.notifyAll();
		}
		catch (Exception ex) {
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), ex);
			throw new Exception("Q-ERR");
		}
	}

	public synchronized QueueEntity recvDequeue() {

		try {
			if (recvQueue.size() > 0) {
				QueueEntity entity = (QueueEntity) recvQueue.remove(0);
				return entity;
			}
		} catch(Exception e) {
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		} 
		return null;
	}

	public synchronized void sendEnqueue(QueueEntity entity) throws Exception {

		try {
			if (sendQueue.size() >= iMaxQueueSize) {
				UtilLogger.alertErrorLog(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), "QueueHandler", "sendEnqueue     : sendQueue.size ["+ sendQueue.size() +"]");
				throw new Exception("sendEnqueue : Queue Max Size Overflow");
			}
			sendQueue.addElement(entity);
			UtilLogger.log(UtilConfig.getValue(ConstConfig.LOG_PATH_BIZ), Thread.currentThread().getName() + ": QueueHandler.sendEnqueue...." + sendQueue.size());
			this.notifyAll();
		} catch (Exception ex) {
			throw new Exception("Q-ERR");
		}
	}
	
	public synchronized QueueEntity sendDequeue2() {
		QueueEntity entity = null;
		try {
			if (sendQueue.size() > 0) {
				entity = (QueueEntity) sendQueue.remove(0);
			} 
		} catch(Exception e) {
			UtilLogger.doLoggingException(UtilConfig.getValue(ConstConfig.LOG_PATH_ERR), e);
		}                                
		return entity;
	}

	public int recvSize() {
		return recvQueue.size();
	}

	public int sendSize() {
		return sendQueue.size();
	}

	public int getMaxQueueSize() {
		return iMaxQueueSize;
	}

	public Vector getRecvQueue() {
		return recvQueue;
	}

	public Vector getSendQueue() {
		return sendQueue;
	}

	public void finalize(){
		if(sendQueue != null) {
			sendQueue.removeAllElements();
			sendQueue = null;
		}
		if(recvQueue != null) {
			recvQueue.removeAllElements();
			recvQueue = null;
		}

	}

}

