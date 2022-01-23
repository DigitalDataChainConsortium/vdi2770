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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link ReferencedObject}s.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class ReferencedObjectTest {

	private final Locale locale = Locale.getDefault();

	private static Party createParty() {
		return createParty(Role.Manufacturer);
	}

	private static Party createParty(final Role role) {

		final Party result = new Party();
		result.setRole(role);

		final Organization ule = new Organization();
		ule.setOrganizationId("ULE");
		ule.setOrganizationName("Uni Leipzig");
		ule.setOrganizationOfficialName("Universität Leipzig");

		result.setOrganization(ule);

		return result;
	}

	/**
	 * Valid {@link ReferencedObject} instance test.
	 */
	@Test
	public void validSimple() {

		final ReferencedObject reference = new ReferencedObject(Arrays.asList(
				new ObjectId(ObjectType.Individual, "4711", Boolean.FALSE, RefType.SERIAL_NUMBER)),
				Arrays.asList(createParty()));

		final List<ValidationFault> faults = reference.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		// expecting zero faults
		assertTrue(faults.size() == 0);
	}

	/**
	 * Valid {@link ReferencedObject} instance test.
	 */
	@Test
	public void validSimpleWithInputLists() {

		final ReferencedObject reference = new ReferencedObject(Arrays.asList(
				new ObjectId(ObjectType.Individual, "4711", Boolean.FALSE, RefType.SERIAL_NUMBER),
				new ObjectId(ObjectType.Type, "ABC_MODEL", Boolean.FALSE, RefType.PRODUCT_TYPE),
				new ObjectId(ObjectType.Type, "https://www.demo.de/example", Boolean.FALSE,
						RefType.DIN_SPEC_91406_ID)),
				Arrays.asList(createParty(), createParty(Role.Supplier)));

		final List<ValidationFault> faults = reference.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		// expecting zero faults
		assertTrue(faults.size() == 0);
	}

	/**
	 * Missing list of parties test.
	 */
	@Test
	public void emptyParties() {

		final ReferencedObject reference = new ReferencedObject(Arrays.asList(
				new ObjectId(ObjectType.Individual, "4711", Boolean.FALSE, RefType.SERIAL_NUMBER)),
				new ArrayList<>());

		final List<ValidationFault> faults = reference.validate(this.locale);

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "ReferencedObject");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "party");
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);

	}

	/**
	 * Missing list of parties test.
	 */
	@Test
	public void nullParties() {

		final ReferencedObject reference = new ReferencedObject(Arrays.asList(
				new ObjectId(ObjectType.Individual, "4711", Boolean.FALSE, RefType.SERIAL_NUMBER)),
				null);

		final List<ValidationFault> faults = reference.validate(this.locale);

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "ReferencedObject");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "party");
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);

	}

	/**
	 * Missing list of object IDs test.
	 */
	@Test
	public void emptyObjectIdsTest() {

		final ReferencedObject reference = new ReferencedObject(new ArrayList<>(),
				Arrays.asList(createParty(), createParty(Role.Supplier)));

		final List<ValidationFault> faults = reference.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "ReferencedObject");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "objectId");
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);

	}

	/**
	 * Missing list of object IDs test.
	 */
	@Test
	public void nullObjectIdsTest() {

		final ReferencedObject reference = new ReferencedObject(new ArrayList<>(),
				Arrays.asList(createParty(), createParty(Role.Supplier)));

		final List<ValidationFault> faults = reference.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "ReferencedObject");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "objectId");
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);

	}

	/**
	 * Missing manufacturer test.
	 */
	@Test
	public void missingManufacturerRole() {

		final ReferencedObject reference = new ReferencedObject(
				Arrays.asList(new ObjectId(ObjectType.Individual, "4711", Boolean.FALSE,
						RefType.SERIAL_NUMBER)),
				Arrays.asList(createParty(Role.Supplier), createParty(Role.Author)));

		final List<ValidationFault> faults = reference.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "ReferencedObject");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "party");
		assertTrue(fault.getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);

	}

	/**
	 * Onyl accept one individual ID.
	 */
	@Test
	public void duplicateIndividualIds() {

		final ReferencedObject reference = new ReferencedObject(
				Arrays.asList(
						new ObjectId(ObjectType.Individual, "4711", Boolean.FALSE,
								RefType.SERIAL_NUMBER),
						new ObjectId(ObjectType.Individual, "471211", Boolean.FALSE,
								RefType.SERIAL_NUMBER)),
				Arrays.asList(createParty(Role.Manufacturer)));

		final List<ValidationFault> faults = reference.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "ReferencedObject");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "objectId");
		assertTrue(fault.getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(fault.getLevel() == FaultLevel.INFORMATION);

	}

}
