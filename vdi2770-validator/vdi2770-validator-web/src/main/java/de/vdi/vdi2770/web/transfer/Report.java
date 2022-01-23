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

import de.vdi.vdi2770.processor.common.ContainerType;
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
public class Report {

	private Locale locale;

	private String id;

	private ContainerType containerType;

	private String fileName;

	private String fileHash;

	private final List<Report> subReports = new ArrayList<>();

	private final List<Message> messages = new ArrayList<>();

}
