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
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;

import de.vdi.vdi2770.metadata.MetadataException;
import de.vdi.vdi2770.metadata.xml.XmlWriter;

/**
 * </p>
 * This class implements and instance of the information for demonstration
 * purposes.
 * </p>
 * <p>
 * This class is only intended to demonstrate object creations and relations.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class DemoModel {

	/**
	 * Create a demo XML file for a demo
	 * {@link de.vdi.vdi2770.metadata.xsd.Document}.
	 *
	 * @param fileName Path to an XML file to write
	 * @throws MetadataException There was an error creating the XML file.
	 */
	public void createXmlFile(final String fileName) throws MetadataException {
		createXmlFile(fileName, Locale.getDefault());
	}

	/**
	 * Create a demo XML file for a demo
	 * {@link de.vdi.vdi2770.metadata.xsd.Document}.
	 *
	 * @param fileName Path to an XML file to write
	 * @param locale   Locale information
	 * @throws MetadataException There was an error creating the XML file.
	 */
	public void createXmlFile(final String fileName, final Locale locale) throws MetadataException {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(fileName));
		Preconditions.checkArgument(locale != null);

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

		// convert to JAXB POJO and save as XML file
		final XmlWriter writer = new XmlWriter(locale);
		writer.write(fileName, doc, false);
	}

	private static void createDocumentVersion(final Document doc) {

		final DocumentVersion version = new DocumentVersion();

		version.setDocumentVersionId("1.0");

		version.addLanguage("de");
		version.addLanguage("en");

		version.setNumberOfPages(Integer.valueOf(100));

		createParty(version);
		createDescription(version);
		createLifeCycleStatus(version);
		createDocumentRelationship(version);
		createDigitalFile(version);

		doc.addDocumentVersion(version);

	}

	private static void createDocumentRelationship(final DocumentVersion version) {

		final DocumentRelationship rel = new DocumentRelationship();

		final DocumentId id = new DocumentId();
		id.setDomainId("DemoDomain");
		id.setId("449890");

		rel.setDocumentId(id);
		rel.setType(DocumentRelationshipType.RefersTo);

		version.addDocumentRelationship(rel);

		final DocumentRelationship rel1 = new DocumentRelationship();

		final DocumentId id1 = new DocumentId();
		id1.setDomainId("AnotherID");
		id1.setId("ABCABSKDJWIIE  SADLKSAJD #12901823");

		rel1.setDocumentId(id1);
		rel1.addDocumentVersionId("1.0");
		rel1.addDocumentVersionId("2.0");
		rel1.setType(DocumentRelationshipType.RefersTo);

		version.addDocumentRelationship(rel1);
	}

	private static void createLifeCycleStatus(final DocumentVersion version) {

		final LifeCycleStatus status = new LifeCycleStatus();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse("2019-05-07", formatter);
		status.setSetDate(date);
		status.setStatusValue(LifeCycleStatusValue.Released);

		final Party author = new Party();
		author.setRole(Role.Responsible);

		final Organization orga = new Organization();
		orga.setOrganizationId("ULE");
		orga.setOrganizationName("Uni Leipzig");
		orga.setOrganizationOfficialName("Universität Leipzig");
		author.setOrganization(orga);

		status.addParty(author);

		final TranslatableString germanComment = new TranslatableString();
		germanComment.setLanguage("de");
		germanComment.setText("Die Version ist frei erfunden, aber freigegeben.");
		status.addComment(germanComment);

		final TranslatableString englishComment = new TranslatableString();
		englishComment.setLanguage("en");
		englishComment.setText("This milestone is just for testing purposes");
		status.addComment(englishComment);

		version.setLifeCycleStatus(status);
	}

	private static void createReferencedObject(final Document doc) {

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

		final Organization orga = new Organization();
		orga.setOrganizationId("ULE");
		orga.setOrganizationName("Uni Leipzig");
		orga.setOrganizationOfficialName("Universität Leipzig");
		manufacturer.setOrganization(orga);

		o.addParty(manufacturer);

		doc.addReferencedObject(o);
	}

	private static void createDigitalFile(final DocumentVersion version) {

		final DigitalFile pdfFile = new DigitalFile();
		pdfFile.setFileFormat(MediaType.PDF.toString());
		pdfFile.setFileName("test.pdf");

		version.addDigitalFile(pdfFile);

		final DigitalFile xlsFile = new DigitalFile();
		xlsFile.setFileFormat("application/vnd.ms-excel");
		xlsFile.setFileName("test.xls");

		version.addDigitalFile(xlsFile);
	}

	private static void createParty(final DocumentVersion version) {

		final Party author = new Party();

		final Organization authorOrganisation = new Organization();
		authorOrganisation.setOrganizationId("ULE");
		authorOrganisation.setOrganizationName("Uni Leipzig");
		authorOrganisation.setOrganizationOfficialName("Universität Leipzig");
		author.setOrganization(authorOrganisation);

		author.setRole(Role.Author);

		version.addParty(author);
	}

	private static void createDescription(final DocumentVersion version) {

		final DocumentDescription germanDesc = new DocumentDescription();

		germanDesc.setLanguage("de");
		germanDesc.setTitle("Demo Bericht");
		germanDesc.setSubTitle("Ein kleines Beispiel für einen Untertitel");
		germanDesc.setSummary(
				"Dies ist eine Zusammenfassung in deutsch für den beispielhaften Demo Bericht");
		germanDesc.addKeyWord("Test");
		germanDesc.addKeyWord("Demo");

		version.addDocumentDescription(germanDesc);

		final DocumentDescription englishDesc = new DocumentDescription();

		englishDesc.setLanguage("en");
		englishDesc.setTitle("Demo Report");
		englishDesc.setSubTitle("A short example for sub titles");
		englishDesc.setSummary("This is a summary in english for the Demo Report document");
		englishDesc.addKeyWord("Test");
		englishDesc.addKeyWord("Demo");

		version.addDocumentDescription(englishDesc);
	}

	private static void createDocumentClassification(final Document doc) {
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
		englishName.setText(
				Constants.getVdi2770EnglishCategoryNames().get(Constants.VDI2770_MOUNT_CATEGORY));

		classification.addClassName(englishName);

		doc.addDocumentClassification(classification);

		final DocumentClassification iecClassification = new DocumentClassification();

		iecClassification.setClassificationSystem("IEC61355");
		iecClassification.setClassId("DD");

		doc.addDocumentClassification(iecClassification);
	}

	private static void createDocumentIdDomain(final Document doc) {
		final DocumentIdDomain domain = new DocumentIdDomain();

		domain.setDocumentDomainId("DemoDomain");

		final Party party = new Party();
		party.setRole(Role.Responsible);

		final Organization orga = new Organization();
		orga.setOrganizationId("ULE");
		orga.setOrganizationName("Uni Leipzig");
		orga.setOrganizationOfficialName("Universität Leipzig");
		party.setOrganization(orga);

		domain.setParty(party);

		doc.addDocumentIdDomain(domain);
	}

	private static void createDocumentId(final Document doc) {

		final DocumentId id = new DocumentId();
		id.setDomainId("DemoDomain");
		id.setIsPrimary(Boolean.TRUE);
		id.setId("4711Demo");
		doc.addDocumentId(id);
	}
}
