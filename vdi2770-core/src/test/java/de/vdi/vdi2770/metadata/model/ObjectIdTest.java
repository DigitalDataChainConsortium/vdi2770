/*******************************************************************************
 * Copyright (C) 2021-2023 Johannes Schmidt
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
 * Test class for {@link ObjectId}.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class ObjectIdTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * type id
	 */
	@Test
	public void validTest1() {

		final ObjectId id = new ObjectId();
		id.setId("My ID");
		id.setIsGloballyBiunique(Boolean.FALSE);
		id.setObjectType(ObjectType.Type);
		id.setRefType("Something");

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * instance id
	 */
	@Test
	public void validTest2() {

		final ObjectId id = new ObjectId();
		id.setId("My ID");
		id.setIsGloballyBiunique(Boolean.FALSE);
		id.setObjectType(ObjectType.Individual);
		id.setRefType("Something");

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * optional globally bi unique
	 */
	@Test
	public void validTest3() {

		final ObjectId id = new ObjectId();
		id.setId("My ID");
		id.setObjectType(ObjectType.Individual);
		id.setRefType("Something");

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * optional ref type
	 */
	@Test
	public void validTest4() {

		final ObjectId id = new ObjectId();
		id.setId("My ID");
		id.setObjectType(ObjectType.Individual);

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * missing id
	 */
	@Test
	public void missingIdValue() {

		final ObjectId id = new ObjectId();
		id.setObjectType(ObjectType.Individual);
		id.setRefType("Something");

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "ObjectId");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == ObjectId.Fields.id);
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * missing type
	 */
	@Test
	public void missingTypeValue() {

		final ObjectId id = new ObjectId();
		id.setId("My ID");
		id.setRefType("Something");

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "ObjectId");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == ObjectId.Fields.objectType);
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Test URLs that are object IDs
	 * 
	 * See DIN SPEC 91406 / IEC 61409 for more information
	 * 
	 */
	@Test
	public void instanceOfObjectUriTest() {
		
		final ObjectId id = new ObjectId();
		id.setId("https://www.domain-abc.com/free_text");
		id.setObjectType(ObjectType.Individual);
		id.setRefType(RefType.DIN_SPEC_91406_ID);

		List<ValidationFault> faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		id.setId("HTTPS://wWw.DOMAIN-ABC.com/free_text");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 1);
		
		final ValidationFault caseFault = faults.get(0);

		assertTrue(caseFault.getEntity() == "ObjectId");
		assertTrue(caseFault.getProperties().size() == 1);
		assertTrue(caseFault.getProperties().get(0) == ObjectId.Fields.id);
		assertTrue(caseFault.getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(caseFault.getLevel() == FaultLevel.ERROR);
		assertTrue(caseFault.getMessage().startsWith("OI_004"));
		
		id.setId("https://www.domain-abc.com/Model-Nr-1234/Serial-Nr-5678");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);

		id.setId("http://www.domain-abc.com/sd09fqw4hrdfj0as89u7");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		id.setId("http://www.domain-abc.com/Baureihe/Model/Seriennummer");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		// see issue #32 
		id.setId("www.domain-abc.com/23456tdhfe65ur67uztm");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		// see issue #32 
		id.setId("domain-abc.com/23456tdhfe65ur67uztm");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		// see issue #32 
		id.setId("ed2k://domain-abc.com/23456tdhfe65ur67uztm");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		id.setId("http://domain-abc.com/23456tdhfe65ur67uztm");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		id.setId("https://www.domain-abc.com/sd09fqw4hrdfj0as89u7?counter=1");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		id.setId("http://www.domain-abc.com/sd09fqw4hrdfj0as89u7?counter=2");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		id.setId("https://www.domain-abc.com/sd09fqw4?37S=UN123456789111222333IIICCC+54321");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		id.setId("https://foo/bar/demo");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
		
		id.setId("ftp://www2.foo/bar/demo");
		faults = id.validate(Locale.getDefault());
		assertTrue(faults.size() == 0);
	}

}
