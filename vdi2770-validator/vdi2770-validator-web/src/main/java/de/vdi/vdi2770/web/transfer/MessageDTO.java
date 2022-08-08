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

import com.google.common.base.Preconditions;

import de.vdi.vdi2770.processor.common.Message;
import de.vdi.vdi2770.processor.common.MessageLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A {@link MessageDTO} is used for notifications and errors while validation.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Data
@ToString
@EqualsAndHashCode
public class MessageDTO {

	/**
	 * Severity of the message.
	 */
	private MessageLevel level;

	/**
	 * The text of the message.
	 */
	private String text;

	/**
	 * An indent value that can be used for indent. See also {@link IndentUtils}.
	 */
	private int indent;
	
	/**
	 * Copy ctor
	 * 
	 * @param message The original {@link Message} instance to copy.
	 */
	public MessageDTO (final Message message) {
		
		Preconditions.checkArgument(message != null);
		
		this.level = message.getLevel();
		this.text = message.getText();
		this.indent = message.getIndent();
	}
}
