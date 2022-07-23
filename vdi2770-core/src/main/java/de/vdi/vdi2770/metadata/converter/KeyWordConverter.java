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
package de.vdi.vdi2770.metadata.converter;

import java.util.ArrayList;

import de.vdi.vdi2770.metadata.xsd.DocumentDescription;
import de.vdi.vdi2770.metadata.xsd.DocumentDescription.KeyWords;

import com.github.dozermapper.core.CustomConverter;

/**
 * Custom Converter to convert XML keywords to keywords in the information
 * model.
 * <p>
 * This class is needed for dozermapper usage.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public class KeyWordConverter implements CustomConverter {

	/**
	 * <p>
	 * Convert keywords
	 * </p>
	 * <p>
	 * Convert a List of Keywords as {@link java.util.List} of {@link String}s to a an
	 * instance of {@link KeyWords}
	 * </p>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object convert(final Object destination, final Object source, final Class<?> destClass,
			final Class<?> sourceClass) {

		// null source
		if (source == null) {
			return null;
		}

		// source must be List (of Strings) and destination must be KeyWords
		if (sourceClass == ArrayList.class && destClass == DocumentDescription.KeyWords.class) {

			// create new instance
			final KeyWords documentKeyWord = new DocumentDescription.KeyWords();

			// convert source to List<String>
			for (final String keyWord : (ArrayList<String>) source) {
				// add keyword to KeyWord instance
				documentKeyWord.getKeyWord().add(keyWord);
			}

			// return KeyWord instance
			return documentKeyWord;
		}

		// return null otherwise
		return null;
	}
}
