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
package de.vdi.vdi2770.web.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import de.vdi.vdi2770.metadata.MetadataException;
import de.vdi.vdi2770.processor.ProcessorException;
import de.vdi.vdi2770.processor.common.Message;
import de.vdi.vdi2770.processor.common.MessageLevel;
import de.vdi.vdi2770.processor.report.ContainerValidator;
import de.vdi.vdi2770.processor.report.Report;
import de.vdi.vdi2770.processor.report.ReportStatistics;
import de.vdi.vdi2770.processor.report.StatisticsWriter;
import de.vdi.vdi2770.processor.zip.ZipUtils;
import de.vdi.vdi2770.web.transfer.ReportProperties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;

import lombok.extern.log4j.Log4j2;

/**
 * This is a service implementation for file validation.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Service
@Log4j2
public class ReportService {

	@Value("${vdi2770.statistic.logfile:./stats/statistics.csv}")
	private String logFile;

	/**
	 * ctor
	 */
	public ReportService() {
		super();
	}

	/**
	 * Validate and report on a given {@link File}. A {@link Report} is returned
	 * that contains validation messages and information.
	 *
	 * <p>
	 * This service is going to validate a given file. This file may be
	 * <ul>
	 * <li>A XML meta data file</li>
	 * <li>A PDF file</li>
	 * <li>A document container file</li>
	 * <li>A documentation container file</li>
	 * </ul>
	 * </p>
	 *
	 * @param file   A {@link File} instance (must not be <code>null</code> and must
	 *               exist).
	 * @param props  Properties for reporting.
	 * @param locale The desired {@link Locale} for reporting messages.
	 * @return A {@link Report} on the given file.
	 */
	public Report validateFile(final File file, final ReportProperties props, final Locale locale) {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(locale != null, "locale is null");

		// set default properties
		ReportProperties config = props;
		if (config == null) {
			config = new ReportProperties();
		}

		// prefix is RS
		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.web", locale);

		Report result = null;

		String mimeType = "UNKNOWN";

		MessageLevel logLevel = MessageLevel.INFO;
		if (!config.isRenderInfo()) {
			logLevel = MessageLevel.WARN;
		}
		if (!config.isRenderWarning()) {
			logLevel = MessageLevel.ERROR;
		}

		if (!file.isDirectory()) {
			// probe content type of the given file
			try {
				mimeType = Files.probeContentType(file.toPath());
			} catch (final IOException e) {
				log.error("can not get MIME type", e);
				result = new Report(locale, file, logLevel);
				result.addMessage(new Message(MessageLevel.ERROR,
						MessageFormat.format(bundle.getString("RS_MESSAGE_001"), mimeType)));
			}

			// validate PDF file
			if (MediaType.PDF.toString().equals(mimeType)) {
				result = validatePdfFile(file, locale, logLevel, config.isEnableStrictMode());
			}
			// validate container file
			else if (ZipUtils.ZIP_CONTENT_TYPE.contains(mimeType)) {
				result = validateContainerFile(file, locale, logLevel, config.isEnableStrictMode());
			}
			// validate XML meta data file
			else if (MediaType.APPLICATION_XML_UTF_8.withoutParameters().toString().equals(mimeType)
					|| "text/xml".equals(mimeType)) {
				result = validateXmlFile(file, locale, logLevel, config.isEnableStrictMode());
			} else {
				result = new Report(locale, file, logLevel);
				result.addMessage(new Message(MessageLevel.ERROR,
						MessageFormat.format(bundle.getString("RS_MESSAGE_001"), mimeType)));
			}
		} else {
			log.error("Given file is a directory: " + file.getAbsolutePath());
			result = new Report(locale, file, logLevel);
			result.addMessage(new Message(MessageLevel.ERROR, MessageFormat
					.format(bundle.getString("RS_MESSAGE_001"), "application/x-directory")));
		}

		// save statistics, if allowed
		if (config.isAllowStatistics()) {

			final StatisticsWriter statsWriter = new StatisticsWriter(this.logFile);
			statsWriter.write(new ReportStatistics(result));
		}

		return result;
	}

	private static Report validateXmlFile(final File file, final Locale locale,
			final MessageLevel logLevel, boolean strictModeEnabled) {

		final ContainerValidator reporting = new ContainerValidator(locale, strictModeEnabled);

		final Report result = new Report(locale, file, logLevel);
		reporting.validateAndReportVdiXmlFile(file, result, 0, false);

		return result;
	}

	private static Report validateContainerFile(final File file, final Locale locale,
			final MessageLevel logLevel, boolean strictModeEnabled) {

		final ContainerValidator reporting = new ContainerValidator(locale, strictModeEnabled);
		try {
			return reporting.validate(file, logLevel);
		} catch (final MetadataException | ProcessorException ex) {
			log.error("Error validating file", ex);
			final Report result = new Report(locale, file, logLevel);
			result.addMessage(new Message(MessageLevel.ERROR, ex.getMessage()));
			return result;
		}
	}

	private static Report validatePdfFile(final File file, final Locale locale,
			final MessageLevel logLevel, boolean strictModeEnabled) {

		final Report result = new Report(locale, file, logLevel);

		final ContainerValidator validator = new ContainerValidator(locale, strictModeEnabled);
		result.addMessages(validator.validatePdfFile(file, false, 0));

		return result;
	}
}
