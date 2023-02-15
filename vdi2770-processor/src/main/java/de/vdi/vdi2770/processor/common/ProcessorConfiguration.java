/*******************************************************************************
 * Copyright (C) 2021-2023 Johannes Schmidt
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
package de.vdi.vdi2770.processor.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.tika.Tika;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.processor.ProcessorException;
import lombok.extern.log4j.Log4j2;

/**
 * This processor project support configuration options defined in an
 * app.properties file. This class provides access to the property settings
 * including basic validation.
 * 
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class ProcessorConfiguration {

	// Prefix is PC
	private final ResourceBundle bundle;

	private static final String VDI_PREFIX = "vdi2770.";

	private static final String REPORT_PREFIX = VDI_PREFIX + "report.pdf.";
	
	private static final String ZIP_PREFIX = VDI_PREFIX + "zip.";
	
	private static final String VALIDATOR_PREFIX = VDI_PREFIX + "validator.";

	// PDF properties

	private static final String REPORT_LOGO_FILE_PROPERTY = REPORT_PREFIX + "logo.file";

	private static final String REPORT_LOGO_HEIGHT_PROPERTY = REPORT_PREFIX + "logo.height";

	private static final String REPORT_TITLE_LOGO_HEIGHT_PROPERTY = REPORT_PREFIX
			+ "title.logo.height";

	private static final String REPORT_AUTHOR_PROPERTY = REPORT_PREFIX + "author";

	private static final String REPORT_HEADING_COLOR_PROPERTY = REPORT_PREFIX + "heading.color";

	private static final String REPORT_TITLE_COLOR_PROPERTY = REPORT_PREFIX + "title.color";

	private static final String REPORT_TABLE_BORDER_COLOR_PROPERTY = REPORT_PREFIX
			+ "table.border.color";

	private static final String REPORT_FONT_COLOR_PROPERTY = REPORT_PREFIX + "font.color";

	private static final String REPORT_LINK_COLOR_PROPERTY = REPORT_PREFIX + "link.color";
	
	// ZIP properties
	
	private static final String ZIP_MAX_COMPRESSION = ZIP_PREFIX + "maxcompression";

	private static final String ZIP_MAX_FILE_SIZE = ZIP_PREFIX + "maxfilesize";
	
	// Strict mode properties
	
	private static final String VALIDATOR_TREAT_PDF_ERROR_AS_WARNING = VALIDATOR_PREFIX
			+ "pdfaError.asWarning";

	// property file
	private static final String APP_PROPERTIES_FILE_NAME = "app.properties";

	// properties to access
	private final Properties properties;

	private static Map<Locale, ProcessorConfiguration> instances = new HashMap<>();

	/**
	 * Get an instance of {@link ProcessorConfiguration} for a desired language
	 * 
	 * @param locale Desired {@link Locale} for validation messages; must not be
	 *               <code>null</code>.
	 * @return An instance of this class
	 */
	public static ProcessorConfiguration getInstance(final Locale locale) {
		if (instances.containsKey(locale)) {
			return instances.get(locale);
		}

		ProcessorConfiguration newInstance = new ProcessorConfiguration(locale);
		instances.put(locale, newInstance);

		return newInstance;
	}

	private ProcessorConfiguration(final Locale locale) {

		Preconditions.checkArgument(locale != null);

		this.properties = new Properties();
		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
		loadProperties();
		
		if(log.isDebugEnabled()) {
			
			log.debug("application settings:");
			try {
				log.debug(REPORT_LOGO_FILE_PROPERTY + ": " + getLogoFile());
			} catch (final ProcessorException e) {
				e.printStackTrace();
			}
			log.debug(REPORT_LOGO_HEIGHT_PROPERTY + ": " + getReportLogoHeight());
			log.debug(REPORT_TITLE_LOGO_HEIGHT_PROPERTY + ": " + getReportLogoTitleHeight());
			log.debug(REPORT_AUTHOR_PROPERTY + ": " + getReportAuthor());
			log.debug(REPORT_HEADING_COLOR_PROPERTY + ": " + getReportHeadingColor());
			log.debug(REPORT_TITLE_COLOR_PROPERTY + ": " + getReportTitleColor());
			log.debug(REPORT_TABLE_BORDER_COLOR_PROPERTY + ": " + getReportTableBorderColor());
			log.debug(REPORT_FONT_COLOR_PROPERTY + ": " + getReportFontColor());
			log.debug(REPORT_LINK_COLOR_PROPERTY + ": " + getReportLinkColor());
			log.debug(ZIP_MAX_COMPRESSION + ": " + getMaxZipCompressionFactor());
			log.debug(ZIP_MAX_FILE_SIZE + ": " + getMaxZipFileSize());
			log.debug(VALIDATOR_TREAT_PDF_ERROR_AS_WARNING + ": " + isTreatPdfErrorsAsWarnings());
		}
	}

	/**
	 * Read properties
	 */
	private Properties loadProperties() {

		// load app properties in directory
		final File appConfigFile = new File("./" + APP_PROPERTIES_FILE_NAME);
		if (appConfigFile.exists()) {
			try (InputStream input = new FileInputStream(appConfigFile)) {
				this.properties.load(new InputStreamReader(input, Charset.forName("UTF-8")));
			} catch (final IOException e) {
				log.warn(this.bundle.getString("PC_WARN_001"), e);
			}
		} else {
			// load app properties from resources
			try (InputStream input = ProcessorConfiguration.class
					.getResourceAsStream("/" + APP_PROPERTIES_FILE_NAME)) {
				this.properties.load(new InputStreamReader(input, Charset.forName("UTF-8")));
			} catch (final IOException e) {
				log.warn(this.bundle.getString("PC_WARN_002"), e);
			}
		}

		return this.properties;
	}

	/**
	 * Get an logo file to include in a main document header.
	 * 
	 * @return An image {@link File}
	 * @throws ProcessorException The image format is not supported / invalid or the
	 *                            file does not exist
	 */
	public File getLogoFile() throws ProcessorException {

		String logoPath = this.properties.getProperty(REPORT_LOGO_FILE_PROPERTY);
		if (!Strings.isNullOrEmpty(logoPath)) {

			File tmp = new File(logoPath);
			if (tmp.exists()) {

				// is the file an image?
				final Tika tika = new Tika();
				try {
					String fileMimeType = tika.detect(tmp);
					if (log.isDebugEnabled()) {
						log.debug("Logo file fomat ist " + fileMimeType);
					}

					// supported mime types
					if (Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/bmp",
							"image/x-bmp", "image/x-ms-bmp", "image/gif", "image/tiff",
							"application/postscript").contains(fileMimeType)) {
						return tmp;
					}
					throw new ProcessorException(this.bundle.getString("PC_EXCEPTION_003"));
				} catch (final IOException e) {
					throw new ProcessorException(this.bundle.getString("PC_EXCEPTION_004"), e);
				}
			}

			// file not found
			throw new ProcessorException(
					MessageFormat.format(this.bundle.getString("PC_EXCEPTION_007"), logoPath));
		}

		return null;
	}

	/**
	 * Get the height of the logo at the heading area as {@link String}.
	 * 
	 * @return Height definition like e.g. '3cm'.
	 */
	public String getReportLogoHeight() {
		String height = this.properties.getProperty(REPORT_LOGO_HEIGHT_PROPERTY);
		if (Strings.isNullOrEmpty(height)) {
			return "2cm";
		}

		return height;
	}

	/**
	 * Get the height of the logo at the title page as {@link String}.
	 * 
	 * @return Height definition like e.g. '3cm'.
	 */
	public String getReportLogoTitleHeight() {
		String height = this.properties.getProperty(REPORT_TITLE_LOGO_HEIGHT_PROPERTY);
		if (Strings.isNullOrEmpty(height)) {
			return "2cm";
		}

		return height;
	}

	/**
	 * Get the name of the author of the report.
	 * 
	 * @return The author name.
	 */
	public String getReportAuthor() {
		String height = this.properties.getProperty(REPORT_AUTHOR_PROPERTY);
		if (Strings.isNullOrEmpty(height)) {
			return "Author of the Report";
		}

		return height;
	}

	/**
	 * Get the color of links in the PDF report.
	 * 
	 * @return The configured color. Black is the default value, if the application
	 *         property is not set.
	 */
	public String getReportHeadingColor() {
		String color = this.properties.getProperty(REPORT_HEADING_COLOR_PROPERTY);
		if (Strings.isNullOrEmpty(color)) {
			return "#000000";
		}

		return color;
	}

	/**
	 * Get the color of the title in the PDF report.
	 * 
	 * @return The configured color. Black is the default value, if the application
	 *         property is not set.
	 */
	public String getReportTitleColor() {
		String color = this.properties.getProperty(REPORT_TITLE_COLOR_PROPERTY);
		if (Strings.isNullOrEmpty(color)) {
			return "#000000";
		}

		return color;
	}

	/**
	 * Get the color of table borders in the PDF report.
	 * 
	 * @return The configured color. Black is the default value, if the application
	 *         property is not set.
	 */
	public String getReportTableBorderColor() {
		String color = this.properties.getProperty(REPORT_TABLE_BORDER_COLOR_PROPERTY);
		if (Strings.isNullOrEmpty(color)) {
			return "#000000";
		}

		return color;
	}

	/**
	 * Get the color of the font in the PDF report.
	 * 
	 * @return The configured color. Black is the default value, if the application
	 *         property is not set.
	 */
	public String getReportFontColor() {
		String color = this.properties.getProperty(REPORT_FONT_COLOR_PROPERTY);
		if (Strings.isNullOrEmpty(color)) {
			return "#000000";
		}

		return color;
	}

	/**
	 * Get the color of links in the PDF report.
	 * 
	 * @return The configured color. Black is the default value, if the application
	 *         property is not set.
	 */
	public String getReportLinkColor() {
		String color = this.properties.getProperty(REPORT_LINK_COLOR_PROPERTY);
		if (Strings.isNullOrEmpty(color)) {
			return "#000000";
		}

		return color;
	}

	/**
	 * Zip bomb detection: maximum supported compression rate for zip files.
	 * 
	 * @return The value of the property. If the application property is not set, -1
	 *         returns.
	 */
	public int getMaxZipCompressionFactor() {
		String factor = this.properties.getProperty(ZIP_MAX_COMPRESSION);

		final int defaultValue = -1;

		if (Strings.isNullOrEmpty(factor)) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(factor);
		} catch (@SuppressWarnings("unused") final NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Zip bomb detection: maximum supported uncompressed file size of a Zip entry
	 * given in Bytes. If maximum file size shall be 2MB the value of this property
	 * must be 2000000.
	 * 
	 * @return The value of the property. If the application property is not set, -1
	 *         returns.
	 */
	public int getMaxZipFileSize() {
		String factor = this.properties.getProperty(ZIP_MAX_FILE_SIZE);

		final int defaultValue = -1;

		if (Strings.isNullOrEmpty(factor)) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(factor);
		} catch (@SuppressWarnings("unused") final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	/**
	 * According to VDI 2770, PDF files shall be PDF/A files (normally PDF/A-{1,2,3}a files
	 * and in case of certificates PDF/A-{1,2,3}b files).
	 * If this application property is set to <code>true</code>, PDF files, that do not
	 * conform to any PDF/A specification are reported as warnings instead of errors.
	 * It is the same, if a PDF/A files shall be PDF/A-{1,2,3}a but is a PDF/A-{1,2,3}b file. 
	 * 
	 * @return <code>true</code>, if PDF validation errors shall be handled as warnings
	 */
	public boolean isTreatPdfErrorsAsWarnings() {
		String setting = this.properties.getProperty(VALIDATOR_TREAT_PDF_ERROR_AS_WARNING);
		if (Strings.isNullOrEmpty(setting)) {
			return false;
		}
		
		setting = setting.toLowerCase();
        if (Arrays.asList("1", "true", "yes").contains(setting))
            return true;

        return false;
	}
}
