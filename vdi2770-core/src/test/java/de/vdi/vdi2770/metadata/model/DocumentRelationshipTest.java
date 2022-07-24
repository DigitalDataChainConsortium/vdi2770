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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link DocumentRelationship}.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class DocumentRelationshipTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * Valid relation test.
	 */
	@Test
	public void validTest() {

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

		final List<ValidationFault> faults = relation.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(0, faults.size());
	}

	/**
	 * optional version id
	 */
	@Test
	public void validTest1() {

		final DocumentRelationship relation = new DocumentRelationship();

		TranslatableString desc_de = new TranslatableString("Demo", "de");
		TranslatableString desc_en = new TranslatableString("Demo", "en");

		relation.setDescription(Arrays.asList(desc_en, desc_de));

		final DocumentId id = new DocumentId();
		id.setDomainId("INFAI");
		id.setId("278912387213");
		relation.setDocumentId(id);

		relation.setType(DocumentRelationshipType.RefersTo);

		final List<ValidationFault> faults = relation.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(0, faults.size());
	}

	/**
	 * Optional description
	 */
	@Test
	public void validTest2() {

		final DocumentRelationship relation = new DocumentRelationship();

		final DocumentId id = new DocumentId();
		id.setDomainId("INFAI");
		id.setId("278912387213");
		relation.setDocumentId(id);

		relation.setType(DocumentRelationshipType.RefersTo);

		relation.setDocumentVersionId(Arrays.asList("00.02", "00.01"));

		final List<ValidationFault> faults = relation.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(0, faults.size());
	}

	/**
	 * document id in relation not specified
	 */
	@Test
	public void missingDocumentId() {

		final DocumentRelationship relation = new DocumentRelationship();

		relation.setType(DocumentRelationshipType.RefersTo);

		relation.setDocumentVersionId(Arrays.asList("00.02", "00.01"));

		final List<ValidationFault> faults = relation.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentRelationship", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentRelationship.Fields.documentId, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_NULL);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * type of relation is not set
	 */
	@Test
	public void missingType() {

		final DocumentRelationship relation = new DocumentRelationship();

		final DocumentId id = new DocumentId();
		id.setDomainId("INFAI");
		id.setId("278912387213");
		relation.setDocumentId(id);

		relation.setDocumentVersionId(Arrays.asList("00.02", "00.01"));

		final List<ValidationFault> faults = relation.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		final ValidationFault fault = faults.get(0);

		assertSame("DocumentRelationship", fault.getEntity());
		assertEquals(1, fault.getProperties().size());
		assertSame(DocumentRelationship.Fields.type, fault.getProperties().get(0));
		assertSame(fault.getType(), FaultType.IS_NULL);
		assertSame(fault.getLevel(), FaultLevel.ERROR);
	}

}
