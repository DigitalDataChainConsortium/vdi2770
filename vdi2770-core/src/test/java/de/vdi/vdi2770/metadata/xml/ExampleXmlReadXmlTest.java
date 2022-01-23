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
package de.vdi.vdi2770.metadata.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.model.Document;
import de.vdi.vdi2770.metadata.model.ValidationFault;

/**
 * Reading and validating XML files from the example folder.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class ExampleXmlReadXmlTest {

	private static final String EXAMPLES_FOLDER = "../examples/xml";

	/**
	 * Read all example XML files.
	 *
	 * @throws XmlProcessingException
	 */
	@Test
	public void readXmlExamples() throws XmlProcessingException {

		final File dir = new File(EXAMPLES_FOLDER);
		final File[] xmlFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.toLowerCase().endsWith(".xml")
						&& !name.toLowerCase().contains("invalid");
			}
		});

		if (xmlFiles != null && xmlFiles.length > 0) {
			for (final File xmlFile : xmlFiles) {
				final XmlReader reader = new XmlReader(Locale.getDefault());
				final Document xmlDocument = reader.read(xmlFile);
				final List<ValidationFault> errors = xmlDocument.validate(Locale.getDefault());
				assertTrue(errors.size() == 0 || errors.stream()
						.filter(f -> f.getLevel() == FaultLevel.ERROR).count() == 0);
			}
		}
	}
}
