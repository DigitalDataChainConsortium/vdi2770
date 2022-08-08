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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.vdi.vdi2770.web.transfer.ReportDTO;
import de.vdi.vdi2770.web.transfer.ReportProperties;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		"vdi2770.http.auth.tokenValue=vdi2770", "vdi2770.http.auth.tokenName=Api-Key"})
public class ReportSettingsExceptionsTest extends BaseControllerTest {

	private static final String DEMO_VDI_ZIP = "demo_vdi.zip";

	/**
	 * Generated port
	 */
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String EXAMPLES_FOLDER = "../../examples/";
	
	private static MultiValueMap<String, Object> createInvalidFormData() throws JsonProcessingException {
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
		formData.add("file",
				new FileSystemResource(new File(EXAMPLES_FOLDER, DEMO_VDI_ZIP).toPath()));

		final ReportProperties props = createReportProperties();
		
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(props);
		
		formData.add("settings", json.replaceAll("true", "FOO"));

		return formData;
	}
	
	@Test
	public void InvalidSettingsTest() throws JsonProcessingException {
		MultiValueMap<String, Object> formData = createInvalidFormData();
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData,
				getHeaders(Locale.LanguageRange.parse("de")));

		final String serverUrl = "http://localhost:" + this.port + "/rest/report";
		ResponseEntity<ReportDTO> response = this.restTemplate.postForEntity(serverUrl, requestEntity,
				ReportDTO.class);
		
		assertTrue(response != null);
		assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
	}

}
