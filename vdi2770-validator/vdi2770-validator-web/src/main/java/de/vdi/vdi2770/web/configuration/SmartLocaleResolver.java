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
package de.vdi.vdi2770.web.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * With this implementation of an {@link AcceptHeaderLocaleResolver}, we can read the
 * Accept-Language header from requests to set the desired {@link Locale} for report outputs.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied Informatics InfAI)
 *
 */
@Component
public class SmartLocaleResolver extends AcceptHeaderLocaleResolver {

	// at the moment, we support GERMAN, ENGLISH and Chinese
	private final List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("de"),
			new Locale("zh"));

	@Override
	public Locale resolveLocale(HttpServletRequest request) {

		// Accept-Language header exists?
		if (!StringUtils.hasText(request.getHeader("Accept-Language"))) {
			// return default locale
			return Locale.getDefault();
		}
		
		// read languages
		String requestesLanguages = request.getHeader("Accept-Language");
		List<Locale.LanguageRange> list = Locale.LanguageRange
				.parse(requestesLanguages);

		Locale locale = Locale.lookup(list, this.LOCALES);

		if (locale == null) {
			return Locale.getDefault();
		}
		
		if (locale.getLanguage().toLowerCase().equals("zh")) {
			locale = new Locale("zh", "CN");
		}
		
		return locale;
	}
}
