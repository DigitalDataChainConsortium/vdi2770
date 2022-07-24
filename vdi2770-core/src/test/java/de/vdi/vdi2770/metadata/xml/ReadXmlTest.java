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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import de.vdi.vdi2770.metadata.model.Constants;
import de.vdi.vdi2770.metadata.model.DigitalFile;
import de.vdi.vdi2770.metadata.model.Document;
import de.vdi.vdi2770.metadata.model.DocumentClassification;
import de.vdi.vdi2770.metadata.model.DocumentDescription;
import de.vdi.vdi2770.metadata.model.DocumentId;
import de.vdi.vdi2770.metadata.model.DocumentIdDomain;
import de.vdi.vdi2770.metadata.model.DocumentRelationship;
import de.vdi.vdi2770.metadata.model.DocumentRelationshipType;
import de.vdi.vdi2770.metadata.model.DocumentVersion;
import de.vdi.vdi2770.metadata.model.LifeCycleStatus;
import de.vdi.vdi2770.metadata.model.LifeCycleStatusValue;
import de.vdi.vdi2770.metadata.model.ObjectId;
import de.vdi.vdi2770.metadata.model.ObjectType;
import de.vdi.vdi2770.metadata.model.Organization;
import de.vdi.vdi2770.metadata.model.Party;
import de.vdi.vdi2770.metadata.model.RefType;
import de.vdi.vdi2770.metadata.model.ReferencedObject;
import de.vdi.vdi2770.metadata.model.Role;
import de.vdi.vdi2770.metadata.model.TranslatableString;
import de.vdi.vdi2770.metadata.model.ValidationFault;

/**
 * Tests for reading XML files.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class ReadXmlTest {

	private static final String EXAMPLES_FOLDER = "../examples/xml";

	private final Document xmlDocument;

	/**
	 * Standard ctor.
	 * <p>
	 * For setup, an XML file is read to provide input for the test methods.
	 * </p>
	 *
	 * @throws XmlProcessingException There was an error reading the metadata XML
	 *                                file.
	 */
	public ReadXmlTest() throws XmlProcessingException {

		final XmlReader reader = new XmlReader(Locale.getDefault());
		this.xmlDocument = reader.read(new File(EXAMPLES_FOLDER, "validation.xml"));
	}

	/**
	 * Validate a XML file.
	 */
	@Test
	public void validateXml() {

		final List<ValidationFault> errors = this.xmlDocument.validate(Locale.getDefault(), true);

		assertEquals(0, errors.size());
	}

	/**
	 * Validate document IDs read from XML file.
	 */
	@Test
	public void documentIdTest() {

		final List<DocumentId> documentIds = this.xmlDocument.getDocumentId();

		assertEquals(2, documentIds.size());

		final DocumentId firstDocument = documentIds.get(0);

		assertEquals(firstDocument.getDomainId(), "DemoDomain");
		assertTrue(firstDocument.getIsPrimary());
		assertEquals(firstDocument.getId(), "4711Demo");

		final DocumentId secondDocument = documentIds.get(1);

		assertEquals(secondDocument.getDomainId(), "MyDomain");
		assertFalse(secondDocument.getIsPrimary());
		assertEquals(secondDocument.getId(), "AB-8889-XY/40");
	}

	/**
	 * Validate document ID domain read from XML file.
	 */
	@Test
	public void documentIdDomainIdTest() {

		final List<DocumentIdDomain> domains = this.xmlDocument.getDocumentIdDomain();

		assertEquals(2, domains.size());

		final DocumentIdDomain firstDomain = domains.get(0);

		assertEquals(firstDomain.getDocumentDomainId(), "DemoDomain");
		assertNotNull(firstDomain.getParty());

		assertSame(firstDomain.getParty().getRole(), Role.Responsible);
		assertNotNull(firstDomain.getParty().getOrganization());

		final Organization firstOrganization = firstDomain.getParty().getOrganization();

		assertEquals(firstOrganization.getOrganizationId(), "ULE");
		assertEquals(firstOrganization.getOrganizationName(), "Uni Leipzig");
		assertEquals(firstOrganization.getOrganizationOfficialName(),
				"Universität Leipzig");

		final DocumentIdDomain secondDomain = domains.get(1);

		assertEquals(secondDomain.getDocumentDomainId(), "MyDomain");
		assertNotNull(secondDomain.getParty());

		assertSame(secondDomain.getParty().getRole(), Role.Responsible);
		assertNotNull(secondDomain.getParty().getOrganization());

		final Organization secondOrganization = secondDomain.getParty().getOrganization();

		assertEquals(secondOrganization.getOrganizationId(), "CUS");
		assertEquals(secondOrganization.getOrganizationName(), "Customer");
		assertEquals(secondOrganization.getOrganizationOfficialName(),
				"Customer GmbH");

	}

	/**
	 * Validate document classifications read from XML file.
	 */
	@Test
	public void documentClassificationTest() {

		final List<DocumentClassification> classifications = this.xmlDocument
				.getDocumentClassification();

		assertEquals(2, classifications.size());

		final DocumentClassification firstClassification = classifications.get(0);

		assertEquals(firstClassification.getClassificationSystem(),
				Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME);
		assertEquals(firstClassification.getClassId(), "03-01");
		assertEquals(2, firstClassification.getClassName().size());

		final TranslatableString firstName = firstClassification.getClassName().get(0);

		assertEquals(firstName.getLanguage(), "de");
		assertEquals(firstName.getText(), "Montage, Demontage");

		final TranslatableString secondName = firstClassification.getClassName().get(1);

		assertEquals(secondName.getLanguage(), "en");
		assertEquals(secondName.getText(), "Assembly, disassembly");

		final DocumentClassification secondClassification = classifications.get(1);

		assertEquals(secondClassification.getClassificationSystem(),
				Constants.IEC61355_CLASSIFICATION_NAME);
		assertEquals(secondClassification.getClassId(), "DD");
		assertEquals(2, secondClassification.getClassName().size());

	}

	/**
	 * Validate reference objects read from XML file.
	 */
	@Test
	public void referencedObjectTest() {

		final List<ReferencedObject> objects = this.xmlDocument.getReferencedObject();

		assertEquals(1, objects.size());

		final ReferencedObject o = objects.get(0);

		assertEquals(2, o.getReferenceDesignation().size());
		assertEquals(o.getReferenceDesignation().get(1), "#ABC 001 .99");

		assertEquals(1, o.getEquipmentId().size());
		assertEquals(o.getEquipmentId().get(0), "900.330.30/100");

		assertEquals(1, o.getProjectId().size());
		assertEquals(o.getProjectId().get(0), "P001");

		assertEquals(2, o.getParty().size());

		final Party firstParty = o.getParty().get(0);

		assertSame(firstParty.getRole(), Role.Manufacturer);
		assertNotNull(firstParty.getOrganization());

		final Organization firstOrganization = firstParty.getOrganization();

		assertEquals(firstOrganization.getOrganizationId(), "ULE");
		assertEquals(firstOrganization.getOrganizationName(), "Uni Leipzig");
		assertEquals(firstOrganization.getOrganizationOfficialName(), "Universität Leipzig");

		final Party secondParty = o.getParty().get(1);

		assertSame(secondParty.getRole(), Role.Supplier);
		assertNotNull(secondParty.getOrganization());

		final Organization secondOrganization = secondParty.getOrganization();

		assertEquals(secondOrganization.getOrganizationId(), "ACME");
		assertEquals(secondOrganization.getOrganizationName(), "ACME");
		assertEquals(secondOrganization.getOrganizationOfficialName(), "ACME INC");

		assertEquals(2, o.getDescription().size());

		final TranslatableString firstDescription = o.getDescription().get(0);

		assertEquals(firstDescription.getLanguage(), "en");
		assertEquals(firstDescription.getText(), "Product A");

		final TranslatableString secondDescription = o.getDescription().get(1);

		assertEquals(secondDescription.getLanguage(), "de");
		assertEquals(secondDescription.getText(), "Produkt A");

		assertEquals(2, o.getObjectId().size());

		final ObjectId firstId = o.getObjectId().get(0);

		assertSame(firstId.getObjectType(), ObjectType.Individual);
		assertEquals(firstId.getRefType(), RefType.SERIAL_NUMBER);
		assertEquals(firstId.getId(), "29389-2139292");

		final ObjectId secondId = o.getObjectId().get(1);

		assertSame(secondId.getObjectType(), ObjectType.Type);
		assertEquals(secondId.getRefType(), RefType.PRODUCT_TYPE);
		assertEquals(secondId.getId(), "4023/A");

	}

	/**
	 * Validate document versions read from XML file.
	 */
	@Test
	public void documentVersionTest() {

		final List<DocumentVersion> versions = this.xmlDocument.getDocumentVersion();

		assertEquals(1, versions.size());

		final DocumentVersion version = versions.get(0);

		assertEquals(version.getDocumentVersionId(), "1.0");
		assertEquals(100, version.getNumberOfPages().intValue());
		assertEquals(2, version.getLanguage().size());
		assertTrue(CollectionUtils.isEqualCollection(version.getLanguage(),
				Lists.newArrayList("de", "en")));

		assertEquals(1, version.getParty().size());

		final Party firstParty = version.getParty().get(0);

		assertSame(firstParty.getRole(), Role.Author);
		assertNotNull(firstParty.getOrganization());

		final Organization firstOrganization = firstParty.getOrganization();

		assertEquals(firstOrganization.getOrganizationId(), "ULE");
		assertEquals(firstOrganization.getOrganizationName(), "Uni Leipzig");
		assertEquals(firstOrganization.getOrganizationOfficialName(),
				"Universität Leipzig");

		assertEquals(2, version.getDigitalFile().size());

		final DigitalFile firstFile = version.getDigitalFile().get(0);

		assertEquals(firstFile.getFileName(), "test.pdf");
		assertEquals(firstFile.getFileFormat(), "application/pdf");

		final DigitalFile secondFile = version.getDigitalFile().get(1);

		assertEquals(secondFile.getFileName(), "test.xls");
		assertEquals(secondFile.getFileFormat(), "application/vnd.ms-excel");

		assertNotNull(version.getLifeCycleStatus());

		final LifeCycleStatus status = version.getLifeCycleStatus();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate expectedDate = LocalDate.parse("2019-05-07", formatter);

		assertEquals(status.getSetDate(), expectedDate);
		assertSame(status.getStatusValue(), LifeCycleStatusValue.Released);
		assertEquals(1, status.getParty().size());

		final Party statusParty = status.getParty().get(0);

		assertSame(statusParty.getRole(), Role.Responsible);
		assertNotNull(statusParty.getOrganization());

		final Organization statusOrganization = statusParty.getOrganization();

		assertEquals(statusOrganization.getOrganizationId(), "ULE");
		assertEquals(statusOrganization.getOrganizationName(), "Uni Leipzig");
		assertEquals(statusOrganization.getOrganizationOfficialName(),
				"Universität Leipzig");

		assertEquals(2, status.getComments().size());

		final TranslatableString firstCommment = status.getComments().get(0);

		assertEquals(firstCommment.getLanguage(), "de");
		assertEquals(firstCommment.getText(),
				"Die Version ist frei erfunden, aber freigegeben.");

		assertEquals(2, version.getDocumentDescription().size());

		final DocumentDescription firstDesc = version.getDocumentDescription().get(0);

		assertEquals(firstDesc.getLanguage(), "de");
		assertEquals(firstDesc.getTitle(), "Demo Bericht");
		assertEquals(firstDesc.getSubTitle(),
				"Ein kleines Beispiel für einen Untertitel");
		assertEquals(firstDesc.getSummary(),
				"Dies ist eine Zusammenfassung in deutsch für den beispielhaften Demo Bericht");
		assertEquals(2, firstDesc.getKeyWords().size());
		assertTrue(CollectionUtils.isEqualCollection(firstDesc.getKeyWords(),
				Arrays.asList("Test", "Demo")));

		final DocumentDescription secondDesc = version.getDocumentDescription().get(1);

		assertEquals(secondDesc.getLanguage(), "en");
		assertEquals(secondDesc.getTitle(), "Demo Report");
		assertEquals(secondDesc.getSubTitle(), "A short example for sub titles");
		assertEquals(secondDesc.getSummary(),
				"This is a summary in english for the Demo Report document");
		assertEquals(2, secondDesc.getKeyWords().size());
		assertTrue(CollectionUtils.isEqualCollection(secondDesc.getKeyWords(),
				Arrays.asList("Test", "Demo")));

		assert version.getDocumentRelationship().size() == 2;

		final List<DocumentRelationship> relations = version.getDocumentRelationship();

		final DocumentRelationship firstRel = relations.get(0);

		assertSame(firstRel.getType(), DocumentRelationshipType.RefersTo);

		assertNotNull(firstRel.getDocumentId());

		assertNotNull(firstRel.getDocumentVersionId());
		assertTrue(firstRel.getDocumentVersionId().isEmpty());

		final DocumentId firstDocumentId = firstRel.getDocumentId();

		assertEquals(firstDocumentId.getId(), "449890");
		assertEquals(firstDocumentId.getDomainId(), "DemoDomain");

		assertEquals(2, firstRel.getDescription().size());

		assertEquals(firstRel.getDescription().get(0).getLanguage(), "de");
		assertEquals(firstRel.getDescription().get(0).getText(),
				"Dies ist eine Dokumentenbeziehung, die ausschließlich auf eine Dokument ID verweist.");

		final DocumentRelationship secondRel = relations.get(1);

		assertSame(secondRel.getType(), DocumentRelationshipType.Affecting);

		assertNotNull(secondRel.getDocumentId());

		assertEquals(2, secondRel.getDocumentVersionId().size());
		assertTrue(CollectionUtils.isEqualCollection(secondRel.getDocumentVersionId(),
				Arrays.asList("1.0", "2.0")));

		final DocumentId secondDocumentId = secondRel.getDocumentId();

		assertEquals(secondDocumentId.getId(), "9994/22029");
		assertEquals(secondDocumentId.getDomainId(), "MyDomain");

		assertEquals(1, secondRel.getDescription().size());

		assertEquals(secondRel.getDescription().get(0).getLanguage(), "de");
		assertEquals(secondRel.getDescription().get(0).getText(),
				"Diese Beziehung gilt für genau zwei Dokumentversion eines Dokumentes.");

	}

}
