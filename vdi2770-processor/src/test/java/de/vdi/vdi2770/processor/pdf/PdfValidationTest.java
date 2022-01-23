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
import java.util.Locale;

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

}
