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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import de.vdi.vdi2770.processor.common.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Data
public class ReportStatistics {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Setter(value = AccessLevel.NONE)
	private final LocalDateTime timestamp;

	@Setter(value = AccessLevel.NONE)
	private final String hash;

	@Setter(value = AccessLevel.NONE)
	private final Set<String> errorIds = new HashSet<>();

	@Setter(value = AccessLevel.NONE)
	private final Set<String> warningIds = new HashSet<>();

	public ReportStatistics(final String hash, final String timestamp, final String[] errorIds,
			final String[] warningIds) {

		this.hash = hash;

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
		this.timestamp = LocalDateTime.parse(timestamp, formatter);

		for (String error : errorIds) {
			this.errorIds.add(error.trim());
		}

		for (String warn : warningIds) {
			this.warningIds.add(warn.trim());
		}
	}

	public ReportStatistics(final Report report) {

		this.timestamp = LocalDateTime.now();
		this.hash = report.getFileHash();

		setErrorsIds(report.getErrorMessages(true));
		setWarningIds(report.getWarnMessages(true, true));

	}

	private void setWarningIds(final List<Message> warnings) {

		this.warningIds.addAll(
				warnings.stream().map(w -> w.getText().substring(0, w.getText().indexOf(" ")))
						.collect(Collectors.toList()));
	}

	private void setErrorsIds(final List<Message> errors) {

		this.errorIds
				.addAll(errors.stream().map(w -> w.getText().substring(0, w.getText().indexOf(" ")))
						.collect(Collectors.toList()));
	}

	@JsonIgnore
	public String getHeader() {
		StringBuilder builder = new StringBuilder();
		builder.append("File;");
		builder.append("Timestamp;");
		builder.append("Errors;");
		builder.append("Warnings");
		builder.append("\r\n");

		return builder.toString();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append(this.getHash());
		builder.append(";");

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
		String formattedTimestamp = this.timestamp.toString(formatter);
		builder.append(formattedTimestamp);
		builder.append(";");

		builder.append(this.errorIds.stream().map(n -> String.valueOf(n))
				.collect(Collectors.joining(", ")));
		builder.append(";");

		builder.append(this.warningIds.stream().map(n -> String.valueOf(n))
				.collect(Collectors.joining(", ")));
		builder.append("\r\n");

		return builder.toString();
	}

}
