package xmlformatter.com.xml.formatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	@Autowired
	private FileValue fileStorageService;
	private Path outputPath = Paths.get("target/*.xml");

	@PostMapping("/insertXMLfile")
	public FileDTO insertfile(@RequestParam("file") MultipartFile file) throws FileNotFoundException {
		String filename = file.getName().toUpperCase();
		if (file.getOriginalFilename().contains("XML")) {
			String fileName = fileStorageService.storeFile(file);

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
					.path(fileName).toUriString();

			return new FileDTO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
		} else {
			throw new FileNotFoundException();

		}
	}

	@PostMapping("/insertXMLfiles")
	public List<FileDTO> insertfiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.asList(files).stream().map(file -> {
			try {
				return insertfile(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {

		Resource resource = fileStorageService.loadFileAsResource(fileName);

		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Wrong file");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/formatxml")
	public ResponseEntity<Resource> formatfile(@PathVariable String fileName, HttpServletRequest request)
			throws Exception {
		Resource resource = fileStorageService.loadFileAsResource(fileName);
		File file = new File(outputPath.toString() + "/" + resource.getFilename());
		FileCopyUtils.copy(resource.getFile(), file);
		File xslFile = null;
		javax.xml.transform.Source xmlSource = new javax.xml.transform.stream.StreamSource(resource.getFile());
		StreamResult sr = new StreamResult(new File(outputPath.toString() + "/" + file.getName()));
		Transformer transformer = getTransformer();
		transformer.transform(xmlSource, sr);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Wrong file");
		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		StreamResult result = new StreamResult(new FileOutputStream(file));
		transformer.transform(xmlSource, sr);
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	private Transformer getTransformer() throws Exception {
		File xslFile = null;
		javax.xml.transform.TransformerFactory transFact = javax.xml.transform.TransformerFactory.newInstance();
		javax.xml.transform.Transformer trans = transFact.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		xslFile = new File("/com.xml.formatter/src/main/java/xmlformatter/com/xml/formatter/format-xml.xsl");
		trans = transFact.newTransformer(new StreamSource(xslFile));
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		trans.setOutputProperty("indent", "yes");
		return trans;
	}

}
