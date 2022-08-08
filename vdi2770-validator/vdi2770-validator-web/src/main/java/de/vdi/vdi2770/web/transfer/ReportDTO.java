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
package de.vdi.vdi2770.web.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;

import de.vdi.vdi2770.processor.common.ContainerType;
import de.vdi.vdi2770.processor.common.Message;
import de.vdi.vdi2770.processor.report.Report;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A report contains validation and processing messages for a document /
 * documentation container or an XML metadata file. Reports are designed
 * hierarchically, because a main document might refer to another document. So,
 * reports can have sub-reports.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Data
@ToString
@EqualsAndHashCode
public class ReportDTO {

	private Locale locale;

	private String id;

	private ContainerType containerType;

	private String fileName;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String fileHash;

	private final List<ReportDTO> subReports = new ArrayList<>();

	private final List<MessageDTO> messages = new ArrayList<>();

	/**
	 * Copy ctor
	 * 
	 * @param report The original {@link Report} instance to copy.
	 */
	public ReportDTO(final Report report) {

		Preconditions.checkArgument(report != null, "Report shall not be null");

		this.locale = report.getLocale();
		this.id = report.getId();
		this.containerType = report.getContainerType();
		this.fileName = report.getFileName();
		this.fileHash = report.getFileHash();

		for (Report sub : report.getSubReports()) {
			this.subReports.add(new ReportDTO(sub));
		}

		for (Message message : report.getMessages()) {
			this.messages.add(new MessageDTO(message));
		}
	}

}
