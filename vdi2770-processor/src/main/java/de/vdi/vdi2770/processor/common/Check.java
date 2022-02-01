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

import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang3.SystemUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.processor.ProcessorException;
import de.vdi.vdi2770.processor.pdf.PdfValidator;
import de.vdi.vdi2770.processor.zip.ZipUtils;

/**
 * This class is a utility class for common check operations like missing files
 * and so on.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
public class Check {

	private final ResourceBundle bundle;

	/**
	 * ctor
	 * 
	 * @param locale Desired {@link Locale} for validation messages.
	 */
	public Check(final Locale locale) {
		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
	}

	/**
	 * Check, whether a {@link File} exists. If not, throw a
	 * {@link ProcessorException} with a given code for a {@link ResourceBundle}.
	 *
	 * @param file        The {@link File} to be checked.
	 * @param messageCode Message definition must contain a place holder for the
	 *                    file name; must not be <code>null</code> or empty.
	 * @throws ProcessorException       The file does not exists.
	 * @throws IllegalArgumentException One of the given parameter is invalid.
	 */
	public void fileExists(final File file, final String messageCode) throws ProcessorException {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(messageCode),
				"message code is null or empty");

		if (!file.exists()) {
			throw new ProcessorException(MessageFormat.format(this.bundle.getString(messageCode),
					file.getAbsolutePath()));
		}
	}

	/**
	 * 
	 * Check, whether a {@link File} is not a directory. If not, throw a
	 * {@link ProcessorException} with a given code for a {@link ResourceBundle}.
	 *
	 * @param file        The {@link File} to be checked.
	 * @param messageCode Message definition must contain a place holder for the
	 *                    file name; must not be <code>null</code> or empty.
	 * @throws ProcessorException       The file is a directory.
	 * @throws IllegalArgumentException One of the given parameter is invalid.
	 */
	public void fileIsNotDirectory(final File file, final String messageCode)
			throws ProcessorException {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(messageCode),
				"message code is null or empty");

		if (file.isDirectory()) {
			throw new ProcessorException(MessageFormat.format(this.bundle.getString(messageCode),
					file.getAbsolutePath()));
		}

	}

	/**
	 * Check, whether a {@link File} is a directory. If not, throw a
	 * {@link ProcessorException} with a given code for a {@link ResourceBundle}.
	 *
	 * @param file        The {@link File} to be checked.
	 * @param messageCode Message definition must contain a place holder for the
	 *                    file name; must not be <code>null</code> or empty.
	 * @throws ProcessorException The file does not exists.
	 */
	public void isDirectory(final File file, final String messageCode) throws ProcessorException {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(messageCode),
				"message code is null or empty");

		if (!file.isDirectory()) {
			throw new ProcessorException(MessageFormat.format(this.bundle.getString(messageCode),
					file.getAbsolutePath()));
		}
	}

	/**
	 * Check, whether a {@link File} is a ZIP file. If not, throw a
	 * {@link ProcessorException} with a given code for a {@link ResourceBundle}.
	 *
	 * @param file        The {@link File} to be checked.
	 * @param messageCode Message definition must contain a place holder for the
	 *                    file name; must not be <code>null</code> or empty.
	 * @throws ProcessorException The file does not exists.
	 */
	public void isZipFile(final File file, final String messageCode) throws ProcessorException {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(messageCode),
				"message code is null or empty");

		if (!ZipUtils.isZipFile(file)) {
			throw new ProcessorException(MessageFormat.format(this.bundle.getString(messageCode),
					file.getAbsolutePath()));
		}
	}

	/**
	 * 
	 * Check, whether a ZIP {@link File} is a valid file. If not, throw a
	 * {@link ProcessorException} with a given code for a {@link ResourceBundle}.
	 *
	 * @param file        The ZIP {@link File} to be checked.
	 * @param messageCode Message definition must contain a place holder for the
	 *                    file name; must not be <code>null</code> or empty.
	 * @throws ProcessorException       The file is not a valid ZIP {@link File}.
	 * @throws IllegalArgumentException One of the given parameter is invalid.
	 */
	public void isValidZipFile(final File file, final String messageCode)
			throws ProcessorException {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(messageCode),
				"message code is null or empty");

		if (!ZipUtils.isValidZipFile(file)) {
			throw new ProcessorException(MessageFormat.format(this.bundle.getString(messageCode),
					file.getAbsolutePath()));
		}
	}

	/**
	 * 
	 * Check, whether a ZIP {@link File} is not encrypted. If not, throw a
	 * {@link ProcessorException} with a given code for a {@link ResourceBundle}.
	 *
	 * @param file        The ZIP {@link File} to be checked.
	 * @param messageCode Message definition must contain a place holder for the
	 *                    file name; must not be <code>null</code> or empty.
	 * @throws ProcessorException       The ZIL file encrypted.
	 * @throws IllegalArgumentException One of the given parameter is invalid.
	 */
	public void isNotEncryptedZipFile(final File file, final String messageCode)
			throws ProcessorException {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(messageCode),
				"message code is null or empty");

		if (ZipUtils.isEncryptedZipFile(file)) {
			throw new ProcessorException(MessageFormat.format(this.bundle.getString(messageCode),
					file.getAbsolutePath()));
		}
	}

	/**
	 * Check, whether the current operating system is Microsoft Windows. If not,
	 * throw a {@link ProcessorException} with a given code for a
	 * {@link ResourceBundle}.
	 *
	 * @param messageCode Message definition must contain a place holder for the
	 *                    file name; must not be <code>null</code> or empty.
	 * @throws ProcessorException The file does not exists.
	 */
	public void isWindowsOs(final String messageCode) throws ProcessorException {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(messageCode),
				"message code is null or empty");

		if (!SystemUtils.IS_OS_WINDOWS) {
			throw new ProcessorException(this.bundle.getString(messageCode));
		}
	}

	public void isPdfFile(final File file, final String messageCode) throws ProcessorException {

		if (!PdfValidator.isPdfFile(file)) {
			throw new ProcessorException(MessageFormat.format(this.bundle.getString(messageCode),
					file.getAbsolutePath()));
		}

	}

}
