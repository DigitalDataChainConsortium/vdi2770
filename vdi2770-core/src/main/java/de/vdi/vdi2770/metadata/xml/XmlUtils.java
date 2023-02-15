/*******************************************************************************
 * Copyright (C) 2021-2023 Johannes Schmidt
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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.io.FileUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import de.vdi.vdi2770.metadata.xsd.Document;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * This class wraps utility function to read and write XML files.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class XmlUtils {

	private final ResourceBundle bundle;

	private final Locale locale;

	/**
	 * ctor
	 *
	 * @param locale Desired {@link Locale} for validation messages; must not be
	 *               <code>null</code>.
	 */
	public XmlUtils(final Locale locale) {

		Preconditions.checkArgument(locale != null);

		this.bundle = ResourceBundle.getBundle("i8n.metadata", locale);
		this.locale = (Locale) locale.clone();
	}

	/**
	 * Save a {@link Document}, that represents the metadata of a document, as XML
	 * file.
	 *
	 * @param filePath  Path for the XML file; must not be <code>null</code> or
	 *                  empty and must not be a path to a folder.
	 * @param document  The {@link Document} instance to serialize.
	 * @param exportXsd Export the XSD file next to the XML file.
	 * @throws XmlProcessingException   There was an error while saving the XML
	 *                                  file.
	 * @throws IllegalArgumentException The given file is a folder.
	 */
	public void saveAsXml(final String filePath, final Document document, final boolean exportXsd)
			throws XmlProcessingException {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(filePath),
				"The given path is null or empty");
		Preconditions.checkArgument(document != null, "Can not save a docment that is null");

		final File file = new File(filePath);

		saveAsXml(file, document, exportXsd);
	}

	/**
	 * Save a JAXB POJO as XML file.
	 *
	 * @param file      A {@link File} handle to write as XML file.
	 * @param document  The {@link Document} instance to serialize.
	 * @param exportXsd Export the XSD file next to the XML file.
	 * @throws XmlProcessingException   There was an error writing the XML file.
	 * @throws IllegalArgumentException The given parameter are not valid.
	 */
	public void saveAsXml(final File file, final Document document, final boolean exportXsd)
			throws XmlProcessingException {

		Preconditions.checkArgument(file != null, "The given file is null");
		Preconditions.checkArgument(document != null, "Can not save a docment that is null");

		if (file.isDirectory()) {
			throw new XmlProcessingException(this.bundle.getString("XmlUtils_EX2"));
		}

		if (exportXsd) {
			final String basePath = file.getAbsoluteFile().getParent();

			final URL inputUrl = XmlUtils.class.getResource("/vdi2770.xsd");
			final File xsdFile = new File(basePath, "vdi2770.xsd");

			try {
				FileUtils.copyURLToFile(inputUrl, xsdFile);
			} catch (final IOException e) {
				throw new XmlProcessingException(this.bundle.getString("XmlUtils_EX6"), e);
			}
		}

		try {
			final Marshaller jaxbMarshaller = getMarshaller();

			if (exportXsd) {
				jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
						"http://www.vdi.de/schemas/vdi2770 vdi2770.xsd");
			}

			jaxbMarshaller.marshal(document, file);

		} catch (final JAXBException e) {
			throw new XmlProcessingException(this.bundle.getString("XmlUtils_EX3"), e);
		}
	}

	/**
	 * Get a XML marshaller instance.
	 *
	 * @return A marshaller
	 */
	private Marshaller getMarshaller() throws XmlProcessingException {

		final MarshalUtils utils = new MarshalUtils(this.locale);

		return utils.getMarshaller();
	}
	
	/**
	 * Get a XML reader instance
	 * 
	 * @return A configured {@link XMLReader} instance
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public XMLReader getXmlReader() throws ParserConfigurationException, SAXException {
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        SAXParser parser = parserFactory.newSAXParser();
        return parser.getXMLReader();
	}

	/**
	 * Get an instance of Dozer Mapper.
	 *
	 * @return A {@link Mapper} to convert between JAXB POJOs ans the entities of
	 *         the information model.
	 * @throws XmlProcessingException The mapping could not be read.
	 */
	public Mapper getMapper() throws XmlProcessingException {

		// mapping definitions between information model and xml document
		// structure is
		// defined as
		// XML file.
		final URL mappingFileUrl = XmlUtils.class.getClassLoader().getResource("mappings.xml");
		if (mappingFileUrl == null) {
			throw new XmlProcessingException(this.bundle.getString("XmlUtils_EX1"));
		}

		return DozerBeanMapperBuilder.create().withMappingFiles(mappingFileUrl.toString()).build();
	}

	/**
	 * Serialize a {@link Document} as XML {@link String}.
	 *
	 * @param doc A {@link Document} instance that will be converted as XML
	 *            {@link String}.
	 * @return The {@link Document} represented as a {@link String}.
	 * @throws XmlProcessingException   There was an error while XML conversion.
	 * @throws IllegalArgumentException The given parameter is invalid.
	 */
	public String asXml(final Document doc) throws XmlProcessingException {

		Preconditions.checkArgument(doc != null, "doc is null");

		try {
			final Marshaller jaxbMarshaller = getMarshaller();

			final StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(doc, writer);

			return writer.toString();
		} catch (final JAXBException e) {
			throw new XmlProcessingException(this.bundle.getString("XmlUtils_EX4"), e);
		}
	}

	/**
	 * Read a XML metadata stream and return the {@link Document} POJO. While
	 * reading, the XML is validated according to the specification of the VDI 2770
	 * DTD. There is no logical validation.
	 *
	 * @param stream An input stream representing XML metadata; must not be
	 *               <code>null</code>.
	 * @return A new instance of {@link Document} which is the POJO representation
	 *         of the XML file.
	 * @throws XmlProcessingException There was an error reading the XML stream.
	 */
	private Document readXmlRaw(final InputStream stream) throws XmlProcessingException {

		Preconditions.checkArgument(stream != null);

		try {

			// XML reader instance
            XMLReader reader = getXmlReader();

			// instantiate the VDI filter
			// this filter will append the VDI 2770 namespace to every entity
			VdiNamespaceFilter inFilter = new VdiNamespaceFilter();
			inFilter.setParent(reader);

			// convert to Input Source
			InputSource is = new InputSource(stream);

			// Create a SAXSource applying the filter
			SAXSource source = new SAXSource(inFilter, is);

			// get unmarshaller
			final MarshalUtils utils = new MarshalUtils(this.locale);
			final Unmarshaller unmarshaller = utils.getUnmarshaller();

			VdiValidationEventHandler handler = new VdiValidationEventHandler();
			unmarshaller.setEventHandler(handler);

			JAXBElement<Document> jaxbResult = unmarshaller.unmarshal(source, Document.class);
			final Document doc = jaxbResult.getValue();

			if (handler.hasErrors()) {
				throw new XmlValidationException(this.bundle.getString("XmlUtils_EX7"),
						handler.getFaults());
			}

			return doc;

		} catch (XmlValidationException e) {
			throw e;
		} catch (final Exception e) {
			throw new XmlProcessingException(this.bundle.getString("XmlUtils_EX5"), e);
		}
	}

	/**
	 * Read a XML document and convert the information according to the information
	 * model.
	 *
	 * @param stream A file input stream of an XML file.
	 * @return A {@link de.vdi.vdi2770.metadata.model.Document} instance built from
	 *         the XML stream.
	 * @throws XmlValidationException The XML document is not not valid
	 * @throws XmlProcessingException There was an error reading the XML file /
	 *                                stream.
	 */
	public de.vdi.vdi2770.metadata.model.Document readXml(final InputStream stream)
			throws XmlValidationException, XmlProcessingException {

		// unmarshal the stream as JAXB Document class instance
		final Document document = readXmlRaw(stream);

		// map to POJO representation using dozermapper
		final Mapper mapper = getMapper();
		return mapper.map(document, de.vdi.vdi2770.metadata.model.Document.class);
	}

}
