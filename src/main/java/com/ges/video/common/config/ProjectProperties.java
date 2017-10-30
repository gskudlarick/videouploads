package com.ges.video.common.config;

import org.springframework.beans.factory.annotation.Value;



public class ProjectProperties {
	
	
	@Value("${api.config.uploaddir}")
	private String upDir;

}
