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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.web.configuration.SmartLocaleResolver;
import de.vdi.vdi2770.web.transfer.ErrorPayload;
import lombok.extern.log4j.Log4j2;

/**
 * Global controller exception handler
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@ControllerAdvice
@Log4j2
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Maximum upload file size configured in application properties
	 */
	@Value("${spring.servlet.multipart.max-file-size:''}")
	private String fileSize;

	/**
	 * Locale resolver to localize error messages
	 */
	private final SmartLocaleResolver localeResolver;

	/**
	 * ctor
	 * 
	 * @param resolver Locale resolver instance
	 */
	public RestExceptionHandler(final SmartLocaleResolver resolver) {
		super();

		Preconditions.checkArgument(resolver != null, "resolver is null");

		this.localeResolver = resolver;
	}

	/**
	 * Return HTTP 413 in case the given file exceeds the maximum upload file size
	 * 
	 * @param exception A {@link MaxUploadSizeExceededException} instance
	 * @return HTTP 413 using {@link ErrorPayload} JSON structure.
	 */
	@ResponseBody
	@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ErrorPayload handleMaxUploadSizeExceededException(
			final MaxUploadSizeExceededException exception, final HttpServletRequest request) {

		Preconditions.checkArgument(exception != null, "exception is null");
		Preconditions.checkArgument(request != null, "request is null");

		log.error("Maximum upload size detected.", exception);

		// read language from request
		final Locale locale = this.localeResolver.resolveLocale(request);

		// prefix is REH
		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.web", locale);

		String message = null;
		if (!Strings.isNullOrEmpty(this.fileSize)) {
			message = MessageFormat.format(bundle.getString("REH_MESSAGE_001"), this.fileSize);
		} else {
			message = bundle.getString("REH_MESSAGE_002");
		}

		ErrorPayload error = new ErrorPayload(message, HttpStatus.PAYLOAD_TOO_LARGE);

		return error;
	}

	/**
	 * Return HTTP 400 in case there was an error while file upload
	 * 
	 * <p>
	 * {@link FileUploadException} may raise in case of empty file or empty folders.
	 * 
	 * @param exception A {@link FileUploadException} instance
	 * @return HTTP 400 using {@link ErrorPayload} JSON structure.
	 */
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(FileUploadException.class)
	public ErrorPayload handleFileUploadException(final FileUploadException exception,
			final HttpServletRequest request) {

		log.error("Error while uploading file.", exception);

		// read language from request
		final Locale locale = this.localeResolver.resolveLocale(request);

		// prefix is REH
		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.web", locale);

		final String message = bundle.getString("REH_MESSAGE_003");
		ErrorPayload error = new ErrorPayload(message, HttpStatus.BAD_REQUEST);

		return error;
	}
}
