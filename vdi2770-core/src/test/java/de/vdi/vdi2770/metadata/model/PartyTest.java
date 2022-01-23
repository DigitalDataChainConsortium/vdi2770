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

		final List<ValidationFault> faults = party.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Missing organization test.
	 */
	@Test
	public void missingRoleTest() {

		final Party party = new Party();

		Organization orga = createOrganization();
		party.setOrganization(orga);

		final List<ValidationFault> faults = party.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "Party");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == Party.Fields.role);
		assertTrue(fault.getType() == FaultType.IS_NULL);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Missing organization test.
	 */
	@Test
	public void missingOrganizationTest() {

		final Party party = new Party();
		party.setRole(Role.Supplier);

		final List<ValidationFault> faults = party.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "Party");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == Party.Fields.organization);
		assertTrue(fault.getType() == FaultType.IS_NULL);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Empty organization official name in party test.
	 */
	@Test
	public void missingOrganiziationOfficialName() {

		final Party party = new Party();
		party.setRole(Role.Author);

		final Organization orga = new Organization();
		orga.setOrganizationName("DEMO");
		orga.setOrganizationId("asijdasd");

		party.setOrganization(orga);

		final List<ValidationFault> faults = party.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "Organization");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == Organization.Fields.organizationOfficialName);
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

}
