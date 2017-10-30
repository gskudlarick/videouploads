package com.ges.video.domain.video;

import javax.persistence.Embeddable;

@Embeddable
public class Dummy {
	
	String alpha;

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}
	

}
