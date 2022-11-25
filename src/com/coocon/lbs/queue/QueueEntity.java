package com.coocon.lbs.queue;

import com.coocon.lbs.entity.EntityMsgCommon;


public class QueueEntity {

	private byte[] m_bMessage       = null;
	private EntityMsgCommon cEntity = null;
	private String sRecvTime 	    = null;
	private String sMsgKey          = null;

	public QueueEntity() {
	}
	public byte[] getMessage() {
		return m_bMessage;
	}

	public void setMessage(byte[] bMessage) {
		this.m_bMessage = bMessage;
	}

	public String toString() {
		return cEntity.toString() + "\n" + new String(m_bMessage);
	}

	public void setRecvTime(String sRecvTime) {
		this.sRecvTime = sRecvTime;
	}
	public String getRecvTime() {
		return sRecvTime;
	}        

	public void setEntityMsgCommon(EntityMsgCommon cEntity) {
		this.cEntity = cEntity;
	}        

	public EntityMsgCommon getEntityMsgCommon() {
		return this.cEntity;
	}

	public void _setMsgKey(String sMsgKey) {
		this.sMsgKey = sMsgKey; 
	}     
	public String getMsgKey() {
		return this.sMsgKey; 
	} 

	public void setSsSysNo(String sSsSysNo) {
		System.arraycopy(sSsSysNo.getBytes(), 0, m_bMessage, 22, 4);
		cEntity.ss_sys_no = sSsSysNo;
	}
	public void setMsgKey(EntityMsgCommon cEntity) {
		// TODO Auto-generated method stub
		this.cEntity = cEntity;
		this.sMsgKey = cEntity.gw_sys_no +cEntity.trn_cd1 +cEntity.trn_cd2 +cEntity.tx_cd + cEntity.tr_seq;
	}
	
}
