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
 * Test class for {@link DocumentDescription}s.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class DocumentDescriptionTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * A valid document description test.
	 */
	@Test
	public void validTest() {

		final DocumentDescription description = new DocumentDescription();

		description.setTitle("Test");
		description.setSubTitle("Test");
		description.setSummary("Summary");
		description.setLanguage("en");
		description.setKeyWords(Arrays.asList("Test1", "Test2"));

		final List<ValidationFault> faults = description.validate(this.locale);

		assertTrue(faults.size() == 0);

	}

	/**
	 * Invalid language code test.
	 */
	@Test
	public void invalidLanguageTest() {

		final DocumentDescription description = new DocumentDescription();

		description.setTitle("Test");
		description.setSubTitle("Test");
		description.setSummary("Summary");
		description.setLanguage("gasd");
		description.setKeyWords(Arrays.asList("Test1", "Test2"));

		final List<ValidationFault> faults = description.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		assertTrue(faults.get(0).getType() == FaultType.HAS_INVALID_VALUE);
		assertTrue(faults.get(0).getLevel() == FaultLevel.ERROR);
		assertTrue(faults.get(0).getProperties().contains(DocumentDescription.Fields.language));

	}

	/**
	 * Invalid description title test.
	 */
	@Test
	public void invalidTitleTest() {

		final DocumentDescription description = new DocumentDescription();

		description.setTitle("");
		description.setSubTitle("Test");
		description.setSummary("Summary");
		description.setLanguage("de");
		description.setKeyWords(Arrays.asList("Test1", "Test2"));

		final List<ValidationFault> faults = description.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		assertTrue(faults.get(0).getType() == FaultType.IS_EMPTY);
		assertTrue(faults.get(0).getLevel() == FaultLevel.ERROR);
		assertTrue(faults.get(0).getProperties().contains(DocumentDescription.Fields.title));

	}

	/**
	 * Invalid description summary test.
	 */
	@Test
	public void invalidSummaryTest() {

		final DocumentDescription description = new DocumentDescription();

		description.setTitle("Test");
		description.setSubTitle("Test");
		description.setSummary("");
		description.setLanguage("de");
		description.setKeyWords(Arrays.asList("Test1", "Test2"));

		final List<ValidationFault> faults = description.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		assertTrue(faults.get(0).getType() == FaultType.IS_EMPTY);
		assertTrue(faults.get(0).getLevel() == FaultLevel.ERROR);
		assertTrue(faults.get(0).getProperties().contains(DocumentDescription.Fields.summary));

	}

	/**
	 * Invalid description key words test.
	 */
	@Test
	public void invalidKeywordsTest() {

		final DocumentDescription description = new DocumentDescription();

		description.setTitle("Test");
		description.setSubTitle("Test");
		description.setSummary("asdasd");
		description.setLanguage("de");

		final List<ValidationFault> faults = description.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		assertTrue(faults.get(0).getType() == FaultType.IS_EMPTY);
		assertTrue(faults.get(0).getLevel() == FaultLevel.ERROR);
		assertTrue(faults.get(0).getProperties().contains(DocumentDescription.Fields.keyWords));

	}

	/**
	 * Duplicate description key words test.
	 */
	@Test
	public void duplicateKeywordsTest() {

		final DocumentDescription description = new DocumentDescription();

		description.setTitle("Test");
		description.setSubTitle("Test");
		description.setSummary("asdasd");
		description.setLanguage("de");
		description.setKeyWords(Arrays.asList("A", "A"));

		final List<ValidationFault> faults = description.validate(this.locale);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		assertTrue(faults.get(0).getType() == FaultType.HAS_DUPLICATE_VALUE);
		assertTrue(faults.get(0).getLevel() == FaultLevel.ERROR);
		assertTrue(faults.get(0).getProperties().contains(DocumentDescription.Fields.keyWords));

	}
}
