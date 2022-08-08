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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXParseException;

import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.common.FaultLevel;

import com.google.common.base.Preconditions;

import lombok.extern.log4j.Log4j2;

/**
 * This class contains function to read and validate XML file according to the
 * specifications of VDI 2770 guideline.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class XmlReader {

	private final ResourceBundle bundle;
	private Locale locale;

	/**
	 * ctor
	 *
	 * @param locale Desired {@link Locale} for validation messages.
	 */
	public XmlReader(final Locale locale) {

		this.bundle = ResourceBundle.getBundle("i8n.metadata", locale);
		this.locale = (Locale) locale.clone();
	}

	/**
	 * Check, whether a file is a metadata file for a main document.
	 *
	 * @param file A file to be checked; must not be <code>null</code>
	 * @return <code>true</code>, if the given file is a XML meta data file for a
	 *         main document.
	 * @throws IllegalArgumentException The given file is not a file.
	 */
	public boolean isMetadataFileForMainDocument(final File file) {

		Preconditions.checkArgument(file != null, "The given file is null");
		Preconditions.checkArgument(file.isFile(), "The given file is not a file");

		// file must be named according to the VDI 2770 specification
		if (!StringUtils.equals(file.getName(), FileNames.MAIN_DOCUMENT_XML_FILE_NAME)) {

			return false;
		}

		return true;
	}

	/**
	 * Check, whether a file is a metadata file for a document.
	 *
	 * @param file A file to be checked; must not be <code>null</code>
	 * @return <code>true</code>, if the given file is a XML meta data file for a
	 *         document.
	 * @throws IllegalArgumentException The given file is not a file.
	 */
	public boolean isMetadataFileForDocument(final File file) {

		Preconditions.checkArgument(file != null, "The given file is null");
		Preconditions.checkArgument(file.isFile(), "The given file is not a file");

		// file must be named according to the VDI 2770 specification
		if (!StringUtils.equals(file.getName(), FileNames.METADATA_XML_FILE_NAME)) {
			return false;
		}

		return true;
	}

	/**
	 * Check, whether a file is a metadata file.
	 *
	 * @param file A file to be checked; must not be <code>null</code>
	 * @return <code>true</code>, if the given file is a XML meta data file.
	 * @throws IllegalArgumentException The given file is not a file.
	 */
	public boolean isMetadataFile(final File file) {

		Preconditions.checkArgument(file != null, "The given file is null");
		Preconditions.checkArgument(file.isFile(), "The given file is not a file");

		try {

			// file exists
			if (!file.exists()) {
				return false;
			}

			// is an VDI 2770 metadata file
			List<XmlValidationFault> faults = validate(file);

			// if no warnings (including no errors) return true
			return !Fault.hasErrors(faults);

		} catch (final Exception e) {
			log.warn(this.bundle.getString("XmlReader_EX1"), e);
		}

		return false;
	}

	/**
	 * Validate a given XML file that contains meta data according to VDI 2770.
	 *
	 * @param xmlFile A XML file to be checked; must not be <code>null</code>
	 * @return List of {@link XmlValidationFault}s
	 * @throws XmlProcessingException   There was an error reading the XML file.
	 * @throws IllegalArgumentException The given file is not a file.
	 */
	public List<XmlValidationFault> validate(final File xmlFile) throws XmlProcessingException {

		try (FileInputStream fis = new FileInputStream(xmlFile)) {

			// Note: The following code is based on
			// https://stackoverflow.com/questions/277502/jaxb-how-to-ignore-namespace-during-unmarshalling-xml-document
			// https://stackoverflow.com/questions/31323475/jaxb-exception-messages-how-to-change-language

			// XML reader instance
			XmlUtils xmlUtils = new XmlUtils(this.locale);
			XMLReader reader = xmlUtils.getXmlReader();

			// instantiate the VDI filter
			// this filter will append the VDI 2770 namespace to every entity
			VdiNamespaceFilter inFilter = new VdiNamespaceFilter();
			inFilter.setParent(reader);

			// convert to Input Source
			InputSource is = new InputSource(fis);

			// Create a SAXSource applying the filter
			SAXSource source = new SAXSource(inFilter, is);

			final MarshalUtils utils = new MarshalUtils(this.locale);

			// log warnings and errors by a custom handler
			// this handler does not throw exceptions but collect all messages
			VdiValidationErrorHandler errorHandler = new VdiValidationErrorHandler();

			Validator validator = utils.getValidator(errorHandler);

			// validate the XML file
			validator.validate(source);

			// return warning and messages from the custom handler
			return errorHandler.getFaults();

		} catch (final IOException e) {
			throw new XmlProcessingException(
					MessageFormat.format(this.bundle.getString("XmlReader_EX3"), xmlFile), e);
		} catch (final SAXParseException e) {
			XmlValidationFault fault = new XmlValidationFault(FaultLevel.ERROR, e.getLineNumber(),
					e.getColumnNumber());
			fault.setMessage(e.getLocalizedMessage());
			return Arrays.asList(fault);
		} catch (final Exception e) {
			throw new XmlProcessingException(
					MessageFormat.format(this.bundle.getString("XmlReader_EX4"), xmlFile), e);
		}
	}

	/**
	 * Try to read an XML file. In case of an Exception, return <code>null</code>.
	 *
	 * @see XmlReader#read(File)
	 * @param file A file representing XML metadata; must not be <code>null</code>.
	 * @return a {@link de.vdi.vdi2770.metadata.model.Document} or
	 *         <code>null</code>.
	 */
	public de.vdi.vdi2770.metadata.model.Document tryRead(final File file) {

		Preconditions.checkArgument(file != null, "The given file is null");
		Preconditions.checkArgument(file.isFile(), "The given file is not a file");

		try {
			return read(file);
		} catch (XmlProcessingException e) {
			log.warn("Can not read XML file " + file.getAbsolutePath(), e.getMessage());
			return null;
		}
	}

	/**
	 * Read a XML metadata file and return the
	 * {@link de.vdi.vdi2770.metadata.model.Document} POJO. While reading, the XML
	 * is validated according to the specification of the VDI 2770 DTD. There is no
	 * logical validation.
	 *
	 * @param xmlFile A file representing XML metadata; must not be
	 *                <code>null</code>.
	 * @return A new instance of {@link de.vdi.vdi2770.metadata.model.Document}
	 *         which is the POJO representation of the XML file.
	 * @throws XmlProcessingException   There was an error reading the XML stream.
	 * @throws IllegalArgumentException The given file is not a file.
	 */
	public de.vdi.vdi2770.metadata.model.Document read(final File xmlFile)
			throws XmlProcessingException {

		Preconditions.checkArgument(xmlFile != null, "xmlFile is null");
		Preconditions.checkArgument(xmlFile.isFile(), "xmlFile is not a file");

		if (log.isDebugEnabled()) {
			log.debug("Reading XML file " + xmlFile.getAbsolutePath());
		}

		try (FileInputStream tmpStream = new FileInputStream(xmlFile)) {
			final XmlUtils xmlUtils = new XmlUtils(this.locale);
			return xmlUtils.readXml(tmpStream);
		} catch (final FileNotFoundException e) {
			throw new XmlProcessingException(
					MessageFormat.format(this.bundle.getString("XmlReader_EX5"), xmlFile), e);
		} catch (final IOException e) {
			throw new XmlProcessingException(
					MessageFormat.format(this.bundle.getString("XmlReader_EX6"), xmlFile), e);
		} catch (final XmlProcessingException e) {
			throw e;
		} catch (final Exception e) {
			throw new XmlProcessingException(
					MessageFormat.format(this.bundle.getString("XmlReader_EX7"), xmlFile), e);
		}
	}
}
