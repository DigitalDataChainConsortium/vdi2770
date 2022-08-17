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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.vdi.vdi2770.web.transfer.ReportDTO;

@SuppressWarnings("javadoc")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
		"vdi2770.http.auth.tokenValue=vdi2770", "vdi2770.http.auth.tokenName=Api-Key",
		"spring.servlet.multipart.max-file-size=100KB",
		"spring.servlet.multipart.max-request-size=100KB" })
public class ReportFileTooLargeTest extends BaseControllerTest {

	private static final String DEMO_VDI_ZIP = "demo_vdi.zip";

	/**
	 * Generated port
	 */
	@LocalServerPort
	private int port;

	private static final String EXAMPLES_FOLDER = "../../examples/";

	/**
	 * Upload a file, that exceeds the maximum file upload size and check for HTTP
	 * 413 error
	 */
	@Test
	public void PayloadTooLargeTest() {
		ResponseEntity<ReportDTO> response = requestReportRest(Locale.LanguageRange.parse("de"),
				new File(EXAMPLES_FOLDER, DEMO_VDI_ZIP), this.port);

		assertTrue(response != null);
		assertTrue(response.getStatusCode() == HttpStatus.PAYLOAD_TOO_LARGE);
	}
}
