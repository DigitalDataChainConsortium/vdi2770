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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import de.vdi.vdi2770.metadata.common.Constants;

/**
 * This is a specialized {@link XMLFilterImpl} that applies the VDI 2770
 * namespace to XML elements.
 * <p>
 * If the XML namespace is not defined, XML validation will fail. So, this
 * filter implementation is going to append the VDI 2770 namespace to every
 * element in a XML file.
 * </p>
 *
 * @see Constants#VDI_XML_NS
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class VdiNamespaceFilter extends XMLFilterImpl {

	// namespace already added?
	private boolean addedNamespace = false;

	/**
	 * ctor
	 */
	public VdiNamespaceFilter() {
		super();
	}

	/**
	 * Filter a start document event.
	 *
	 * @throws org.xml.sax.SAXException The client may throw an exception during
	 *                                  processing.
	 */
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		startControlledPrefixMapping();
	}

	/**
	 * Filter a start element event.
	 *
	 * @param uri       The element's Namespace URI, or the empty string.
	 * @param localName The element's local name, or the empty string.
	 * @param qName     The element's qualified (prefixed) name, or the empty
	 *                  string.
	 * @param attrs     The element's attributes.
	 * @throws SAXException The client may throw an exception during processing.
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrs)
			throws SAXException {

		super.startElement(Constants.VDI_XML_NS, localName, qName, attrs);
	}

	/**
	 * Filter an end element event.
	 *
	 * @param uri       The element's Namespace URI, or the empty string.
	 * @param localName The element's local name, or the empty string.
	 * @param qName     The element's qualified (prefixed) name, or the empty
	 *                  string.
	 * @throws org.xml.sax.SAXException The client may throw an exception during
	 *                                  processing.
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		super.endElement(Constants.VDI_XML_NS, localName, qName);
	}

	/**
	 * Filter a start Namespace prefix mapping event.
	 *
	 * @param prefix The Namespace prefix.
	 * @param uri    The Namespace URI.
	 * @throws org.xml.sax.SAXException The client may throw an exception during
	 *                                  processing.
	 */
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {

		this.startControlledPrefixMapping();
	}

	private void startControlledPrefixMapping() throws SAXException {

		if (!this.addedNamespace) {
			// We should add namespace since it is set and has not yet been
			// done.
			super.startPrefixMapping("", Constants.VDI_XML_NS);

			// Make sure we do not do it twice
			this.addedNamespace = true;
		}
	}

}
