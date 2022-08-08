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

import de.vdi.vdi2770.web.service.SettingsService;
import de.vdi.vdi2770.web.transfer.ApplicationSettingsDTO;

/**
 * Provide information about application settings.
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 * @since 0.9.9
 */
@RestController
@RequestMapping(path = "/rest")
public class SettingsController {

	/**
	 * The service implementation
	 */
	private final SettingsService service;

	/**
	 * Boolean flag indicating that the version endpoint is exposed
	 */
	@Value("${vdi2770.settings.expose:true}")
	private boolean settingsControllerEnabled;

	/**
	 * ctor
	 *
	 * @param service The service implementation
	 */
	public SettingsController(final SettingsService service) {

		Preconditions.checkArgument(service != null, "service is null");

		this.service = service;
	}

	/**
	 * Return application settings
	 * 
	 * @return Application settings wrapped as data transfer object (DTO)
	 * 
	 * @throws ResponseStatusException If controller is not enabled, return HTTP 404
	 */
	@RequestMapping(path = "/settings", method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ApplicationSettingsDTO> getSettings() throws ResponseStatusException {

		// version information must be enabled
		if (!this.settingsControllerEnabled) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
		}

		// return String as JSON
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

		return new ResponseEntity<ApplicationSettingsDTO>(this.service.getApplicationSettings(),
				responseHeaders, HttpStatus.OK);
	}
}
