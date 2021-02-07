package com.intecon.docsign.main;

public class DocumentModel {
	
	
	private String documentId;
	private byte[] binaryData;
	private String fileExtansion;
	private String key;//transid
	private String client;
	private boolean signed;
	private String crt_date;
	private String erpID;
	private String documentUrl;
	private String name;
	private String trid;
	private String bytesData;
	
	
	public String getBytesData() {
		return bytesData;
	}
	public void setBytesData(String bytesData) {
		this.bytesData = bytesData;
	}
	public String getDocumentUrl() {
		return documentUrl;
	}
	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}
	public byte[] getBinaryData() {
		return binaryData;
	}
	public void setBinaryData(byte[] binaryData) {
		this.binaryData = binaryData;
	}
	public String getFileExtansion() {
		return fileExtansion;
	}
	public void setFileExtansion(String fileExtansion) {
		this.fileExtansion = fileExtansion;
	}
	public String getErpID() {
		return erpID;
	}
	public void setErpID(String erpID) {
		this.erpID = erpID;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getDocumentId() {
		return documentId;
	}

	public boolean isSigned() {
		return signed;
	}
	public void setSigned(boolean signed) {
		this.signed = signed;
	}
	public String getCrt_date() {
		return crt_date;
	}
	public void setCrt_date(String crt_date) {
		this.crt_date = crt_date;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	@Override
	public String toString() {
		return "DocumentModel [binaryData=" + binaryData + ", fileExtansion=" + fileExtansion + ", key=" + key
				+ ", client=" + client + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTrid() {
		return trid;
	}
	public void setTrid(String trid) {
		this.trid = trid;
	}
}
