package com.intecon.docsign.main;

import java.time.LocalDate;

public class DocumentToSign {
	
	private String transactionId;
	private String path;
	private String name;
	private LocalDate date;
	private String owner;
	private boolean signed;
	
	
	public DocumentToSign(String transactionId, String path, String name, LocalDate date, String owner, boolean signed) {
		super();
		this.transactionId = transactionId;
		this.path = path;
		this.name = name;
		this.date = date;
		this.owner = owner;
		this.signed = signed;
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isSigned() {
		return signed;
	}
	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
