package com.ges.video.service.videoprocessing;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ges.video.domain.video.VideoUrls;

@Service
public class VideoProcessor {

	VideoProcessor(){

	}
	
	// Transcoding Support
	@Value("${api.config.ffprobe.bin}")
	protected String ffprobe;
	@Value("${api.config.ffmpeg.bin}")
	protected String ffmpeg;
	@Value("${api.config.python.bin}")
	protected String python;
	@Value("${api.config.gregspritesv1}")
	protected String gregspritesv1;
	
	//Image Magick
	@Value("${api.config.mogrify}")
	protected String mogrify;
	@Value("${api.config.identify}")
	protected String identify;
	@Value("${api.config.montage}")
	protected String montage;
	
	//File Processing 
	@Value("${transcoding.assets.dest.dir}")
	protected String TRANSCODING_ASSETS_DEST_DIR;
	@Value("${transcoding.thumbs.processing.dir}")
	protected String TRANSCODING_THUMBS_PROCESSING_DIR;
	@Value("${transcoding.upload.dir}")
	protected String TRANSCODING_UPLOAD_DIR;
	@Value("${transcoding.pipeline.processing.list}")
	protected String TRANSCODING_PIPELINE_PROCESS_LIST;
	@Value("${transcoding.pipeline.asset.list}")
	protected String TRANSCODING_PIPELINE_ASSET_LIST;
	
	
	//Video Info/ Player info
	@Value("${transcoding.output.base.url}")
	protected String TRANSCODING_OUTPUT_BASE_URL;
	
	// HLS
	@Value("${transcoding.hls.processing.dir}")
	protected String TRANSCODING_HLS_PROCESSING_DIR;
	@Value("${transcoding.assets.dest.dir.hls}")
	protected String TRANSCODING_ASSETS_DEST_DIR_HLS;

	
	// General
	@Value("${spring.env}")
	protected String env;
	
	

	
	
	
	
	

}
