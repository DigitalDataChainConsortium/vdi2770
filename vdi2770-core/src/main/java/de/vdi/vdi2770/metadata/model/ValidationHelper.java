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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;

/**
 * Utility functions for validation.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class ValidationHelper {

	/**
	 * Validate a {@link List} of {@link ModelEntity}s.
	 *
	 * @param list         A {@link List} of information model entities.
	 * @param parent       The name of the parent entity.
	 * @param propertyName The name of the property the parent entity.
	 * @param locale       Desired {@link Locale} for validation messages.
	 * @return A {@link List} of {@link ValidationFault}s, if there are errors or
	 *         warnings. Otherwise, an empty {@link List} will return.
	 */
	public static List<ValidationFault> validateEntityList(final List<? extends ModelEntity> list,
			final String parent, final String propertyName, final Locale locale,
			boolean strict) {

		Preconditions.checkArgument(locale != null);
		Preconditions.checkArgument(list != null, "list must not be null");

		final List<ValidationFault> faults = new ArrayList<>();

		if (list.isEmpty()) {
			return faults;
		}

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		for (int i = 0; i < list.size(); i++) {

			// get model entity in list
			final ModelEntity entity = list.get(i);

			// must not be null
			if (entity == null) {
				final ValidationFault fault = new ValidationFault(parent, propertyName,
						FaultLevel.ERROR, FaultType.IS_NULL);
				fault.setIndex(Integer.valueOf(i));
				fault.setMessage(bundle.getString("ENTITIES_VAL1"));
				faults.add(fault);
			} else {
				// validate the entity
				final List<ValidationFault> validationFaults = entity.validate(parent, locale, strict);
				if (!validationFaults.isEmpty()) {
					for (final ValidationFault fault : validationFaults) {
						fault.setIndex(Integer.valueOf(i));
						faults.add(fault);
					}
				}
			}
		}

		return faults;
	}

	/**
	 * Validate a {@link List} of {@link String}s.
	 * <p>
	 * Check for empty {@link String}s or duplicate {@link String}s.
	 * </p>
	 *
	 * @param list         A {@link List} of {@link String}s.
	 * @param entityName   The name of the parent entity.
	 * @param propertyName The name of the property the parent entity.
	 * @param locale       Desired {@link Locale} for validation messages.
	 * @return A {@link List} of {@link ValidationFault}s, if there are errors or
	 *         warnings. Otherwise, an empty {@link List} will return.
	 */
	public static List<ValidationFault> validateStrings(final List<String> list,
			final String entityName, final String propertyName, final Locale locale) {

		Preconditions.checkArgument(list != null, "list must not be null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(entityName), "entity name not set");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(propertyName), "property name not set");
		Preconditions.checkArgument(locale != null);

		final List<ValidationFault> faults = new ArrayList<>();

		if (list.isEmpty()) {
			return faults;
		}

		final Set<String> values = new HashSet<>();

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		for (int i = 0; i < list.size(); i++) {

			// Strings in the list must not be null or empty
			if (Strings.isNullOrEmpty(list.get(i))) {

				final ValidationFault fault = new ValidationFault(entityName, propertyName,
						FaultLevel.ERROR, FaultType.IS_EMPTY);
				if (!Strings.isNullOrEmpty(entityName)) {
					fault.setParentIndex(Integer.valueOf(i));
				}
				fault.setMessage(
						MessageFormat.format(bundle.getString("STRINGS_VAL1"), propertyName));
				faults.add(fault);
			}

			// there shall not be any duplicate Strings
			if (!values.add(list.get(i))) {

				final ValidationFault fault = new ValidationFault(entityName, propertyName,
						FaultLevel.ERROR, FaultType.HAS_DUPLICATE_VALUE);
				if (!Strings.isNullOrEmpty(entityName)) {
					fault.setParentIndex(Integer.valueOf(i));
				}
				fault.setMessage(
						MessageFormat.format(bundle.getString("STRINGS_VAL2"), propertyName));
				faults.add(fault);
			}
		}

		return faults;
	}
}
