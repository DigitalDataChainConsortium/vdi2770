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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.extern.java.Log;

/**
 * <p>
 * Information model entity DigitalFile.
 * </p>
 * <p>
 * <em>DigitalFile</em> represents any file including a file name and a file
 * type (MIME type).
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log
@ToString(includeFieldNames = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class DigitalFile implements ModelEntity {

	private static final String ENTITY = "DigitalFile";

	private String fileName;

	private String fileFormat;

	/**
	 * Validate this instance of <em>DigitalFile</em>.
	 *
	 * @param parent The name of a parent element. Can be <code>null</code>.
	 * @param locale Desired {@link Locale} for validation messages.
	 * @param strict If <code>true</code>, strict validation is enabled.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	@Override
	public List<ValidationFault> validate(final String parent, final Locale locale,
			boolean strict) {

		Preconditions.checkArgument(locale != null);

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		final List<ValidationFault> faults = new ArrayList<>();

		// file name must not be null or empty
		if (Strings.isNullOrEmpty(this.fileName)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.fileName, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL4"));
			faults.add(fault);
		}

		// file format must not be null or empty
		if (Strings.isNullOrEmpty(this.fileFormat)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.fileFormat, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL5"));
			faults.add(fault);
		} else {
			// try to parse media type
			try {
				MediaType.parse(this.fileFormat);
			} catch (final IllegalArgumentException e) {

				if (log.isLoggable(Level.FINE)) {
					log.log(Level.FINE, e.getMessage());
				}

				// unknown media type, return as warning
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.fileFormat, parent,
						FaultLevel.WARNING, FaultType.HAS_INVALID_VALUE);
				fault.setOriginalValue(this.fileFormat);
				fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
				faults.add(fault);
			}
		}

		if (!Strings.isNullOrEmpty(this.fileName) && !Strings.isNullOrEmpty(this.fileFormat)) {

			// if media type is PDF, the file extension must be .pdf
			if (MediaType.PDF.toString().equalsIgnoreCase(this.fileFormat.toLowerCase())
					&& !this.fileName.toLowerCase().endsWith(".pdf")) {
				final ValidationFault fault = new ValidationFault(ENTITY,
						Arrays.asList(Fields.fileName, Fields.fileFormat), parent, FaultLevel.ERROR,
						FaultType.IS_INCONSISTENT);
				fault.setMessage(bundle.getString(ENTITY + "_VAL2"));
				faults.add(fault);
			}

			// if media type is ZIP, the file extension must be .zip
			if (MediaType.ZIP.toString().equalsIgnoreCase(this.fileFormat.toLowerCase())
					&& !this.fileName.toLowerCase().endsWith(".zip")) {
				final ValidationFault fault = new ValidationFault(ENTITY,
						Arrays.asList(Fields.fileName, Fields.fileFormat), parent,
						FaultLevel.WARNING, FaultType.IS_INCONSISTENT);
				fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
				faults.add(fault);
			}
		}

		return faults;
	}
}
