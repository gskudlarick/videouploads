package com.ges.video.service.videoprocessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ges.video.domain.video.VideoUrls;
import com.ges.video.view.mvc.FileUploadController;

@Service
public class ThumbNailProcessor extends VideoProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	public void processThumbs(String videoFilePath, String videoFileFullName, VideoUrls videoUrls) throws VideoProcessingException {

		this.runThumbsScripts(videoFilePath, videoFileFullName);
		this.copyFilesToTarget(videoFileFullName);

	}

	/**
	 *  Class to Run Python Script to Generate the Video Thumbs
	 *  	-Python Scrupt runs ffmpeg to gemerate thumbs.
	 *  -Python Script runs imagemagick to generate sprites from thumbs
	 *  -Python Script generates custom .JSON to map sprite for video-js player
	 *  
	 * @param videoFilePath
	 * @param videoFileFullName
	 * @throws VideoProcessingException
	 */
	private void runThumbsScripts(String videoFilePath, String videoFileFullName) throws VideoProcessingException {

		logger.info("gesx: Processing Thumbnails for");
		logger.info("gesx: Running :" + python + " " + gregspritesv1 + " " + videoFilePath + videoFileFullName);
		List<String> commands = new ArrayList<String>();
		commands.add(python);
		commands.add(gregspritesv1);
		commands.add(videoFilePath + videoFileFullName);

		ProcessBuilder pb = null;
		Process process = null;
		try {
			pb = new ProcessBuilder(commands);
			pb.redirectErrorStream(true); // Required, Merges Standard Error, with stdout
			process = pb.start();

			// TODO Run as Threads
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s;
			while ((s = reader.readLine()) != null) {
				logger.info(s);
			}

			// TODO Run as Threads
			BufferedReader readerErrors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String sErrors;
			while ((sErrors = readerErrors.readLine()) != null) {
				logger.error(sErrors);
			}

			System.out.println("Waiting for python sprite script process");
			try {
				process.waitFor();
			} catch (InterruptedException ex) {
				logger.error("Error with Thumbnail processor:" + ex);
				ex.printStackTrace();
				throw new VideoProcessingException(ex.getMessage());
			}
		} catch (IOException ex) {
			logger.error("IO Exception  with Thumbnail Processing:" + ex);
			throw new VideoProcessingException(ex.getMessage());
		}

		int exitValue = process.exitValue();
		if (exitValue != 0) {
			logger.error("Error with Thumbnail processor, Process Exit Status =" + exitValue);
			throw new VideoProcessingException("process Exit Status =" + exitValue);
		}

	}
	

	/** 
	 * Processes files dynamically based on whats defined in the application.properties file.
	 *  -Should support adding files to the processing pipeline w/o changes to this code. 
	 * 
	 * TRANSCODING_PIPELINE_PROCESS_LIST transcoding.pipeline.process.list and
	 * TRANSCODING_PIPELINE_ASSET_LIST transcoding.pipeline.asset.list
	 * 
	 * @param videoFilePath
	 * @param videoFileFullName
	 * @throws Exception
	 */
	private  void copyFilesToTarget( String videoFileFullName)  throws VideoProcessingException {

		
		try {
			//movie.mp4 -> 605
			String baseName = FilenameUtils.getBaseName(videoFileFullName);
			logger.info("Processing files from application.properties TRANSCODING_PIPELINE_PROCESS_LIST: " + TRANSCODING_PIPELINE_PROCESS_LIST  + " from: " + TRANSCODING_THUMBS_PROCESSING_DIR  + " to: "+ TRANSCODING_UPLOAD_DIR);
			ArrayList<String> processedFileNamesAuto = new ArrayList<String>(Arrays.asList(TRANSCODING_PIPELINE_PROCESS_LIST.split("\\s*,\\s*")));
			for( String currentFileSuffix : processedFileNamesAuto) {
				try {
				Files.copy(Paths.get(TRANSCODING_THUMBS_PROCESSING_DIR, baseName + currentFileSuffix), Paths.get(TRANSCODING_UPLOAD_DIR, baseName +currentFileSuffix), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("GESX *** TO URI: " + Paths.get(TRANSCODING_THUMBS_PROCESSING_DIR, baseName + currentFileSuffix).toUri());
				
				}catch (Exception ex) {
			
					logger.warn("Verify application.properties (transcoding.pipeline.process.list )"
							+ " file entries match dir structure for env.  Error processing file: " + TRANSCODING_THUMBS_PROCESSING_DIR+ baseName  + currentFileSuffix + " Exception:" + ex);					
				}
			}
			logger.info("Processing files from application.properties TRANSCODING_PIPELINE_ASSET_LIST: " + TRANSCODING_PIPELINE_ASSET_LIST + " from: " + TRANSCODING_UPLOAD_DIR  + " to: "+ TRANSCODING_ASSETS_DEST_DIR);
			ArrayList<String> assetFileNamesAuto = new ArrayList<String>(Arrays.asList(TRANSCODING_PIPELINE_ASSET_LIST.split("\\s*,\\s*")));
			for( String currentFileSuffix : assetFileNamesAuto) {
				try {
				Files.copy(Paths.get(TRANSCODING_UPLOAD_DIR, baseName + currentFileSuffix), Paths.get(TRANSCODING_ASSETS_DEST_DIR, baseName +currentFileSuffix), StandardCopyOption.REPLACE_EXISTING);
				
				}catch (Exception ex) {
					logger.warn(";Verify application.properties (transcoding.pipeline.asset.list )"
							+ " file entries match dir structure for env.  Error processing file: " + TRANSCODING_UPLOAD_DIR+ baseName  +  currentFileSuffix +" Exception:" + ex);
				}
			}
		} catch (Exception ex) {
			throw new VideoProcessingException(ex.getMessage());

		}
		
		
	}
	
	/**
	 * Put in here for now
	 * @param videoUrls
	 * @param videoFileFullName
	 */
	public   void  buildVideoUrls(VideoUrls videoUrls, String videoFileFullName) {
		
		// TODO hard code for now. - automate later. 
		String videoBaseName = FilenameUtils.getBaseName(videoFileFullName);
		//e.g. file name 605.mp4
		
		String videoUrl = TRANSCODING_OUTPUT_BASE_URL + videoFileFullName ; // 605.mp4
		String videoSpriteUrl = TRANSCODING_OUTPUT_BASE_URL + videoBaseName + "_sprite.jpg" ;  // 605_sprite.jpg
		String videoThumbsJsonUrl  =TRANSCODING_OUTPUT_BASE_URL + videoBaseName + "_thumbs.json" ; // 605_thumbs.json
		String videoPosterUrl  = TRANSCODING_OUTPUT_BASE_URL + videoBaseName + "_poster.jpg" ; // 605_poster.jpg
		String videoHlsUrl = TRANSCODING_OUTPUT_BASE_URL + "HLS/" + videoBaseName + ".m3u8";   // HLS/605.m3u8
		String videoMpdUrl = TRANSCODING_OUTPUT_BASE_URL + "MPD/-TBD-" + videoBaseName + ".mpd";    // MPD/605.mpd
		
		videoUrls.setVideoUrl(videoUrl);
		videoUrls.setVideoSpriteUrl(videoSpriteUrl);;
		videoUrls.setVideoThumbsJsonUrl(videoThumbsJsonUrl);
		videoUrls.setVideoPosterUrl(videoPosterUrl);
		videoUrls.setVideoHlsUrl(videoHlsUrl);
		videoUrls.setVideoMpdUrl(videoMpdUrl);;

		
		
	}



}
