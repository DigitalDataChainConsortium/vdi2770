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

import java.util.ArrayList;
import java.util.List;

import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.model.ValidationFault;

/**
 * This {@link Exception} indicates a validation error.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class XmlValidationException extends XmlProcessingException {

	private static final long serialVersionUID = 1L;

	private final List<? extends Fault> faults;

	/**
	 * Create an exception with a message
	 *
	 * @param message A message
	 */
	public XmlValidationException(final String message) {
		super(message);

		this.faults = new ArrayList<>();
	}

	/**
	 * Create an exception with a message including the origin exception
	 *
	 * @param message A message
	 * @param cause   An origin exception
	 */
	public XmlValidationException(final String message, final Throwable cause) {
		super(message, cause);

		this.faults = new ArrayList<>();
	}

	/**
	 * Create an exception with a message
	 *
	 * @param message A message
	 * @param faults  A {@link List} of {@link ValidationFault}s
	 */
	public XmlValidationException(final String message, final List<? extends Fault> faults) {

		super(message);

		if (faults == null || faults.isEmpty()) {
			this.faults = new ArrayList<>();
		} else {
			this.faults = new ArrayList<>(faults);
		}
	}

	/**
	 * Get a {@link List} of {@link ValidationFault} associated with this exception.
	 *
	 * @return {@link List} of {@link ValidationFault}
	 */
	public List<? extends Fault> getFaults() {
		return new ArrayList<>(this.faults);
	}

}
