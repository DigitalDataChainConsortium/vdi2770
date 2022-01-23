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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;

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
 * Information model entity DocumentVersion.
 * </p>
 * <p>
 * <em>Document Version</em> represents metadata specific for a document
 * version. Please notice the difference to {@link Document}.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(includeFieldNames = true, of = { "documentVersionId" })
@Data
@FieldNameConstants
public class DocumentVersion implements ModelEntity {

	private static final String ENTITY = "DocumentVersion";

	private String documentVersionId;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<String> language;

	public void setLanguage(final List<String> languages) {
		this.language.clear();
		if (languages != null && !languages.isEmpty()) {
			this.language.addAll(languages);
		}
	}

	public List<String> getLanguage() {
		return new ArrayList<>(this.language);
	}

	public void addLanguage(final String language) {
		Preconditions.checkArgument(language != null);

		this.language.add(language);
	}

	public void removeLanguage(final String language) {
		Preconditions.checkArgument(language != null);

		if (this.language.contains(language)) {
			this.language.remove(language);
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

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<DocumentDescription> documentDescription;

	public void setDocumentDescription(final List<DocumentDescription> documentDescriptions) {
		this.documentDescription.clear();
		if (documentDescriptions != null && !documentDescriptions.isEmpty()) {
			this.documentDescription.addAll(documentDescriptions);
		}
	}

	public List<DocumentDescription> getDocumentDescription() {
		return new ArrayList<>(this.documentDescription);
	}

	public void addDocumentDescription(final DocumentDescription documentDescription) {
		Preconditions.checkArgument(documentDescription != null);

		this.documentDescription.add(documentDescription);
	}

	public void removeDocumentDescription(final DocumentDescription documentDescription) {
		Preconditions.checkArgument(documentDescription != null);

		if (this.documentDescription.contains(documentDescription)) {
			this.documentDescription.remove(documentDescription);
		}
	}

	private LifeCycleStatus lifeCycleStatus;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<DocumentRelationship> documentRelationship;

	public void setDocumentRelationship(final List<DocumentRelationship> relations) {
		this.documentRelationship.clear();
		if (relations != null && !relations.isEmpty()) {
			this.documentRelationship.addAll(relations);
		}
	}

	public List<DocumentRelationship> getDocumentRelationship() {
		return new ArrayList<>(this.documentRelationship);
	}

	public void addDocumentRelationship(final DocumentRelationship rel) {
		Preconditions.checkArgument(rel != null);

		this.documentRelationship.add(rel);
	}

	public void removeDocumentRelationship(final DocumentRelationship rel) {
		Preconditions.checkArgument(rel != null);

		if (this.documentRelationship.contains(rel)) {
			this.documentRelationship.remove(rel);
		}
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<DigitalFile> digitalFile;

	public void setDigitalFile(final List<DigitalFile> digitalFiles) {
		this.digitalFile.clear();
		if (digitalFiles != null && !digitalFiles.isEmpty()) {
			this.digitalFile.addAll(digitalFiles);
		}
	}

	public List<DigitalFile> getDigitalFile() {
		return new ArrayList<>(this.digitalFile);
	}

	public void addDigitalFile(final DigitalFile digitalFile) {
		Preconditions.checkArgument(digitalFile != null);

		this.digitalFile.add(digitalFile);
	}

	public void removeDigitalFile(final DigitalFile digitalFile) {
		Preconditions.checkArgument(digitalFile != null);

		if (this.digitalFile.contains(digitalFile)) {
			this.digitalFile.remove(digitalFile);
		}
	}

	private Integer numberOfPages;

	/**
	 * Create a new empty instance of a {@link DocumentVersion}.
	 */
	public DocumentVersion() {
		this.language = new ArrayList<>();
		this.party = new ArrayList<>();
		this.documentDescription = new ArrayList<>();
		this.documentRelationship = new ArrayList<>();
		this.digitalFile = new ArrayList<>();
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

		// documentVersionId must not be empty
		if (Strings.isNullOrEmpty(this.documentVersionId)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentVersionId,
					parent, FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL5"));
			faults.add(fault);
		}

		// language must not be null or empty
		if (CollectionUtils.isEmpty(this.language)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.language, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL6"));
			faults.add(fault);
		} else {
			// list of strings must not contain empty entries
			faults.addAll(ValidationHelper.validateStrings(this.language, ENTITY, Fields.language,
					locale));

			for (int i = 0; i < this.language.size(); i++) {
				final String lang = this.language.get(i);

				// languages must conform to ISO standard
				if (!Constants.getIsoLanguageCodes().contains(lang.toLowerCase())) {
					final ValidationFault fault = new ValidationFault(ENTITY, Fields.language,
							parent, FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
					fault.setMessage(
							MessageFormat.format(bundle.getString(ENTITY + "_VAL7"), lang));
					fault.setIndex(Integer.valueOf(i));
					fault.setOriginalValue(lang);
					faults.add(fault);
				}
			}
		}

		// party must not be null or empty
		if (CollectionUtils.isEmpty(this.party)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.party, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL8"));
			faults.add(fault);
		} else {
			// list must not contain null entries
			faults.addAll(
					ValidationHelper.validateEntityList(this.party, ENTITY, Fields.party, locale));

			// list of party must contain at least one Author
			if (this.party.stream().filter(p -> p.getRole() == Role.Author).count() == 0) {
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.party, parent,
						FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
				fault.setOriginalValue(this.party.stream()
						.map(p -> p.getRole() != null ? p.getRole().toString() : "")
						.collect(Collectors.joining(", ")));
				faults.add(fault);
			}
		}

		// documentDescription must not be null or empty
		if (CollectionUtils.isEmpty(this.documentDescription)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.documentDescription,
					parent, FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL9"));
			faults.add(fault);
		} else {

			// list of descriptions must not contain null entry
			faults.addAll(ValidationHelper.validateEntityList(this.documentDescription, ENTITY,
					Fields.documentDescription, locale));

			// only one entry per language allowed
			if (this.documentDescription.stream().map(d -> d.getLanguage())
					.collect(Collectors.toSet()).size() != this.documentDescription.size()) {
				final ValidationFault fault = new ValidationFault(ENTITY,
						Fields.documentDescription, parent, FaultLevel.ERROR,
						FaultType.HAS_DUPLICATE_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL2"));
				faults.add(fault);
			}
		}

		// lifecycleStatus must not be null
		if (this.lifeCycleStatus == null) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.lifeCycleStatus,
					parent, FaultLevel.ERROR, FaultType.IS_NULL);
			fault.setMessage(bundle.getString(ENTITY + "_VAL10"));
			faults.add(fault);
		} else {
			faults.addAll(this.lifeCycleStatus.validate(ENTITY, locale));
		}

		// digitalFile must not be null or empty, because at least one
		// PDF/A file must be provided.
		if (CollectionUtils.isEmpty(this.digitalFile)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.digitalFile, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL11"));
			faults.add(fault);
		} else {
			faults.addAll(ValidationHelper.validateEntityList(this.digitalFile, ENTITY,
					Fields.digitalFile, locale));

			// no duplicate file names allowed
			if (this.digitalFile.stream().map(f -> f.getFileName()).collect(Collectors.toSet())
					.size() != this.digitalFile.size()) {
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.digitalFile,
						parent, FaultLevel.ERROR, FaultType.HAS_DUPLICATE_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL12"));
				faults.add(fault);
			}

			// at least one PDF file must be contained.
			if (this.digitalFile.stream()
					.filter(d -> MediaType.PDF.toString().equalsIgnoreCase(d.getFileFormat()))
					.count() == 0) {
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.digitalFile,
						parent, FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
				fault.setOriginalValue(this.digitalFile.stream().map(f -> f.getFileFormat())
						.collect(Collectors.joining(", ")));
				faults.add(fault);
			}
		}

		if (this.numberOfPages != null) {

			// numberOfPages must be greater than zero
			if (this.numberOfPages.intValue() < 0) {
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.numberOfPages,
						parent, FaultLevel.ERROR, FaultType.EXCEEDS_LOWER_BOUND);
				fault.setMessage(bundle.getString(ENTITY + "_VAL13"));
				faults.add(fault);
			}
		}

		for (int l = 0; l < this.language.size(); l++) {

			final String lang = this.language.get(l);

			final long descriptionCount = this.documentDescription.stream()
					.filter(d -> StringUtils.equalsIgnoreCase(d.getLanguage(), lang)).count();

			// no description for language?
			if (descriptionCount != 1) {
				final ValidationFault fault = new ValidationFault(ENTITY,
						Arrays.asList(Fields.language, Fields.documentDescription), parent,
						FaultLevel.ERROR, FaultType.IS_INCONSISTENT);
				fault.setMessage(MessageFormat.format(bundle.getString(ENTITY + "_VAL4"), lang));
				fault.setOriginalValue(lang);
				faults.add(fault);
			}
		}

		return faults;
	}
}
