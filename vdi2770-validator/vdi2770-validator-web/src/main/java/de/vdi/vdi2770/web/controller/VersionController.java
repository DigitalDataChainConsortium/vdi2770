/*******************************************************************************
 * Copyright (C) 2022 Johannes Schmidt
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.base.Preconditions;

import de.vdi.vdi2770.web.service.VersionService;

/**
 * Provide version information.
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@RestController
@RequestMapping(path = "/rest")
public class VersionController {

	/**
	 * The service implementation
	 */
	private final VersionService service;

	/**
	 * Boolean flag indicating that the version endpoint is exposed
	 */
	@Value("${vdi2770.version.expose:false}")
	private boolean versionControllerEnabled;

	/**
	 * ctor
	 *
	 * @param service The service implementation
	 */
	public VersionController(final VersionService service) {

		Preconditions.checkArgument(service != null, "service is null");

		this.service = service;
	}

	/**
	 * Return version as String
	 * 
	 * @return
	 */
	@RequestMapping(path = "/version", method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVersion() {

		// version information must be enabled
		if (!this.versionControllerEnabled) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
		}

		// return String as JSON
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		return new ResponseEntity<String>(this.service.getVersion(), responseHeaders,
				HttpStatus.OK);
	}
}
