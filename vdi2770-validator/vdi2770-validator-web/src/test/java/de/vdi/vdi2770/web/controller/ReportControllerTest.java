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
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.google.common.io.Files;

import de.vdi.vdi2770.processor.common.ContainerType;
import de.vdi.vdi2770.web.transfer.Report;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Test implementations for container validation and reporting. See
 * {@link ReportController} for details on the implementation.
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		"vdi2770.http.auth.tokenValue=vdi2770", "vdi2770.http.auth.tokenName=Api-Key" })
public class ReportControllerTest extends BaseControllerTest {

	private static final String DEMO_VDI_ZIP = "demo_vdi.zip";

	/**
	 * Generated port
	 */
	@LocalServerPort
	private int port;

	private static final String EXAMPLES_FOLDER = "../../examples/";

	/**
	 * Call /rest/reportpdf.
	 * 
	 * @param languages Range of languages to accept
	 * @return The response as {@link ResponseEntity} instance.
	 */
	private ResponseEntity<byte[]> requestReportPdf(final List<Locale.LanguageRange> languages) {

		MultiValueMap<String, Object> formData = createFormData(
				new File(EXAMPLES_FOLDER, DEMO_VDI_ZIP));
		HttpHeaders headers = getHeaders(languages);
		// extend the header and set application/pdf as expected result
		headers.add("ACCEPT", MediaType.APPLICATION_PDF_VALUE);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData,
				headers);
		final String serverUrl = "http://localhost:" + this.port + "/rest/reportpdf";
		ResponseEntity<byte[]> response = this.restTemplate.exchange(serverUrl, HttpMethod.POST,
				requestEntity, byte[].class);

		return response;
	}

	/**
	 * Validate a document container file.
	 */
	@Test
	public void validateContainerRest() {

		// REST call
		ResponseEntity<Report> response = requestReportRest(Locale.LanguageRange.parse("de"),
				new File(EXAMPLES_FOLDER, DEMO_VDI_ZIP), this.port);

		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody() != null);

		final Report result = response.getBody();

		// Report validation
		assertTrue(result.getContainerType() == ContainerType.DOCUMENTATION_CONTAINER);
		assertTrue(StringUtils.equals(result.getFileName(), DEMO_VDI_ZIP));

		assertTrue(result.getLocale().equals(Locale.forLanguageTag("de")));
		assertTrue(result.getMessages().size() == 17);
		assertTrue(result.getSubReports().size() == 2);
	}

	/**
	 * Use the REST validation API call to test language support.
	 * 
	 * <p>
	 * This test demonstrates the processing of the accept language header.
	 * </p>
	 */
	@Test
	public void checkLocales() {

		final File upload = new File(EXAMPLES_FOLDER, DEMO_VDI_ZIP);

		// use language and country language tag
		ResponseEntity<Report> response = requestReportRest(Locale.LanguageRange.parse("en-US"),
				upload, this.port);
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody() != null);
		Report result = response.getBody();
		assertTrue(result.getLocale().equals(Locale.forLanguageTag("en")));

		// request Spanish language and return English
		response = requestReportRest(Locale.LanguageRange.parse("es"), upload, this.port);
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody() != null);
		result = response.getBody();
		assertTrue(result.getLocale().equals(Locale.forLanguageTag("en")));

		// use a list of languages (first two are not supported)
		response = requestReportRest(Locale.LanguageRange.parse("es,ja,de"), upload, this.port);
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody() != null);
		result = response.getBody();
		assertTrue(result.getLocale().equals(Locale.forLanguageTag("de")));

		// use a list of languages (first two are not supported) and use
		// country code
		response = requestReportRest(Locale.LanguageRange.parse("es,ja,de-AT,en"), upload,
				this.port);
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody() != null);
		result = response.getBody();
		assertTrue(result.getLocale().equals(Locale.forLanguageTag("de")));

		// use a list of languages (first two are not supported) and use
		// country code
		response = requestReportRest(Locale.LanguageRange.parse("es,ja,en,de-AT"), upload,
				this.port);
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody() != null);
		result = response.getBody();
		assertTrue(result.getLocale().equals(Locale.forLanguageTag("en")));

		// use a list of languages (first two are not supported) and use
		// country code
		response = requestReportRest(Locale.LanguageRange.parse("zh"), upload, this.port);
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody() != null);
		result = response.getBody();
		assertTrue(result.getLocale().equals(Locale.forLanguageTag("zh-cn")));
	}

	/**
	 * Call /rest/reportpdf API endpoint and check, whether the result bytes are a
	 * PDF file.
	 * 
	 * @throws IOException
	 */
	@Test
	public void validateContainerPdf() throws IOException {

		// REST call
		ResponseEntity<byte[]> response = requestReportPdf(Locale.LanguageRange.parse("de"));

		// HTTP 200
		assertTrue(response.getStatusCode() == HttpStatus.OK);
		assertTrue(response.getBody() != null);

		// check body
		final byte[] result = response.getBody();
		assertTrue(result != null);
		assertTrue(result.length > 0);

		// save as PDF file (and delete after test run)
		File pdfFile = new File(UUID.randomUUID() + ".pdf");
		pdfFile.deleteOnExit();
		Files.write(result, pdfFile);

		// Check mediatype
		final Tika tika = new Tika();
		final String fileMimeType = tika.detect(pdfFile);
		assertTrue(fileMimeType.equals(com.google.common.net.MediaType.PDF.toString()));
	}
}
