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
 * Information model entity DocumentRelationship.
 * </p>
 * <p>
 * A <em>DocumentRelationship</em> relates a document with another document or
 * document version.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(includeFieldNames = true, of = { "documentId", "documentVersionId", "type" })
@Data
@FieldNameConstants
public class DocumentRelationship implements ModelEntity {

	private static final String ENTITY = "DocumentRelationship";

	private DocumentId documentId;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<String> documentVersionId;

	public void setDocumentVersionId(final List<String> documentVersionIds) {
		this.documentVersionId.clear();
		if (documentVersionIds != null && !documentVersionIds.isEmpty()) {
			this.documentVersionId.addAll(documentVersionIds);
		}
	}

	public List<String> getDocumentVersionId() {
		return new ArrayList<>(this.documentVersionId);
	}

	public void addDocumentVersionId(final String documentVersionId) {
		Preconditions.checkArgument(documentVersionId != null);

		this.documentVersionId.add(documentVersionId);
	}

	public void removeDocumentVersionId(final String documentVersionId) {
		Preconditions.checkArgument(documentVersionId != null);

		if (this.documentVersionId.contains(documentVersionId)) {
			this.documentVersionId.remove(documentVersionId);
		}
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<TranslatableString> description;

	public void setDescription(final List<TranslatableString> descriptions) {
		this.description.clear();
		if (descriptions != null && !descriptions.isEmpty()) {
			this.description.addAll(descriptions);
		}
	}

	public List<TranslatableString> getDescription() {
		return new ArrayList<>(this.description);
	}

	public void addDescription(final TranslatableString description) {
		Preconditions.checkArgument(description != null);

		this.description.add(description);
	}

	public void removeDescription(final TranslatableString description) {
		Preconditions.checkArgument(description != null);

		if (this.description.contains(description)) {
			this.description.remove(description);
		}
	}

	private DocumentRelationshipType type;

	/**
	 * Create a new instance of a {@link DocumentRelationship}.
	 */
	public DocumentRelationship() {
		this.documentVersionId = new ArrayList<>();
		this.description = new ArrayList<>();
	}

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
	public List<ValidationFault> validate(final String parent, final Locale locale,
			boolean strict) {

		Preconditions.checkArgument(locale != null);

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		final List<ValidationFault> faults = new ArrayList<>();

		// the reference to document ID is required
		if (this.documentId == null) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentId, parent,
					FaultLevel.ERROR, FaultType.IS_NULL);
			fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
			faults.add(fault);
		} else {
			// validate the document ID instance
			faults.addAll(this.documentId.validate(ENTITY, locale));
		}

		// A relationship type is required
		if (this.type == null) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.type, parent,
					FaultLevel.ERROR, FaultType.IS_NULL);
			fault.setMessage(bundle.getString(ENTITY + "_VAL2"));
			faults.add(fault);
		}

		// relation description is optional
		if (!this.description.isEmpty()) {
			// validate the description text
			faults.addAll(ValidationHelper.validateEntityList(this.description, ENTITY,
					Fields.description, locale, strict));
		}

		// a document version ID may be used
		if (!this.documentVersionId.isEmpty()) {
			// validate the document version ID
			faults.addAll(ValidationHelper.validateStrings(this.documentVersionId, ENTITY,
					Fields.documentVersionId, locale));
		}

		return faults;
	}
}
