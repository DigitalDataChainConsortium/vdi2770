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
 * Information model entity Role.
 * </p>
 * <p>
 * The enumeration <em>Role</em> formalizies role names used in VDI 2770
 * guideline.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public enum Role {
	/**
	 * See IEC 82045-2.
	 */
	Author,

	/**
	 * See IEC 82045-2.
	 */
	Manufacturer,

	/**
	 * See IEC 82045-2.
	 */
	Supplier,

	/**
	 * See IEC 82045-2.
	 */
	Responsible;

	/**
	 * Check, whether a given text represents a role name according to VDI 2770
	 * guideline.
	 *
	 * @param text A text that might represent a role name .
	 * @return <code>true</code>, if the given {@link String} is a valid role name .
	 */
	public static boolean isRole(final String text) {

		if (Strings.isNullOrEmpty(text)) {
			return false;
		}

		try {
			valueOf(text);
		} catch (final IllegalArgumentException e) {
			return false;
		}

		return true;
	}
}
