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

/**
 * Pre defined ref types for objects accoring to VDII 2770.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class RefType {

	/**
	 * Predefined name for an order code.
	 */
	public static final String ORDER_CODE = "order code";

	/**
	 * Predefined name for an article number.
	 */
	public static final String ARTICLE_NUMER = "article number";

	/**
	 * Predefined name for a product type.
	 */
	public static final String PRODUCT_TYPE = "product type";

	/**
	 * Predefined name for a GTIN (global trade ID).
	 */
	public static final String GTIN = "GTIN";

	/**
	 * Predefined name for a EAN (European article number).
	 */
	public static final String EAN = "EAN";

	/**
	 * Predefined name for an ID according to DIN SPEC 91406.
	 */
	public static final String DIN_SPEC_91406_ID = "instance of object uri";

	/**
	 * Predefined name for a serial number.
	 */
	public static final String SERIAL_NUMBER = "serial number";
}
