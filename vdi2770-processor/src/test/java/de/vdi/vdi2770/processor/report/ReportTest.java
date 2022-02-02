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
package de.vdi.vdi2770.processor.report;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.vdi.vdi2770.metadata.MetadataException;
import de.vdi.vdi2770.processor.ProcessorException;
import de.vdi.vdi2770.processor.common.IndentUtils;
import lombok.extern.log4j.Log4j2;

/**
 * Tests for the {@link Report} class.
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class ReportTest {

	/**
	 * Report on a valid document container file.
	 * 
	 * @throws ProcessorException
	 * @throws MetadataException
	 */
	@Test
	public void validDocumentReportTest() throws ProcessorException, MetadataException {

		final ContainerValidator report = new ContainerValidator(Locale.getDefault(), true);
		final Report result = report.validate("../examples/container/documentcontainer.zip");

		printReport(result, 0);
	}

	/**
	 * Report on a valid documentation container file
	 * 
	 * @throws ProcessorException
	 * @throws MetadataException
	 */
	@Test
	public void validDocumentationReportTest() throws ProcessorException, MetadataException {

		final ContainerValidator report = new ContainerValidator(Locale.getDefault(), true);
		final Report result = report.validate("../examples/container/documentationcontainer.zip");

		printReport(result, 0);
	}

	private static void printReport(final Report report, final int indentLevel) {

		report.getMessages().forEach(m -> {
			log.debug(IndentUtils.indent(m.getLevel() + " " + m.getText(), m.getIndent()));
		});
		report.getSubReports().forEach(r -> printReport(r, indentLevel + 1));
	}

	/**
	 * Report object id relations as information.
	 * 
	 * @throws ProcessorException
	 * @throws MetadataException
	 */
	@Test
	public void invalidObjectIdsReportTest() throws ProcessorException, MetadataException {

		final ContainerValidator report = new ContainerValidator(Locale.getDefault(), true);
		final Report result = report.validate("../examples/container/objectreferences.zip");

		printReport(result, 0);
	}

}
