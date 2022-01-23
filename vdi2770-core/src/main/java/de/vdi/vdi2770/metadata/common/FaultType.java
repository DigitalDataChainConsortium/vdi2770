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
package de.vdi.vdi2770.metadata.common;

/**
 * Semantic types of {@link Fault}s.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public enum FaultType {

	/**
	 * Type is unknown.
	 */
	UNKNOWN,

	/**
	 * A {@link String} or object is <code>null</code>.
	 */
	IS_NULL,

	/**
	 * A {@link String} or list is empty.
	 */
	IS_EMPTY,

	/**
	 * The value of an object is not valid according to a given constraint.
	 */
	HAS_INVALID_VALUE,

	/**
	 * One or more object have values that are inconsistent in conjunction.
	 */
	IS_INCONSISTENT,

	/**
	 * A list has duplicate values.
	 */
	HAS_DUPLICATE_VALUE,

	/**
	 * A numerical values exceeds upper bounds.
	 */
	EXCEEDS_UPPER_BOUND,

	/**
	 * A numerical values exceeds upper bounds.
	 */
	EXCEEDS_LOWER_BOUND

}
