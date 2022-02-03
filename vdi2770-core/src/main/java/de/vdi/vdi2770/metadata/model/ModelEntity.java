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
package de.vdi.vdi2770.metadata.model;

import java.util.List;
import java.util.Locale;

import com.google.common.base.Preconditions;

/**
 * A simple base class for entities of the information model.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public interface ModelEntity {

	/**
	 * Validate this model instance.
	 *
	 * @param locale Desired {@link Locale} for validation messages.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	public default List<ValidationFault> validate(final Locale locale) {
		Preconditions.checkArgument(locale != null, "locale is null");

		return validate(null, locale, false);
	}
	
	public default List<ValidationFault> validate(final Locale locale, boolean strict) {
		Preconditions.checkArgument(locale != null, "locale is null");

		return validate(null, locale, strict);
	}

	/**
	 * Validate this model instance.
	 *
	 * @param parent The name of a parent element. Can be null.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	public default List<ValidationFault> validate(final String parent) {
		return validate(parent, Locale.getDefault(), false);
	}

	/**
	 * Validate this model instance. Strict validation is disabled.
	 *
	 * @param parent The name of a parent element. Can be <code>null</code>.
	 * @param locale Desired {@link Locale} for validation messages.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	public default List<ValidationFault> validate(final String parent, final Locale locale) {
		return validate(parent, locale, false);
	}

	/**
	 * Validate this model instance.
	 *
	 * @param parent The name of a parent element. Can be <code>null</code>.
	 * @param locale Desired {@link Locale} for validation messages.
	 * @param strict If <code>true</code>, strict validation is enabled.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	public List<ValidationFault> validate(final String parent, final Locale locale, boolean strict);
}
