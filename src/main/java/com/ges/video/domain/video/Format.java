package com.ges.video.domain.video;

import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonProperty;
@Embeddable
public class Format {
	
	private String filename;
	@JsonProperty("format_name")
	private String formatName;
	private float duration;
	private int size;
	@JsonProperty("bit_rate")
	private int bitRate;
	

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFormatName() {
		return formatName;
	}
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getBitRate() {
		return bitRate;
	}
	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}

}
