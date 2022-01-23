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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.vdi.vdi2770.processor.report.ReportStatistics;

/**
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@RestController
@RequestMapping(path = "/rest")
public class StatsController {

	@Value("${vdi2770.statistic.logfile:./stats/statistics.csv}")
	private File logFile;

	public StatsController() {
	}

	@RequestMapping(path = "/stats", method = { RequestMethod.GET }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public List<ReportStatistics> getStatistics(
			@RequestParam(name = "timestamp", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime requestTimesamp)
			throws IOException {

		List<ReportStatistics> result = new ArrayList<ReportStatistics>();

		if (this.logFile == null || !this.logFile.exists()) {
			return result;
		}

		List<String> allLines = Files.readAllLines(this.logFile.toPath());
		allLines.remove(0);
		for (String line : allLines) {

			if (!StringUtils.isEmpty(line)) {

				String[] tokens = line.split(";");

				final String hash = tokens[0].trim();
				final String timestamp = tokens[1].trim();

				DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat
						.forPattern("yyyy-MM-dd'T'HH:mm:ss");
				LocalDateTime t = LocalDateTime.parse(timestamp, formatter);

				if (requestTimesamp == null || t.isAfter(requestTimesamp)) {

					String[] errors = new String[] {};
					if (tokens.length > 2 && !StringUtils.isEmpty(tokens[2])) {
						errors = tokens[2].trim().split(",");
					}

					String[] warnings = new String[] {};
					if (tokens.length > 3 && !StringUtils.isEmpty(tokens[3])) {
						warnings = tokens[3].trim().split(",");
					}

					ReportStatistics stats = new ReportStatistics(hash, timestamp, errors,
							warnings);

					result.add(stats);
				}

			}
		}

		return result;

	}

}
