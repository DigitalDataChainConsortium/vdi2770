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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * The elements of the information model of VDI 2770 are sometimes a deeply
 * structured. In some cases, one needs a textual representation of these
 * elements. This class provides some {@link String} conversion methods.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class StringRepresentations {

	/**
	 * Convert a {@link DocumentId} to as String.
	 * <p>
	 * The {@link DocumentId#getId()} is concatenated with
	 * {@link DocumentId#getDomainId()} by using a '@'.
	 * </p>
	 *
	 * @param id A {@link DocumentId}
	 * @return The {@link String} representation of the Id.
	 * @throws IllegalArgumentException The given id is <code>null</code>.
	 */
	public static String documentIdAsText(final DocumentId id) {

		Preconditions.checkArgument(id != null);

		return id.getId() + "@" + id.getDomainId();
	}

	/**
	 * Convert a {@link List} of languages of a {@link DocumentVersion}.
	 * <p>
	 * The language codes are concatenated by comma.
	 * </p>
	 *
	 * @param version A {@link DocumentVersion} with language codes
	 * @return The {@link String} representation of languages.
	 * @throws IllegalArgumentException The given version is <code>null</code>.
	 */
	public static String languagesAsText(final DocumentVersion version) {

		Preconditions.checkArgument(version != null);

		return version.getLanguage().stream().collect(Collectors.joining(", "));
	}

	/**
	 * Filter every VDI 2770 classification of a {@link List} of
	 * {@link DocumentClassification}s and concat them with comma.
	 * <p>
	 * A {@link Document} can be classified multiple times.
	 * </p>
	 *
	 * @param classification A {@link List} of {@link DocumentClassification}.
	 * @return he {@link String} representation of the VDI 2770 classes. * @throws
	 *         IllegalArgumentException The given classification is
	 *         <code>null</code>.
	 */
	public static String vdi2770ClassIdsAsText(final List<DocumentClassification> classification) {

		Preconditions.checkArgument(classification != null);

		return classification.stream()
				.filter(c -> StringUtils.equalsIgnoreCase(
						Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME, c.getClassificationSystem()))
				.map(c -> c.getClassId()).collect(Collectors.joining(", "));
	}
}
