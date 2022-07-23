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
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.common.Fault;

/**
 * Tests for corrupted XML files.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
class InvalidXmlTest {

	private static final String EXAMPLES_FOLDER = "../examples/xml";

	/**
	 * Party1 Element instead of Party
	 *
	 * @throws XmlProcessingException
	 */
	@Test
	public void invalidXmlFile1() throws XmlProcessingException {

		final XmlReader reader = new XmlReader(Locale.getDefault());
		List<XmlValidationFault> faults = reader
				.validate(new File(EXAMPLES_FOLDER, "Invalid1.xml"));

		assertTrue(Fault.hasErrors(faults));
	}

	/**
	 * Same as invalidXmlFile1 but check, whether validation messages return in the
	 * desired language.
	 * 
	 * @throws XmlProcessingException
	 */
	@Test
	public void invalidXmlFileAndDifferentLocale() throws XmlProcessingException {

		// German language
		XmlReader reader = new XmlReader(Locale.GERMAN);
		List<XmlValidationFault> faults = reader
				.validate(new File(EXAMPLES_FOLDER, "Invalid1.xml"));

		assertTrue(Fault.hasErrors(faults));

		XmlValidationFault fault = faults.get(0);
		assertTrue(
				fault.getMessage().startsWith("cvc-complex-type.2.4.a: Ungültiger Content wurde"));

		// English
		reader = new XmlReader(Locale.ENGLISH);
		faults = reader.validate(new File(EXAMPLES_FOLDER, "Invalid1.xml"));

		assertTrue(Fault.hasErrors(faults));

		fault = faults.get(0);
		assertTrue(fault.getMessage().startsWith("cvc-complex-type.2.4.a: Invalid content was"));

		// Chinese
		reader = new XmlReader(Locale.CHINA);
		faults = reader.validate(new File(EXAMPLES_FOLDER, "Invalid1.xml"));

		assertTrue(Fault.hasErrors(faults));

		fault = faults.get(0);
		assertTrue(fault.getMessage().startsWith("cvc-complex-type.2.4.a: 发现了以元素"));

		// Chinese
		Locale locale = new Locale("zh", "CN");
		reader = new XmlReader(locale);
		faults = reader.validate(new File(EXAMPLES_FOLDER, "Invalid1.xml"));

		assertTrue(Fault.hasErrors(faults));

		fault = faults.get(0);
		assertTrue(fault.getMessage().startsWith("cvc-complex-type.2.4.a: 发现了以元素"));

		// German
		locale = new Locale("de", "DE");
		reader = new XmlReader(locale);
		faults = reader.validate(new File(EXAMPLES_FOLDER, "Invalid1.xml"));

		assertTrue(Fault.hasErrors(faults));

		fault = faults.get(0);
		assertTrue(
				fault.getMessage().startsWith("cvc-complex-type.2.4.a: Ungültiger Content wurde"));

	}

	/**
	 * Invalid XSD date
	 *
	 * @throws XmlProcessingException
	 */
	@Test
	public void invalidXmlFile2() throws XmlProcessingException {

		final XmlReader reader = new XmlReader(Locale.getDefault());
		List<XmlValidationFault> faults = reader
				.validate(new File(EXAMPLES_FOLDER, "Invalid2.xml"));

		assertTrue(Fault.hasErrors(faults));
	}

}
