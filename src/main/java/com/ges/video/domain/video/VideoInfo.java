package com.ges.video.domain.video;

public class VideoInfo {
	private Format format;
	private String erroMessage;
	private String processingMessage;
	private String processingResultsJSON;
	private String fileName;
	private boolean processingError;
	
	//poster
	private boolean posterProcessingError;
	private String posterStatusMessage;
	
	//hls
	private boolean hlsProcessingError;
	private String hlsStatusMessage;
	
	

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public String getErroMessage() {
		return erroMessage;
	}

	public void setErroMessage(String erroMessage) {
		this.erroMessage = erroMessage;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public boolean isProcessingError() {
		return processingError;
	}

	public void setProcessingError(boolean processingError) {
		this.processingError = processingError;
	}

	public String getProcessingMessage() {
		return processingMessage;
	}

	public void setProcessingMessage(String processingMessage) {
		this.processingMessage = processingMessage;
	}

	public String getProcessingResultsJSON() {
		return processingResultsJSON;
	}

	public void setProcessingResultsJSON(String processingResultsJSON) {
		this.processingResultsJSON = processingResultsJSON;
	}

	public boolean isPosterProcessingError() {
		return posterProcessingError;
	}

	public void setPosterProcessingError(boolean posterProcessingError) {
		this.posterProcessingError = posterProcessingError;
	}

	public String getPosterStatusMessage() {
		return posterStatusMessage;
	}

	public void setPosterStatusMessage(String posterStatusMessage) {
		this.posterStatusMessage = posterStatusMessage;
	}

	public boolean isHlsProcessingError() {
		return hlsProcessingError;
	}

	public void setHlsProcessingError(boolean hlsProcessingError) {
		this.hlsProcessingError = hlsProcessingError;
	}

	public String getHlsStatusMessage() {
		return hlsStatusMessage;
	}

	public void setHlsStatusMessage(String hlsStatusMessage) {
		this.hlsStatusMessage = hlsStatusMessage;
	}



}
