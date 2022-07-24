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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.vdi.vdi2770.metadata.common.FaultLevel;

import javax.xml.validation.Validator;

/**
 * This error handler collects warnings and errors while XML validation.
 * Validation messages are converted to {@link XmlValidationFault}s.
 *
 * @see Validator
 * @see XmlValidationFault
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class VdiValidationErrorHandler implements ErrorHandler {

	/**
	 * List of warnings
	 */
	private final List<XmlValidationFault> warnings;

	public List<XmlValidationFault> getWarnings() {
		return new ArrayList<>(this.warnings);
	}

	/**
	 * List of errors
	 */
	private final List<XmlValidationFault> errors;

	public List<XmlValidationFault> getErrors() {
		return new ArrayList<>(this.errors);
	}

	/**
	 * ctor
	 */
	public VdiValidationErrorHandler() {
		this.warnings = new ArrayList<>();
		this.errors = new ArrayList<>();
	}

	/**
	 * Validation errors found
	 *
	 * @return <code>True</code>, if validation errors has been found.
	 */
	public boolean hasErrors() {
		return this.errors.size() > 0;
	}

	/**
	 * Validation warnings found
	 *
	 * @return <code>True</code>, if validation warnings or errors has been found.
	 */
	public boolean hasWarnings() {
		return this.warnings.size() > 0;
	}

	/**
	 * Get a {@link List} of {@link XmlValidationFault}s that may represent errors
	 * or warnings.
	 *
	 * @return {@link List} of {@link XmlValidationFault}s
	 */
	public List<XmlValidationFault> getFaults() {
		return Stream.concat(this.errors.stream(), this.warnings.stream())
				.collect(Collectors.toList());
	}

	/**
	 * Receive notification of a warning and save them in the {@link List} of
	 * warnings.
	 */
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		XmlValidationFault fault = new XmlValidationFault(FaultLevel.WARNING,
				exception.getLineNumber(), exception.getColumnNumber());
		fault.setMessage(exception.getLocalizedMessage());
		this.warnings.add(fault);
	}

	/**
	 * Receive notification of an error and save them in the {@link List} of errors.
	 */
	@Override
	public void error(SAXParseException exception) throws SAXException {
		XmlValidationFault fault = new XmlValidationFault(FaultLevel.ERROR,
				exception.getLineNumber(), exception.getColumnNumber());
		fault.setMessage(exception.getLocalizedMessage());
		this.errors.add(fault);
	}

	/**
	 * Receive notification of a fatal error and save them in the {@link List} of
	 * errors.
	 */
	@Override
	public void fatalError(SAXParseException exception) throws SAXException {

		XmlValidationFault fault = new XmlValidationFault(FaultLevel.ERROR,
				exception.getLineNumber(), exception.getColumnNumber());
		fault.setMessage(exception.getLocalizedMessage());
		this.errors.add(fault);

	}

}
