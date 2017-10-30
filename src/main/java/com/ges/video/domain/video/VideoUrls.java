package com.ges.video.domain.video;

import javax.persistence.Embeddable;

@Embeddable
public class VideoUrls {
	
	private String videoUrl; // 605.mp4
	private String videoSpriteUrl;  // 605_sprite.jpg
	private String videoThumbsJsonUrl; // 605_thumbs.json
	private String videoPosterUrl; // 605_poster.jpg
	private String videoHlsUrl; // hls/605.m3u8
	private String videoMpdUrl; // mpd/605.mpd
	
	
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public String getVideoSpriteUrl() {
		return videoSpriteUrl;
	}
	public void setVideoSpriteUrl(String videoSpriteUrl) {
		this.videoSpriteUrl = videoSpriteUrl;
	}
	public String getVideoThumbsJsonUrl() {
		return videoThumbsJsonUrl;
	}
	public void setVideoThumbsJsonUrl(String videoThumbsJsonUrl) {
		this.videoThumbsJsonUrl = videoThumbsJsonUrl;
	}
	public String getVideoPosterUrl() {
		return videoPosterUrl;
	}
	public void setVideoPosterUrl(String videoPosterUrl) {
		this.videoPosterUrl = videoPosterUrl;
	}
	public String getVideoHlsUrl() {
		return videoHlsUrl;
	}
	public void setVideoHlsUrl(String videoHlsUrl) {
		this.videoHlsUrl = videoHlsUrl;
	}
	public String getVideoMpdUrl() {
		return videoMpdUrl;
	}
	public void setVideoMpdUrl(String videoMpdUrl) {
		this.videoMpdUrl = videoMpdUrl;
	}
	
	

	

}
