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

import com.google.common.base.Strings;

/**
 * <p>
 * Information model entity DocumentRelationshipType.
 * </p>
 * <p>
 * The enumeration <em>DocumentRelationshipType</em> formalizes relation types
 * between documents and / or document versions.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public enum DocumentRelationshipType {

	/**
	 * see Affection in IEC 82045-2.
	 */
	Affecting,

	/**
	 * see RefersTo in IEC 82045-2.
	 */
	RefersTo,

	/**
	 * See BasedOn in IEC 82045-2.
	 */
	BasedOn,

	/**
	 * See TranslationOf in IEC 82045-2.
	 */
	TranslationOf;

	/**
	 * Check, whether a given text represents a document relationship type according
	 * to VDI 2770 guideline.
	 *
	 * @param text A text that might represent a document relationship type.
	 * @return <code>true</code>, if the given {@link String} is a valid document
	 *         relationship type.
	 */
	public static boolean isDocumentRelationshipType(final String text) {

		if (Strings.isNullOrEmpty(text)) {
			return false;
		}

		try {
			valueOf(text);
		} catch (@SuppressWarnings("unused") final IllegalArgumentException e) {
			return false;
		}

		return true;
	}
}
