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
 * Test class for {@link DocumentClassification}.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class DocumentClassificationTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * Valid {@link DocumentClassification} instance test with required fields only.
	 */
	@Test
	public void valid() {

		final DocumentClassification system = new DocumentClassification("classId", "system");

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Valid {@link DocumentClassification} instance test with every field included.
	 * <p>
	 * The given document class name is given in two languages.
	 * </p>
	 */
	@Test
	public void validComplete() {

		final DocumentClassification system = new DocumentClassification("classId", "system");

		// add name of class in German
		final TranslatableString name_de = new TranslatableString("Name", "de");
		system.addClassName(name_de);

		// add name of class in English
		final TranslatableString name_en = new TranslatableString("English Name", "en");
		system.addClassName(name_en);

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Missing document class ID test.
	 */
	@Test
	public void emptyClassId() {

		final DocumentClassification system = new DocumentClassification("", "system");

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentClassification");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "classId");
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Missing document class ID test.
	 */
	@Test
	public void emptyClassificationSystem() {

		final DocumentClassification system = new DocumentClassification("classId", "");

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentClassification");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "classificationSystem");
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Empty required fields test.
	 */
	@Test
	public void emptyRequired() {

		final DocumentClassification system = new DocumentClassification("", "");

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 2);
	}

	/**
	 * Invalid VDI 2770 category ID test.
	 */
	@Test
	public void invalidVdi2770ClassId() {

		final DocumentClassification system = new DocumentClassification("classId",
				Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME);

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentClassification");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "classId");
		assertTrue(fault.getType() == FaultType.IS_INCONSISTENT);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Valid VDI 2770 class name test.
	 */
	@Test
	public void validVdi2770ClassName() {

		final DocumentClassification system = new DocumentClassification("01-01",
				Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME);

		final TranslatableString name = new TranslatableString("Identifikation", "de");
		system.addClassName(name);

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Invalid VDI 2770 class name test.
	 */
	@Test
	public void invalidVdi2770ClassName() {

		final DocumentClassification system = new DocumentClassification("01-01",
				Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME);

		final TranslatableString name = new TranslatableString("Demo", "de");
		system.addClassName(name);

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentClassification");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "className");
		assertTrue(fault.getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Duplicate class names for a VDI 2770 category test.
	 */
	@Test
	public void duplicateClassName() {

		final DocumentClassification system = new DocumentClassification("01-01",
				Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME);

		final TranslatableString name1 = new TranslatableString("Identifikation", "de");
		final TranslatableString name2 = new TranslatableString("Identifikation", "de");
		system.addClassName(name1);
		system.addClassName(name2);

		final List<ValidationFault> faults = system.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DocumentClassification");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "className");
		assertTrue(fault.getType() == FaultType.HAS_DUPLICATE_VALUE);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}
	
	@Test
	public void invalidStrictVdi2770ClassName() {

		final DocumentClassification system = new DocumentClassification("01-01",
				Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME);

		final TranslatableString name = new TranslatableString("identification", "en");
		system.addClassName(name);

		// strict mode enabled
		List<ValidationFault> faults = system.validate(this.locale, true);
		faults.stream().forEach(f -> log.debug(f.toString()));
		assertTrue(faults.size() == 1);
		
		// strict mode disabled
		faults = system.validate(this.locale, false);
		faults.stream().forEach(f -> log.debug(f.toString()));
		assertTrue(faults.size() == 0);
	}

}
