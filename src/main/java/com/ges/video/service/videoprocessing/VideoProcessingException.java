package com.ges.video.service.videoprocessing;

public class VideoProcessingException extends Exception {


	private static final long serialVersionUID = -2642662010783915010L;
	
    public VideoProcessingException(String message) {
        super(message);
        final String errorCode = "1009";
    }

}
