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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity ObjectId.
 * </p>
 * <p>
 * <em>ObjectId</em> represents identification information for a
 * {@link ReferencedObject}. The object can be of type individual or type.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(includeFieldNames = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(of = { "id", "objectType" })
public class ObjectId implements ModelEntity {

	private static final String ENTITY = "ObjectId";

	private ObjectType objectType;

	private String id;

	private Boolean isGloballyBiunique;

	/**
	 * Is this {@link ObjectId} globally biunique.
	 *
	 * @return <code>True</code>, if the {@link ObjectId} is globally biunique.
	 */
	public Boolean getIsGloballyBiunique() {
		if (this.isGloballyBiunique == null) {
			return Boolean.FALSE;
		}

		return this.isGloballyBiunique;
	}

	private String refType;

	/**
	 * Validate this instance.
	 *
	 * @param parent The name of a parent element. Can be <code>null</code>.
	 * @param locale Desired {@link Locale} for validation messages.
	 * @param strict If <code>true</code>, strict validation is enabled.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	@Override
	public List<ValidationFault> validate(final String parent, final Locale locale, boolean strict) {

		Preconditions.checkArgument(locale != null);

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		final List<ValidationFault> faults = new ArrayList<>();

		// objectType is required
		if (this.objectType == null) {
			ValidationFault fault = new ValidationFault(ENTITY, Fields.objectType, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
			faults.add(fault);
		}

		// id is required
		if (Strings.isNullOrEmpty(this.id)) {
			ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent, FaultLevel.ERROR,
					FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL2"));
			faults.add(fault);
		}

		return faults;
	}
}
