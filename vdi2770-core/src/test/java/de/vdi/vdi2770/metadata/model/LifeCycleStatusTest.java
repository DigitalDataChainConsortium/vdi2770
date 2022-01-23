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

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link LifeCycleStatus}.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class LifeCycleStatusTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * Create demo {@link Party}
	 * 
	 * @return
	 */
	private static Party createParty() {

		final Organization orga = new Organization();
		orga.setOrganizationName("DEMO");
		orga.setOrganizationOfficialName("DEMO");
		orga.setOrganizationId("asijdasd");

		final Party party = new Party();
		party.setRole(Role.Responsible);
		party.setOrganization(orga);

		return party;
	}

	/**
	 * Valid lifecycle status test.
	 */
	@Test
	public void validTest() {

		final LifeCycleStatus status = new LifeCycleStatus();
		status.setSetDate(LocalDate.now());
		status.setStatusValue(LifeCycleStatusValue.Released);

		Party party1 = createParty();
		Party party2 = createParty();
		party2.setRole(Role.Author);
		status.setParty(Arrays.asList(party1, party2));

		TranslatableString comment = new TranslatableString();
		comment.setLanguage("de");
		comment.setText("Das ist ein kleiner Test");
		status.setComments(Arrays.asList(comment));

		final List<ValidationFault> faults = status.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Missing role Responsible
	 */
	@Test
	public void missingResponsibleTest() {

		final LifeCycleStatus status = new LifeCycleStatus();
		status.setSetDate(LocalDate.now());
		status.setStatusValue(LifeCycleStatusValue.Released);

		Party party = createParty();
		party.setRole(Role.Author);
		status.setParty(Arrays.asList(party));

		TranslatableString comment = new TranslatableString();
		comment.setLanguage("de");
		comment.setText("Das ist ein kleiner Test");
		status.setComments(Arrays.asList(comment));

		final List<ValidationFault> faults = status.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "LifeCycleStatus");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == LifeCycleStatus.Fields.party);
		assertTrue(fault.getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Missing status value test
	 */
	@Test
	public void missingStatusTest() {

		final LifeCycleStatus status = new LifeCycleStatus();
		status.setSetDate(LocalDate.now());

		Party party = createParty();
		status.setParty(Arrays.asList(party));

		final List<ValidationFault> faults = status.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "LifeCycleStatus");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == LifeCycleStatus.Fields.statusValue);
		assertTrue(fault.getType() == FaultType.IS_NULL);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

}
