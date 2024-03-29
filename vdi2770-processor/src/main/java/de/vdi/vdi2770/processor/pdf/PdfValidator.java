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
package de.vdi.vdi2770.processor.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.preflight.Format;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.DomXmpParser;
import org.apache.xmpbox.xml.XmpParsingException;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;

import de.vdi.vdi2770.processor.common.Message;
import de.vdi.vdi2770.processor.common.MessageLevel;
import lombok.extern.log4j.Log4j2;

/**
 * This class implements PDF validation.
 *
 * <p>
 * According to VDI 2770 specification, PDF files must be PDF/A-2a or PDF/A-3a
 * in general, exception some exceptional cases.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class PdfValidator {

	// prefix is PV
	private final ResourceBundle bundle;
	private final boolean isStrictMode;

	// PDF type constants

	/**
	 * PDF/A1-A
	 */
	public static final String PDF_A_1A = "1A";

	/**
	 * PDF/A1-B
	 */
	public static final String PDF_A_1B = "1B";

	/**
	 * PDF/A2-A
	 */
	public static final String PDF_A_2A = "2A";

	/**
	 * PDF/A2-B
	 */
	public static final String PDF_A_2B = "2B";

	/**
	 * PDF/A2-U
	 */
	public static final String PDF_A_2U = "2U";

	/**
	 * PDF/A3-A
	 */
	public static final String PDF_A_3A = "3A";

	/**
	 * PDF/A3-B
	 */
	public static final String PDF_A_3B = "3B";

	/**
	 * PDF/A3-U
	 */
	public static final String PDF_A_3U = "3U";

	private static List<String> supportedFormats = Arrays.asList(PDF_A_1A, PDF_A_1B, PDF_A_2A,
			PDF_A_2B, PDF_A_2U, PDF_A_3A, PDF_A_3B, PDF_A_3U);

	/**
	 * ctor
	 * 
	 * @param locale       Desired {@link Locale} for validation messages; must not
	 *                     be <code>null</code>.
	 * @param isStrictMode Enable or disable strict validation.
	 */
	public PdfValidator(final Locale locale, final boolean isStrictMode) {

		super();

		Preconditions.checkArgument(locale != null);

		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
		this.isStrictMode = isStrictMode;

		// improve performance of pdfbox with java8 or higher
		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
	}

	private String getPdfAVersion(final XMPMetadata xmp, final String pdfFileName)
			throws PdfValidationException {

		Preconditions.checkArgument(xmp != null, "XMP metadata is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(pdfFileName),
				"PDF file name is null or empty");

		final PDFAIdentificationSchema pdfaSchema = xmp.getPDFIdentificationSchema();
		if (pdfaSchema == null) {
			// PDF is not a PDF/A file
			throw new PdfValidationException(
					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_004"), pdfFileName));
		}

		final String partAndLevel = pdfaSchema.getPart() + pdfaSchema.getConformance();
		if (!supportedFormats.contains(partAndLevel)) {
			throw new PdfValidationException(MessageFormat
					.format(this.bundle.getString("PV_EXCEPTION_005"), pdfFileName, partAndLevel));
		}

		log.info("PDF/A version is " + partAndLevel);
		return partAndLevel;
	}

	private String getPdfAVersion(final DomXmpParser parser, final PDMetadata metadata,
			final String pdfFileName) throws PdfValidationException {

		Preconditions.checkArgument(parser != null, "parser is null");
		Preconditions.checkArgument(metadata != null, "metadata is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(pdfFileName),
				"PDF file name is null or empty");

		try (InputStream input = metadata.createInputStream()) {
			final XMPMetadata xmp = parser.parse(input);

			if (xmp == null) {
				throw new PdfValidationException(MessageFormat
						.format(this.bundle.getString("PV_EXCEPTION_003"), pdfFileName));
			}

			return getPdfAVersion(xmp, pdfFileName);

		} catch (final IOException e) {
			throw new PdfValidationException(
					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_006"), pdfFileName),
					e);
		} catch (final XmpParsingException e) {
			throw new PdfValidationException(
					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_003"), pdfFileName),
					e);
		}
	}

	private String getPdfAVersion(final PDDocument pdfDocument, final String pdfFileName)
			throws PdfValidationException {

		Preconditions.checkArgument(pdfDocument != null, "PDF document is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(pdfFileName),
				"PDF file name is null or empty");

		final PDMetadata metadata = pdfDocument.getDocumentCatalog().getMetadata();
		if (metadata == null) {
			if (log.isWarnEnabled()) {
				log.warn("XMP meta data is null");
			}
			// can not read metadata
			throw new PdfValidationException(
					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_002"), pdfFileName));
		}

		try {
			final DomXmpParser xmpParser = new DomXmpParser();
			xmpParser.setStrictParsing(this.isStrictMode);
			return getPdfAVersion(xmpParser, metadata, pdfFileName);

		} catch (final XmpParsingException e) {

			log.warn("Can not extract XMP metadata using Apache PDFBox");

			// try to read PDF/ A conformance manually
			String level = tryGetPdfAConformance(metadata);
			if (!Strings.isNullOrEmpty(level)) {
				return level;
			}

			log.warn("Can not extract XMP metadata manually from XML: " + tryXmpToString(metadata));

			throw new PdfValidationException(
					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_003"), pdfFileName),
					e);
		}
	}

	/**
	 * Manually extract XMP XML and read PDF/A conformance level using a SAX parser
	 * implementation.
	 * 
	 * @param metadata Apache PDFBox PDF metadata
	 * @return Empty {@link String}, if PDF/A conformance level could not be read.
	 *         Otherwise, PDF/A id concated with leven will return.
	 */
	private static String tryGetPdfAConformance(final PDMetadata metadata) {

		try (COSInputStream xmpStream = metadata.createInputStream()) {

			// using SAX to parse the XML
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();

			// custom SAX handler to read PDF/A XMP metadata data
			XMPSaxHandler handler = new XMPSaxHandler();
			parser.parse(xmpStream, handler);

			return handler.getPdfALevel();
		} catch (final Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Error extracting metadata", e);
			}
			return "";
		}
	}

	private static String tryXmpToString(final PDMetadata metadata) {

		try (COSInputStream rd = metadata.createInputStream()) {
			String xmlText = IOUtils.toString(rd, StandardCharsets.UTF_8);
			return xmlText;
		} catch (final Exception e) {
			log.warn("Can not convert XMP metadata to String", e);
			return "";
		}
	}

	/**
	 * Read the declared PDF/A version from a given PDF file.
	 *
	 * @param pdfFile An existing PDF file
	 * @return The PDF/A version. For VDI 2770, "2A", "2B", "2U", "3A", "3B" and
	 *         "3U" are valid;
	 * @throws PdfValidationException The given PDF file is not conform to the
	 *                                supported list of PDF/A formats.
	 */
	public String getPdfAVersion(final File pdfFile) throws PdfValidationException {

		Preconditions.checkArgument(pdfFile != null);
		if (!pdfFile.exists()) {
			throw new PdfValidationException(MessageFormat
					.format(this.bundle.getString("PV_EXCEPTION_001"), pdfFile.getName()));
		}

		final String pdfFileName = pdfFile.getName();

		log.info("Reading PDF/A version and level from file " + pdfFile.getAbsolutePath());
		try (InputStream pdfStream = new RandomAccessBufferedFileInputStream(pdfFile)) {

			final PDFParser parser = new PDFParser(
					new RandomAccessBufferedFileInputStream(pdfFile));
			parser.parse();

			try (PDDocument pdfDocument = parser.getPDDocument()) {

				if (pdfDocument == null) {
					throw new PdfValidationException(MessageFormat
							.format(this.bundle.getString("PV_EXCEPTION_006"), pdfFileName));
				}

				return getPdfAVersion(pdfDocument, pdfFileName);
			}
		} catch (final IOException e) {
			throw new PdfValidationException(
					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_006"), pdfFileName),
					e);
		}
	}

	/**
	 * Check, if a file is a PDF file.
	 * 
	 * @param pdfFile An existing file
	 * @throws IOException              Error read the PDF file
	 * @throws IllegalArgumentException The given file is <code>null</code>, does
	 *                                  not exist or is not a PDF file.
	 * @return <code>true</code>.
	 */
	public static boolean isPdfFile(final File pdfFile) throws IOException {

		final Tika tika = new Tika();
		final String fileMimeType = tika.detect(pdfFile);
		return StringUtils.equals(fileMimeType, MediaType.PDF.toString());
	}

	/**
	 * Check, if the PDF file is encrypted.
	 * 
	 * <p>
	 * Password protection is handled as encrypted.
	 * </p>
	 * 
	 * <p>
	 * According to VDI 2770:2020 PDF must not be encrypted or password protected.
	 * </p>
	 * 
	 * @param pdfFile An existing PDF file
	 * @throws IOException              Error while reading the PDF file
	 * @throws IllegalArgumentException The given file is <code>null</code>, does
	 *                                  not exist or is not a PDF file.
	 * @return <code>true</code>, if the given PDF File is encrypted.
	 */
	public boolean isEncrypted(final File pdfFile) throws IOException {

		Preconditions.checkArgument(pdfFile != null, "pdfFile is null");
		Preconditions.checkArgument(pdfFile.exists(), "pdfFile does not exist");
		Preconditions.checkArgument(isPdfFile(pdfFile), "pdfFile is not a PDF file");

		try (PDDocument d = PDDocument.load(pdfFile)) {
			return d.isEncrypted();
		} catch (final InvalidPasswordException e) {
			log.warn("PDF file " + pdfFile.getAbsolutePath() + " is password protected: ",
					e.getMessage());
			// password protected is not allowed, too
			return true;
		}
	}

	/**
	 * VDI 2770 requires full-text search for PDF documents.
	 * 
	 * <p>
	 * This simple method tries to extract text from a PDF document. If any text
	 * could be extracted, this method returns true. But, it is not possible to
	 * extract the semantic of the text. We cannot check, whether the extracted text
	 * has any sense.
	 * </p>
	 * 
	 * @param pdfFile An existing, non-<code>null</code> PDF file
	 * @return True, if any text can be extracted, otherwise <code>false</code>.
	 * @throws IOException              There was an error reading the PDF file.
	 * @throws IllegalArgumentException The given {@link File} is <code>null</code>,
	 *                                  does not exist or is not a PDF file.
	 */
	public boolean hasText(final File pdfFile) throws IOException {

		Preconditions.checkArgument(pdfFile != null, "pdfFile is null");
		Preconditions.checkArgument(pdfFile.exists(), "pdfFile does not exist");
		Preconditions.checkArgument(isPdfFile(pdfFile), "pdfFile is not a PDF file");

		// try to load the PDF document
		try (PDDocument d = PDDocument.load(pdfFile)) {

			// iterate over pages
			int numPages = d.getNumberOfPages();
			for (int i = 1; i <= numPages; i++) {

				// text extractor class
				final PDFTextStripper pdfStripper = new PDFTextStripper();
				pdfStripper.setStartPage(i);
				pdfStripper.setEndPage(i);
				String extractedText = pdfStripper.getText(d);
				if (!StringUtils.isEmpty(extractedText)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Execute PDF/A preflight for a PDF document.
	 * 
	 * <p>
	 * At the moment, only preflight of PDF/A-1a and PDF/A-1b is supported. In case
	 * of PDF/A{2,3}-{a,b} files, validation is skipped.
	 * </p>
	 * 
	 * @param pdfFile An existing, non-<code>null</code> PDF file
	 * @return A {@link Collection} of validation {@link Message}s.
	 * @throws IOException              There was an error reading the PDF file.
	 * @throws IllegalArgumentException The given {@link File} is <code>null</code>,
	 *                                  does not exist or is not a PDF file.
	 */
	public List<Message> preflight(final File pdfFile) throws IOException {

		Preconditions.checkArgument(pdfFile != null, "pdfFile is null");
		Preconditions.checkArgument(pdfFile.exists(), "pdfFile does not exist");
		Preconditions.checkArgument(isPdfFile(pdfFile), "pdfFile is not a PDF file");

		boolean isPdfA1 = false;
		boolean isPDFAa = false;
		try {
			String version = getPdfAVersion(pdfFile);

			// is PDF/A-1{a,b}?
			isPdfA1 = StringUtils.containsIgnoreCase(version, "1");

			// is PDF/A-{1,2,3}a?
			isPDFAa = StringUtils.endsWithIgnoreCase(version, "a");
		} catch (final Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("Can not read PDF/A conformance level: " + e.getMessage());
			}
		}

		if (isPdfA1) {
			return preflight1(pdfFile, isPDFAa);
		}

		return new ArrayList<>();
	}

	private List<Message> preflight1(final File pdfFile, boolean isPdfA) throws IOException {

		// init result
		ValidationResult result = null;
		PreflightParser parser = new PreflightParser(pdfFile);
		parser.parse(isPdfA ? Format.PDF_A1A : Format.PDF_A1B);

		try (PreflightDocument document = parser.getPreflightDocument()) {
			document.validate();
			result = document.getResult();
		} catch (SyntaxValidationException e) {
			result = e.getResult();
		}

		final List<Message> messages = new ArrayList<>();

		// return validation result
		if (result.isValid()) {
			messages.add(new Message(MessageFormat.format(this.bundle.getString("PV_MESSAGE_001"),
					pdfFile.getName())));
		} else {
			messages.add(new Message(MessageFormat.format(this.bundle.getString("PV_MESSAGE_002"),
					pdfFile.getName())));
			for (ValidationError error : result.getErrorsList()) {
				messages.add(new Message(MessageLevel.ERROR,
						error.getErrorCode() + ": " + error.getDetails()));
			}
		}

		return messages;
	}
}
