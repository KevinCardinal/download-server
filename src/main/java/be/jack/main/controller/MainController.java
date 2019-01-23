package be.jack.main.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController
{
	@Value("${directory}")
	private String directory;

	@Autowired
	private ServletContext servletContext;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping("/download")
	public ResponseEntity<InputStreamResource> download(
			@RequestParam(name = "file", required = true) String fileName)
	{
		MediaType mediaType = getMediaTypeForFileName(this.servletContext, fileName);
		File file = Paths.get(directory, fileName).toFile();
		logger.debug("Absolute Path : {}", file.getAbsolutePath());
		try
		{
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			logger.info("Download {} - SUCCESS", file.getName());
			return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
	                .contentType(mediaType)
	                .contentLength(file.length()) //
	                .body(resource);
		}
		catch(FileNotFoundException e)
		{
			logger.info("Download {} - FAILED", file.getName());
			return ResponseEntity.notFound().build();
		}
	}

	private static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName)
	{
		String mineType = servletContext.getMimeType(fileName);
		try
		{
			MediaType mediaType = MediaType.parseMediaType(mineType);
			return mediaType;
		}
		catch (Exception e)
		{
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}
}
