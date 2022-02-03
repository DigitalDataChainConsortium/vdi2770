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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import de.vdi.vdi2770.metadata.xml.FileNames;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * In VDI 2770 guideline, there is a distinction between any document and a main
 * document. For main documents, there are some more constraints to consider.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MainDocument extends Document implements ModelEntity {

	private static final String ENTITY = "MainDocument";

	/**
	 * Create a new / empty main document.
	 */
	public MainDocument() {
		super();

		this.referencedMainDocuments = new ArrayList<>();
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<MainDocument> referencedMainDocuments;

	/**
	 * Create a main document from a document.
	 *
	 * @param document A document that will be marked as main document.
	 */
	public MainDocument(final Document document) {

		super();

		this.referencedMainDocuments = new ArrayList<>();

		Preconditions.checkArgument(document != null, "document not defined");

		this.setDocumentId(document.getDocumentId());
		this.setDocumentVersion(document.getDocumentVersion());
		this.setDocumentClassification(document.getDocumentClassification());
		this.setDocumentIdDomain(document.getDocumentIdDomain());
		this.setReferencedObject(document.getReferencedObject());
	}

	/**
	 * Validate this instance
	 *
	 * @param locale Desired {@link Locale} for validation messages.
	 * @param strict If <code>true</code>, strict validation is enabled.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	@Override
	public List<ValidationFault> validate(final Locale locale, boolean strict) {

		Preconditions.checkArgument(locale != null);

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		final List<ValidationFault> faults = new ArrayList<>();

		// call Document validation
		faults.addAll(super.validate(locale, strict));

		// a main document must only have one document version
		if (getDocumentVersion().size() != 1) {
			final ValidationFault fault = new ValidationFault(ENTITY,
					Document.Fields.documentVersion, FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
			fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
			faults.add(fault);
		}

		// we always take the first document version
		final DocumentVersion version = getDocumentVersion().get(0);

		// Check, whether document lifecycle status is RELEASED
		if (version.getLifeCycleStatus() != null) {

			if (version.getLifeCycleStatus().getStatusValue() != LifeCycleStatusValue.Released) {

				final ValidationFault fault = new ValidationFault(ENTITY,
						Document.Fields.documentVersion, FaultLevel.ERROR,
						FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL9"));
				faults.add(fault);
			}
		}

		// Check, whether the PDF file for the main document is contained.
		// This file has a special name.
		if (version.getDigitalFile().stream().filter(f -> StringUtils
				.equalsIgnoreCase(f.getFileName(), FileNames.MAIN_DOCUMENT_PDF_FILE_NAME))
				.count() != 1) {

			final ValidationFault fault = new ValidationFault(DigitalFile.class.getSimpleName(),
					DigitalFile.Fields.fileName, FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
			fault.setMessage(MessageFormat.format(bundle.getString(ENTITY + "_VAL2"),
					FileNames.MAIN_DOCUMENT_PDF_FILE_NAME));
			faults.add(fault);
		}

		// document relationships must be defined
		if (version.getDocumentRelationship().isEmpty()) {
			final ValidationFault fault = new ValidationFault(DocumentVersion.class.getSimpleName(),
					DocumentVersion.Fields.documentRelationship, FaultLevel.ERROR,
					FaultType.HAS_INVALID_VALUE);
			fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
			faults.add(fault);
		} else {

			// should use RefersTo only
			final Set<DocumentRelationshipType> relTypes = version.getDocumentRelationship()
					.stream().map(r -> r.getType()).collect(Collectors.toSet());

			if (relTypes.size() != 1 || !relTypes.contains(DocumentRelationshipType.RefersTo)) {
				final ValidationFault fault = new ValidationFault(
						DocumentVersion.class.getSimpleName(),
						DocumentVersion.Fields.documentRelationship, FaultLevel.WARNING,
						FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL4"));
				faults.add(fault);
			}
		}

		// main documents shall only refer to one reference object
		if (getReferencedObject().size() != 1) {
			final ValidationFault fault = new ValidationFault(ENTITY,
					Document.Fields.referencedObject, FaultLevel.ERROR,
					FaultType.HAS_INVALID_VALUE);
			fault.setMessage(bundle.getString(ENTITY + "_VAL5"));
			faults.add(fault);
		} else {
			// main documents must refer to instances
			ReferencedObject object = getReferencedObject().get(0);

			// no individual id found?
			if (object.getObjectId().stream()
					.filter(o -> o.getObjectType() == ObjectType.Individual).count() == 0) {
				final ValidationFault fault = new ValidationFault(ENTITY,
						Document.Fields.referencedObject, FaultLevel.ERROR,
						FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL7"));
				faults.add(fault);
			}
		}

		return faults;
	}
}
