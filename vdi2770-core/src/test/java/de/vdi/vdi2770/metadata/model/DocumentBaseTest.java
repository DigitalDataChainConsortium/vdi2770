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
import java.util.Arrays;
import java.util.List;

import com.google.common.net.MediaType;

class DocumentBaseTest {

	public Document getTestDocument() {

		final Document doc = new Document();

		doc.addDocumentId(getTestDocumentId());
		doc.addDocumentIdDomain(getTestDocumentIdDomain());
		doc.setDocumentClassification(getTestDocumentClassification());
		doc.addReferencedObject(getTestReferencedObject());
		doc.addDocumentVersion(getTestDocumentVersion());

		return doc;
	}

	protected List<DocumentClassification> getTestDocumentClassification() {

		final DocumentClassification classification = new DocumentClassification();

		classification.setClassificationSystem(Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME);
		classification.setClassId("03-01");

		final TranslatableString germanName = new TranslatableString();
		germanName.setLanguage("de");
		germanName.setText(
				Constants.getVdi2770GermanCategoryNames().get(Constants.VDI2770_MOUNT_CATEGORY));

		classification.addClassName(germanName);

		final TranslatableString englishName = new TranslatableString();
		englishName.setLanguage("en");
		englishName.setText("Assembly, disassembly");

		classification.addClassName(englishName);

		final DocumentClassification iecClassification = new DocumentClassification();

		iecClassification.setClassificationSystem("IEC61355");
		iecClassification.setClassId("DD");

		return Arrays.asList(iecClassification, classification);
	}

	protected DocumentVersion getTestDocumentVersion() {

		final DocumentVersion version = new DocumentVersion();

		version.setDocumentVersionId("1.0");

		version.addLanguage("de");
		version.addLanguage("en");

		version.setNumberOfPages(Integer.valueOf(100));

		version.addParty(getTestAuthor());
		version.setDocumentDescription(getTestDescriptions());
		version.setLifeCycleStatus(getTestLifeCycleStatus());
		version.setDocumentRelationship(getTestDocumentRelationships());
		version.setDigitalFile(getTestDigitalFiles());

		return version;
	}

	protected List<DocumentRelationship> getTestDocumentRelationships() {

		final DocumentRelationship rel = new DocumentRelationship();

		final DocumentId id = new DocumentId();
		id.setDomainId("DemoDomain");
		id.setId("449890");

		rel.setDocumentId(id);
		rel.setType(DocumentRelationshipType.RefersTo);

		final DocumentRelationship rel1 = new DocumentRelationship();

		final DocumentId id1 = new DocumentId();
		id1.setDomainId("AnotherID");
		id1.setId("ABCABSKDJWIIE  SADLKSAJD #12901823");

		rel1.setDocumentId(id1);
		rel1.addDocumentVersionId("1.0");
		rel1.addDocumentVersionId("2.0");
		rel1.setType(DocumentRelationshipType.RefersTo);

		return Arrays.asList(rel, rel1);
	}

	protected LifeCycleStatus getTestLifeCycleStatus() {

		final LifeCycleStatus status = new LifeCycleStatus();

		status.setSetDate(LocalDate.now());
		status.setStatusValue(LifeCycleStatusValue.Released);

		final Party author = getTestResponsible();
		status.addParty(author);

		return status;
	}

	protected ReferencedObject getTestReferencedObject() {

		final ReferencedObject o = new ReferencedObject();

		final TranslatableString description = new TranslatableString();
		description.setLanguage("en");
		description.setText("Product A");
		o.addDescription(description);

		final ObjectId oid = new ObjectId();
		oid.setId("29389-2139292");
		oid.setObjectType(ObjectType.Individual);

		o.addObjectId(oid);

		final Party manufacturer = new Party();
		manufacturer.setRole(Role.Manufacturer);

		final Organization orga = getUniversityOfLeipzigOrganization();
		manufacturer.setOrganization(orga);

		o.addParty(manufacturer);

		return o;
	}

	protected List<DigitalFile> getTestDigitalFiles() {

		final DigitalFile pdfFile = new DigitalFile();
		pdfFile.setFileFormat(MediaType.PDF.toString());
		pdfFile.setFileName("test.pdf");

		final DigitalFile xlsFile = new DigitalFile();
		xlsFile.setFileFormat("application/vnd.ms-excel");
		xlsFile.setFileName("test.xls");

		return Arrays.asList(pdfFile, xlsFile);
	}

	protected Party getTestAuthor() {

		final Party author = new Party();

		final Organization authorOrganisation = getUniversityOfLeipzigOrganization();
		author.setOrganization(authorOrganisation);
		author.setRole(Role.Author);

		return author;
	}

	protected Party getTestResponsible() {

		final Party author = new Party();

		final Organization authorOrganisation = getUniversityOfLeipzigOrganization();
		author.setOrganization(authorOrganisation);
		author.setRole(Role.Responsible);

		return author;
	}

	protected List<DocumentDescription> getTestDescriptions() {

		final DocumentDescription germanDesc = new DocumentDescription();

		germanDesc.setLanguage("de");
		germanDesc.setTitle("Demo Bericht");
		germanDesc.setSubTitle("Ein kleines Beispiel für einen Untertitel");
		germanDesc.setSummary(
				"Dies ist eine Zusammenfassung in deutsch für den beispielhaften Demo Bericht");
		germanDesc.addKeyWord("Test");
		germanDesc.addKeyWord("Demo");

		final DocumentDescription englishDesc = new DocumentDescription();

		englishDesc.setLanguage("en");
		englishDesc.setTitle("Demo Report");
		englishDesc.setSubTitle("A short example for sub titles");
		englishDesc.setSummary("This is a summary in english for the Demo Report document");
		englishDesc.addKeyWord("Test");
		englishDesc.addKeyWord("Demo");

		return Arrays.asList(germanDesc, englishDesc);
	}

	protected Organization getUniversityOfLeipzigOrganization() {

		final Organization orga = new Organization();
		orga.setOrganizationId("ULE");
		orga.setOrganizationName("Uni Leipzig");
		orga.setOrganizationOfficialName("Universität Leipzig");

		return orga;
	}

	private DocumentIdDomain getTestDocumentIdDomain() {

		final DocumentIdDomain domain = new DocumentIdDomain();

		domain.setDocumentDomainId("DemoDomain");

		final Party party = getTestResponsible();
		domain.setParty(party);

		return domain;
	}

	protected DocumentId getTestDocumentId() {

		final DocumentId id = new DocumentId();
		id.setDomainId("DemoDomain");
		id.setIsPrimary(Boolean.TRUE);
		id.setId("4711Demo");

		return id;
	}

}
