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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * This class represents a validation fault.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
public class ValidationFault extends Fault {

	/**
	 * The fault refers to a given entity in the information model of VDI 2770
	 * guideline.
	 */
	@NonNull
	private String entity;

	private String parent;

	private Integer parentIndex;

	/**
	 * The fault refers to a one or more properties of the entity in the information
	 * model of VDI 2770 guideline.
	 */
	private List<String> properties = new ArrayList<>();

	/**
	 * Create a validation fault indicating a problem with a property of an entity.
	 *
	 * @param entity   The name of the entity.
	 * @param property A name of a property.
	 * @param level    The fault level.
	 * @param type     The type of fault.
	 */
	public ValidationFault(final String entity, final String property, final FaultLevel level,
			final FaultType type) {

		super(level, type);

		this.entity = entity;
		this.properties = Arrays.asList(property);
	}

	/**
	 * Create a validation fault indicating a problem with a property of an entity
	 * which is a child of a parent entity.
	 *
	 * @param entity   The name of the entity.
	 * @param property A name of a property.
	 * @param parent   The name of the parent entity.
	 * @param level    The fault level.
	 * @param type     The type of fault.
	 */
	public ValidationFault(final String entity, final String property, final String parent,
			final FaultLevel level, final FaultType type) {

		super(level, type);

		this.entity = entity;
		this.properties = Arrays.asList(property);
		this.parent = parent;
	}

	/**
	 * Create a validation fault indicating a problem resulting of properties.
	 *
	 * @param entity     The name of the entity.
	 * @param properties A {@link List} of properties that are the reason of the
	 *                   fault (in combination).
	 * @param parent     The name of the parent entity.
	 * @param level      The fault level.
	 * @param type       The type of fault.
	 */
	public ValidationFault(final String entity, final List<String> properties, final String parent,
			final FaultLevel level, final FaultType type) {

		super(level, type);

		this.entity = entity;
		this.properties = properties;
		this.parent = parent;
	}

	/**
	 * Custom {@link Object#toString()} serialization including detailed fault
	 * information.
	 */
	@Override
	public String toString() {
		return toString(Locale.getDefault());
	}

	/**
	 * Represent this class a {@link String} in a given language
	 *
	 * @param locale Desired {@link Locale} for messages.
	 * @return {@link String}
	 */
	public String toString(final Locale locale) {

		Preconditions.checkArgument(locale != null);

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		final StringBuilder builder = new StringBuilder();

		builder.append(getLevel());
		builder.append(" -- ");
		builder.append(getEntity());
		addProperties(getProperties(), builder);
		if (getIndex() != null) {
			builder.append("@");
			builder.append(getIndex());
		}
		builder.append(": ");
		builder.append(getType());
		if (!Strings.isNullOrEmpty(getMessage())) {
			builder.append(" '");
			builder.append(getMessage());
			builder.append("' ");
		}
		if (!Strings.isNullOrEmpty(getOriginalValue())) {
			builder.append(bundle.getString("ValidationFault_M1"));
			builder.append(" ");
			builder.append(getOriginalValue());
		}

		if (!Strings.isNullOrEmpty(getParent())) {
			builder.append(" parent=");
			builder.append(getParent());
			if (getParentIndex() != null) {
				builder.append("@");
				builder.append(getParentIndex());
			}

		}

		return builder.toString();
	}

	private void addProperties(final List<String> properties, final StringBuilder builder) {

		if (properties.size() > 0) {
			builder.append("[");
			builder.append(getProperties().stream().collect(Collectors.joining(",")));
			builder.append("]");
		}

	}
}
