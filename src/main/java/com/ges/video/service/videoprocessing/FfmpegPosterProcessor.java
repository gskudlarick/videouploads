package com.ges.video.service.videoprocessing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ges.video.domain.video.VideoInfo;
import com.ges.video.view.mvc.FileUploadController;

@Service
public class FfmpegPosterProcessor extends VideoProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);




	/**
	 * @param file
	 * @return
	 * @throws VideoProcessingException
	 */
	public void processVideoPoster(MultipartFile file, VideoInfo videoInfo) throws VideoProcessingException {
		
		
	    List<String> commands = new ArrayList<String>();
	    
	    //String targetFileName = "upload-dir/" + file.getOriginalFilename();
	    String fileName = file.getOriginalFilename();
	    String baseName = FilenameUtils.getBaseName(fileName);  //e.g. 605

	    commands.add(ffmpeg);
	    
	    commands.add("-i"); 
	    commands.add(TRANSCODING_UPLOAD_DIR + fileName); 
	    
	    commands.add("-deinterlace"); 
	    commands.add("-an"); 
	    
	    commands.add("-ss"); 
	    commands.add("0");
	    
	    commands.add("-f");
	    commands.add("mjpeg");
	    
	    commands.add("-t");
	    commands.add("1");
	    
	    commands.add("-r");
	    commands.add("1");
	    
	    commands.add("-y");
	    
	    commands.add("-s");
	    commands.add("640x480");
	    
	    commands.add(TRANSCODING_THUMBS_PROCESSING_DIR + baseName + "_poster.jpg");
	                      

	    String exceptionMsg = "";
	    int exitValue =0;
	    try {
	      ProcessBuilder pb = new ProcessBuilder(commands);
	      pb.redirectErrorStream(true); // Required, Merges Standard Error, with stdout 
	      Process process = pb.start();

	      //Process Input
	      BufferedReader reader = new BufferedReader(new InputStreamReader( 
	            process.getInputStream()));                                          
	      String s;                                                                
	      while ((s = reader.readLine()) != null) {                                
	        System.out.println(s);                             
	      }                                                                        

	      //Process Errors   TBD, currentlyu merged with output.
	      // TODO run  threads to get both Properly!!!
	      // https://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html
	      BufferedReader readerErrors = new BufferedReader(new InputStreamReader( 
	            process.getErrorStream()));                                          
	      String sErrors;                                                                
	      while ((sErrors = reader.readLine()) != null) {                                
	        System.out.println("Error Output: " + sErrors);                             
	      } 
	      
	      process.waitFor();
	      exitValue = process.exitValue();
	      System.out.println("Exit Value=" + exitValue);

	    } catch (Exception ex)
	    {
	    	logger.error("Error creating Poster File:" + ex);
	    	exceptionMsg = ex.toString();
	    	exitValue = 99;
	    	throw new VideoProcessingException(ex.toString());
	      //ex.printStackTrace();
	    }

	    copyFilesToTarget(file.getOriginalFilename());


		////////////////////////////////
		//
		// Process Return Information
		//
		///////////////////////////////
		if (exitValue != 0) {
			//take out, handled by Exception . 
			videoInfo.setPosterProcessingError(true);
			videoInfo.setPosterStatusMessage("Error processing Poster File.  Contact tech support with the following information:" + exceptionMsg);
		} else {
			videoInfo.setPosterProcessingError(false);
			videoInfo.setPosterStatusMessage("Video Poster Created Successfully");
			
		}
	

	}
	
	private  void copyFilesToTarget( String videoFileFullName)  throws VideoProcessingException {

		// Keep Simple:  Posteer in /thumbs dir  TRANSCODING_THUMBS_PROCESSING_DIR   605_poster.jpg
		// Copy get name, and copy to ASSETX. Somple.  TRANSCODING_ASSETS_DEST_DIR
		
		String baseName = FilenameUtils.getBaseName(videoFileFullName);  //e.g. 605
		String currentFileSuffix = "_poster.jpg";
				
		try {
		Path sourcePath = Paths.get(TRANSCODING_THUMBS_PROCESSING_DIR, baseName + currentFileSuffix);
		Path destPath = Paths.get(TRANSCODING_ASSETS_DEST_DIR, baseName + currentFileSuffix);
		logger.info("Copying Poster File from:" + sourcePath.toString() + " to:" + destPath.toString() );
		Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING );
		logger.info("Copied Poster File from:" + sourcePath.toString() + " to:" + destPath.toString() );
		
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new VideoProcessingException(ex.getMessage());

		}
		
	}
		
		

}