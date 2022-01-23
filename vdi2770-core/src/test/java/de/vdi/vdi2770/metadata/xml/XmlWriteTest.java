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
import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.MetadataException;
import de.vdi.vdi2770.metadata.model.DemoModel;

/**
 * Write Demo XML files test.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class XmlWriteTest {

	/**
	 * Write XML test using special {@link DemoXml} class.
	 *
	 * @throws MetadataException Metadata may be invalid.
	 */
	@Test
	public void writeXml() throws MetadataException {

		final DemoXml demo = new DemoXml(Locale.getDefault());

		try {
			File result = new File("demo1.xml");
			demo.createXmlFile(result);
			result.deleteOnExit();

			assertTrue(result != null);
			assertTrue(result.exists());
		} catch (final MetadataException e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * Write XML test using an information model instance defined in
	 * {@link DemoModel}.
	 *
	 * @throws MetadataException Metadata may be invalid.
	 */
	@Test
	public void writeXmlFromModel() throws MetadataException {

		final DemoModel demo = new DemoModel();

		try {
			File result = new File("demo2.xml");
			demo.createXmlFile("demo2.xml");
			result.deleteOnExit();

			assertTrue(result != null);
			assertTrue(result.exists());
		} catch (final MetadataException e) {
			e.printStackTrace();
			throw e;
		}
	}

}
