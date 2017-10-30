


package com.ges.video.service.videoprocessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ges.video.domain.video.VideoInfo;
import com.ges.video.view.mvc.FileUploadController;

@Service
public class FfmpegHlsProcessor extends VideoProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);




	/**
	 * @param file
	 * @return
	 * @throws VideoProcessingException
	 */
	public void processHlsFile(MultipartFile file, VideoInfo videoInfo) throws VideoProcessingException {
		
		
	    List<String> commands = new ArrayList<String>();
	    
	    //String targetFileName = "upload-dir/" + file.getOriginalFilename();
	    String fileName = file.getOriginalFilename();
	    String baseName = FilenameUtils.getBaseName(fileName);  //e.g. 605


	    commands.add(ffmpeg);
	    
	    commands.add("-i"); 
	    commands.add(TRANSCODING_UPLOAD_DIR + fileName); 
	    
	    commands.add("-strict"); 
	    commands.add("-2"); 
	    
	    commands.add("-acodec"); 
	    commands.add("aac");
	    
	    commands.add("-vcodec");
	    commands.add("libx264");
	    
	    commands.add("-crf");
	    commands.add("50");
	    
	    commands.add(TRANSCODING_HLS_PROCESSING_DIR + baseName + ".m3u8");

	    logger.info("Processing HLS File from:" + TRANSCODING_UPLOAD_DIR + fileName + "to: " + TRANSCODING_HLS_PROCESSING_DIR + baseName + ".m3u8" );

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
			videoInfo.setHlsProcessingError(true);
			videoInfo.setHlsStatusMessage("Error processing HLS File.  Contact tech support with the following information:" + exceptionMsg);
		} else {
			videoInfo.setHlsProcessingError(false);
			videoInfo.setHlsStatusMessage("Video HLS Created Successfully");
			
		}
	

	}
	
	private  void copyFilesToTarget( String videoFileFullName)  throws VideoProcessingException {

		// Keep Simple:  Posteer in /thumbs dir  TRANSCODING_THUMBS_PROCESSING_DIR   605_poster.jpg
		// Copy get name, and copy to ASSETX. Somple.  TRANSCODING_ASSETS_DEST_DIR
		
		String baseName = FilenameUtils.getBaseName(videoFileFullName);  //e.g. 605
		String currentFileSuffix = ".m3u8";
		
		Path dir = Paths.get(TRANSCODING_HLS_PROCESSING_DIR);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{ts,m3u8}")) {
		    for (Path file: stream) {
		    		String currentFileName = file.toString();
		    		int idx = currentFileName.lastIndexOf("/");
		    		String finalString= currentFileName.substring(idx +1);
		        Path sourceFile = Paths.get(file.toString());
		        Path destFile = Paths.get(TRANSCODING_ASSETS_DEST_DIR_HLS, finalString);
		        Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING );
		    }
		} catch (IOException | DirectoryIteratorException x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    System.err.println(x);
		    x.printStackTrace();
		    throw new VideoProcessingException(x.getMessage());
		
		}
				
		/*
		try {
		Path sourcePath = Paths.get(TRANSCODING_HLS_PROCESSING_DIR, baseName + currentFileSuffix);
		Path destPath = Paths.get(TRANSCODING_ASSETS_DEST_DIR_HLS, baseName + currentFileSuffix);
		logger.info("Copying HLS File from:" + sourcePath.toString() + " to:" + destPath.toString() );
		Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING );
		logger.info("Copied HLS File from:" + sourcePath.toString() + " to:" + destPath.toString() );
		
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new VideoProcessingException(ex.getMessage());

		}
		*/
		
	}
		
		

}






