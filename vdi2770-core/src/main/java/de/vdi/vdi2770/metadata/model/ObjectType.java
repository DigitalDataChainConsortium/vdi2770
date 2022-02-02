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
 * Information model entity ObjectType.
 * </p>
 * <p>
 * The enumeration <em>ObjectType</em> defines type information for objects: an
 * object type and an individual.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public enum ObjectType {

	/**
	 * Object type.
	 */
	Type,

	/**
	 * An individual object.
	 * <p>
	 * This individual can be an instance of an object type.
	 * </p>
	 */
	Individual;

	/**
	 * Check, whether a given text represents an object type according to VDI 2770
	 * guideline.
	 *
	 * @param text A text that might represent an object type.
	 * @return <code>true</code>, if the given {@link String} is a valid object
	 *         type.
	 */
	public static boolean isObjectType(final String text) {

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
