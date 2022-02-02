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
package de.vdi.vdi2770.processor;

import java.io.File;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.vdi.vdi2770.processor.common.IndentUtils;
import de.vdi.vdi2770.processor.common.MessageLevel;
import de.vdi.vdi2770.processor.report.ContainerValidator;
import de.vdi.vdi2770.processor.report.Report;
import de.vdi.vdi2770.processor.zip.ContainerProcessor;
import lombok.extern.log4j.Log4j2;

/**
 * Simple command line application to validate container files and process
 * document meta data
 * 
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class Application {

	private static final String PROCESS_FOLDER = "process";
	private static final String CONTAINER_FILE = "report";

	/**
	 * Main method to run the application
	 * 
	 * @param args Command line argument processed by apache commons cli
	 */
	public static void main(String[] args) {

		Options options = new Options();

		Option containerFileOption = Option.builder(CONTAINER_FILE).hasArg().optionalArg(false)
				.argName("container").desc("validate a container file").build();

		Option processFolderOption = Option.builder(PROCESS_FOLDER).hasArg().optionalArg(false)
				.argName("folder").desc("process a folder").build();

		options.addOption(processFolderOption);

		options.addOption(containerFileOption);

		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption(CONTAINER_FILE)) {

				final String containterFilePath = cmd.getOptionValue(CONTAINER_FILE);
				File containerFile = new File(containterFilePath);
				if (!containerFile.exists()) {
					System.err
							.println("Container file '" + containterFilePath + "' does not exist");
					return;
				}

				final ContainerValidator report = new ContainerValidator(Locale.getDefault(), true);
				final Report result = report.validate(containerFile, MessageLevel.INFO);

				printReport(result, 0);
			} else if (cmd.hasOption(PROCESS_FOLDER)) {

				final String folderPath = cmd.getOptionValue(PROCESS_FOLDER);
				File folder = new File(folderPath);
				if (!folder.exists()) {
					System.err.println("Folder '" + folder.getAbsolutePath() + "' does not exist");
					return;
				}

				ContainerProcessor processor = new ContainerProcessor(Locale.getDefault(), true);
				File zipFile = processor.createContainer(folder);

				System.out.println(
						"File " + zipFile.getAbsolutePath() + " has been created successfully.");
			} else {
				printHelp(options);
			}

		} catch (final ParseException e) {
			log.warn("Command line problem", e);
			printHelp(options);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("VDI 2770 Processor", options);
	}

	private static void printReport(final Report report, final int indentLevel) {

		report.getMessages().forEach(m -> {
			System.out.println(IndentUtils.indent(m.getLevel() + " " + m.getText(), m.getIndent()));
		});
		report.getSubReports().forEach(r -> printReport(r, indentLevel + 1));
	}
}
