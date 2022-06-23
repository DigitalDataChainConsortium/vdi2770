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

import com.google.common.net.MediaType;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.extern.log4j.Log4j2;

/**
 * Test class for {@link DigitalFile}s.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class DigitalFileTest {

	private final Locale locale = Locale.getDefault();

	/**
	 * Valid {@link DigitalFile} instance test.
	 */
	@Test
	public void valid() {

		final DigitalFile file = new DigitalFile("Demo.zip", MediaType.ZIP.toString());

		final List<ValidationFault> faults = file.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		// expecting zero faults
		assertTrue(faults.size() == 0);
	}

	/**
	 * Missing file name test.
	 */
	@Test
	public void emptyFileName() {

		final DigitalFile file = new DigitalFile("", MediaType.ZIP.toString());

		final List<ValidationFault> faults = file.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DigitalFile");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "fileName");
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Missing MIME type test.
	 */
	@Test
	public void emptyMimetype() {

		final DigitalFile file = new DigitalFile("Demo.zip", "");

		final List<ValidationFault> faults = file.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DigitalFile");
		assertTrue(fault.getProperties().size() == 1);
		assertTrue(fault.getProperties().get(0) == "fileFormat");
		assertTrue(fault.getType() == FaultType.IS_EMPTY);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Invalid file extension test.
	 * <p>
	 * Validation of the real MIME type shall fail.
	 * </p>
	 */
	@Test
	public void invalidZipExtension() {

		final DigitalFile file = new DigitalFile("Demo.pdf", MediaType.ZIP.toString());

		final List<ValidationFault> faults = file.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DigitalFile");
		assertTrue(fault.getProperties().size() == 2);
		assertTrue(fault.getProperties().contains("fileFormat"));
		assertTrue(fault.getProperties().contains("fileName"));
		assertTrue(fault.getType() == FaultType.IS_INCONSISTENT);
		assertTrue(fault.getLevel() == FaultLevel.WARNING);
	}

	/**
	 * Invalid file extension test.
	 * <p>
	 * Validation of the real MIME type shall fail.
	 * </p>
	 */
	@Test
	public void invalidPdfExtension() {

		final DigitalFile file = new DigitalFile("Demo.zip", MediaType.PDF.toString());

		final List<ValidationFault> faults = file.validate(this.locale, true);

		faults.stream().forEach(f -> log.debug(f.toString()));

		assertTrue(faults.size() == 1);

		final ValidationFault fault = faults.get(0);

		assertTrue(fault.getEntity() == "DigitalFile");
		assertTrue(fault.getProperties().size() == 2);
		assertTrue(fault.getProperties().contains("fileFormat"));
		assertTrue(fault.getProperties().contains("fileName"));
		assertTrue(fault.getType() == FaultType.IS_INCONSISTENT);
		assertTrue(fault.getLevel() == FaultLevel.ERROR);
	}

	/**
	 * Validate media type only and ignore parameter of media type
	 */
	@Test
	public void issue16test() {

		final DigitalFile file = new DigitalFile("Issue16.zip", "image/vnd.dxf; format=ascii");

		final List<ValidationFault> faults = file.validate(this.locale, true);
		assertTrue(faults.size() == 0);
	}
}
