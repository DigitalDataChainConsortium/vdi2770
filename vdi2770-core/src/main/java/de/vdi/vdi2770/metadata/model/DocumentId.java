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
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity DocumentId.
 * </p>
 * <p>
 * A <em>DocumentId</em> is a unique identifier for a document within a given
 * domain.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(of = { "id", "domainId" }, includeFieldNames = true)
@Data
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "domainId", "id" })
public class DocumentId implements ModelEntity {

	private static final String ENTITY = "DocumentId";

	private String domainId;

	private String id;

	@Setter
	private Boolean isPrimary;

	/**
	 * Getter method for isPrimary property.
	 *
	 * @return <code>true</code>, if the given {@link DocumentId} is a primary ID.
	 */
	public Boolean getIsPrimary() {

		if (this.isPrimary == null) {
			return Boolean.FALSE;
		}

		return this.isPrimary;

	}

	/**
	 * Validate this instance.
	 *
	 * @param parent The name of a parent element. Can be null.
	 * @return A {@link List} of {@link ValidationFault}s, if there are errors or
	 *         warnings. Otherwise, an empty {@link List} will return.
	 */
	@Override
	public List<ValidationFault> validate(final String parent, final Locale locale) {

		Preconditions.checkArgument(locale != null);

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		final List<ValidationFault> faults = new ArrayList<>();

		// domainId must not be empty
		if (Strings.isNullOrEmpty(this.domainId)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.domainId, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
			faults.add(fault);
		}

		// id must not be empty
		if (Strings.isNullOrEmpty(this.id)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL2"));
			faults.add(fault);
		}

		return faults;
	}

	public String getAsText() {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getId());
		builder.append(";");
		builder.append(this.getDomainId());
		builder.append(";");
		builder.append(this.getIsPrimary());

		return builder.toString();
	}
}
