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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.xml.DomXmpParser;
import org.apache.xmpbox.xml.XmpParsingException;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.processor.common.Message;
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
	 * @param locale Desired {@link Locale} for validation messages; must not be
	 *               <code>null</code>.
	 */
	public PdfValidator(final Locale locale) {

		super();

		Preconditions.checkArgument(locale != null);

		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);

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
			// can not read metadata
			throw new PdfValidationException(
					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_002"), pdfFileName));
		}

		try {
			final DomXmpParser xmpParser = new DomXmpParser();
			return getPdfAVersion(xmpParser, metadata, pdfFileName);

		} catch (final XmpParsingException e) {
			throw new PdfValidationException(
					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_003"), pdfFileName),
					e);
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

	public Collection<Message> validate(final File pdfFile) throws PdfValidationException {

		Preconditions.checkArgument(pdfFile != null, "PDF file is null");
		if (!pdfFile.exists()) {
			throw new PdfValidationException(MessageFormat
					.format(this.bundle.getString("PV_EXCEPTION_001"), pdfFile.getName()));
		}

//		final String pdfFileName = pdfFile.getName();

//		VeraGreenfieldFoundryProvider.initialise();
//
//		// read the PDF/A version from XMP metadata
//		final String pdfFormat = getPdfAVersion(pdfFile);
//		// set target PDF/A version for validation
//		final PDFAFlavour flavour = PDFAFlavour.fromString(pdfFormat.toLowerCase());
//
//		if (log.isInfoEnabled()) {
//			log.info("Validating PDF/A " + pdfFormat + " format for PDF file " + pdfFileName);
//		}

		// Resulting messages generated from veraPDF
		final Collection<Message> result = new ArrayList<>();

//		try (final InputStream pdfStream = new FileInputStream(pdfFile)) {
//
//			try (final VeraPDFFoundry boundary = Foundries.defaultInstance()) {
//
//				try (final PDFAParser parser = boundary.createParser(pdfStream, flavour)) {
//					try (final PDFAValidator validator = boundary.createValidator(flavour, false)) {
//
//						// validate PDF
//						final ValidationResult validationResult = validator.validate(parser);
//						if (!validationResult.isCompliant()) {
//
//							for (final TestAssertion failed : validationResult.getTestAssertions().stream()
//									.filter(a -> a.getStatus() == Status.FAILED).collect(Collectors.toList())) {
//
//								result.add(new Message(MessageLevel.ERROR,
//										failed.getLocation() + ": " + failed.getMessage()));
//							}
//						}
//					}
//				}
//			} catch (final EncryptedPdfException e) {
//				throw new PdfValidationException(
//						MessageFormat.format(this.bundle.getString("PV_EXCEPTION_007"), pdfFileName), e);
//			} catch (final ValidationException | ModelParsingException e) {
//				throw new PdfValidationException(
//						MessageFormat.format(this.bundle.getString("PV_EXCEPTION_008"), pdfFileName), e);
//			}
//		} catch (final IOException e) {
//			throw new PdfValidationException(
//					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_006"), pdfFileName), e);
//		} catch (Exception e) {
//			throw new PdfValidationException(
//					MessageFormat.format(this.bundle.getString("PV_EXCEPTION_009"), pdfFileName), e);
//		}
//
//		if (Message.hasWarnings(result)) {
//			log.warn("Found " + result.size() + " PDF/A problems.");
//		} else {
//			result.add(new Message(this.bundle.getString("PV_MESSAGE_001")));
//		}

		return result;
	}
}
