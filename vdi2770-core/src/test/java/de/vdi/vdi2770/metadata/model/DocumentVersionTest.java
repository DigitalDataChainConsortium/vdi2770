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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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
	 * @return A demo {@link Party}
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
		status.setComments(List.of(comment));

		return status;
	}

	private static DocumentRelationship createRelation() {
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

		version.setParty(List.of(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(List.of("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(List.of(file));

		version.setDocumentDescription(List.of(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(100);

		version.setDocumentRelationship(List.of(createRelation()));

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(0, faults.size());
	}

	/**
	 * missing document version id
	 */
	@Test
	public void missingDocumentVersionId() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(List.of(createParty(Role.Author)));

		version.setLanguage(List.of("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(List.of(file));

		version.setDocumentDescription(List.of(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(100);

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(1, faults.size());

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentVersion", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentVersion.Fields.documentVersionId, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_EMPTY);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * invalid role of party
	 */
	@Test
	public void invalidParty1() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(List.of(createParty(Role.Manufacturer)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(List.of("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(List.of(file));

		version.setDocumentDescription(List.of(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(100);

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(1, faults.size());

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentVersion", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentVersion.Fields.party, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.HAS_INVALID_VALUE);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * missing pdf
	 */
	@Test
	public void invalidDigitalFile1() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(List.of(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(List.of("de"));

		final DigitalFile file = new DigitalFile("Demo.zip", MediaType.ZIP.toString());
		version.setDigitalFile(List.of(file));

		version.setDocumentDescription(List.of(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(100);

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(1, faults.size());

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentVersion", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentVersion.Fields.digitalFile, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.HAS_INVALID_VALUE);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * missing file
	 */
	@Test
	public void invalidDigitalFile2() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(List.of(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(List.of("de"));

		version.setDocumentDescription(List.of(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(100);

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(1, faults.size());

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentVersion", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentVersion.Fields.digitalFile, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_EMPTY);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * missing language
	 */
	@Test
	public void missingLanguage1() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(List.of(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(List.of(file));

		version.setDocumentDescription(List.of(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(100);

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentVersion", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentVersion.Fields.language, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_EMPTY);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * description has no language
	 */
	@Test
	public void missingDescriptionForLanguage() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(List.of(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(Arrays.asList("de", "en"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(List.of(file));

		version.setDocumentDescription(List.of(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(100);

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentVersion", fault.getEntity());
		assertEquals(2, fault.getProperties().size());
		assertSame(DocumentVersion.Fields.language, fault.getProperties().get(0));
		assertSame(DocumentVersion.Fields.documentDescription, fault.getProperties().get(1));
		assertSame(fault.getType(), FaultType.IS_INCONSISTENT);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * missing document description
	 */
	@Test
	public void missingDescription() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(List.of(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(List.of("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(List.of(file));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(100);

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentVersion", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentVersion.Fields.documentDescription, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_EMPTY);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * invalid number of pages
	 */
	@Test
	public void invalidNumberOfPages() {

		final DocumentVersion version = new DocumentVersion();

		version.setParty(List.of(createParty(Role.Author)));

		version.setDocumentVersionId("00.02");
		version.setLanguage(List.of("de"));

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.PDF.toString());
		version.setDigitalFile(List.of(file));

		version.setDocumentDescription(List.of(createDocumentDescription("de")));

		version.setLifeCycleStatus(createLifecycleStatus());

		version.setNumberOfPages(-100);

		final List<ValidationFault> faults = version.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentVersion", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentVersion.Fields.numberOfPages, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.EXCEEDS_LOWER_BOUND);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

}
