package com.intecon.docsign.log;

import java.util.Date;

public class LogModel {
	
	private String user;
	private String operation;
	private Date operationDate;
	private String operationResult;
	private Integer transactionId;
	private String host;
	private String detail;
	
	
	public LogModel(String user, String operation, Date operationDate, String operationResult, Integer transactionId,
			String host, String detail) {
		super();
		this.user = user;
		this.operation = operation;
		this.operationDate = operationDate;
		this.operationResult = operationResult;
		this.transactionId = transactionId;
		this.host = host;
		this.detail = detail;
	}
	
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}
	public String getOperationResult() {
		return operationResult;
	}
	public void setOperationResult(String operationResult) {
		this.operationResult = operationResult;
	}
	public Integer getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}

}
