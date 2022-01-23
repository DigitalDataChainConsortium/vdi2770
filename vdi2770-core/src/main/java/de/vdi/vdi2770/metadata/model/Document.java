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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import de.vdi.vdi2770.metadata.xml.FileNames;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity Document.
 * </p>
 * <p>
 * <em>Document</em> represents static metadata of a document.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(includeFieldNames = true, of = { "documentId" })
@Data
@FieldNameConstants
public class Document implements ModelEntity {

	private static final String ENTITY = "Document";

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<DocumentId> documentId;

	/**
	 * Get a {@link List} of {@link DocumentId}s that identify the {@link Document}.
	 *
	 * @return {@link List} of {@link DocumentId}s.
	 */
	public List<DocumentId> getDocumentId() {
		return new ArrayList<>(this.documentId);
	}

	/**
	 * Set the {@link List} of {@link DocumentId}s for this {@link Document}.
	 *
	 * <p>
	 * Given {@link List} can be <code>null</code> or empty.
	 * </p>
	 *
	 * @param documentIds {@link List} of {@link DocumentId}s.
	 */
	public void setDocumentId(final List<DocumentId> documentIds) {

		this.documentId.clear();
		if (documentIds != null && !documentIds.isEmpty()) {
			this.documentId.addAll(documentIds);
		}
	}

	/**
	 * Add a {@link DocumentId} for the {@link Document}.
	 *
	 * <p>
	 * Given {@link DocumentId} must not be null be <code>null</code>.
	 * </p>
	 *
	 * @param documentId {@link DocumentId} to add.
	 */
	public void addDocumentId(final DocumentId documentId) {
		Preconditions.checkArgument(documentId != null);

		this.documentId.add(documentId);
	}

	public void removeDocumentId(final DocumentId documentId) {
		Preconditions.checkArgument(documentId != null);

		if (this.documentId.contains(documentId)) {
			this.documentId.remove(documentId);
		}
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<DocumentVersion> documentVersion;

	public List<DocumentVersion> getDocumentVersion() {
		return new ArrayList<>(this.documentVersion);
	}

	public void setDocumentVersion(final List<DocumentVersion> documentVersion) {
		this.documentVersion.clear();
		if (documentVersion != null && !documentVersion.isEmpty()) {
			this.documentVersion.addAll(documentVersion);
		}
	}

	public void addDocumentVersion(final DocumentVersion documentVersion) {
		Preconditions.checkArgument(documentVersion != null);

		this.documentVersion.add(documentVersion);
	}

	public void removeDocumentVersion(final DocumentVersion documentVersion) {
		Preconditions.checkArgument(documentVersion != null);

		if (this.documentVersion.contains(documentVersion)) {
			this.documentVersion.remove(documentVersion);
		}
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<DocumentClassification> documentClassification;

	public List<DocumentClassification> getDocumentClassification() {
		return new ArrayList<>(this.documentClassification);
	}

	public void setDocumentClassification(
			final List<DocumentClassification> documentClassification) {
		this.documentClassification.clear();
		if (documentClassification != null && !documentClassification.isEmpty()) {
			this.documentClassification.addAll(documentClassification);
		}
	}

	public void addDocumentClassification(final DocumentClassification documentClassification) {
		Preconditions.checkArgument(documentClassification != null);

		this.documentClassification.add(documentClassification);
	}

	public void removeDocumentClassification(final DocumentClassification documentClassification) {
		Preconditions.checkArgument(documentClassification != null);

		if (this.documentClassification.contains(documentClassification)) {
			this.documentClassification.remove(documentClassification);
		}
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<DocumentIdDomain> documentIdDomain;

	public List<DocumentIdDomain> getDocumentIdDomain() {
		return new ArrayList<>(this.documentIdDomain);
	}

	public void setDocumentIdDomain(final List<DocumentIdDomain> documentIdDomain) {
		this.documentIdDomain.clear();
		if (documentIdDomain != null && !documentIdDomain.isEmpty()) {
			this.documentIdDomain.addAll(documentIdDomain);
		}
	}

	public void addDocumentIdDomain(final DocumentIdDomain documentIdDomain) {
		Preconditions.checkArgument(documentIdDomain != null);

		this.documentIdDomain.add(documentIdDomain);
	}

	public void removeDocumentIdDomain(final DocumentIdDomain documentIdDomain) {
		Preconditions.checkArgument(documentIdDomain != null);

		if (this.documentIdDomain.contains(documentIdDomain)) {
			this.documentIdDomain.remove(documentIdDomain);
		}
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<ReferencedObject> referencedObject;

	public List<ReferencedObject> getReferencedObject() {
		return new ArrayList<>(this.referencedObject);
	}

	public void setReferencedObject(final List<ReferencedObject> referencedObject) {
		this.referencedObject.clear();
		if (referencedObject != null && !referencedObject.isEmpty()) {
			this.referencedObject.addAll(referencedObject);
		}
	}

	public void addReferencedObject(final ReferencedObject referencedObject) {
		Preconditions.checkArgument(referencedObject != null);

		this.referencedObject.add(referencedObject);
	}

	public void removeReferencedObject(final ReferencedObject referencedObject) {
		Preconditions.checkArgument(referencedObject != null);

		if (this.referencedObject.contains(referencedObject)) {
			this.referencedObject.remove(referencedObject);
		}
	}

	/**
	 * Create a new instance of a {@link Document}.
	 */
	public Document() {
		this.documentId = new ArrayList<>();
		this.documentVersion = new ArrayList<>();
		this.documentClassification = new ArrayList<>();
		this.documentIdDomain = new ArrayList<>();
		this.referencedObject = new ArrayList<>();
	}

	/**
	 * Check, whether this {@link Document} might be a {@link MainDocument}.
	 *
	 * @return <code>true</code>, if the document is a main document.
	 */
	public boolean isMainDocument() {

		final Optional<DigitalFile> mainDocumentFile = this.documentVersion.stream()
				.map(v -> v.getDigitalFile()).flatMap(d -> d.stream()).filter(d -> StringUtils
						.equalsIgnoreCase(d.getFileName(), FileNames.MAIN_DOCUMENT_PDF_FILE_NAME))
				.findFirst();

		return mainDocumentFile.isPresent();
	}

	/**
	 * Validate this instance of <em>Document</em>.
	 *
	 * @param parent The name of the parent entity.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	@Override
	public List<ValidationFault> validate(final String parent, final Locale locale) {

		Preconditions.checkArgument(locale != null);

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		final List<ValidationFault> faults = new ArrayList<>();

		// A document must have at least one document ID
		if (CollectionUtils.isEmpty(this.documentId)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentId,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL5"));
			faults.add(fault);
		} else {
			// Validate each entry in the list of document IDs
			faults.addAll(ValidationHelper.validateEntityList(this.documentId, ENTITY,
					Fields.documentId, locale));

			// more than one document ID defined?
			if (this.documentId.size() >= 2) {

				// exact one id must be primary
				if (this.documentId.stream().filter(id -> id.getIsPrimary().booleanValue() == true)
						.count() != 1) {
					final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentId,
							parent, FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
					fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
					faults.add(fault);
				}
			}
		}

		// each document must have at least one document version
		if (CollectionUtils.isEmpty(this.documentVersion)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentVersion,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL6"));
			faults.add(fault);
		} else {
			// Validate the list of document versions
			faults.addAll(ValidationHelper.validateEntityList(this.documentVersion, ENTITY,
					Fields.documentVersion, locale));
		}

		// each document must be at least classified according to VDI 2770
		if (CollectionUtils.isEmpty(this.documentClassification)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentClassification,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL7"));
			faults.add(fault);
		} else {
			// Validate each entry in the list of document classifications
			faults.addAll(ValidationHelper.validateEntityList(this.documentClassification, ENTITY,
					Fields.documentClassification, locale));

			// No classification according to VDI 2770 given?
			if (this.documentClassification.stream().map(c -> c.getClassificationSystem())
					.filter(s -> StringUtils.equals(s, Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME))
					.count() == 0) {
				final ValidationFault fault = new ValidationFault(ENTITY,
						Fields.documentClassification, parent, FaultLevel.ERROR,
						FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL2"));
				faults.add(fault);
			}

			// We recommend to classify the document according to IEC 61355
			if (this.documentClassification.stream().map(c -> c.getClassificationSystem())
					.filter(s -> StringUtils.equals(s, Constants.IEC61355_CLASSIFICATION_NAME))
					.count() == 0) {
				final ValidationFault fault = new ValidationFault(ENTITY,
						Fields.documentClassification, parent, FaultLevel.INFORMATION,
						FaultType.IS_INCONSISTENT);
				fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
				faults.add(fault);
			}
		}

		// Each document must have a document domain ID
		if (CollectionUtils.isEmpty(this.documentIdDomain)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentIdDomain,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL8"));
			faults.add(fault);
		} else {
			// Validate each document domain ID
			faults.addAll(ValidationHelper.validateEntityList(this.documentIdDomain, ENTITY,
					Fields.documentIdDomain, locale));
		}

		// Each document must refer at least to one reference object
		if (CollectionUtils.isEmpty(this.referencedObject)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.referencedObject,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL9"));
			faults.add(fault);
		} else {
			// Validate each reference object in the list
			faults.addAll(ValidationHelper.validateEntityList(this.referencedObject, ENTITY,
					Fields.referencedObject, locale));
		}

		return faults;
	}

	/**
	 * Validate relationships of a {@link Document}.
	 *
	 * @param otherDocuments A {@link List} of other {@link Document}s (may be
	 *                       empty).
	 * @param isMainDocument If <code>true</code>, the source {@link Document} is a
	 *                       main document.
	 * @param locale         Desired {@link Locale} for validation messages.
	 * @return A {@link List} of validation faults according to the constraints
	 *         specified in VDI 2770 guideline.
	 */
	public List<ValidationFault> validateDocumentRelations(final List<Document> otherDocuments,
			final boolean isMainDocument, final Locale locale) {

		Preconditions.checkArgument(otherDocuments != null, "other documents is null");

		final List<ValidationFault> errors = new ArrayList<>();

		// read the document IDs from 'otherDocuments'
		final List<DocumentId> documentIds = otherDocuments.stream().map(d -> d.getDocumentId())
				.flatMap(id -> id.stream()).collect(Collectors.toList());

		// for each Document Version
		for (final DocumentVersion version : getDocumentVersion()) {
			errors.addAll(validateDocumentRelations(version, documentIds, isMainDocument, locale));
		}

		return errors;
	}

	/**
	 * Validate relationships of a {@link DocumentVersion}.
	 *
	 * @param version          The source {@link DocumentVersion} referring to other
	 *                         {@link Document}s or {@link DocumentVersion}s.
	 * @param knownDocumentIds A {@link List} of DocumentIds.
	 * @param isMainDocument   If <code>true</code>, the source {@link Document} is
	 *                         a main document.
	 * @param locale           Desired {@link Locale} for validation messages.
	 * @return A {@link List} of validation faults according to the constraints
	 *         specified in VDI 2770 guideline.
	 */
	public List<ValidationFault> validateDocumentRelations(final DocumentVersion version,
			final List<DocumentId> knownDocumentIds, final boolean isMainDocument,
			final Locale locale) {

		Preconditions.checkArgument(version != null, "version id is null");
		Preconditions.checkArgument(knownDocumentIds != null, "known document ids is null");
		Preconditions.checkArgument(locale != null);

		final List<ValidationFault> errors = new ArrayList<>();

		// read the document relationships
		for (final DocumentRelationship rel : version.getDocumentRelationship()) {

			// for each DocumentId
			final DocumentId id = rel.getDocumentId();
			final Optional<ValidationFault> fault = validateDocumentRelations(id, knownDocumentIds,
					isMainDocument, locale);
			if (fault.isPresent()) {
				errors.add(fault.get());
			}
		}

		return errors;
	}

	/**
	 * Validate relationships to other {@link Document}s.
	 * <p>
	 * In general, it will be checked, whether the {@link DocumentId}s are known.
	 * </p>
	 *
	 * @param documentId       The source {@link Document} represented by a
	 *                         {@link DocumentId}
	 * @param knownDocumentIds Other known {@link DocumentId}s.
	 * @param isMainDocument   If <code>true</code>, the source {@link Document} is
	 *                         a main document.
	 * @param locale           Desired {@link Locale} for validation messages.
	 * @return A {@link ValidationFault}, if there is a warning or error.
	 */
	public Optional<ValidationFault> validateDocumentRelations(final DocumentId documentId,
			final List<DocumentId> knownDocumentIds, final boolean isMainDocument,
			final Locale locale) {

		Preconditions.checkArgument(locale != null);
		Preconditions.checkArgument(documentId != null, "document id is null");
		Preconditions.checkArgument(knownDocumentIds != null, "known document ids is null");

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		// list of known IDs contains documentId?
		if (knownDocumentIds.stream()
				.filter(id -> StringUtils.equalsIgnoreCase(
						StringRepresentations.documentIdAsText(id),
						StringRepresentations.documentIdAsText(documentId)))
				.count() == 0) {

			final Optional<ValidationFault> fault = Optional
					.of(new ValidationFault(ENTITY, Fields.documentVersion, null,
							isMainDocument ? FaultLevel.ERROR : FaultLevel.INFORMATION,
							FaultType.IS_INCONSISTENT));
			fault.get().setMessage(MessageFormat.format(bundle.getString(ENTITY + "_VAL4"),
					documentId.getAsText()));
			return fault;
		}

		return Optional.empty();
	}

	public ValidationFault validateObjects(final Document parent, final Locale locale) {

		Preconditions.checkArgument(locale != null, "locale is null");

		if (parent == null) {
			return null;
		}

		List<ObjectId> objectIds = this.getReferencedObject().stream().map(v -> v.getObjectId())
				.flatMap(Collection::stream).collect(Collectors.toList());

		List<ObjectId> parentObjectIds = parent.getReferencedObject().stream()
				.map(v -> v.getObjectId()).flatMap(Collection::stream).collect(Collectors.toList());

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		if (!objectIds.stream().anyMatch(parentObjectIds::contains)) {

			final ValidationFault fault = new ValidationFault(ENTITY,
					Document.Fields.referencedObject, FaultLevel.WARNING,
					FaultType.IS_INCONSISTENT);
			fault.setMessage(MessageFormat.format(bundle.getString(ENTITY + "_VAL10"),
					parent.getDocumentId().get(0).getAsText()));
			return fault;
		}

		return null;
	}
}
