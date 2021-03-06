package com.ges.video.view.mvc;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ges.video.domain.video.Video;
import com.ges.video.domain.video.VideoInfo;
import com.ges.video.domain.video.VideoRepository;
import com.ges.video.domain.video.VideoUrls;
import com.ges.video.service.filestorage.StorageFileNotFoundException;
import com.ges.video.service.filestorage.StorageService;
import com.ges.video.service.videoprocessing.FfmpegBasicsProcessor;
import com.ges.video.service.videoprocessing.FfmpegHlsProcessor;
import com.ges.video.service.videoprocessing.FfmpegPosterProcessor;
import com.ges.video.service.videoprocessing.ThumbNailProcessor;
import com.ges.video.service.videoprocessing.VideoProcessingException;

// also enable cross origin at controller level  
//https://spring.io/blog/2015/06/08/cors-support-in-spring-framework
// Can also do in MvcConfig.java WebMvcConfigurerAdapter
//@CrossOrigin
@Controller
public class FileUploadController {

	// File System
	@Value("${api.config.uploaddir}")
	private String uploadDir;

	// Transcoding Support
	@Value("${api.config.ffprobe.bin}")
	private String ffprobe;
	@Value("${api.config.ffmpeg.bin}")
	private String ffmpeg;
	@Value("${api.config.python.bin}")
	private String python;
	@Value("${api.config.gregspritesv1}")
	private String gregspritesv1;

	// Image Magick
	@Value("${api.config.mogrify}")
	private String mogrify;
	@Value("${api.config.identify}")
	private String identify;
	@Value("${api.config.montage}")
	private String montage;

	// gesx
	@Autowired // This means to get the bean called userRepository
	// Which is auto-generated by Spring, we will use it to handle the data
	private VideoRepository videoRepository;

	private final StorageService storageService;
	private final ThumbNailProcessor tnProcessor;
	private final FfmpegBasicsProcessor ffmpegBasicsProcessor;
	private final FfmpegPosterProcessor posterProcessor;
	private final FfmpegHlsProcessor hlsProcessor;
	
	

	private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	public FileUploadController(StorageService storageService, ThumbNailProcessor tnProcessor, FfmpegBasicsProcessor ffmpegBasicsProcessor, FfmpegPosterProcessor posterProcessor,
			FfmpegHlsProcessor hlsProcessor) {
		this.storageService = storageService;
		this.tnProcessor = tnProcessor;
		this.ffmpegBasicsProcessor = ffmpegBasicsProcessor;
		this.posterProcessor = posterProcessor;
		this.hlsProcessor = hlsProcessor;

	}
	
	  // Total control - setup a model and return the view name yourself. Or
	  // consider subclassing ExceptionHandlerExceptionResolver (see below).
	  @ExceptionHandler()
	  public ModelAndView handleError(HttpServletRequest req, Exception ex) {
	    logger.error("Request: " + req.getRequestURL() + " raised " + ex);

	    ModelAndView mav = new ModelAndView();
	    mav.addObject("exception", ex);
	    mav.addObject("url", req.getRequestURL());
	    mav.addObject("alpha", "Yooo dude");
	    mav.setViewName("error");
	    return mav;
	  }

	@GetMapping("/upload")
	public String listUploadedFiles(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName(); // get logged in username

		String ipAddress = this.getIpAddr(request);
		logger.info("Path: /upload GET, Username: " + name + ", Login From IP: " + ipAddress);

		model.addAttribute("files",
				storageService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
								.build().toString())
						.collect(Collectors.toList()));

		return "uploadForm";
	}

	/*
	 * @GetMapping("/loginForm") public String dummyGreg(Model model) throws
	 * IOException {
	 * 
	 * model.addAttribute("name", "Greg"); List<String> list = new
	 * ArrayList<String>(); list.add("alpha"); list.add("theta"); list.add("omega");
	 * model.addAttribute("myList", list);
	 * 
	 * 
	 * return "loginForm"; }
	 */

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
	/*
	 * @GetMapping("/upload") public String goToUploadPage(@PathVariable String
	 * placeHolder){ return "/upload"; }
	 */

	@PostMapping("/upload")
	// public String handleFileUpload(@RequestParam("file") MultipartFile file,
	public String handleFileUpload(@ModelAttribute Video video,

			RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String author = auth.getName(); // get logged in username

		String ipAddress = this.getIpAddr(request);
		logger.info("Path: /upload POST, Username: " + author + ", Login From IP: " + ipAddress);

		MultipartFile file = video.getFile();
		if (!file.getOriginalFilename().toUpperCase().contains(".MP4")) {
			// Invalid, must be mp4 file
			redirectAttributes.addFlashAttribute("message",
					"Sorry, but Your file is  must be of type MP4. {" + file.getOriginalFilename() + "}");
			return "redirect:/upload";

		}

		///////////////////////////////
		//
		// I. FFPROBE file inro
		// -Use TransferObject for POC
		//
		////////////////////////////////
		String processsingMessage;
		String responseErrorMessage;
		boolean processingError;
		String processingResultsJSON;
		VideoInfo videoInfo = new VideoInfo();
		VideoUrls videoUrls = new VideoUrls();
		storageService.store(file);
		try {
			videoInfo = ffmpegBasicsProcessor.getBasicVideoFileInfo(file);
		} catch (VideoProcessingException e) {
			// TODO Auto-generated catch block
			responseErrorMessage = e.toString();
			e.printStackTrace();
		}
		responseErrorMessage = videoInfo.getErroMessage();
		processsingMessage = videoInfo.getProcessingMessage();
		processingError = videoInfo.isProcessingError();
		processingResultsJSON = videoInfo.getProcessingResultsJSON();
		
		// backword compat for development POC !!
		if(processingError) {
			processsingMessage = responseErrorMessage;
		}
		redirectAttributes.addFlashAttribute("message", processsingMessage);
		
		redirectAttributes.addFlashAttribute("processsingMessage", processsingMessage);
		redirectAttributes.addFlashAttribute("responseErrorMessage", responseErrorMessage);
		redirectAttributes.addFlashAttribute("processingError", processingError);
		redirectAttributes.addFlashAttribute("processingResultsJSON", processingResultsJSON);
		
		///////////////////////////////
		//
		// II THUMB SPRITE GEN 
		// -Use TransferObject for POC
		//
		////////////////////////////////
		if (!videoInfo.isProcessingError()) {
			try {
				// TODO from properties file  transcoding.upload.dir=upload-dir/
				this.tnProcessor.processThumbs("upload-dir/", file.getOriginalFilename(), videoUrls);
				} catch(VideoProcessingException ex) {
					redirectAttributes.addFlashAttribute("responseErrorMessage", "Error Processing Thumbs File.  Please contact tech support and reference the following error message:  ((" + ex + "))");
				}
		}
		
		///////////////////////////////
		//
		// Poster
		// 
		//
		////////////////////////////////
		try {
			posterProcessor.processVideoPoster(file, videoInfo);
			videoInfo.setPosterProcessingError(false);
			videoInfo.setPosterStatusMessage("Poster Processed Successfully");
			redirectAttributes.addFlashAttribute("posterStatusMessage", "Poster Processed Successfully" );
		} catch (VideoProcessingException e) {
			logger.error("Error Processing Video Thumb:" + e);
			videoInfo.getPosterStatusMessage().concat("--Error Processing Posters: Exception:" + e);
			redirectAttributes.addFlashAttribute("posterStatusMessage", "Poster Processed Successfully" );
		}

		///////////////////////////////
		//
		// HLS
		// 
		//
		////////////////////////////////
		try {
			this.hlsProcessor.processHlsFile(file, videoInfo);
			videoInfo.setHlsProcessingError(false);
			videoInfo.setHlsStatusMessage("HLS Processed Successfully");
			redirectAttributes.addFlashAttribute("hlsStatusMessage", "HLS Processed Successfully" );
		} catch (VideoProcessingException e) {
			logger.error("Error Processing HLS file:" + e);
			videoInfo.getHlsStatusMessage().concat("--Error Processing HLS: Exception:" + e);
			redirectAttributes.addFlashAttribute("hlsStatusMessage", "HLS Processed Successfully" );
		}
		
		///////////////////////////////
		//
		// MPD
		// 
		//
		////////////////////////////////
		

		///////////////////////////////
		//
		// III SAVE to DB 
		// -keep at end
		// -TODO: add URL's etc.
		//
		////////////////////////////////
		tnProcessor.buildVideoUrls(videoUrls, file.getOriginalFilename());
		
		video.setAuthor(author);
		video.setAuthorIpAddress(ipAddress);
		video.setFormat(videoInfo.getFormat());
		video.setVideourls(videoUrls);
		videoRepository.save(video);
		
		return "redirect:/upload";
	}



	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

}
