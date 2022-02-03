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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link DocumentId}s.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class DocumentIdTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * Valid document ID test.
	 */
	@Test
	public void validTest() {

		final DocumentId id = new DocumentId("DOMAIN", "IDVALUE", Boolean.FALSE);

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Check for isPrimary test.
	 */
	@Test
	public void validPrimaryTest() {

		final DocumentId id = new DocumentId("DOMAIN", "IDVALUE", Boolean.TRUE);

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Validate a list of document IDs test.
	 */
	@Test
	public void validListTest() {

		final DocumentId id1 = new DocumentId("DOMAIN", "IDVALUE", Boolean.FALSE);
		final DocumentId id2 = new DocumentId("DOMAIN1", "IDVALUE1", Boolean.FALSE);

		final List<ValidationFault> faults = ValidationHelper.validateEntityList(
				Arrays.asList(id1, id2), "Demo", "Test", Locale.getDefault(), true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);
	}

	/**
	 * Invalid {@link List} of document IDs.
	 */
	@Test
	public void invalidListTest() {

		final DocumentId id1 = new DocumentId("DOMAIN", "IDVALUE", Boolean.TRUE);
		final DocumentId id2 = new DocumentId();
		final DocumentId id3 = null;

		final List<ValidationFault> faults = ValidationHelper.validateEntityList(
				Arrays.asList(id1, id2, id3), "Document", "documentId", Locale.getDefault(),
				true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 3);
	}

	/**
	 * Invalid document ID domain test
	 */
	@Test
	public void invalidDomainTest() {

		final DocumentId id = new DocumentId("", "IDVALUE", Boolean.TRUE);

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		assertTrue(faults.get(0).getType() == FaultType.IS_EMPTY);
		assertTrue(faults.get(0).getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Invalid document ID test.
	 */
	@Test
	public void invalidIdTest() {

		final DocumentId id = new DocumentId("DOMAIN", "", Boolean.TRUE);

		final List<ValidationFault> faults = id.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		assertTrue(faults.get(0).getType() == FaultType.IS_EMPTY);
		assertTrue(faults.get(0).getLevel() == FaultLevel.ERROR);
	}
}
