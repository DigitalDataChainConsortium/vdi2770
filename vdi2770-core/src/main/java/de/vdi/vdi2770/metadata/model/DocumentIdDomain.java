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
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity DocumentIdDomain.
 * </p>
 * <p>
 * <em>DocumentIdDomain</em> specifies a domain for a document id.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(includeFieldNames = true, of = { "documentDomainId" })
@Data
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
public class DocumentIdDomain implements ModelEntity {

	private static final String ENTITY = "DocumentIdDomain";

	private String documentDomainId;

	private Party party;

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

		// a domain ID is required for a document domain
		if (Strings.isNullOrEmpty(this.documentDomainId)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentDomainId,
					parent, FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL2"));

			faults.add(fault);
		}

		// a responsible party is required
		if (this.party == null) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.party, parent,
					FaultLevel.ERROR, FaultType.IS_NULL);
			fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
			faults.add(fault);
		} else {

			// validate the party entity
			final List<ValidationFault> partyFaults = this.party.validate(parent, locale);
			faults.addAll(partyFaults);

			if (partyFaults.isEmpty()) {

				// the given party must be responsible for the ID domain
				if (this.party.getRole() != Role.Responsible) {

					final ValidationFault fault = new ValidationFault(ENTITY, Fields.party, parent,
							FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
					fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
					fault.setOriginalValue(
							this.party.getRole() != null ? this.party.getRole().toString() : "");
					faults.add(fault);
				}
			}
		}

		return faults;
	}

}
