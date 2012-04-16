package com.criticalalerts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PSIRT {
	private String id; 
	private String matchConfidence; 
	private String matchReason; 
	private String softwareType; 
	private String softwareVersion;
	
	// Details 
	private String distributionType; 
	private String alertVersion; 
	private String externalURL; 
	private String firstPublished; 
	private String headline; 
	private String lastUpdated;
	private String revisionNumber; 
	private String messageDetail;
	private String messageType;
	private String read;
	private String status; 
	private String dateReceived;
	private String impact; 
	
	public String getImpact() {
		return impact;
	}
	public void setImpact(String impact) {
		this.impact = impact;
	}
	public String getDateReceived() {
		return dateReceived;
	}
	public void setDateReceived(String dateReceived) {
		this.dateReceived = dateReceived;
	}
	// Database info 
	private int databaseKeyId; 
	
	public int getDatabaseKeyId() {
		return databaseKeyId;
	}
	public void setDatabaseKeyId(int id) {
		this.databaseKeyId = id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMatchConfidence() {
		return matchConfidence;
	}
	public void setMatchConfidence(String matchConfidence) {
		this.matchConfidence = matchConfidence;
	}
	public String getMatchReason() {
		return matchReason;
	}
	public void setMatchReason(String matchReason) {
		this.matchReason = matchReason;
	}
	public String getSoftwareType() {
		return softwareType;
	}
	public void setSoftwareType(String softwareType) {
		this.softwareType = softwareType;
	}
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	public String getDistributionType() {
		return distributionType;
	}
	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}
	public String getAlertVersion() {
		return alertVersion;
	}
	public void setAlertVersion(String documentNumber) {
		this.alertVersion = documentNumber;
	}
	public String getExternalURL() {
		return externalURL;
	}
	public void setExternalURL(String externalURL) {
		this.externalURL = externalURL;
	}
	public String getFirstPublished() {
		return firstPublished;
	}
	public void setFirstPublished(String string) {
		this.firstPublished = string;
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getRevisionNumber() {
		return revisionNumber;
	}
	public void setRevisionNumber(String revisionNumber) {
		this.revisionNumber = revisionNumber;
	}
	public String getMessageDetail() {
		return messageDetail;
	}
	public void setMessageDetail(String messageDetail) {
		this.messageDetail = messageDetail;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getRead(){
		return this.read; 
	}
	public void setRead(String read){
		this.read = read;
	}
	public String getStatus(){
		return this.status; 
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getFirstPublishedDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MMMMM d HH:mm z");
		Date date = new Date(this.firstPublished);
		return date; 
	}
	
	public Date getLastUpdatedDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MMMMM d HH:mm z");
		Date date = null;
		date = new Date(this.lastUpdated); 
		return date; 
	}
	
	public Date getDateReceivedDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy MMMMM d HH:mm z");
		Date date;
		try {
			date = dateFormatter.parse(this.dateReceived);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return date; 
	}
	
}
