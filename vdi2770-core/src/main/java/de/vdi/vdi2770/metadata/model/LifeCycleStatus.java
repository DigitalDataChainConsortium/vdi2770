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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Preconditions;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity LifeCycleStatus.
 * </p>
 * <p>
 * <em>LifeCycleStatus</em> represents the milestone of a document version, e.g.
 * a release.
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
@FieldNameConstants
public class LifeCycleStatus implements ModelEntity {

	private static final String ENTITY = "LifeCycleStatus";

	private LifeCycleStatusValue statusValue;

	private LocalDate setDate;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<TranslatableString> comments;

	public void setComments(final List<TranslatableString> comments) {
		this.comments.clear();
		if (comments != null && !comments.isEmpty()) {
			this.comments.addAll(comments);
		}
	}

	public List<TranslatableString> getComments() {
		return new ArrayList<>(this.comments);
	}

	public void addComment(final TranslatableString comment) {
		Preconditions.checkArgument(comment != null);

		this.comments.add(comment);
	}

	public void removeComment(final TranslatableString comment) {
		Preconditions.checkArgument(comment != null);

		if (this.comments.contains(comment)) {
			this.comments.remove(comment);
		}
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<Party> party;

	public void setParty(final List<Party> parties) {
		this.party.clear();
		if (parties != null && !parties.isEmpty()) {
			this.party.addAll(parties);
		}
	}

	public List<Party> getParty() {
		return new ArrayList<>(this.party);
	}

	public void addParty(final Party party) {
		Preconditions.checkArgument(party != null);

		this.party.add(party);
	}

	public void removeParty(final Party party) {
		Preconditions.checkArgument(party != null);

		if (this.party.contains(party)) {
			this.party.remove(party);
		}
	}

	/**
	 * Create a new instance of a {@link LifeCycleStatus}.
	 */
	public LifeCycleStatus() {
		this.comments = new ArrayList<>();
		this.party = new ArrayList<>();
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

		// the status value is required
		if (this.statusValue == null) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.statusValue, parent,
					FaultLevel.ERROR, FaultType.IS_NULL);
			fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
			faults.add(fault);
		}

		// at least one party must be given
		if (CollectionUtils.isEmpty(this.party)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.party, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL4"));
			faults.add(fault);
		} else {
			// validate given list of parties
			faults.addAll(
					ValidationHelper.validateEntityList(this.party, ENTITY, Fields.party, locale));

			// The list of parties must contain at least one responsible party
			if (this.party.stream().filter(p -> p.getRole() == Role.Responsible).count() == 0) {

				final ValidationFault fault = new ValidationFault(ENTITY, Fields.party, parent,
						FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
				fault.setOriginalValue(this.party.stream()
						.map(p -> p.getRole() != null ? p.getRole().toString() : "")
						.collect(Collectors.joining(", ")));
				faults.add(fault);
			}
		}

		// comments are optional
		if (!this.comments.isEmpty()) {
			// validate comments
			faults.addAll(ValidationHelper.validateEntityList(this.comments, ENTITY,
					Fields.comments, locale));
		}

		return faults;

	}

}
