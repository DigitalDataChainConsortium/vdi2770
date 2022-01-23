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
package de.vdi.vdi2770.metadata.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.MetadataException;
import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import de.vdi.vdi2770.metadata.model.DigitalFile;
import de.vdi.vdi2770.metadata.model.Document;
import de.vdi.vdi2770.metadata.model.DocumentClassification;
import de.vdi.vdi2770.metadata.model.DocumentDescription;
import de.vdi.vdi2770.metadata.model.DocumentId;
import de.vdi.vdi2770.metadata.model.DocumentIdDomain;
import de.vdi.vdi2770.metadata.model.DocumentVersion;
import de.vdi.vdi2770.metadata.model.ObjectId;
import de.vdi.vdi2770.metadata.model.Organization;
import de.vdi.vdi2770.metadata.model.ReferencedObject;
import de.vdi.vdi2770.metadata.model.TranslatableString;
import de.vdi.vdi2770.metadata.model.ValidationFault;
import lombok.extern.log4j.Log4j2;

/**
 * Validate an XML file.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class XmlValidationTest {

	private static final String EXAMPLES_FOLDER = "../examples/xml";

	@Test
	void validateXml() throws MetadataException {

		final File xmlFile = new File(EXAMPLES_FOLDER, "Datasheet.xml");

		final XmlReader reader = new XmlReader(Locale.getDefault());

		final List<XmlValidationFault> faults = reader.validate(xmlFile);

		assertTrue(!Fault.hasWarnings(faults));
	}

	@Test
	void validateEmptyXml() throws MetadataException {

		final File xmlFile = new File(EXAMPLES_FOLDER, "InvalidEmpty.xml");

		final XmlReader reader = new XmlReader(Locale.getDefault());

		final List<XmlValidationFault> faults = reader.validate(xmlFile);

		assertTrue(!Fault.hasErrors(faults));

		final Document xmlDocument = reader.read(xmlFile);

		final List<ValidationFault> errors = xmlDocument.validate(Locale.getDefault());

		errors.stream().forEach(f -> log.debug(f.toString()));

		Optional<ValidationFault> error = errors.stream()
				.filter(e -> e.getMessage().startsWith("DI_001")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentId");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentId.Fields.domainId);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DI_002")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentId");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentId.Fields.id);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DV_005")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentVersion");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentVersion.Fields.documentVersionId);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getEntity() == "DocumentVersion" && e.getMessage().startsWith("S_001"))
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentVersion");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentVersion.Fields.language);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DV_007")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentVersion");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentVersion.Fields.language);
		assertTrue(error.get().getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("O_001") && e.getParent() == "DocumentVersion")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Organization");
		assertTrue(error.get().getParent() == "DocumentVersion");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == Organization.Fields.organizationName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("O_002") && e.getParent() == "DocumentVersion")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Organization");
		assertTrue(error.get().getParent() == "DocumentVersion");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(
				error.get().getProperties().get(0) == Organization.Fields.organizationOfficialName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DD_001")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentDescription");
		assertTrue(error.get().getParent() == "DocumentVersion");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentDescription.Fields.language);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DD_003")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentDescription");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentDescription.Fields.title);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DD_004")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentDescription");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentDescription.Fields.summary);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("S_001") && e.getEntity() == "DocumentDescription")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentDescription");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentDescription.Fields.keyWords);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("O_001") && e.getParent() == "LifeCycleStatus")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Organization");
		assertTrue(error.get().getParent() == "LifeCycleStatus");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == Organization.Fields.organizationName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("O_002") && e.getParent() == "LifeCycleStatus")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Organization");
		assertTrue(error.get().getParent() == "LifeCycleStatus");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(
				error.get().getProperties().get(0) == Organization.Fields.organizationOfficialName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DF_004")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DigitalFile");
		assertTrue(error.get().getParent() == "DocumentVersion");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DigitalFile.Fields.fileName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DF_005")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DigitalFile");
		assertTrue(error.get().getParent() == "DocumentVersion");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DigitalFile.Fields.fileFormat);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DV_003")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentVersion");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentVersion.Fields.digitalFile);
		assertTrue(error.get().getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DC_005")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentClassification");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentClassification.Fields.classId);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("TS_001")
				&& e.getParent() == "DocumentClassification").findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "TranslatableString");
		assertTrue(error.get().getParent() == "DocumentClassification");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == TranslatableString.Fields.text);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("TS_002")
				&& e.getParent() == "DocumentClassification").findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "TranslatableString");
		assertTrue(error.get().getParent() == "DocumentClassification");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == TranslatableString.Fields.language);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DC_006")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentClassification");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties()
				.get(0) == DocumentClassification.Fields.classificationSystem);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("D_002")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Document");
		assertTrue(StringUtils.isEmpty(error.get().getParent()));
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == Document.Fields.documentClassification);
		assertTrue(error.get().getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("D_003")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Document");
		assertTrue(StringUtils.isEmpty(error.get().getParent()));
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == Document.Fields.documentClassification);
		assertTrue(error.get().getType() == FaultType.IS_INCONSISTENT);
		assertTrue(error.get().getLevel() == FaultLevel.INFORMATION);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DID_002")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "DocumentIdDomain");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == DocumentIdDomain.Fields.documentDomainId);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream()
				.filter(e -> e.getMessage().startsWith("O_001") && e.getParent() == "Document")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Organization");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == Organization.Fields.organizationName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream()
				.filter(e -> e.getMessage().startsWith("O_002") && e.getParent() == "Document")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Organization");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(
				error.get().getProperties().get(0) == Organization.Fields.organizationOfficialName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("OI_002")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "ObjectId");
		assertTrue(error.get().getParent() == "ReferencedObject");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == ObjectId.Fields.id);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("O_001") && e.getParent() == "ReferencedObject")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Organization");
		assertTrue(error.get().getParent() == "ReferencedObject");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == Organization.Fields.organizationName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("O_002") && e.getParent() == "ReferencedObject")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "Organization");
		assertTrue(error.get().getParent() == "ReferencedObject");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(
				error.get().getProperties().get(0) == Organization.Fields.organizationOfficialName);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("RO_002")).findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "ReferencedObject");
		assertTrue(error.get().getParent() == "Document");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == ReferencedObject.Fields.party);
		assertTrue(error.get().getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("TS_001") && e.getParent() == "ReferencedObject")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "TranslatableString");
		assertTrue(error.get().getParent() == "ReferencedObject");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == TranslatableString.Fields.text);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getMessage().startsWith("TS_002") && e.getParent() == "ReferencedObject")
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "TranslatableString");
		assertTrue(error.get().getParent() == "ReferencedObject");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == TranslatableString.Fields.language);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getEntity() == "ReferencedObject" && e.getMessage().startsWith("S_001"))
				.findFirst();
		assertTrue(error.isPresent());

		assertTrue(error.get().getEntity() == "ReferencedObject");
		assertTrue(error.get().getProperties().size() == 1);
		assertTrue(error.get().getProperties().get(0) == ReferencedObject.Fields.projectId);
		assertTrue(error.get().getType() == FaultType.IS_EMPTY);
		assertTrue(error.get().getLevel() == FaultLevel.ERROR);

		ValidationFault empty = errors.stream().filter(
				e -> e.getEntity() == "ReferencedObject" && e.getMessage().startsWith("S_001"))
				.collect(Collectors.toList()).get(1);

		assertTrue(empty.getEntity() == "ReferencedObject");
		assertTrue(empty.getProperties().size() == 1);
		assertTrue(empty.getProperties().get(0) == ReferencedObject.Fields.referenceDesignation);
		assertTrue(empty.getType() == FaultType.IS_EMPTY);
		assertTrue(empty.getLevel() == FaultLevel.ERROR);

		empty = errors.stream().filter(
				e -> e.getEntity() == "ReferencedObject" && e.getMessage().startsWith("S_001"))
				.collect(Collectors.toList()).get(2);

		assertTrue(empty.getEntity() == "ReferencedObject");
		assertTrue(empty.getProperties().size() == 1);
		assertTrue(empty.getProperties().get(0) == ReferencedObject.Fields.equipmentId);
		assertTrue(empty.getType() == FaultType.IS_EMPTY);
		assertTrue(empty.getLevel() == FaultLevel.ERROR);
	}

}
