/*******************************************************************************
 * Copyright (C) 2021 Johannes Schmidt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.vdi.vdi2770.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.web.configuration.SmartLocaleResolver;
import de.vdi.vdi2770.web.transfer.ErrorPayload;
import lombok.extern.log4j.Log4j2;

/**
 * Base class for controller implementations.
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class BaseController {

	private final SmartLocaleResolver localeResolver;

	public BaseController(final SmartLocaleResolver resolver) {

		Preconditions.checkArgument(resolver != null, "resolver is null");

		this.localeResolver = resolver;
	}

	protected Locale getLocale(final HttpServletRequest request) {

		Preconditions.checkArgument(request != null, "request is null");

		final Locale locale = this.localeResolver.resolveLocale(request);
		return locale;
	}

	/**
	 * Convert an exception to a HTTP response
	 * 
	 * @param exception A non-<code>null</code> exception.
	 * @return A HTTP {@link ResponseEntity}
	 */
	protected ResponseEntity<?> toResponse(final Exception exception,
			final HttpServletRequest request) {

		Preconditions.checkArgument(exception != null, "exception is null");

		log.error("Returning error to client", exception);

		final Locale locale = getLocale(request);

		// prefix is BC
		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.web", locale);

		// illegal argument exceptions are bad requests
		if (exception instanceof IllegalArgumentException) {

			final HttpStatus status = HttpStatus.BAD_REQUEST;
			final String message = bundle.getString("BC_MESSAGE_001");
			ErrorPayload error = new ErrorPayload(message, status);

			return new ResponseEntity<>(error, status);
		}

		// otherwise, an internal server error occurred
		final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		final String message = bundle.getString("BC_MESSAGE_002");
		final ErrorPayload error = new ErrorPayload(message, status);

		return new ResponseEntity<>(error, status);
	}

	/**
	 * Return file content.
	 * 
	 * @param file File content to be returned as HTTP response.
	 * 
	 * @return HTTP response with bytes.
	 */
	protected static ResponseEntity<byte[]> toResponse(final byte[] file) {

		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());

		ResponseEntity<byte[]> response = new ResponseEntity<>(file, headers, HttpStatus.OK);
		return response;
	}

	/**
	 * Create a temporary folder, that will be be deleted on exit.
	 * 
	 * @return A {@link File} instance.
	 * 
	 * @throws IOException There was an error while creating the folder.
	 */
	protected final static File createTempFolder() throws IOException {

		final Path tempFolder = Files.createTempDirectory("vdi2770_");

		final File tmpFile = tempFolder.toFile();
		tmpFile.deleteOnExit();

		return tmpFile;
	}

	/**
	 * Transfer a {@link MultipartFile} as {@link File} in a temporary folder.
	 * 
	 * @param file A non-<code>null</code> {@link MultipartFile} instance.
	 * @return A {@link File}
	 * @throws IOException Error while transferring the file content to local file.
	 */
	protected final static File transferFile(final MultipartFile file) throws IOException {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(file.getOriginalFilename()),
				"missing file name");

		final File tmpFile = createTempFolder();

		final File transferdFile = new File(tmpFile, file.getOriginalFilename());
		transferdFile.deleteOnExit();
		file.transferTo(transferdFile);

		return transferdFile;
	}
}
