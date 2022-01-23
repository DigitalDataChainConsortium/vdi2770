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

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import de.vdi.vdi2770.web.transfer.ReportPropertiesConverter;

/**
 * Web locale configuration
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

	@Autowired
	private ReportPropertiesConverter converter;

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(this.converter);
	}

	@Value("${vdi2770.http.cors.domains:[]}")
	private String[] corsDomains;

	@Value("${vdi2770.http.auth.tokenName:Api-Key}")
	private String authHeaderName;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/rest/**").allowedOrigins(this.corsDomains)
				.allowedMethods("GET", "HEAD", "POST", "OPTIONS").allowCredentials(true)
				.allowedHeaders(this.authHeaderName);
	}

	/**
	 * Define a {@link Locale} resolver using {@link SmartLocaleResolver}
	 *
	 * @return a {@link SmartLocaleResolver}
	 */
	@Bean
	@Primary
	public LocaleResolver localeResolver() {

		AcceptHeaderLocaleResolver localeResolver = new SmartLocaleResolver();
		localeResolver.setDefaultLocale(Locale.ENGLISH);
		Locale.setDefault(Locale.ENGLISH);

		return localeResolver;
	}
}
