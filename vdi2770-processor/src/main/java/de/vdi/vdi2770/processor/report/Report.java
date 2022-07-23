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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.logging.log4j.util.Strings;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;

import de.vdi.vdi2770.processor.common.ContainerType;
import de.vdi.vdi2770.processor.common.IndentUtils;
import de.vdi.vdi2770.processor.common.Message;
import de.vdi.vdi2770.processor.common.MessageLevel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

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
@Log4j2
public class Report {

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final ResourceBundle bundle;

	@Setter(value = AccessLevel.NONE)
	private final Locale locale;

	/**
	 * A unique report entry id
	 */
	@Setter(value = AccessLevel.NONE)
	private final String id = new RandomStringGenerator.Builder().withinRange('0', 'z')
			.filteredBy(LETTERS, DIGITS).build().generate(8);

	/**
	 * If the validated object is a container, this property contains the container.
	 * type.
	 */
	private ContainerType containerType;

	/**
	 * The name of the validated file.
	 */
	@Setter(value = AccessLevel.NONE)
	private String fileName;

	@Setter(value = AccessLevel.NONE)
	private String fileHash;

	/**
	 * A {@link List} of sub-reports.
	 */
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<Report> subReports = new ArrayList<>();

	/**
	 * Create a new {@link Report} instance that as sub report.
	 * 
	 * @param file File the sub report relates to. Must not be <code>null</code>.
	 * @return The resulting sub report.
	 */
	public Report createSubReport(final File file) {

		Preconditions.checkArgument(file != null, "file is null");

		Report sub = new Report(this.locale, file, this.logThreshold);
		addSubReport(sub);

		return sub;
	}

	/**
	 * Get a sub report for file
	 * 
	 * <p>
	 * If a sub report already exists, it will be returned. Otherwise, a new sub
	 * report is created.
	 * </p>
	 * 
	 * @param file File the sub report relates to. Must not be <code>null</code>.
	 * @return An existing or new sub report.
	 */
	public Report getSubReport(final File file) {

		Preconditions.checkArgument(file != null, "file is null");

		final String fileName = fixFileName(file);
		Optional<Report> sub = getSubReports().stream()
				.filter(s -> StringUtils.equals(fileName, s.getFileName())).findFirst();
		if (sub.isPresent()) {
			return sub.get();
		}

		return createSubReport(file);
	}

	private static String fixFileName(final File file) {

		if (file.isDirectory()) {
			return file.getName() + ".zip";
		}

		final String fileExt = com.google.common.io.Files.getFileExtension(file.getName());
		String fileName = file.getName();
		if (Strings.isEmpty(fileExt)) {
			fileName += ".zip";
		}

		return fileName;
	}

	/**
	 * A a new sub-report for this {@link Report} instance.
	 *
	 * @param report A report; must not be <code>null</code>.
	 */
	public void addSubReport(final Report report) {

		Preconditions.checkArgument(report != null, "report is null");

		this.subReports.add(report);
	}

	/**
	 * Get all sub-reports for the next level in the hierarchy.
	 *
	 * @return A {@link List} of {@link Report}s; can be empty.
	 */
	public List<Report> getSubReports() {

		return Collections.unmodifiableList(this.subReports);
	}

	/**
	 * A {@link List} of messages. Message may contain notification, information,
	 * warnings or errors.
	 */
	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<Message> messages = new ArrayList<>();

	@Getter(value = AccessLevel.NONE)
	private MessageLevel logThreshold;

	/**
	 * Add a {@link Message} for this {@link Report} instance.
	 *
	 * @param message A {@link Message} to be added; must not be <code>null</code>.
	 */
	public void addMessage(final Message message) {

		Preconditions.checkArgument(message != null, "message is null or empty");

		this.messages.add(message);
	}

	/**
	 * Add a {@link List} of {@link Message}s for this {@link Report} instance.
	 *
	 * @param messages A {@link List} of {@link Message}s; must not be
	 *                 <code>null</code>.
	 */
	public void addMessages(final List<Message> messages) {

		Preconditions.checkArgument(messages != null, "messages is null");

		this.messages.addAll(messages);
	}

	/**
	 * Get all messages for this report.
	 *
	 * @return A {@link List} of messages; may be empty.
	 */
	public List<Message> getMessages() {

		return Collections.unmodifiableList(filter(this.logThreshold, false, false));
	}

	/**
	 * Get an unmodifiable {@link List} of information messages.
	 * 
	 * If the {@link Report#getLogThreshold()} is set to {@link MessageLevel#WARN}
	 * or {@link MessageLevel#ERROR}, the result will always be an empty
	 * {@link List}.
	 * 
	 * @param deep               If set to <code>true</code>, nested information
	 *                           messages from sub reports will return.
	 * @param ignoreLogThreshold If set to <code>true</code>, the internal
	 *                           {@link Report#getLogThreshold()} value is ignored.
	 * @return A {@link List} of information messages (may be empty).
	 */
	public List<Message> getInfoMessages(boolean deep, boolean ignoreLogThreshold) {

		if (ignoreLogThreshold || this.logThreshold.numeric() == MessageLevel.INFO.numeric()) {
			return Collections.unmodifiableList(filter(MessageLevel.INFO, true, deep));
		}

		return Collections.unmodifiableList(new ArrayList<>());
	}

	/**
	 * Get an unmodifiable {@link List} of warning messages.
	 * 
	 * If the {@link Report#getLogThreshold()} is set to {@link MessageLevel#ERROR},
	 * the result will always be an empty {@link List}.
	 * 
	 * @param deep               If set to <code>true</code>, nested warning
	 *                           messages from sub reports will return.
	 * @param ignoreLogThreshold If set to <code>true</code>, the internal
	 *                           {@link Report#getLogThreshold()} value is ignored.
	 * @return A {@link List} of warning messages (may be empty).
	 */
	public List<Message> getWarnMessages(boolean deep, boolean ignoreLogThreshold) {

		if (ignoreLogThreshold || this.logThreshold.numeric() <= MessageLevel.WARN.numeric()) {
			return Collections.unmodifiableList(filter(MessageLevel.WARN, true, deep));
		}

		return Collections.unmodifiableList(new ArrayList<>());
	}

	/**
	 * Get an unmodifiable {@link List} of error messages.
	 * 
	 * @param deep If set to <code>true</code>, nested error messages from sub
	 *             reports will return.
	 * @return A {@link List} of error messages (may be empty).
	 */
	public List<Message> getErrorMessages(boolean deep) {

		return Collections.unmodifiableList(filter(MessageLevel.ERROR, true, deep));
	}

	/**
	 * ctor
	 * 
	 * @param locale         Desired {@link Locale} for validation messages; must
	 *                       not be <code>null</code>.
	 * @param file           The file this the sub report refers to. Must not be
	 *                       <code>null</code>.
	 * @param minReportLevel A minimal logging level (as threshold)
	 */
	public Report(final Locale locale, final File file, final MessageLevel minReportLevel) {

		Preconditions.checkArgument(locale != null);
		Preconditions.checkArgument(file != null);
		Preconditions.checkArgument(minReportLevel != null);

		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
		this.locale = (Locale) locale.clone();
		this.fileName = fixFileName(file);
		this.logThreshold = minReportLevel;
		try {
			this.fileHash = Hashing.sha256().hashBytes(Files.readAllBytes(file.toPath()))
					.toString();
		} catch (IOException e) {
			log.warn("Can not generate SHA 256 hash", e);
			this.fileHash = UUID.randomUUID().toString();
		}
	}

	/**
	 * Log Messages to logging system
	 * 
	 * @param indentLevel
	 */
	public void logReport(final int indentLevel) {

		logReport(this, indentLevel);
	}

	private void logReport(final Report report, final int indentLevel) {

		log.info(IndentUtils.indent(MessageFormat.format(
				this.bundle.getString("REPORT_MESSAGE_001"), report.getFileName()), indentLevel));
		log.info(
				IndentUtils.indent(MessageFormat.format(this.bundle.getString("REPORT_MESSAGE_002"),
						report.getContainerType()), indentLevel));
		report.getMessages().forEach(m -> {
			log.info(IndentUtils.indent(m.getLevel() + " " + m.getText(), m.getIndent()));
		});
		report.getSubReports().forEach(r -> logReport(r, indentLevel + 1));
	}

	/**
	 * Check, whether this report has error messages. Sub-Reports are not included.
	 * 
	 * @return <code>true</code>, if at least one {@link Message} with the
	 *         {@link MessageLevel#ERROR} exists.
	 */
	public boolean hasErrors() {

		return filter(MessageLevel.ERROR).size() > 0;
	}

	/**
	 * Check, whether this report has at least warnings. Sub-Reports are not
	 * included.
	 * 
	 * @return <code>true</code>, if at least one {@link Message} with the
	 *         {@link MessageLevel#WARN} or {@link MessageLevel#ERROR} exists.
	 */
	public boolean hasWarnings() {

		return filter(MessageLevel.WARN).size() > 0;
	}

	/**
	 * Check, whether this report has error messages.
	 * 
	 * @param deep Include {@link Message}s, of sub-reports.
	 * @return <code>true</code>, if at least one {@link Message} with the
	 *         {@link MessageLevel#ERROR} exists.
	 */
	public boolean hasErrors(boolean deep) {

		return filter(MessageLevel.ERROR, true, deep).size() > 0;
	}

	/**
	 * Filter the messages of this {@link Report} instance by a given
	 * {@link MessageLevel} or above. Sub-Reports are not included.
	 * 
	 * @param level A message level filter. Resulting messages have this level or
	 *              above.
	 * @return {@link List} of {@link Message}, that apply to the given
	 *         {@link MessageLevel} or above.
	 */
	public List<Message> filter(final MessageLevel level) {
		return filter(level, false, false);
	}

	/**
	 * Check, whether this report has at least warnings.
	 * 
	 * @param deep Include {@link Message}s, of sub-reports.
	 * @return <code>true</code>, if at least one {@link Message} with the
	 *         {@link MessageLevel#WARN} or {@link MessageLevel#ERROR} exists.
	 */
	public boolean hasWarnings(boolean deep) {

		return filter(MessageLevel.WARN, false, deep).size() > 0;
	}

	/**
	 * Filter the messages of this {@link Report} instance by a given
	 * {@link MessageLevel} or above.
	 * 
	 * @param level A message level filter. Resulting messages have this level (or
	 *              above).
	 * @param exact If <code>true</code>, the given level is considered exactly. No
	 *              levels above are considered.
	 * @param deep  Include messages of sub-reports.
	 * @return {@link List} of {@link Message}, that apply to the given
	 *         {@link MessageLevel} or above. If deep is set to true,
	 *         {@link Message}s of sub-reports are included in the result, too.
	 */
	public List<Message> filter(final MessageLevel level, boolean exact, boolean deep) {

		final List<Message> result = new ArrayList<>();

		// INFO is the detailed level
		if (level == MessageLevel.INFO) {
			result.addAll(this.messages);
		}

		// Include WARN and ERROR
		if (level == MessageLevel.WARN && !exact) {
			result.addAll(this.messages.stream().filter(
					f -> f.getLevel() == MessageLevel.WARN || f.getLevel() == MessageLevel.ERROR)
					.collect(Collectors.toList()));
		}

		// Include WARN and ERROR
		if (level == MessageLevel.WARN && exact) {
			result.addAll(this.messages.stream().filter(f -> f.getLevel() == MessageLevel.WARN)
					.collect(Collectors.toList()));
		}

		// Include ERROR
		if (level == MessageLevel.ERROR) {
			result.addAll(this.messages.stream().filter(f -> f.getLevel() == level)
					.collect(Collectors.toList()));
		}

		// check sub-reports?
		if (deep && this.subReports.size() > 0) {
			for (final Report sub : this.subReports) {
				result.addAll(sub.filter(level, exact, deep));
			}
		}

		return result;
	}

	public List<Message> filterExact(final MessageLevel level, boolean deep) {

		final List<Message> result = new ArrayList<>();

		result.addAll(this.messages.stream().filter(f -> f.getLevel() == level)
				.collect(Collectors.toList()));

		// check sub-reports?
		if (deep && this.subReports.size() > 0) {
			for (final Report sub : this.subReports) {
				result.addAll(sub.filterExact(level, deep));
			}
		}

		return result;
	}
}