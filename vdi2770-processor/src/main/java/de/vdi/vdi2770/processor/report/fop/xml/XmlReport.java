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
package de.vdi.vdi2770.processor.report.fop.xml;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import de.vdi.vdi2770.processor.common.Message;
import de.vdi.vdi2770.processor.report.Report;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Data
public class XmlReport {

	public XmlReport(final Report report) {

		this.id = report.getId();

		if (report.getContainerType() != null) {
			this.containerType = report.getContainerType().name();
		} else {
			this.containerType = "UNKNOWN";
		}

		this.fileHash = report.getFileHash();
		this.fileName = report.getFileName();

		List<Message> reportErrors = report.getErrorMessages(false);
		List<Message> reportWarnings = report.getWarnMessages(false, true);

		this.errorCount = reportErrors.size();
		this.warnCount = reportWarnings.size();

		reportErrors.stream().forEach(m -> this.errors.add(m.getText()));
		reportWarnings.stream().forEach(m -> this.warnings.add(m.getText()));
		report.getInfoMessages(false, true).stream().forEach(m -> this.infos.add(m.getText()));

		for (Report sub : report.getSubReports()) {
			this.subReports.add(new XmlSubReport(sub.getId(), sub.getFileName()));
		}
	}

	@Setter(value = AccessLevel.NONE)
	private final String id;

	private String containerType;

	@Setter(value = AccessLevel.NONE)
	private String fileName;

	@Setter(value = AccessLevel.NONE)
	private String fileHash;

	@JacksonXmlElementWrapper(localName = "SubReports")
	@JacksonXmlProperty(localName = "subReport")
	@Setter(value = AccessLevel.NONE)
	private final List<XmlSubReport> subReports = new ArrayList<>();

	@JacksonXmlElementWrapper(localName = "Infos")
	@JacksonXmlProperty(localName = "info")
	@Setter(value = AccessLevel.NONE)
	private final List<String> infos = new ArrayList<>();

	@JacksonXmlElementWrapper(localName = "Warnings")
	@JacksonXmlProperty(localName = "warning")
	@Setter(value = AccessLevel.NONE)
	private final List<String> warnings = new ArrayList<>();

	@JacksonXmlElementWrapper(localName = "Errors")
	@JacksonXmlProperty(localName = "error")
	@Setter(value = AccessLevel.NONE)
	private final List<String> errors = new ArrayList<>();

	@Setter(value = AccessLevel.NONE)
	private final long errorCount;

	@Setter(value = AccessLevel.NONE)
	private final long warnCount;

}
