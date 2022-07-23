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

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Preconditions;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity ReferencedObject.
 * </p>
 * <p>
 * <em>ReferencedObject</em> defines essential metadata to identify an object.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(of = { "objectId" })
@Data
@FieldNameConstants
@EqualsAndHashCode(of = { "objectId", "party" })
public class ReferencedObject implements ModelEntity {

	private static final String ENTITY = "ReferencedObject";

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<ObjectId> objectId;

	public void setObjectId(final List<ObjectId> objectIds) {
		this.objectId.clear();
		if (objectIds != null && !objectIds.isEmpty()) {
			this.objectId.addAll(objectIds);
		}
	}

	public List<ObjectId> getObjectId() {
		return new ArrayList<>(this.objectId);
	}

	public void addObjectId(final ObjectId objectId) {
		Preconditions.checkArgument(objectId != null);

		this.objectId.add(objectId);
	}

	public void removeObjectId(final ObjectId objectId) {
		Preconditions.checkArgument(objectId != null);

		this.objectId.remove(objectId);
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<String> referenceDesignation;

	public void setReferenceDesignation(final List<String> referenceDesignations) {
		this.referenceDesignation.clear();
		if (referenceDesignations != null && !referenceDesignations.isEmpty()) {
			this.referenceDesignation.addAll(referenceDesignations);
		}
	}

	public List<String> getReferenceDesignation() {
		return new ArrayList<>(this.referenceDesignation);
	}

	public void addReferenceDesignation(final String referenceDesignation) {
		Preconditions.checkArgument(referenceDesignation != null);

		this.referenceDesignation.add(referenceDesignation);
	}

	public void removeReferenceDesignation(final String referenceDesignation) {
		Preconditions.checkArgument(referenceDesignation != null);

		this.referenceDesignation.remove(referenceDesignation);
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<String> equipmentId;

	public void setEquipmentId(final List<String> equipmentIds) {
		this.equipmentId.clear();
		if (equipmentIds != null && !equipmentIds.isEmpty()) {
			this.equipmentId.addAll(equipmentIds);
		}
	}

	public List<String> getEquipmentId() {
		return new ArrayList<>(this.equipmentId);
	}

	public void addEquipmentId(final String equipmentId) {
		Preconditions.checkArgument(equipmentId != null);

		this.equipmentId.add(equipmentId);
	}

	public void removeEquipmentId(final String equipmentId) {
		Preconditions.checkArgument(equipmentId != null);

		this.equipmentId.remove(equipmentId);
	}

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<String> projectId;

	public void setProjectId(final List<String> projectIds) {
		this.projectId.clear();
		if (projectIds != null && !projectIds.isEmpty()) {
			this.projectId.addAll(projectIds);
		}
	}

	public List<String> getProjectId() {
		return new ArrayList<>(this.projectId);
	}

	public void addProjectId(final String projectId) {
		Preconditions.checkArgument(projectId != null);

		this.projectId.add(projectId);
	}

	public void removeProjectId(final String projectId) {
		Preconditions.checkArgument(projectId != null);

		this.projectId.remove(projectId);
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

		this.description.remove(description);
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

		this.party.remove(party);
	}

	/**
	 * Create a new (empty) instance of {@link ReferencedObject}.
	 */
	public ReferencedObject() {
		this.objectId = new ArrayList<>();
		this.projectId = new ArrayList<>();
		this.equipmentId = new ArrayList<>();
		this.referenceDesignation = new ArrayList<>();
		this.description = new ArrayList<>();
		this.party = new ArrayList<>();
	}

	/**
	 * Create new {@link ReferencedObject} including required information.
	 *
	 * @param objectIds List of object IDs for the object. Must not be
	 *                  <code>null</code> or empty.
	 * @param parties   List of parties for the object. At least one Party defined
	 *                  as {@link Role#Manufacturer} must be given.
	 */
	public ReferencedObject(final List<ObjectId> objectIds, final List<Party> parties) {
		this.objectId = new ArrayList<>();
		setObjectId(objectIds);

		this.projectId = new ArrayList<>();
		this.equipmentId = new ArrayList<>();
		this.referenceDesignation = new ArrayList<>();
		this.description = new ArrayList<>();

		this.party = new ArrayList<>();
		setParty(parties);
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

		if (CollectionUtils.isEmpty(this.objectId)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.objectId, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
			faults.add(fault);
		} else {
			faults.addAll(ValidationHelper.validateEntityList(this.objectId, ENTITY, Fields.party,
					locale, strict));

			final List<ObjectId> serialIds = this.objectId.stream()
					.filter(o -> o.getObjectType() == ObjectType.Individual).toList();

			// only one serial id is allowed?
			if (serialIds.size() > 1) {
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.objectId, parent,
						FaultLevel.INFORMATION, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
				faults.add(fault);
			}
		}

		// party is required
		if (CollectionUtils.isEmpty(this.party)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.party, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL4"));
			faults.add(fault);
		} else {
			faults.addAll(ValidationHelper.validateEntityList(this.party, ENTITY, Fields.party,
					locale, strict));

			// party must contain manufacturer
			if (this.party.stream().noneMatch(p -> p.getRole() == Role.Manufacturer)) {
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.party, parent,
						FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL2"));
				faults.add(fault);
			}
		}

		if (!CollectionUtils.isEmpty(this.description)) {
			faults.addAll(ValidationHelper.validateEntityList(this.description, ENTITY,
					Fields.description, locale, strict));
		}

		if (!CollectionUtils.isEmpty(this.projectId)) {
			faults.addAll(ValidationHelper.validateStrings(this.projectId, ENTITY, Fields.projectId,
					locale));
		}

		if (!CollectionUtils.isEmpty(this.referenceDesignation)) {
			faults.addAll(ValidationHelper.validateStrings(this.referenceDesignation, ENTITY,
					Fields.referenceDesignation, locale));
		}

		if (!CollectionUtils.isEmpty(this.equipmentId)) {
			faults.addAll(ValidationHelper.validateStrings(this.equipmentId, ENTITY,
					Fields.equipmentId, locale));
		}

		return faults;
	}
}
