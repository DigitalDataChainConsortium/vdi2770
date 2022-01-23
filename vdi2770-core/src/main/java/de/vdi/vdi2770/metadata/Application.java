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
package de.vdi.vdi2770.metadata;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.xml.DemoXml;
import de.vdi.vdi2770.metadata.xml.FileNames;
import de.vdi.vdi2770.metadata.xml.XmlReader;
import de.vdi.vdi2770.metadata.xml.XmlValidationFault;
import lombok.extern.log4j.Log4j2;

/**
 * Sample application to demonstrate meta data processing.
 * <p>
 * This app supports to modes:
 * <ul>
 * <li>Create a simple XML meta data file including test data.</li>
 * <li>Validate a XML file</li>
 * </ul>
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@Log4j2
public class Application {

	private static final String DEMO = "demo";

	private static final String VALIDATE = "validate";

	/**
	 * Main method
	 *
	 * @param args Command line arguments processed by apache commons cli.
	 */
	public static void main(String[] args) {

		Options options = new Options();

		Option demoOption = Option.builder(DEMO).desc("Create a demo XML file").build();

		Option validateXmlOption = Option.builder(VALIDATE).hasArg().optionalArg(false)
				.argName("metadataFile").desc("validate a XML metadata file").build();

		options.addOption(demoOption);
		options.addOption(validateXmlOption);

		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption(DEMO)) {

				final DemoXml demo = new DemoXml(Locale.getDefault());

				final File demoXmlFile = new File(".", FileNames.METADATA_XML_FILE_NAME);
				demo.createXmlFile(demoXmlFile, true);

				System.out.println("Created demo XML file " + FileNames.METADATA_XML_FILE_NAME);
			} else if (cmd.hasOption(VALIDATE)) {

				final String xmlFilePath = cmd.getOptionValue(VALIDATE);
				File xmlFile = new File(xmlFilePath);
				if (!xmlFile.exists()) {
					System.err.println("XML file '" + xmlFilePath + "' does not exist");
					return;
				}

				final XmlReader reader = new XmlReader(Locale.getDefault());

				final List<XmlValidationFault> faults = reader.validate(xmlFile);

				if (Fault.hasWarnings(faults)) {
					faults.stream().forEach(f -> System.out.println(f.getLevel() + ": "
							+ f.getMessage() + " [ " + f.getOriginalValue() + "]"));
				} else {
					System.out.println("XML is valid");
				}

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
		formatter.printHelp("VDI 2770 Metadata", options);
	}
}
