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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

		final List<ValidationFault> errors = this.xmlDocument.validate(Locale.getDefault());

		assertTrue(errors.size() == 0);
	}

	/**
	 * Validate document IDs read from XML file.
	 */
	@Test
	public void documentIdTest() {

		final List<DocumentId> documentIds = this.xmlDocument.getDocumentId();

		assertTrue(documentIds.size() == 2);

		final DocumentId firstDocument = documentIds.get(0);

		assertTrue(StringUtils.equals(firstDocument.getDomainId(), "DemoDomain"));
		assertTrue(firstDocument.getIsPrimary().booleanValue() == true);
		assertTrue(StringUtils.equals(firstDocument.getId(), "4711Demo"));

		final DocumentId secondDocument = documentIds.get(1);

		assertTrue(StringUtils.equals(secondDocument.getDomainId(), "MyDomain"));
		assertTrue(secondDocument.getIsPrimary().booleanValue() == false);
		assertTrue(StringUtils.equals(secondDocument.getId(), "AB-8889-XY/40"));
	}

	/**
	 * Validate document ID domain read from XML file.
	 */
	@Test
	public void documentIdDomainIdTest() {

		final List<DocumentIdDomain> domains = this.xmlDocument.getDocumentIdDomain();

		assertTrue(domains.size() == 2);

		final DocumentIdDomain firstDomain = domains.get(0);

		assertTrue(StringUtils.equals(firstDomain.getDocumentDomainId(), "DemoDomain"));
		assertTrue(firstDomain.getParty() != null);

		assertTrue(firstDomain.getParty().getRole() == Role.Responsible);
		assertTrue(firstDomain.getParty().getOrganization() != null);

		final Organization firstOrganization = firstDomain.getParty().getOrganization();

		assertTrue(StringUtils.equals(firstOrganization.getOrganizationId(), "ULE"));
		assertTrue(StringUtils.equals(firstOrganization.getOrganizationName(), "Uni Leipzig"));
		assertTrue(StringUtils.equals(firstOrganization.getOrganizationOfficialName(),
				"Universität Leipzig"));

		final DocumentIdDomain secondDomain = domains.get(1);

		assertTrue(StringUtils.equals(secondDomain.getDocumentDomainId(), "MyDomain"));
		assertTrue(secondDomain.getParty() != null);

		assertTrue(secondDomain.getParty().getRole() == Role.Responsible);
		assertTrue(secondDomain.getParty().getOrganization() != null);

		final Organization secondOrganization = secondDomain.getParty().getOrganization();

		assertTrue(StringUtils.equals(secondOrganization.getOrganizationId(), "CUS"));
		assertTrue(StringUtils.equals(secondOrganization.getOrganizationName(), "Customer"));
		assertTrue(StringUtils.equals(secondOrganization.getOrganizationOfficialName(),
				"Customer GmbH"));

	}

	/**
	 * Validate document classifications read from XML file.
	 */
	@Test
	public void documentClassificationTest() {

		final List<DocumentClassification> classifications = this.xmlDocument
				.getDocumentClassification();

		assertTrue(classifications.size() == 2);

		final DocumentClassification firstClassification = classifications.get(0);

		assertTrue(StringUtils.equals(firstClassification.getClassificationSystem(),
				Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME));
		assertTrue(StringUtils.equals(firstClassification.getClassId(), "03-01"));
		assertTrue(firstClassification.getClassName().size() == 2);

		final TranslatableString firstName = firstClassification.getClassName().get(0);

		assertTrue(StringUtils.equals(firstName.getLanguage(), "de"));
		assertTrue(StringUtils.equals(firstName.getText(), "Montage, Inbetriebnahme, Demontage"));

		final TranslatableString secondName = firstClassification.getClassName().get(1);

		assertTrue(StringUtils.equals(secondName.getLanguage(), "en"));
		assertTrue(StringUtils.equals(secondName.getText(), "assembly, disassembly"));

		final DocumentClassification secondClassification = classifications.get(1);

		assertTrue(StringUtils.equals(secondClassification.getClassificationSystem(),
				Constants.IEC61355_CLASSIFICATION_NAME));
		assertTrue(StringUtils.equals(secondClassification.getClassId(), "DD"));
		assertTrue(secondClassification.getClassName().size() == 2);

	}

	/**
	 * Validate reference objects read from XML file.
	 */
	@Test
	public void referencedObjectTest() {

		final List<ReferencedObject> objects = this.xmlDocument.getReferencedObject();

		assertTrue(objects.size() == 1);

		final ReferencedObject o = objects.get(0);

		assertTrue(o.getReferenceDesignation().size() == 2);
		assertTrue(StringUtils.equals(o.getReferenceDesignation().get(1), "#ABC 001 .99"));

		assertTrue(o.getEquipmentId().size() == 1);
		assertTrue(StringUtils.equals(o.getEquipmentId().get(0), "900.330.30/100"));

		assertTrue(o.getProjectId().size() == 1);
		assertTrue(StringUtils.equals(o.getProjectId().get(0), "P001"));

		assertTrue(o.getParty().size() == 2);

		final Party firstParty = o.getParty().get(0);

		assertTrue(firstParty.getRole() == Role.Manufacturer);
		assertTrue(firstParty.getOrganization() != null);

		final Organization firstOrganization = firstParty.getOrganization();

		assertTrue(StringUtils.equals(firstOrganization.getOrganizationId(), "ULE"));
		assertTrue(StringUtils.equals(firstOrganization.getOrganizationName(), "Uni Leipzig"));
		assertTrue(StringUtils.equals(firstOrganization.getOrganizationOfficialName(),
				"Universität Leipzig"));

		final Party secondParty = o.getParty().get(1);

		assertTrue(secondParty.getRole() == Role.Supplier);
		assertTrue(secondParty.getOrganization() != null);

		final Organization secondOrganization = secondParty.getOrganization();

		assertTrue(StringUtils.equals(secondOrganization.getOrganizationId(), "ACME"));
		assertTrue(StringUtils.equals(secondOrganization.getOrganizationName(), "ACME"));
		assertTrue(
				StringUtils.equals(secondOrganization.getOrganizationOfficialName(), "ACME INC"));

		assertTrue(o.getDescription().size() == 2);

		final TranslatableString firstDescription = o.getDescription().get(0);

		assertTrue(StringUtils.equals(firstDescription.getLanguage(), "en"));
		assertTrue(StringUtils.equals(firstDescription.getText(), "Product A"));

		final TranslatableString secondDescription = o.getDescription().get(1);

		assertTrue(StringUtils.equals(secondDescription.getLanguage(), "de"));
		assertTrue(StringUtils.equals(secondDescription.getText(), "Produkt A"));

		assertTrue(o.getObjectId().size() == 2);

		final ObjectId firstId = o.getObjectId().get(0);

		assertTrue(firstId.getObjectType() == ObjectType.Individual);
		assertTrue(StringUtils.equals(firstId.getRefType(), RefType.SERIAL_NUMBER));
		assertTrue(StringUtils.equals(firstId.getId(), "29389-2139292"));

		final ObjectId secondId = o.getObjectId().get(1);

		assertTrue(secondId.getObjectType() == ObjectType.Type);
		assertTrue(StringUtils.equals(secondId.getRefType(), RefType.PRODUCT_TYPE));
		assertTrue(StringUtils.equals(secondId.getId(), "4023/A"));

	}

	/**
	 * Validate document versions read from XML file.
	 *
	 * @throws ParseException
	 */
	@Test
	public void documentVersionTest() {

		final List<DocumentVersion> versions = this.xmlDocument.getDocumentVersion();

		assertTrue(versions.size() == 1);

		final DocumentVersion version = versions.get(0);

		assertTrue(StringUtils.equals(version.getDocumentVersionId(), "1.0"));
		assertTrue(version.getNumberOfPages().intValue() == 100);
		assertTrue(version.getLanguage().size() == 2);
		assertTrue(CollectionUtils.isEqualCollection(version.getLanguage(),
				Lists.newArrayList("de", "en")));

		assertTrue(version.getParty().size() == 1);

		final Party firstParty = version.getParty().get(0);

		assertTrue(firstParty.getRole() == Role.Author);
		assertTrue(firstParty.getOrganization() != null);

		final Organization firstOrganization = firstParty.getOrganization();

		assertTrue(StringUtils.equals(firstOrganization.getOrganizationId(), "ULE"));
		assertTrue(StringUtils.equals(firstOrganization.getOrganizationName(), "Uni Leipzig"));
		assertTrue(StringUtils.equals(firstOrganization.getOrganizationOfficialName(),
				"Universität Leipzig"));

		assertTrue(version.getDigitalFile().size() == 2);

		final DigitalFile firstFile = version.getDigitalFile().get(0);

		assertTrue(StringUtils.equals(firstFile.getFileName(), "test.pdf"));
		assertTrue(StringUtils.equals(firstFile.getFileFormat(), "application/pdf"));

		final DigitalFile secondFile = version.getDigitalFile().get(1);

		assertTrue(StringUtils.equals(secondFile.getFileName(), "test.xls"));
		assertTrue(StringUtils.equals(secondFile.getFileFormat(), "application/vnd.ms-excel"));

		assertTrue(version.getLifeCycleStatus() != null);

		final LifeCycleStatus status = version.getLifeCycleStatus();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate expectedDate = LocalDate.parse("2019-05-07", formatter);

		assertTrue(status.getSetDate().equals(expectedDate));
		assertTrue(status.getStatusValue() == LifeCycleStatusValue.Released);
		assertTrue(status.getParty().size() == 1);

		final Party statusParty = status.getParty().get(0);

		assertTrue(statusParty.getRole() == Role.Responsible);
		assertTrue(statusParty.getOrganization() != null);

		final Organization statusOrganization = statusParty.getOrganization();

		assertTrue(StringUtils.equals(statusOrganization.getOrganizationId(), "ULE"));
		assertTrue(StringUtils.equals(statusOrganization.getOrganizationName(), "Uni Leipzig"));
		assertTrue(StringUtils.equals(statusOrganization.getOrganizationOfficialName(),
				"Universität Leipzig"));

		assertTrue(status.getComments().size() == 2);

		final TranslatableString firstCommment = status.getComments().get(0);

		assertTrue(StringUtils.equals(firstCommment.getLanguage(), "de"));
		assertTrue(StringUtils.equals(firstCommment.getText(),
				"Die Version ist frei erfunden, aber freigegeben."));

		assertTrue(version.getDocumentDescription().size() == 2);

		final DocumentDescription firstDesc = version.getDocumentDescription().get(0);

		assertTrue(StringUtils.equals(firstDesc.getLanguage(), "de"));
		assertTrue(StringUtils.equals(firstDesc.getTitle(), "Demo Bericht"));
		assertTrue(StringUtils.equals(firstDesc.getSubTitle(),
				"Ein kleines Beispiel für einen Untertitel"));
		assertTrue(StringUtils.equals(firstDesc.getSummary(),
				"Dies ist eine Zusammenfassung in deutsch für den beispielhaften Demo Bericht"));
		assertTrue(firstDesc.getKeyWords().size() == 2);
		assertTrue(CollectionUtils.isEqualCollection(firstDesc.getKeyWords(),
				Arrays.asList("Test", "Demo")));

		final DocumentDescription secondDesc = version.getDocumentDescription().get(1);

		assertTrue(StringUtils.equals(secondDesc.getLanguage(), "en"));
		assertTrue(StringUtils.equals(secondDesc.getTitle(), "Demo Report"));
		assertTrue(StringUtils.equals(secondDesc.getSubTitle(), "A short example for sub titles"));
		assertTrue(StringUtils.equals(secondDesc.getSummary(),
				"This is a summary in english for the Demo Report document"));
		assertTrue(secondDesc.getKeyWords().size() == 2);
		assertTrue(CollectionUtils.isEqualCollection(secondDesc.getKeyWords(),
				Arrays.asList("Test", "Demo")));

		assert version.getDocumentRelationship().size() == 2;

		final List<DocumentRelationship> relations = version.getDocumentRelationship();

		final DocumentRelationship firstRel = relations.get(0);

		assertTrue(firstRel.getType() == DocumentRelationshipType.RefersTo);

		assertTrue(firstRel.getDocumentId() != null);

		assertTrue(firstRel.getDocumentVersionId() != null);
		assertTrue(firstRel.getDocumentVersionId().isEmpty());

		final DocumentId firstDocumentId = firstRel.getDocumentId();

		assertTrue(StringUtils.equals(firstDocumentId.getId(), "449890"));
		assertTrue(StringUtils.equals(firstDocumentId.getDomainId(), "DemoDomain"));

		assertTrue(firstRel.getDescription().size() == 2);

		assertTrue(StringUtils.equals(firstRel.getDescription().get(0).getLanguage(), "de"));
		assertTrue(StringUtils.equals(firstRel.getDescription().get(0).getText(),
				"Dies ist eine Dokumentenbeziehung, die ausschließlich auf eine Dokument ID verweist."));

		final DocumentRelationship secondRel = relations.get(1);

		assertTrue(secondRel.getType() == DocumentRelationshipType.Affecting);

		assertTrue(secondRel.getDocumentId() != null);

		assertTrue(secondRel.getDocumentVersionId().size() == 2);
		assertTrue(CollectionUtils.isEqualCollection(secondRel.getDocumentVersionId(),
				Arrays.asList("1.0", "2.0")));

		final DocumentId secondDocumentId = secondRel.getDocumentId();

		assertTrue(StringUtils.equals(secondDocumentId.getId(), "9994/22029"));
		assertTrue(StringUtils.equals(secondDocumentId.getDomainId(), "MyDomain"));

		assertTrue(secondRel.getDescription().size() == 1);

		assertTrue(StringUtils.equals(secondRel.getDescription().get(0).getLanguage(), "de"));
		assertTrue(StringUtils.equals(secondRel.getDescription().get(0).getText(),
				"Diese Beziehung gilt für genau zwei Dokumentversion eines Dokumentes."));

	}

}
