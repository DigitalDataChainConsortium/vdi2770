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

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.github.dozermapper.core.Mapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.model.Document;
import de.vdi.vdi2770.metadata.model.ValidationFault;

/**
 * This class contains functions to write XML files accoring to VDI 2770
 * guideline specification.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class XmlWriter {

	private final ResourceBundle bundle;
	private final Locale locale;

	/**
	 * ctor
	 *
	 * @param locale Desired {@link Locale} for validation messages.
	 */
	public XmlWriter(final Locale locale) {
		super();

		Preconditions.checkArgument(locale != null);

		this.bundle = ResourceBundle.getBundle("i8n.metadata", locale);
		this.locale = (Locale) locale.clone();
	}

	/**
	 * Convert a {@link Document} according to the information model as XML file.
	 *
	 * @param filePath Path for the XML file; must not be <code>null</code> or empty
	 *                 and must not be a path to a folder.
	 * @param document The {@link Document} instance to serialize.
	 * @throws XmlProcessingException   There was an error writing the XMl file. The
	 *                                  given document might have errors.
	 * @throws IllegalArgumentException The given parameter are not valid.
	 */
	public void write(final String filePath, final Document document)
			throws XmlProcessingException {
		write(filePath, document, false);
	}

	/**
	 * Convert a {@link Document} according to the information model as XML file.
	 *
	 * @param filePath  Path for the XML file; must not be <code>null</code> or
	 *                  empty and must not be a path to a folder.
	 * @param document  The {@link Document} instance to serialize.
	 * @param exportXsd If <code>true</code>, save the XSD file in the same folder
	 *                  like the XML file.
	 * @throws XmlProcessingException   There was an error writing the XMl file. The
	 *                                  given document might have errors.
	 * @throws IllegalArgumentException The given parameter are not valid.
	 */
	public void write(final String filePath, final Document document, final boolean exportXsd)
			throws XmlProcessingException {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(filePath),
				"The given path is null or empty");
		Preconditions.checkArgument(document != null);

		write(new File(filePath), document, exportXsd);
	}

	/**
	 * Convert a {@link Document} according to the information model as XML file.
	 *
	 * @param xmlFile   A {@link File} to write.
	 * @param document  A {@link Document} instance; must be <code>not null</code>
	 *                  and valid.
	 * @param exportXsd Export the XML schema file and set XSD schema Location in
	 *                  XML file.
	 * @throws XmlProcessingException
	 */
	public void write(final File xmlFile, final Document document, final boolean exportXsd)
			throws XmlProcessingException {

		Preconditions.checkArgument(xmlFile != null);
		Preconditions.checkArgument(document != null);

		final List<ValidationFault> documentFaults = document.validate(this.locale);

		if (Fault.hasErrors(documentFaults)) {
			throw new XmlValidationException(this.bundle.getString("XmlWriter_EX1"),
					documentFaults);
		}

		XmlUtils xmlUtils = new XmlUtils(this.locale);

		// get the mapper to map the information model to JAXB POJOs
		final Mapper mapper = xmlUtils.getMapper();

		// convert to JAVB POJOs
		final de.vdi.vdi2770.metadata.xsd.Document xmlDocument = mapper.map(document,
				de.vdi.vdi2770.metadata.xsd.Document.class);

		// write the document
		xmlUtils.saveAsXml(xmlFile, xmlDocument, exportXsd);
	}
}
