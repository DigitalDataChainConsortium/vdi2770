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
package de.vdi.vdi2770.metadata.xml;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * Definition of file names according to VDI 2770 guideline.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class FileNames {

	/**
	 * Name of the XML file that represents the metadata for the main document.
	 */
	public static final String MAIN_DOCUMENT_XML_FILE_NAME = "VDI2770_Main.xml";

	/**
	 * The file name of XML metadata files.
	 */
	public static final String METADATA_XML_FILE_NAME = "VDI2770_Metadata.xml";

	/**
	 * Name of the PDF file that represents a main document.
	 */
	public static final String MAIN_DOCUMENT_PDF_FILE_NAME = "VDI2770_Main.pdf";

	/**
	 * Check, whether the name of a given {@link File} is conform to the name of a
	 * PDF main document.
	 *
	 * @param file A file to be checked.
	 * @return <code>true</code>, if the file name is conform to the specification.
	 * @throws IllegalArgumentException The given file is <code>null</code>.
	 */
	public static boolean isPdfMainDocument(final File file) {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(file.exists(), "file does not exist");

		return StringUtils.equals(file.getName(), MAIN_DOCUMENT_PDF_FILE_NAME);
	}

	/**
	 * Check, whether the name of a given {@link File} is conform to the name of a
	 * VDI 2770 metadata file.
	 *
	 * @param file A file to be checked.
	 * @return <code>true</code>, if the file name is conform to the specification.
	 * @throws IllegalArgumentException The given file is <code>null</code> or does
	 *                                  not exist.
	 */
	public static boolean isMetadataFile(final File file) {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(file.exists(), "file does not exist");

		return Arrays.asList(METADATA_XML_FILE_NAME, MAIN_DOCUMENT_XML_FILE_NAME)
				.contains(file.getName());
	}

	/**
	 * Check, whether the name of a given {@link File} is a metadata file for a main
	 * document.
	 *
	 * @param file A file to be checked.
	 * @return <code>true</code>, if the file name is conform to the specification.
	 * @throws IllegalArgumentException The given file is <code>null</code>.
	 */
	public static boolean isMainDocumentMetadataFile(final File file) {

		Preconditions.checkArgument(file != null, "file is null");
		Preconditions.checkArgument(file.exists(), "file does not exist");

		return MAIN_DOCUMENT_XML_FILE_NAME.equals(file.getName());
	}
}
