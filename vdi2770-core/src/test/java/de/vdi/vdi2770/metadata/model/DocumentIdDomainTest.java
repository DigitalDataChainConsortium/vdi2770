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

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link DocumentIdDomain}s.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class DocumentIdDomainTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * Valid {@link DocumentIdDomain} test.
	 */
	@Test
	public void validTest() {

		final Organization orga = new Organization();
		orga.setOrganizationName("Test");
		orga.setOrganizationOfficialName("Test");

		final Party party = new Party(Role.Responsible, orga);

		final DocumentIdDomain domain = new DocumentIdDomain("DOMAIN", party);

		final List<ValidationFault> faults = domain.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Empty domain test.
	 */
	@Test
	public void emptyDomainTest() {

		final Organization orga = new Organization();
		orga.setOrganizationName("Test");
		orga.setOrganizationOfficialName("Test");

		final Party party = new Party(Role.Responsible, orga);

		final DocumentIdDomain domain = new DocumentIdDomain("", party);

		final List<ValidationFault> faults = domain.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);
	}

	/**
	 * Missing party test.
	 */
	@Test
	public void emptyPartyTest() {

		final DocumentIdDomain domain = new DocumentIdDomain();
		domain.setDocumentDomainId("DOMAINID");

		final List<ValidationFault> faults = domain.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);
	}

	/**
	 * Invalid role test. Responsible role is required.
	 */
	@Test
	public void invalidRoleTest() {

		final Organization orga = new Organization();
		orga.setOrganizationName("Test");
		orga.setOrganizationOfficialName("Test");

		final Party party = new Party(Role.Author, orga);

		final DocumentIdDomain domain = new DocumentIdDomain("DOMAIN", party);

		final List<ValidationFault> faults = domain.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		log.info(faults.get(0).getMessage());

		assertTrue(faults.size() == 1);
	}
}
