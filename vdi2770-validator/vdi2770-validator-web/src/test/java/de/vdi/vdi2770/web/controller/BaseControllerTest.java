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
package de.vdi.vdi2770.web.controller;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import de.vdi.vdi2770.web.transfer.Report;
import de.vdi.vdi2770.web.transfer.ReportProperties;

@Component
public class BaseControllerTest {

	@Value("${vdi2770.http.auth.tokenValue:vdi2770}")
	private String apiKey;

	@Value("${vdi2770.http.auth.tokenName:Api-Key}")
	private String apiKeyName;

	@Autowired
	protected TestRestTemplate restTemplate;

	protected HttpHeaders getHeaders(final List<Locale.LanguageRange> languages) {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set(this.apiKeyName, this.apiKey);
		headers.setAcceptLanguage(languages);

		return headers;
	}

	/**
	 * Get default settings for Report API
	 * 
	 * @return
	 * @throws JsonProcessingException
	 */
	protected static ReportProperties createReportProperties() {
		final ReportProperties props = new ReportProperties();
		props.setAllowStatistics(true);
		props.setRenderInfo(true);
		props.setRenderWarning(true);
		props.setEnableStrictMode(true);

		return props;
	}

	/**
	 * Build the form data
	 * 
	 * @return
	 */
	protected static MultiValueMap<String, Object> createFormData(final File file) {
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
		formData.add("file", new FileSystemResource(file.toPath()));

		final ReportProperties props = createReportProperties();
		formData.add("settings", props);

		return formData;
	}

	/**
	 * Call /rest/report.
	 * 
	 * @param languages Range of languages to accept
	 * @return The response as {@link ResponseEntity} instance.
	 */
	protected ResponseEntity<Report> requestReportRest(final List<Locale.LanguageRange> languages,
			final File file, int port) {

		MultiValueMap<String, Object> formData = createFormData(file);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData,
				getHeaders(languages));

		final String serverUrl = "http://localhost:" + port + "/rest/report";
		ResponseEntity<Report> response = this.restTemplate.postForEntity(serverUrl, requestEntity,
				Report.class);

		return response;
	}

}
