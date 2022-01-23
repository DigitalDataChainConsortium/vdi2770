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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import de.vdi.vdi2770.processor.report.Report;
import de.vdi.vdi2770.processor.report.fop.FopReport;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.web.configuration.SmartLocaleResolver;
import de.vdi.vdi2770.web.service.ReportService;
import de.vdi.vdi2770.web.transfer.ReportProperties;

/**
 * This controller handles validation report requests.
 *
 * <p>
 * PDF files, container files and XML files can be validated.
 * </p>
 *
 * <p>
 * Reports can return as JSON data structures or PDF report files.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@RestController
@RequestMapping(path = "/rest")
public class ReportController extends BaseController {

	private final ReportService service;

	/**
	 * ctor
	 *
	 * @param service  The report service implementation
	 * @param resolver A language resolver to translations
	 */
	public ReportController(final ReportService service, final SmartLocaleResolver resolver) {

		super(resolver);

		Preconditions.checkArgument(service != null, "service is null");

		this.service = service;
	}

	/**
	 * Validate a file and return a {@link Report} as JSON structure.
	 *
	 * @param request The REST / web request.
	 * @param file    A file to be validated.
	 * @param props   Optional Configuration parameter to configure reporting
	 * @return A serialized {@link Report} instance or an error message
	 */
	@RequestMapping(path = "/report", method = { RequestMethod.POST }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> checkContainerAndReturnJson(final HttpServletRequest request,
			@RequestParam("file") final MultipartFile file,
			@RequestParam(name = "settings", required = false) final ReportProperties props) {

		try {

			Preconditions.checkArgument(file != null, "file is null");
			Preconditions.checkArgument(!Strings.isNullOrEmpty(file.getOriginalFilename()),
					"empty file name");
			Preconditions.checkArgument(!file.isEmpty(), "file is empty");

			// save file to temporary folder
			final File zipFile = transferFile(file);

			try {

				// read language from request
				final Locale locale = getLocale(request);

				// validate the file
				final Report result = this.service.validateFile(zipFile, props, locale);

				return toResponse(result);
			} finally {
				FileUtils.deleteDirectory(zipFile.getParentFile());
			}
		} catch (final Exception e) {
			return toResponse(e, request);
		}
	}

	@RequestMapping(path = "/reportpdf", method = {
			RequestMethod.POST }, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<?> checkContainerAndReturnPdfFile(final HttpServletRequest request,
			@RequestParam("file") final MultipartFile file,
			@RequestParam(name = "settings", required = false) final ReportProperties props) {

		try {

			Preconditions.checkArgument(file != null, "file is null");
			Preconditions.checkArgument(!Strings.isNullOrEmpty(file.getOriginalFilename()),
					"empty file name");
			Preconditions.checkArgument(!file.isEmpty(), "file is empty");

			final File zipFile = transferFile(file);

			try {

				final Locale locale = getLocale(request);

				final Report result = this.service.validateFile(zipFile, props, locale);

				ReportProperties config = props;
				if (config == null) {
					config = new ReportProperties();
				}

				FopReport doc = new FopReport(locale);
				byte[] pdfBytes = doc.createPdf(result, config.isRenderWarning(),
						config.isRenderInfo());
				return toResponse(pdfBytes);

			} finally {
				FileUtils.deleteDirectory(zipFile.getParentFile());
			}
		} catch (final Exception e) {
			return toResponse(e, request);
		}
	}

	private static ResponseEntity<Report> toResponse(Report report) {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		return new ResponseEntity<Report>(report, responseHeaders, HttpStatus.OK);
	}

}
