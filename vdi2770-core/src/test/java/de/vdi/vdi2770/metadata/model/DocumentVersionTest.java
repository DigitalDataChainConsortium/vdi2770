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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link DocumentVersion}.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class DocumentVersionTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * Create demo {@link Party}
	 *
	 * @return
	 */
	private static Party createParty(Role role) {

		final Organization orga = new Organization();
		orga.setOrganizationName("DEMO");
		orga.setOrganizationOfficialName("DEMO");
		orga.setOrganizationId("asijdasd");

		final Party party = new Party();
		party.setRole(role);
		party.setOrganization(orga);

		return party;
	}

	private static DocumentDescription createDocumentDescription(final String lang) {

		final DocumentDescription description = new DocumentDescription();

		description.setTitle("Test");
		description.setSubTitle("Test");
		description.setSummary("Summary");
		description.setLanguage(lang);
		description.setKeyWords(Arrays.asList("Test1", "Test2"));

		return description;
	}

	private static LifeCycleStatus createLifecycleStatus() {

		final LifeCycleStatus status = new LifeCycleStatus();
		status.setSetDate(LocalDate.now());
		status.setStatusValue(LifeCycleStatusValue.Released);

		Party party1 = createParty(Role.Author);
		Party party2 = createParty(Role.Responsible);
		status.setParty(Arrays.asList(party1, party2));

		TranslatableString comment = new TranslatableString();
		comment.setLanguage("de");
		comment.setText("Das ist ein kleiner Test");
		status.setComments(Arrays.asList(comment));

		return status;
	}

	private static DocumentRelationship createReleation() {
		final DocumentRelationship relation = new DocumentRelationship();

		TranslatableString desc_de = new TranslatableString("Demo", "de");
		TranslatableString desc_en = new TranslatableString("Demo", "en");

		relation.setDescription(Arrays.asList(desc_en, desc_de));

		final DocumentId id = new DocumentId();
		id.setDomainId("INFAI");
		id.setId("278912387213");
		relation.setDocumentId(id);

		relation.setType(DocumentRelationshipType.RefersTo);

		relation.setDocumentVersionId(Arrays.asList("00.02", "00.01"));

		return relation;
	}

	/**
	 * valid
	 */
	@Test
	public void validTest1() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(Arrays.asList("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(Arrays.asList(file));

		version.setDocumentDescription(Arrays.asList(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(100));

		version.setDocumentRelationship(Arrays.asList(createReleation()));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * missing document version id
	 */
	@Test
	public void missingDocumentVersionId() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Author)));

		version.setLanguage(Arrays.asList("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(Arrays.asList(file));

		version.setDocumentDescription(Arrays.asList(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(100));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentVersion");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == DocumentVersion.Fields.documentVersionId);
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * invalid role of party
	 */
	@Test
	public void invalidParty1() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Manufacturer)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(Arrays.asList("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(Arrays.asList(file));

		version.setDocumentDescription(Arrays.asList(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(100));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentVersion");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == DocumentVersion.Fields.party);
		assertTrue(fault.getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * missing pdf
	 */
	@Test
	public void invalidDigitalFile1() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(Arrays.asList("de"));

		final DigitalFile file = new DigitalFile("Demo.zip", MediaType.ZIP.toString());
		version.setDigitalFile(Arrays.asList(file));

		version.setDocumentDescription(Arrays.asList(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(100));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentVersion");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == DocumentVersion.Fields.digitalFile);
		assertTrue(fault.getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * missing file
	 */
	@Test
	public void invalidDigitalFile2() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(Arrays.asList("de"));

		version.setDocumentDescription(Arrays.asList(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(100));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentVersion");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == DocumentVersion.Fields.digitalFile);
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * missig language
	 */
	@Test
	public void missingLanguage1() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(Arrays.asList(file));

		version.setDocumentDescription(Arrays.asList(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(100));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentVersion");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == DocumentVersion.Fields.language);
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * description has no language
	 */
	@Test
	public void missingDescriptionForLanguage() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(Arrays.asList("de", "en"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(Arrays.asList(file));

		version.setDocumentDescription(Arrays.asList(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(100));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentVersion");
		assertTrue(fault.getProperties().size() == 2);
		assertTrue(fault.getProperties().get(0) == DocumentVersion.Fields.language);
		assertTrue(fault.getProperties().get(1) == DocumentVersion.Fields.documentDescription);
		assertTrue(fault.getType() == FaultType.IS_INCONSISTENT);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * missing document description
	 */
	@Test
	public void missingDescription() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(Arrays.asList("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(Arrays.asList(file));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(100));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentVersion");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == DocumentVersion.Fields.documentDescription);
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * invalid number of pages
	 */
	@Test
	public void invalidNumberOfPages() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(Arrays.asList(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(Arrays.asList("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(Arrays.asList(file));

		version.setDocumentDescription(Arrays.asList(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(Integer.valueOf(-100));

		final List<ValidationFault> faults = version.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentVersion");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == DocumentVersion.Fields.numberOfPages);
		assertTrue(fault.getType() == FaultType.EXCEEDS_LOWER_BOUND);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

}
