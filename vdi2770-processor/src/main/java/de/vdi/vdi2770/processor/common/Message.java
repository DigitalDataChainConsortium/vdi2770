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
package de.vdi.vdi2770.processor.common;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.model.ValidationFault;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A {@link Message} is used for notifications and errors while validation.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Data
@ToString
@EqualsAndHashCode
public class Message {

	/**
	 * Severity of the message.
	 */
	private MessageLevel level;

	/**
	 * The text of the message.
	 */
	private String text;

	/**
	 * An indent value that can be used for indention. See also {@link IndentUtils}.
	 */
	private int indent;

	/**
	 * Create a new {@link Message} instance.
	 *
	 * @param level  The severity.
	 * @param text   The message text.
	 * @param indent An indent level; must be 0 or greater.
	 */
	public Message(final MessageLevel level, final String text, final int indent) {

		Preconditions.checkArgument(level != null, "level is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(text), "text is null or empty.");
		Preconditions.checkArgument(indent >= 0, "invalid indent");

		this.level = level;
		this.text = text;
		this.indent = indent;
	}

	/**
	 * Create a {@link Message} from a {@link ValidationFault}.
	 * 
	 * @param fault  A fault; must not be <code>null</code>.
	 * @param indent An indent level
	 */
	public Message(final ValidationFault fault, final int indent) {

		Preconditions.checkArgument(fault != null);

		if (fault.getLevel() == FaultLevel.ERROR) {
			this.level = MessageLevel.ERROR;
		} else if (fault.getLevel() == FaultLevel.WARNING) {
			this.level = MessageLevel.WARN;
		} else {
			this.level = MessageLevel.INFO;
		}
		this.text = fault.getMessage();
		this.indent = indent;

	}

	/**
	 * Create a new {@link Message} instance.
	 *
	 * <p>
	 * The message level is {@link MessageLevel#INFO} and the indent is zero.
	 * </p>
	 *
	 * @param text The message text.
	 */
	public Message(final String text) {

		this(MessageLevel.INFO, text, 0);
	}

	/**
	 * Create a new {@link Message} instance.
	 *
	 * <p>
	 * The message level is {@link MessageLevel#INFO}.
	 * </p>
	 *
	 * @param text   The message text.
	 * @param indent An indent level; must be 0 or greater.
	 */
	public Message(final String text, final int indent) {

		this(MessageLevel.INFO, text, indent);
	}

	/**
	 * Create a new {@link Message} instance.
	 *
	 * <p>
	 * The indent is zero.
	 * </p>
	 *
	 * @param level The severity.
	 * @param text  The message text.
	 */
	public Message(final MessageLevel level, final String text) {

		this(level, text, 0);
	}

	/**
	 * Check, if a {@link List} of {@link Message}s contains errors.
	 * 
	 * @param messages List of {@link Message}s. Must not be <code>null</code>.
	 * @return <code>True</code>, if the {@link List} contains {@link Message} with
	 *         {@link MessageLevel} {@link MessageLevel#ERROR}.
	 */
	public static boolean hasErrors(final Collection<Message> messages) {

		Preconditions.checkArgument(messages != null);

		return messages.stream().filter(m -> m.getLevel() == MessageLevel.ERROR).count() > 0;
	}

	/**
	 * Check, if a {@link List} of {@link Message}s contains warnings or errors.
	 * 
	 * @param messages List of {@link Message}s. Must not be <code>null</code>.
	 * @return <code>True</code>, if the {@link List} contains {@link Message} with
	 *         {@link MessageLevel} {@link MessageLevel#WARN} or
	 *         {@link MessageLevel#ERROR}.
	 */
	public static boolean hasWarnings(final Collection<Message> messages) {

		Preconditions.checkArgument(messages != null);

		return messages.stream().filter(m -> m.getLevel() == MessageLevel.WARN).count() > 0;
	}

	/**
	 * Filter a {@link List} of {@link Message}s by a given {@link MessageLevel}.
	 * 
	 * @param messages List of {@link Message}s. Must not be <code>null</code>.
	 * @param level    {@link MessageLevel} to filer.
	 * @return Filtered {@link Message}.
	 */
	public static List<Message> filter(final List<Message> messages, final MessageLevel level) {

		Preconditions.checkArgument(messages != null);

		if (level == MessageLevel.WARN) {
			return messages.stream().filter(
					m -> m.getLevel() == MessageLevel.WARN || m.getLevel() == MessageLevel.ERROR)
					.collect(Collectors.toList());
		}

		if (level == MessageLevel.ERROR) {
			return messages.stream().filter(m -> m.getLevel() == MessageLevel.ERROR)
					.collect(Collectors.toList());
		}

		return messages;
	}
}
