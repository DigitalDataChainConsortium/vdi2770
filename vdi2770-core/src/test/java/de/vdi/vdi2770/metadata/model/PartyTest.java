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

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link Party}.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class PartyTest {

	private final Locale locale = Locale.getDefault();

	private static Organization createOrganization() {

		final Organization orga = new Organization();
		orga.setOrganizationName("DEMO");
		orga.setOrganizationOfficialName("DEMO");
		orga.setOrganizationId("asijdasd");

		return orga;
	}

	/**
	 * Valid party test.
	 */
	@Test
	public void validTest() {

		final Party party = new Party();
		party.setRole(Role.Author);

		Organization orga = createOrganization();
		party.setOrganization(orga);

		final List<ValidationFault> faults = party.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(0, faults.size());
	}

	/**
	 * Missing organization test.
	 */
	@Test
	public void missingRoleTest() {

		final Party party = new Party();

		Organization orga = createOrganization();
		party.setOrganization(orga);

		final List<ValidationFault> faults = party.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(1, faults.size());

		final ValidationFault fault = faults.get(0);

		assertSame("Party", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(Party.Fields.role, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_NULL);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * Missing organization test.
	 */
	@Test
	public void missingOrganizationTest() {

		final Party party = new Party();
		party.setRole(Role.Supplier);

		final List<ValidationFault> faults = party.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(1, faults.size());

		final ValidationFault fault = faults.get(0);

		assertSame("Party", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(Party.Fields.organization, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_NULL);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * Empty organization official name in party test.
	 */
	@Test
	public void missingOrganizationOfficialName() {

		final Party party = new Party();
		party.setRole(Role.Author);

		final Organization orga = new Organization();
		orga.setOrganizationName("DEMO");
		orga.setOrganizationId("asijdasd");

		party.setOrganization(orga);

		final List<ValidationFault> faults = party.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(1, faults.size());

		final ValidationFault fault = faults.get(0);

		assertSame("Organization", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(Organization.Fields.organizationOfficialName, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_EMPTY);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

}
