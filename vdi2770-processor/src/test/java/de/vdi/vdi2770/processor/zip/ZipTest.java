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
package de.vdi.vdi2770.processor.zip;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Locale;

import de.vdi.vdi2770.metadata.xml.XmlReader;
import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.processor.ProcessorException;

/**
 * Tests for the {@link ZipUtils} class.
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
public class ZipTest {

	private static final String EXAMPLES_FOLDER = "../examples/container";

	/**
	 * Check, if ZIP file is a documentation container
	 *
	 * @throws ProcessorException
	 */
	@Test
	public void isDocumentationContainerTest() throws ProcessorException {

		ZipUtils zip = new ZipUtils(Locale.getDefault());

		final boolean result = zip.isDocumentationContainer(
				new File(EXAMPLES_FOLDER, "documentationcontainer.zip"), true);

		assertTrue(result);
	}

	/**
	 * Check, if ZIP file is a document container
	 *
	 * @throws ProcessorException
	 */
	@Test
	public void isDocumentContainerTest() throws ProcessorException {

		ZipUtils zip = new ZipUtils(Locale.getDefault());

		final boolean result = zip
				.isDocumentContainer(new File(EXAMPLES_FOLDER, "documentcontainer.zip"));

		assertTrue(result);
	}

	/**
	 * Extract a meta data file from a ZIP file
	 *
	 * @throws ProcessorException
	 */
	@Test
	public void readMetadataForDocumentTest() throws ProcessorException {

		ZipUtils zip = new ZipUtils(Locale.getDefault());

		final File metadataFile = zip.getMetadataFileFromDocumentContainer(
				new File(EXAMPLES_FOLDER, "documentcontainer.zip"));
		assertNotNull(metadataFile);

		final XmlReader reader = new XmlReader(Locale.getDefault());

		assertTrue(reader.isMetadataFile(metadataFile));
	}

	/**
	 * Extract a main document meta data from a documentation container.
	 *
	 * @throws ProcessorException
	 */
	@Test
	public void readMetadataForDocumentationTest() throws ProcessorException {

		ZipUtils zip = new ZipUtils(Locale.getDefault());

		final File metadataFile = zip.getMetadataFileFromDocumentationContainer(
				new File(EXAMPLES_FOLDER, "documentationcontainer.zip"));
		assertNotNull(metadataFile);

		final XmlReader reader = new XmlReader(Locale.getDefault());

		assertTrue(reader.isMetadataFile(metadataFile));
	}
}
