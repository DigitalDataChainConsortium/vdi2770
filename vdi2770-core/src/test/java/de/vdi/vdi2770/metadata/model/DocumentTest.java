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
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link Document}s.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class DocumentTest extends DocumentBaseTest {

	private final Locale locale = Locale.getDefault();

	protected List<DocumentId> getInvalidDocumentIds() {

		final DocumentId id1 = new DocumentId("DOMAIN", "IDVALUE", Boolean.TRUE);
		final DocumentId id2 = new DocumentId("DOMAIN1", "IDVALUE1", Boolean.TRUE);

		return Arrays.asList(id1, id2);
	}

	/**
	 * Valid document test.
	 */
	@Test
	public void validTest() {

		final Document doc = getTestDocument();

		final List<ValidationFault> faults = doc.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 0);

	}

	/**
	 * Document has valid document IDs test.
	 */
	@Test
	public void validDocumentIdsTest() {

		final Document doc = getTestDocument();

		doc.setDocumentId(getInvalidDocumentIds());

		final List<ValidationFault> faults = doc.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "Document");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "documentId");
		assertTrue(fault.getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);

	}

	/**
	 * {@link Document} is not a main document test.
	 */
	@Test
	public void notMainDocument() {

		final Document doc = getTestDocument();

		assertFalse(doc.isMainDocument());

	}

}
