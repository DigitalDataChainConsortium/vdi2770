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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Validate PDF files
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
public class PdfValidationTest {

	private static final String EXAMPLES_FOLDER = "../examples";

	/**
	 * Return PDF/A level
	 * 
	 * @throws PdfValidationException
	 */
	@Test
	public void detectValidPdf() throws PdfValidationException {

		PdfValidator pdfValidator = new PdfValidator(Locale.getDefault());
		final String type = pdfValidator.getPdfAVersion(new File(EXAMPLES_FOLDER, "Valid.pdf"));

		assertEquals(type, "3A");
	}

	/**
	 * Check a regular PDF file.
	 */
	@Test
	public void detectInvalid2Pdf() {

		PdfValidator pdfValidator = new PdfValidator(Locale.getDefault());
		final PdfValidationException ex = Assertions.assertThrows(PdfValidationException.class,
				() -> {
					pdfValidator.getPdfAVersion(new File(EXAMPLES_FOLDER, "Invalid2.pdf"));
				});

		// regular PDF files do not have XMP metadata?
		assertTrue(ex.getMessage().startsWith("PV_002"));
	}

	@Test
	public void preflight() throws IOException {

//		final File pdfFile = new File(EXAMPLES_FOLDER, "Valid.pdf");
		
		final File pdfFile = new File("C:\\Temp\\deleteme\\e80260de_03-05.pdf");
		
		PreflightParser parser = new PreflightParser(pdfFile);

		ValidationResult result = null;
		try {
			parser.parse();

			final PDEncryption enc = parser.getEncryption();
			
			
			PreflightDocument document = parser.getPreflightDocument();
			document.validate();

			// Get validation result
			result = document.getResult();
			document.close();

		} catch (SyntaxValidationException e) {
			result = e.getResult();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// display validation result
		if (result.isValid()) {
			System.out.println("The file " + pdfFile + " is a valid PDF/A-1b file");
		} else {
			System.out.println("The file" + pdfFile + " is not valid, error(s) :");
			for (ValidationError error : result.getErrorsList()) {
				System.out.println(error.getErrorCode() + " : " + error.getDetails());
			}
		}

	}

}
