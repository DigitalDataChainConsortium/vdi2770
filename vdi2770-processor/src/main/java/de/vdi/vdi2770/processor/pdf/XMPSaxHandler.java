/*******************************************************************************
 * Copyright (C) 2022 Johannes Schmidt
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

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX handler to read PDF id and conformance for PDF/A documents.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
public class XMPSaxHandler extends DefaultHandler {

	private String pdfaidElementValue;
	private String conformanceElementValue;

	private boolean pdfaidElementActive = false;
	private boolean conformanceElementActive = false;

	/**
	 * Return parsed PDF/A level, otherwise an empty {@link String}.
	 * 
	 * @return The PDF/A conformance level
	 */
	public String getPdfALevel() {
		return this.pdfaidElementValue + this.conformanceElementValue;
	}

	/**
	 * XML start element implementation, used to read pdfaid:part and
	 * pdfaid:conformane
	 */
	@Override
	public void startElement(final String uri, final String localName, final String qName,
			final Attributes attributes) throws SAXException {

		if (StringUtils.equals(uri, "http://www.aiim.org/pdfa/ns/id/")
				&& StringUtils.equals(localName, "part")) {
			this.pdfaidElementActive = true;
		}

		if (StringUtils.equals(uri, "http://www.aiim.org/pdfa/ns/id/")
				&& StringUtils.equals(localName, "conformance")) {
			this.conformanceElementActive = true;
		}

		// read part
		if (StringUtils.equals(qName, "pdfaid:part")) {
			this.pdfaidElementActive = true;
		}

		// read conformance
		if (StringUtils.equals(qName, "pdfaid:conformance")) {
			this.conformanceElementActive = true;
		}
	}

	/**
	 * XML end element implementation, used to read pdfaid:part and
	 * pdfaid:conformane
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (StringUtils.equals(uri, "http://www.aiim.org/pdfa/ns/id/")
				&& StringUtils.equals(localName, "part")) {
			this.pdfaidElementActive = false;
		}

		if (StringUtils.equals(uri, "http://www.aiim.org/pdfa/ns/id/")
				&& StringUtils.equals(localName, "conformance")) {
			this.conformanceElementActive = false;
		}

		if (StringUtils.equals(qName, "pdfaid:part")) {
			this.pdfaidElementActive = false;
		}

		if (StringUtils.equals(qName, "pdfaid:conformance")) {
			this.conformanceElementActive = false;
		}
	}

	/**
	 * Read element value
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if (this.conformanceElementActive) {
			this.conformanceElementValue = new String(ch, start, length);
		}

		if (this.pdfaidElementActive) {
			this.pdfaidElementValue = new String(ch, start, length);
		}
	}
}
