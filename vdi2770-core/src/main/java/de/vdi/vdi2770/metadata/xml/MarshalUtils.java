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

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import de.vdi.vdi2770.metadata.xsd.Document;

import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

/**
 * This class provides utility functions for (un)marshalling XML files.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
class MarshalUtils {

	private final ResourceBundle bundle;

	/**
	 * The VDI 2770 XML Schema is contained as project resource.
	 */
	private static final String VDI2770_SCHEMA = "vdi2770.xsd";

	/**
	 * XML schema used to create XML validator instances.
	 */
	private final Schema vdi2770Schema;

	/**
	 * This is a placeholder for a configuration variable.
	 */
	private boolean enableSchemaLocation = false;

	private final Locale locale;

	/**
	 * Generate XML file including schema location for VDI 2770 XML schema file.
	 *
	 * @param enable
	 */
	public void setEnableSchemaLocation(final boolean enable) {
		this.enableSchemaLocation = enable;
	}

	private MarshalUtils() throws XmlProcessingException {
		this(Locale.getDefault());
	}

	/**
	 * ctor.
	 *
	 * @param locale Desired {@link Locale} for validation messages.
	 * @throws XmlProcessingException Could not initialized XML schema.
	 */
	public MarshalUtils(final Locale locale) throws XmlProcessingException {

		Preconditions.checkArgument(locale != null, "locale is null");

		this.bundle = ResourceBundle.getBundle("i8n.metadata", locale);
		this.locale = (Locale) locale.clone();

		// load XML schema
		final URL schemaUrl = XmlUtils.class.getClassLoader().getResource(VDI2770_SCHEMA);
		if (schemaUrl == null) {
			throw new XmlProcessingException(this.bundle.getString("MarshalUtils_EX1"));
		}

		final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			this.vdi2770Schema = factory.newSchema(schemaUrl);
		} catch (final SAXException e) {
			throw new XmlProcessingException(this.bundle.getString("MarshalUtils_EX2"), e);
		}
	}

	/**
	 * Create marshaller instance
	 *
	 * @param enableSchemaLocation Enable / disable XML schema file location in the
	 *                             generated XML file
	 * @throws XmlProcessingException Could not initialized XML schema.
	 */
	public MarshalUtils(final boolean enableSchemaLocation) throws XmlProcessingException {
		this();

		this.setEnableSchemaLocation(enableSchemaLocation);
	}

	/**
	 * Get a XML marshaller instance to read XML documents.
	 *
	 * @return a marshaller
	 * @throws XmlProcessingException There was an error configuring the marshaller
	 */
	public Marshaller getMarshaller() throws XmlProcessingException {

		try {

			// get marshaller
			final JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setSchema(this.vdi2770Schema);

			// configure XML header as not "standalone"
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			jaxbMarshaller.setProperty("com.sun.xml.bind.xmlHeaders",
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

			// use UTF-8 encoding
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			// format the XML file
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// add XML schema location in XML file
			if (this.enableSchemaLocation) {
				jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "vdi2770.xsd");
			}

			return jaxbMarshaller;
		} catch (final JAXBException e) {
			throw new XmlProcessingException(this.bundle.getString("MarshalUtils_EX3"), e);
		}
	}

	/**
	 * Get a JAXB {@link Validator} instance to validate a VDI 2770 XML file
	 * <p>
	 * Validation message locale is defined by the locale of this class.
	 * </p>
	 *
	 * @param errorHandler A VDI 2770 validation error handler that collects
	 *                     warnings and error messages during XML validation.
	 * @return The validator instance.
	 * @throws SAXException
	 */
	public Validator getValidator(final VdiValidationErrorHandler errorHandler)
			throws SAXException {

		// create a JAXB validator instance
		Validator validator = this.vdi2770Schema.newValidator();

		// log warnings and errors by a custom handler
		// this handler does not throw exceptions but collect all messages
		validator.setErrorHandler(errorHandler);

		Locale errorLocale = this.locale;
		if (!Locale.getDefault().equals(Locale.ENGLISH) && errorLocale.equals(Locale.ENGLISH)) {
			errorLocale = Locale.ROOT;
		}

		validator.setProperty("http://apache.org/xml/properties/locale", errorLocale);

		return validator;
	}

	/**
	 * Get an unmarshaller instance to read XML files.
	 *
	 * @return An unmarshaller
	 * @throws XmlProcessingException There was an error configuring the
	 *                                unmarshaller
	 */
	public Unmarshaller getUnmarshaller() throws XmlProcessingException {

		try {
			// init unmarshaller
			final JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);

			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setSchema(this.vdi2770Schema);

			return jaxbUnmarshaller;

		} catch (final JAXBException e) {
			throw new XmlProcessingException(this.bundle.getString("MarshalUtils_EX4"), e);
		}
	}
}
