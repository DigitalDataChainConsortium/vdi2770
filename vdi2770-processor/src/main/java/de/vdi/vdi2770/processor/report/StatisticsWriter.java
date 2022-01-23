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
package de.vdi.vdi2770.processor.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Preconditions;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import lombok.extern.log4j.Log4j2;

/**
 * This is a writer implementation for anonymized statistics.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class StatisticsWriter {

	private final File logFile;

	public StatisticsWriter(final String logFile) {

		Preconditions.checkArgument(Strings.isNotEmpty(logFile));

		this.logFile = new File(logFile);
	}

	public void write(final ReportStatistics stat) {

		Preconditions.checkArgument(stat != null, "stat is null");

		try {

			if (this.logFile.getParentFile() != null && !this.logFile.getParentFile().exists()) {
				java.nio.file.Files.createDirectories(this.logFile.getParentFile().toPath());
			}

			if (!this.logFile.exists() || this.logFile.length() == 0) {
				Files.asCharSink(this.logFile, Charset.forName("UTF-8"), FileWriteMode.APPEND)
						.write(stat.getHeader());
			}

			Files.asCharSink(this.logFile, Charset.forName("UTF-8"), FileWriteMode.APPEND)
					.write(stat.toString());
		} catch (final JsonProcessingException e) {
			log.error("Error writing JSON", e);
		} catch (final IOException e) {
			log.error("Error writing statistics", e);
		}

	}

}
