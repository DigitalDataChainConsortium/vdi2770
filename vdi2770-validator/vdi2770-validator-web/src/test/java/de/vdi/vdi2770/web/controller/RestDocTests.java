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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.vdi.vdi2770.web.transfer.ReportProperties;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@AutoConfigureRestDocs
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestDocTests {

	private MockMvc mockMvc;

	private static final String EXAMPLES_FOLDER = "../../examples/";

	private static final String DEMO_VDI_ZIP = "demo_vdi.zip";

	@Value("${vdi2770.http.auth.tokenValue:vdi2770}")
	private String apiKey;

	@Value("${vdi2770.http.auth.tokenName:Api-Key}")
	private String apiKeyName;

	private HttpHeaders getHeaders() {

		HttpHeaders headers = new HttpHeaders();
		headers.set(this.apiKeyName, this.apiKey);
		headers.setAcceptLanguage(Locale.LanguageRange.parse("de"));

		return headers;
	}

	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext,
			RestDocumentationContextProvider restDocumentation) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(documentationConfiguration(restDocumentation)).build();
	}

	@Test
	public void validateContainer() throws Exception {

		final ReportProperties props = new ReportProperties();
		props.setAllowStatistics(true);
		props.setRenderInfo(true);
		props.setRenderWarning(true);
		props.setEnableStrictMode(false);

		final File containerFile = new File(EXAMPLES_FOLDER, DEMO_VDI_ZIP);
		byte[] bytes = Files.readAllBytes(containerFile.toPath());
		MockMultipartFile file = new MockMultipartFile("file", DEMO_VDI_ZIP, "application/zip",
				bytes);

		ObjectMapper mapper = new ObjectMapper();

		this.mockMvc
				.perform(multipart("/rest/report").file(file)
						.param("settings", mapper.writeValueAsString(props)).headers(getHeaders())
						.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
						.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andDo(document("report", preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParts(partWithName("file").description("file to validate")),
						requestParameters(
								parameterWithName("settings").description("method settings"))))
				.andDo(document("headers", requestHeaders(
						headerWithName(this.apiKeyName).description("Header Token value"))));

	}
}
