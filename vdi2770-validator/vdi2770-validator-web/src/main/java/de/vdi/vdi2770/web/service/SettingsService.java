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

package de.vdi.vdi2770.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.vdi.vdi2770.web.transfer.ApplicationSettingsDTO;

/**
 * This is a service implementation for application settings.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 * @since 0.9.9
 */
@Service
public class SettingsService {

	/**
	 * Version string as property 
	 */
	@Value("${spring.servlet.multipart.max-request-size:UNKNOWN-VERSION}")
	private String maxUploadSize;
	
	/**
	 * Get application settings as data transfer object (DTO)
	 * @return Application settings
	 */
	public ApplicationSettingsDTO getApplicationSettings() {
		
		ApplicationSettingsDTO result = new ApplicationSettingsDTO();
		result.setMaxUploadSize(this.maxUploadSize);
		
		return result;
	}
	
}
