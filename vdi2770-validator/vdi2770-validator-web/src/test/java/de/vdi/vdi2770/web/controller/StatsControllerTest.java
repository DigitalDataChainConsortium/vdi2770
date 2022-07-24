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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.File;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import de.vdi.vdi2770.web.transfer.Report;
import de.vdi.vdi2770.web.transfer.ReportProperties;
import de.vdi.vdi2770.web.transfer.ReportStatistics;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		"vdi2770.http.auth.tokenValue=vdi2770", "vdi2770.http.auth.tokenName=Api-Key",
		"vdi2770.statistic.logfile=" + StatsControllerTest.STATS_FILE })
public class StatsControllerTest extends BaseControllerTest {

	private static final String DEMO_VDI_ZIP = "empty.zip";

	protected static final String EXAMPLES_FOLDER = "../../examples/";

	protected static final String STATS_FILE = EXAMPLES_FOLDER + "demostats.csv";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	/**
	 * Get report statistics
	 * 
	 * @throws Exception There was an error reading the statistic file
	 */
	@Test
	public void readStatistics() throws Exception {

		final File statsFile = new File(STATS_FILE);

		if (statsFile.exists()) {
			FileUtils.forceDelete(statsFile);
		}

		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
		formData.add("file",
				new FileSystemResource(new File(EXAMPLES_FOLDER, DEMO_VDI_ZIP).toPath()));

		final ReportProperties props = new ReportProperties();
		props.setAllowStatistics(true);
		props.setRenderInfo(true);
		props.setRenderWarning(true);
		props.setEnableStrictMode(true);
		formData.add("settings", props);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData,
				getHeaders(Locale.LanguageRange.parse("en-US")));

		// validate a container file
		final String validateUrl = "http://localhost:" + this.port + "/rest/report";
		this.restTemplate.postForEntity(validateUrl, requestEntity, Report.class);

		// read validation statistics
		final String serverUrl = "http://localhost:" + this.port + "/rest/stats";
		ResponseEntity<ReportStatistics[]> response = this.restTemplate.exchange(serverUrl,
				HttpMethod.GET, new HttpEntity<>(getHeaders(Locale.LanguageRange.parse("de"))),
				ReportStatistics[].class);

		assertSame(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(response.getBody());

		final ReportStatistics[] result = response.getBody();
		assertEquals(1, result.length);

		assertEquals(2, result[0].getErrorIds().size());
		assertEquals(0, result[0].getWarningIds().size());

		FileUtils.forceDelete(statsFile);
	}
}
