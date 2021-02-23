package com.intecon.docsign.model;

import java.util.Date;

public class LogModel implements Comparable<LogModel>  {
	private String clientMACID;
	private String issue;
	private Date issueTime;
	private String issueResult;
	private String trid;
	private String host;
	private String details;
	
	public String getClientMACID() {
		return clientMACID;
	}
	public void setClientMACID(String clientMACID) {
		this.clientMACID = clientMACID;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public Date getIssueTime() {
		return issueTime;
	}
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}
	public String getIssueResult() {
		return issueResult;
	}
	public void setIssueResult(String issueResult) {
		this.issueResult = issueResult;
	}
	public String getTrid() {
		return trid;
	}
	public void setTrid(String trid) {
		this.trid = trid;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	@Override
	public int compareTo(LogModel o) {
		return getIssueTime().compareTo(o.getIssueTime());
	}
}
