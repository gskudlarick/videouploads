package com.ges.video.service.videoprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ges.video.domain.video.Format;
import com.ges.video.domain.video.VideoInfo;
import com.ges.video.view.mvc.FileUploadController;

@Service
public class FfmpegBasicsProcessor extends VideoProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);



	/**
	 *  Class to Run FFPROBE
	 *  	-Get Results in JSON Format
	 *  TODO, this is rough cut for initial proottype
	 *  		-convert Input/Otput buffers to Threads
	 *  		-stream multip part file to ffprobe.
	 *  
	 * @param videoFilePath
	 * @param videoFileFullName
	 * @throws VideoProcessingException
	 */
	public VideoInfo getBasicVideoFileInfo(MultipartFile file) throws VideoProcessingException {
		
		VideoInfo videoInfo = null;
		// Test with ffprobe.
				String responseMessage = "NA";
				java.io.File newFile = new File("upload-dir/" + file.getOriginalFilename());
				BufferedReader reader2 = null;
				StringBuilder ffprobeProcessCommandLineResults = new StringBuilder();
				int exitValue = 0;
				
				String targetFileName = "upload-dir/" + file.getOriginalFilename();
				try {
					reader2 = new BufferedReader(new FileReader(newFile));
					List<String> commands = new ArrayList<String>();


					commands.add(ffprobe); // linux
					commands.add("-print_format");
					commands.add("json");
					commands.add("-loglevel"); // or -v error
					commands.add("16"); // 8 only Fatal, 16, all errors.
					commands.add("-show_entries");
					commands.add("format=,filename,size,duration,format_name,bit_rate"); 
					commands.add(targetFileName);


					ProcessBuilder pb = new ProcessBuilder(commands);
					pb.redirectErrorStream(true); 
					Process process = pb.start();

					// Process Input
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String s;

					while ((s = reader.readLine()) != null) {
						System.out.println(s);
						ffprobeProcessCommandLineResults.append(s);
					}

					BufferedReader readerErrors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					String sErrors;
					while ((sErrors = reader.readLine()) != null) {
						System.out.println("Error Output: " + sErrors);
					}

					System.out.println("Waiting for ffprobe script process");
					process.waitFor();
					exitValue = process.exitValue();
					System.out.println("Exit Value=" + exitValue);


					/////////////////////////////////////
					//
					// Convert ffprobe JSON to Object
					// -VideoInfo.java POJO
					// -Store in DB. -but higher up.
					//
					////////////////////////////////////
					ObjectMapper mapper = new ObjectMapper();
					try {

						videoInfo = mapper.readValue(ffprobeProcessCommandLineResults.toString(), VideoInfo.class);
						videoInfo.setProcessingError(false);
						videoInfo.setFileName(file.getOriginalFilename());
						System.out.println("done");
					} catch (Exception e) {
						e.printStackTrace();
					}


				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				if ( videoInfo == null) {
					videoInfo = new VideoInfo();
					videoInfo.setFileName(file.getOriginalFilename());
					videoInfo.setProcessingError(true);
					Format format = new Format(); //save for now during rapid development.
					videoInfo.setFormat(format);
				}

				// hack for prototype. TODO
				boolean delete = false;



				////////////////////////////////
				//
				// Process Return Information
				//
				///////////////////////////////
				if (exitValue != 0) {
					responseMessage = "There was an Error Uploading: " + file.getOriginalFilename() + "" + "" + ffprobeProcessCommandLineResults.toString() + "";
					delete = true;
					videoInfo.setProcessingError(true);
					videoInfo.setErroMessage(responseMessage);

				} else if (videoInfo.getFormat().getDuration() > 600) {
					int duration = (int) videoInfo.getFormat().getDuration() / 60;
					responseMessage = "Sorry, but Your video file is over" + duration
							+ "minutes long excedded  our 10  Minute Max and can't be uploaded." + " "
							+ file.getOriginalFilename() + "" + "" + ffprobeProcessCommandLineResults.toString() + "";
					videoInfo.setProcessingError(true);
					videoInfo.setErroMessage(responseMessage);
					delete = true;
				}

				else {
					int duration = (int) videoInfo.getFormat().getDuration() / 60;
					responseMessage = "Congratulations. You successfully uploaded  " + file.getOriginalFilename() + ";  (Duration ~= "
							+ duration + " min)";
					videoInfo.setProcessingError(false);
					videoInfo.setProcessingMessage(responseMessage);
					videoInfo.setProcessingResultsJSON(ffprobeProcessCommandLineResults.toString()); // Convience for Server JSP Ajax if needed. 

				}

				if (videoInfo.isProcessingError()) {
					this.deleteFile(targetFileName);
				} 
				
				return videoInfo;



	}
	

	/**
	 * @param fileName
	 */
	private void deleteFile(String fileName) {

		try {
			File theFile = new File(fileName);

			if (theFile.delete()) {
				System.out.println(theFile.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}



}
