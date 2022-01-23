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

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import lombok.extern.log4j.Log4j2;

/**
 * JAXB provides XML schema validation. By default, the first validation fault
 * will stop validation. This class implements a custom validation event handler
 * that collects every validation fault. Use {@link #getFaults()} to get a
 * complete {@link List} of faults. Instances of {@link ValidationEvent} are
 * converted to {@link XmlValidationFault} instances.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class VdiValidationEventHandler implements ValidationEventHandler {

	private final List<XmlValidationFault> warnings;

	public List<XmlValidationFault> getWarnings() {
		return new ArrayList<>(this.warnings);
	}

	private final List<XmlValidationFault> errors;

	public List<XmlValidationFault> getErrors() {
		return new ArrayList<>(this.errors);
	}

	/**
	 * ctor
	 */
	public VdiValidationEventHandler() {
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

	@Override
	public boolean handleEvent(final ValidationEvent ve) {

		final ValidationEventLocator locator = ve.getLocator();
		int line = locator.getLineNumber();
		int col = locator.getColumnNumber();

		if (log.isDebugEnabled()) {
			log.debug(ve.getSeverity() + ": " + ve.getMessage() + " " + line + ", " + col);
		}

		if (ve.getSeverity() == ValidationEvent.FATAL_ERROR
				|| ve.getSeverity() == ValidationEvent.ERROR) {
			XmlValidationFault fault = new XmlValidationFault(FaultLevel.ERROR, line, col);
			fault.setMessage(ve.getMessage());
			this.errors.add(fault);
		}

		if (ve.getSeverity() == ValidationEvent.WARNING) {
			XmlValidationFault warn = new XmlValidationFault(FaultLevel.WARNING, line, col);
			warn.setMessage(ve.getMessage());
			this.warnings.add(warn);
		}

		return true;
	}

}
