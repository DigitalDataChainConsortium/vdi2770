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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

		assertFalse(Fault.hasWarnings(faults));
	}

	@Test
	void validateEmptyXml() throws MetadataException {

		final File xmlFile = new File(EXAMPLES_FOLDER, "InvalidEmpty.xml");

		final XmlReader reader = new XmlReader(Locale.getDefault());

		final List<XmlValidationFault> faults = reader.validate(xmlFile);

		assertFalse(Fault.hasErrors(faults));

		final Document xmlDocument = reader.read(xmlFile);

		final List<ValidationFault> errors = xmlDocument.validate(Locale.getDefault(), true);

		errors.forEach(f -> log.debug(f.toString()));

		Optional<ValidationFault> error = errors.stream()
				.filter(e -> e.getMessage().startsWith("DI_001")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentId", error.get().getEntity());
		assertEquals(error.get().getParent(), "Document");
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentId.Fields.domainId, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DI_002")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentId", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentId.Fields.id, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DV_005")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentVersion", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentVersion.Fields.documentVersionId, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getEntity().equals("DocumentVersion") && e.getMessage().startsWith("S_001"))
				.findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentVersion", error.get().getEntity());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentVersion.Fields.language, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DV_007")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentVersion", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentVersion.Fields.language, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.HAS_INVALID_VALUE);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("O_001")
				&& StringUtils.equals(e.getParent(), "DocumentVersion")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Organization", error.get().getEntity());
		assertEquals("DocumentVersion", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Organization.Fields.organizationName, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("O_002")
				&& StringUtils.equals(e.getParent(), "DocumentVersion")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Organization", error.get().getEntity());
		assertEquals("DocumentVersion", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Organization.Fields.organizationOfficialName,
				error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DD_001")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentDescription", error.get().getEntity());
		assertEquals("DocumentVersion", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentDescription.Fields.language, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DD_003")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentDescription", error.get().getEntity());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentDescription.Fields.title, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DD_004")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentDescription", error.get().getEntity());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentDescription.Fields.summary, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("S_001")
				&& e.getEntity().equals("DocumentDescription")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentDescription", error.get().getEntity());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentDescription.Fields.keyWords, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("O_001")
				&& StringUtils.equals(e.getParent(), "LifeCycleStatus")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Organization", error.get().getEntity());
		assertEquals("LifeCycleStatus", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Organization.Fields.organizationName, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("O_002")
				&& StringUtils.equals(e.getParent(), "LifeCycleStatus")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Organization", error.get().getEntity());
		assertEquals("LifeCycleStatus", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Organization.Fields.organizationOfficialName,
				error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DF_004")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DigitalFile", error.get().getEntity());
		assertEquals("DocumentVersion", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DigitalFile.Fields.fileName, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DF_005")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DigitalFile", error.get().getEntity());
		assertEquals("DocumentVersion", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DigitalFile.Fields.fileFormat, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DV_003")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentVersion", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentVersion.Fields.digitalFile, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.HAS_INVALID_VALUE);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DC_005")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentClassification", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentClassification.Fields.classId, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("TS_001")
				&& StringUtils.equals(e.getParent(), "DocumentClassification")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("TranslatableString", error.get().getEntity());
		assertEquals("DocumentClassification", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(TranslatableString.Fields.text, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("TS_002")
				&& StringUtils.equals(e.getParent(), "DocumentClassification")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("TranslatableString", error.get().getEntity());
		assertEquals("DocumentClassification", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(TranslatableString.Fields.language, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DC_006")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentClassification", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentClassification.Fields.classificationSystem,
				error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("D_002")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Document", error.get().getEntity());
		assertTrue(StringUtils.isEmpty(error.get().getParent()));
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Document.Fields.documentClassification, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.HAS_INVALID_VALUE);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("D_003")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Document", error.get().getEntity());
		assertTrue(StringUtils.isEmpty(error.get().getParent()));
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Document.Fields.documentClassification, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_INCONSISTENT);
		assertSame(error.get().getLevel(), FaultLevel.INFORMATION);

		error = errors.stream().filter(e -> e.getMessage().startsWith("DID_002")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("DocumentIdDomain", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(DocumentIdDomain.Fields.documentDomainId, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("O_001")
				&& StringUtils.equals(e.getParent(), "Document")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Organization", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Organization.Fields.organizationName, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("O_002")
				&& StringUtils.equals(e.getParent(), "Document")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Organization", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Organization.Fields.organizationOfficialName,
				error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("OI_002")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("ObjectId", error.get().getEntity());
		assertEquals("ReferencedObject", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(ObjectId.Fields.id, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("O_001")
				&& StringUtils.equals(e.getParent(), "ReferencedObject")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Organization", error.get().getEntity());
		assertEquals("ReferencedObject", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Organization.Fields.organizationName, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("O_002")
				&& StringUtils.equals(e.getParent(), "ReferencedObject")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("Organization", error.get().getEntity());
		assertEquals("ReferencedObject", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(Organization.Fields.organizationOfficialName,
				error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("RO_002")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("ReferencedObject", error.get().getEntity());
		assertEquals("Document", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(ReferencedObject.Fields.party, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.HAS_INVALID_VALUE);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("TS_001")
				&& StringUtils.equals(e.getParent(), "ReferencedObject")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("TranslatableString", error.get().getEntity());
		assertEquals("ReferencedObject", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(TranslatableString.Fields.text, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(e -> e.getMessage().startsWith("TS_002")
				&& StringUtils.equals(e.getParent(), "ReferencedObject")).findFirst();
		assertTrue(error.isPresent());

		assertEquals("TranslatableString", error.get().getEntity());
		assertEquals("ReferencedObject", error.get().getParent());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(TranslatableString.Fields.language, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		error = errors.stream().filter(
				e -> e.getEntity().equals("ReferencedObject") && e.getMessage().startsWith("S_001"))
				.findFirst();
		assertTrue(error.isPresent());

		assertEquals("ReferencedObject", error.get().getEntity());
		assertEquals(1, error.get().getProperties().size());
		assertEquals(ReferencedObject.Fields.projectId, error.get().getProperties().get(0));
		assertSame(error.get().getType(), FaultType.IS_EMPTY);
		assertSame(error.get().getLevel(), FaultLevel.ERROR);

		ValidationFault empty = errors.stream().filter(
				e -> e.getEntity().equals("ReferencedObject") && e.getMessage().startsWith("S_001"))
				.toList().get(1);

		assertEquals("ReferencedObject", empty.getEntity());
		assertEquals(1, empty.getProperties().size());
		assertEquals(ReferencedObject.Fields.referenceDesignation, empty.getProperties().get(0));
		assertSame(empty.getType(), FaultType.IS_EMPTY);
		assertSame(empty.getLevel(), FaultLevel.ERROR);

		empty = errors.stream().filter(
				e -> e.getEntity().equals("ReferencedObject") && e.getMessage().startsWith("S_001"))
				.toList().get(2);

		assertEquals("ReferencedObject", empty.getEntity());
		assertEquals(1, empty.getProperties().size());
		assertEquals(ReferencedObject.Fields.equipmentId, empty.getProperties().get(0));
		assertSame(empty.getType(), FaultType.IS_EMPTY);
		assertSame(empty.getLevel(), FaultLevel.ERROR);
	}

}
