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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import de.vdi.vdi2770.metadata.xml.FileNames;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link MainDocument}s.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class MainDocumentTest extends DocumentBaseTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * Invalid main document test.
	 */
	@Test
	public void invalidTest() {

		final Document doc = getTestDocument();

		final MainDocument main = new MainDocument(doc);

		final List<ValidationFault> faults = main.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(1, faults.size());

	}

	private static List<DigitalFile> getMainDocumentDigitalFiles() {

		final DigitalFile pdfFile = new DigitalFile();
		pdfFile.setFileFormat(MediaType.PDF.toString());
		pdfFile.setFileName(FileNames.MAIN_DOCUMENT_PDF_FILE_NAME);

		return List.of(pdfFile);
	}

	/**
	 * Valid main document test.
	 */
	@Test
	public void validTest() {

		final Document doc = getTestDocument();

		final MainDocument main = new MainDocument(doc);

		main.getDocumentVersion().get(0).setDigitalFile(getMainDocumentDigitalFiles());

		final List<ValidationFault> faults = main.validate(this.locale, true);

		faults.forEach(f -> log.debug(f.toString()));

		assertEquals(0, faults.size());
	}

	/**
	 * Check {@link Document} is a main document test.
	 */
	@Test
	public void isMainDocument() {

		final Document doc = getTestDocument();

		doc.getDocumentVersion().get(0).setDigitalFile(getMainDocumentDigitalFiles());

		assertTrue(doc.isMainDocument());

	}

	/**
	 * Main documents must only refer to one object
	 */
	@Test
	public void mainDocumentWithMultipleObjects() {

		final Document doc = getTestDocument();

		MainDocument mainDoc = new MainDocument(doc);

		mainDoc.addReferencedObject(getTestReferencedObject2());

		mainDoc.getDocumentVersion().get(0).setDigitalFile(getMainDocumentDigitalFiles());

		List<ValidationFault> faults = mainDoc.validate(this.locale, true);

		List<?> errors = Fault.filter(faults, FaultLevel.ERROR);

		assertEquals(1, errors.size());

		final ValidationFault check = (ValidationFault) errors.get(0);

		assertSame("MainDocument", check.getEntity());
		assertSame("referencedObject", check.getProperties().get(0));
		assertSame(check.getType(), FaultType.HAS_INVALID_VALUE);
		assertSame(check.getLevel(), FaultLevel.ERROR);
	}

	/**
	 * Main document with object that has no individual object id
	 */
	@Test
	public void mainDocumentWithTypeObject() {

		final Document doc = getTestDocument();

		ReferencedObject o = doc.getReferencedObject().get(0);
		ObjectId id = o.getObjectId().get(0);
		id.setObjectType(ObjectType.Type);
		o.addObjectId(id);
		doc.setReferencedObject(List.of(o));

		MainDocument mainDoc = new MainDocument(doc);
		mainDoc.getDocumentVersion().get(0).setDigitalFile(getMainDocumentDigitalFiles());

		List<ValidationFault> faults = mainDoc.validate(this.locale, true);

		List<?> errors = Fault.filter(faults, FaultLevel.ERROR);

		assertEquals(1, errors.size());

		final ValidationFault check = (ValidationFault) errors.get(0);

		assertSame("MainDocument", check.getEntity());
		assertSame("referencedObject", check.getProperties().get(0));
		assertSame(check.getType(), FaultType.HAS_INVALID_VALUE);
		assertSame(check.getLevel(), FaultLevel.ERROR);
	}

	private ReferencedObject getTestReferencedObject2() {

		final ReferencedObject o = new ReferencedObject();

		final TranslatableString description = new TranslatableString();
		description.setLanguage("en");
		description.setText("Product B");
		o.addDescription(description);

		final ObjectId oid = new ObjectId();
		oid.setId("29389-2139292");
		oid.setObjectType(ObjectType.Individual);

		o.addObjectId(oid);

		final Party manufacturer = new Party();
		manufacturer.setRole(Role.Manufacturer);

		final Organization orga = getUniversityOfLeipzigOrganization();
		manufacturer.setOrganization(orga);

		o.addParty(manufacturer);

		return o;
	}

}
