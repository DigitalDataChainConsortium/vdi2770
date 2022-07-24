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

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import de.vdi.vdi2770.metadata.xsd.DigitalFile;
import de.vdi.vdi2770.metadata.xsd.Document;
import de.vdi.vdi2770.metadata.xsd.DocumentClassification;
import de.vdi.vdi2770.metadata.xsd.DocumentDescription;
import de.vdi.vdi2770.metadata.xsd.DocumentIdDomain;
import de.vdi.vdi2770.metadata.xsd.DocumentRelationship;
import de.vdi.vdi2770.metadata.xsd.DocumentVersion;
import de.vdi.vdi2770.metadata.xsd.LifeCycleStatus;
import de.vdi.vdi2770.metadata.xsd.LifeCycleStatus.Comments;
import de.vdi.vdi2770.metadata.xsd.ReferencedObject;

import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;

import de.vdi.vdi2770.metadata.MetadataException;
import de.vdi.vdi2770.metadata.model.Constants;
import de.vdi.vdi2770.metadata.model.DocumentRelationshipType;
import de.vdi.vdi2770.metadata.model.LifeCycleStatusValue;
import de.vdi.vdi2770.metadata.model.ObjectType;
import de.vdi.vdi2770.metadata.model.RefType;
import de.vdi.vdi2770.metadata.model.Role;

/**
 * </p>
 * This class implements an XML {@link Document} structure for demonstration
 * purposes.
 * </p>
 * <p>
 * This class is only intended to demonstrate object creations and relations.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class DemoXml {

	private final Locale locale;

	/**
	 * ctor
	 *
	 * @param locale Desired {@link Locale} for validation messages.
	 */
	public DemoXml(final Locale locale) {
		Preconditions.checkArgument(locale != null);

		this.locale = (Locale) locale.clone();
	}

	/**
	 * Create a new XML file that conforms to the requirements of VDI 2770.
	 *
	 * @param outputFile XML file to create.
	 * @throws MetadataException There was an error creating the XML file.
	 */
	public void createXmlFile(final File outputFile) throws MetadataException {
		createXmlFile(outputFile, false);
	}

	/**
	 * Create a new XML file that conforms to the requirements of VDI 2770.
	 *
	 * @param outputFile XML file to create
	 * @param exportXsd  Create XSD file locally to ensure validation.
	 * @throws MetadataException There was an error creating the XML file.
	 */
	public void createXmlFile(final File outputFile, final boolean exportXsd)
			throws MetadataException {

		Preconditions.checkArgument(outputFile != null);

		// Document is the root of the XML file
		final Document doc = createDocument();

		XmlUtils xmlUtils = new XmlUtils(this.locale);
		xmlUtils.saveAsXml(outputFile, doc, exportXsd);
	}

	/**
	 * Get a demo document as POJO that conforms to VDI 2770 specification. The
	 * POJOs are annotated to create XML objects.
	 *
	 * @return An instance of {@link Document} that is filled with demo values.
	 */
	public Document createDocument() {

		// Document is the root of the XML file
		final Document doc = new Document();

		// generate a document id
		createDocumentId(doc);
		// generate a document id domain
		createDocumentIdDomain(doc);
		// set VDI2770:2018 example classification
		createDocumentClassification(doc);
		// generate a dummy object
		createReferencedObject(doc);
		// generate the document version
		createDocumentVersion(doc);

		return doc;
	}

	private static void createDocumentVersion(final Document doc) {

		final DocumentVersion version = new DocumentVersion();

		// Document version id is a simple string
		version.setDocumentVersionId("1.0");

		// two languages are used in the document
		version.getLanguage().add("de");
		version.getLanguage().add("en");

		// demo number of pages
		version.setNumberOfPages(BigInteger.valueOf(100));

		createParty(version);
		createDescription(version);
		createLifeCycleStatus(version);
		createDocumentRelationship(version);
		createDigitalFile(version);

		doc.getDocumentVersion().add(version);

	}

	private static void createDocumentRelationship(final DocumentVersion version) {

		final DocumentRelationship rel = new DocumentRelationship();

		final DocumentRelationship.DocumentId id = new DocumentRelationship.DocumentId();
		id.setDomainId("DemoDomain");
		id.setValue("449890");

		rel.setDocumentId(id);
		rel.setType(DocumentRelationshipType.RefersTo.toString());

		final DocumentRelationship.Description germanDescription = new DocumentRelationship.Description();
		germanDescription.setLanguage("de");
		germanDescription.setValue(
				"Dies ist eine Dokumentenbeziehung, die ausschließlich auf eine Dokument ID verweist.");

		rel.getDescription().add(germanDescription);

		final DocumentRelationship.Description englishDescription = new DocumentRelationship.Description();
		englishDescription.setLanguage("en");
		englishDescription
				.setValue("This is a document relationship, that only refers to a document id.");

		rel.getDescription().add(englishDescription);

		version.getDocumentRelationship().add(rel);

		final DocumentRelationship rel1 = new DocumentRelationship();

		final DocumentRelationship.DocumentId id1 = new DocumentRelationship.DocumentId();
		id1.setDomainId("MyDomain");
		id1.setValue("9994/22029");

		rel1.setDocumentId(id1);
		rel1.getDocumentVersionId().add("1.0");
		rel1.getDocumentVersionId().add("2.0");
		rel1.setType(DocumentRelationshipType.RefersTo.toString());

		final DocumentRelationship.Description additionalDescription = new DocumentRelationship.Description();
		additionalDescription.setLanguage("de");
		additionalDescription
				.setValue("Diese Beziehung gilt für genau zwei Dokumentversion eines Dokumentes.");

		rel1.getDescription().add(additionalDescription);

		version.getDocumentRelationship().add(rel1);
	}

	private static void createLifeCycleStatus(final DocumentVersion version) {

		final LifeCycleStatus status = new LifeCycleStatus();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse("2019-05-07", formatter);
		status.setSetDate(date);
		status.setStatusValue(LifeCycleStatusValue.Released.toString());

		final LifeCycleStatus.Party author = new LifeCycleStatus.Party();
		author.setRole(Role.Responsible.toString());

		final LifeCycleStatus.Party.Organization orga = new LifeCycleStatus.Party.Organization();
		orga.setOrganizationId("ULE");
		orga.setOrganizationName("Uni Leipzig");
		orga.setOrganizationOfficialName("Universität Leipzig");
		author.setOrganization(orga);

		status.getParty().add(author);

		final Comments germanComment = new LifeCycleStatus.Comments();
		germanComment.setLanguage("de");
		germanComment.setValue("Die Version ist frei erfunden, aber freigegeben.");
		status.getComments().add(germanComment);

		final Comments englishComment = new LifeCycleStatus.Comments();
		englishComment.setLanguage("en");
		englishComment.setValue("This milestone is just for testing purposes");
		status.getComments().add(englishComment);

		version.setLifeCycleStatus(status);
	}

	private static void createReferencedObject(final Document doc) {

		final ReferencedObject o = new ReferencedObject();

		final ReferencedObject.Description englishDescription = new ReferencedObject.Description();
		englishDescription.setLanguage("en");
		englishDescription.setValue("Product A");
		o.getDescription().add(englishDescription);

		final ReferencedObject.Description germanDescription = new ReferencedObject.Description();
		germanDescription.setLanguage("de");
		germanDescription.setValue("Produkt A");
		o.getDescription().add(germanDescription);

		o.getReferenceDesignation().add("#ABC 001");
		o.getProjectId().add("P001");
		o.getEquipmentId().add("900.330.30/100");

		final ReferencedObject.ObjectId serialId = new ReferencedObject.ObjectId();
		serialId.setValue("29389-2139292");
		serialId.setObjectType(ObjectType.Individual.toString());
		serialId.setRefType(RefType.SERIAL_NUMBER);

		o.getObjectId().add(serialId);

		final ReferencedObject.ObjectId productType = new ReferencedObject.ObjectId();
		productType.setValue("4023/A");
		productType.setObjectType(ObjectType.Type.toString());
		productType.setRefType(RefType.PRODUCT_TYPE);

		o.getObjectId().add(productType);

		final ReferencedObject.Party manufacturer = new ReferencedObject.Party();
		manufacturer.setRole(Role.Manufacturer.toString());

		final ReferencedObject.Party.Organization orga = new ReferencedObject.Party.Organization();
		orga.setOrganizationId("ULE");
		orga.setOrganizationName("Uni Leipzig");
		orga.setOrganizationOfficialName("Universität Leipzig");
		manufacturer.setOrganization(orga);

		o.getParty().add(manufacturer);

		doc.getReferencedObject().add(o);
	}

	private static void createDigitalFile(final DocumentVersion version) {

		final DigitalFile pdfFile = new DigitalFile();
		pdfFile.setFileFormat(MediaType.PDF.toString());
		pdfFile.setValue("test.pdf");

		version.getDigitalFile().add(pdfFile);

		final DigitalFile xlsFile = new DigitalFile();
		xlsFile.setFileFormat("application/vnd.ms-excel");
		xlsFile.setValue("test.xls");

		version.getDigitalFile().add(xlsFile);
	}

	private static void createParty(final DocumentVersion version) {

		final DocumentVersion.Party author = new DocumentVersion.Party();

		final DocumentVersion.Party.Organization authorOrganisation = new DocumentVersion.Party.Organization();
		authorOrganisation.setOrganizationId("ULE");
		authorOrganisation.setOrganizationName("Uni Leipzig");
		authorOrganisation.setOrganizationOfficialName("Universität Leipzig");
		author.setOrganization(authorOrganisation);

		author.setRole(Role.Author.toString());

		version.getParty().add(author);
	}

	private static void createDescription(final DocumentVersion version) {

		final DocumentDescription germanDesc = new DocumentDescription();

		germanDesc.setLanguage("de");
		germanDesc.setTitle("Demo Bericht");
		germanDesc.setSubTitle("Ein kleines Beispiel für einen Untertitel");
		germanDesc.setSummary(
				"Dies ist eine Zusammenfassung in deutsch für den beispielhaften Demo Bericht");

		final DocumentDescription.KeyWords germanKeyWords = new DocumentDescription.KeyWords();
		for (final String keyWord : Arrays.asList("Test", "Demo")) {
			germanKeyWords.getKeyWord().add(keyWord);
		}
		germanDesc.setKeyWords(germanKeyWords);

		version.getDocumentDescription().add(germanDesc);

		final DocumentDescription englishDesc = new DocumentDescription();

		englishDesc.setLanguage("en");
		englishDesc.setTitle("Demo Report");
		englishDesc.setSubTitle("A short example for sub titles");
		englishDesc.setSummary("This is a summary in english for the Demo Report document");

		final DocumentDescription.KeyWords englishKeyWords = new DocumentDescription.KeyWords();
		for (final String keyWord : Arrays.asList("Test", "Demo")) {
			englishKeyWords.getKeyWord().add(keyWord);
		}
		englishDesc.setKeyWords(englishKeyWords);

		version.getDocumentDescription().add(englishDesc);
	}

	private static void createDocumentClassification(final Document doc) {
		final DocumentClassification classification = new DocumentClassification();

		classification.setClassificationSystem(Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME);
		classification.setClassId("03-01");

		final DocumentClassification.ClassName germanName = new DocumentClassification.ClassName();
		germanName.setLanguage("de");
		germanName.setValue(
				Constants.getVdi2770GermanCategoryNames().get(Constants.VDI2770_MOUNT_CATEGORY));

		classification.getClassName().add(germanName);

		final DocumentClassification.ClassName englishName = new DocumentClassification.ClassName();
		englishName.setLanguage("en");
		englishName.setValue(
				Constants.getVdi2770EnglishCategoryNames().get(Constants.VDI2770_MOUNT_CATEGORY));

		classification.getClassName().add(englishName);

		doc.getDocumentClassification().add(classification);

		final DocumentClassification iecClassification = new DocumentClassification();

		iecClassification.setClassificationSystem("IEC61355");
		iecClassification.setClassId("DD");

		final DocumentClassification.ClassName iecGermanName = new DocumentClassification.ClassName();
		iecGermanName.setLanguage("de");
		iecGermanName.setValue("Technische Berichte");

		iecClassification.getClassName().add(iecGermanName);

		final DocumentClassification.ClassName iecEnglishName = new DocumentClassification.ClassName();
		iecEnglishName.setLanguage("en");
		iecEnglishName.setValue("Technical reports");

		iecClassification.getClassName().add(iecEnglishName);

		doc.getDocumentClassification().add(iecClassification);
	}

	private static void createDocumentIdDomain(final Document doc) {
		final DocumentIdDomain domain = new DocumentIdDomain();

		domain.setDocumentDomainId("DemoDomain");

		final DocumentIdDomain.Party party = new DocumentIdDomain.Party();
		party.setRole(Role.Responsible.toString());

		final DocumentIdDomain.Party.Organization orga = new DocumentIdDomain.Party.Organization();
		orga.setOrganizationId("ULE");
		orga.setOrganizationName("Uni Leipzig");
		orga.setOrganizationOfficialName("Universität Leipzig");
		party.setOrganization(orga);

		domain.setParty(party);

		doc.getDocumentIdDomain().add(domain);

		final DocumentIdDomain otherDomain = new DocumentIdDomain();

		otherDomain.setDocumentDomainId("MyDomain");

		final DocumentIdDomain.Party otherParty = new DocumentIdDomain.Party();
		otherParty.setRole(Role.Responsible.toString());

		final DocumentIdDomain.Party.Organization otherOrga = new DocumentIdDomain.Party.Organization();
		otherOrga.setOrganizationId("CUS");
		otherOrga.setOrganizationName("Customer");
		otherOrga.setOrganizationOfficialName("Customer GmbH");
		otherParty.setOrganization(otherOrga);

		otherDomain.setParty(otherParty);

		doc.getDocumentIdDomain().add(otherDomain);
	}

	private static void createDocumentId(final Document doc) {

		final Document.DocumentId id = new Document.DocumentId();
		id.setDomainId("DemoDomain");
		id.setIsPrimary(Boolean.TRUE);
		id.setValue("4711Demo");
		doc.getDocumentId().add(id);

		final Document.DocumentId otherId = new Document.DocumentId();
		otherId.setDomainId("MyDomain");
		otherId.setIsPrimary(Boolean.FALSE);
		otherId.setValue("AB-8889-XY/40");
		doc.getDocumentId().add(otherId);
	}
}
