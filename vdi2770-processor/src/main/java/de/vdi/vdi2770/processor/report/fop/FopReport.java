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
package de.vdi.vdi2770.processor.report.fop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import de.vdi.vdi2770.processor.report.Report;
import de.vdi.vdi2770.processor.report.fop.xml.XmlReportContent;
import lombok.extern.log4j.Log4j2;

/**
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class FopReport {

	private final Locale locale;

	private static final String XSL_FOLDER = "fo";

	private static final String XSLT_FILE = "fop.xsl";

	private static final String XCONF_FILE = "fop.xconf";

	public FopReport(final Locale locale) {

		Preconditions.checkArgument(locale != null, "locale is null");

		this.locale = (Locale) locale.clone();

		copyRessources();
	}

	/**
	 * Copy files needed to generate report document
	 */
	private void copyRessources() {

		copyResource(new File(XSL_FOLDER, XSLT_FILE), false);
		copyResource(new File(XSL_FOLDER, XCONF_FILE), false);

		copyFonts();
	}

	private void copyFonts() {

		List<String> fontFileNames = Arrays.asList(
				// Google Roboto files
				"Roboto-Bold.ttf", "Roboto-BoldItalic.ttf", "Roboto-Italic.ttf",
				"Roboto-Regular.ttf",
				// Chinese Babel Stone file
				"BabelStoneHan.ttf",
				// icons as SVG files
				"bug.svg", "circle.svg", "exclamation-triangle.svg", "info.svg");

		for (String fontFileName : fontFileNames) {
			copyResource(new File(XSL_FOLDER, fontFileName));
		}

	}

	private void copyResource(final File file) {
		copyResource(file, file.getName(), false);
	}

	private void copyResource(final File file, boolean copyAlways) {
		copyResource(file, file.getName(), copyAlways);
	}

	private void copyResource(final File file, final String resourceFileName, boolean copyAlways) {

		try {
			if (!file.exists() || copyAlways) {
				try (InputStream is = getClass().getResourceAsStream("/" + resourceFileName)) {
					FileUtils.copyInputStreamToFile(is, file);
				}
			}
		} catch (final IOException e) {
			log.warn("Can not copy " + resourceFileName + " file from ressources", e);
		}
	}

	public void createPdf(final Report report, final File outFile, boolean renderWarning,
			boolean renderInfo, boolean renderFileHash) throws IOException, FopReportException {

		Preconditions.checkArgument(report != null, "report is null");

		byte[] pdfBytes = convertToPDF(report, renderWarning, renderInfo, renderFileHash);
		Files.write(pdfBytes, outFile);
	}

	public byte[] createPdf(final Report report, boolean renderWarning, boolean renderInfo,
			boolean renderFileHash) throws FopReportException {

		Preconditions.checkArgument(report != null, "report is null");

		return convertToPDF(report, renderWarning, renderInfo, renderFileHash);
	}

	private static String convertReport(final Report report) throws JsonProcessingException {

		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

		return xmlMapper.writeValueAsString(new XmlReportContent(report));
	}

	private byte[] convertToPDF(final Report report, boolean renderWarning, boolean renderInfo,
			boolean renderFileHash) throws FopReportException {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			final File xslFolder = new File(XSL_FOLDER);

			final File xsltFile = new File(xslFolder, XSLT_FILE);

			final String xml = convertReport(report);
			final StreamSource xmlSource = new StreamSource(new StringReader(xml));

			final FopFactory fopFactory = FopFactory.newInstance(new File(xslFolder, XCONF_FILE));

			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

			transformer.setParameter("RENDER_INFOS", renderInfo ? "ON" : "OFF");
			transformer.setParameter("RENDER_WARNINGS", renderWarning ? "ON" : "OFF");
			transformer.setParameter("RENDER_SHA256", renderFileHash ? "ON" : "OFF");

			Profile profile = new Profile(this.locale);
			profile.configure(transformer, foUserAgent);

			Fop fop = fopFactory.newFop(org.apache.xmlgraphics.util.MimeConstants.MIME_PDF,
					foUserAgent, out);
			Result res = new SAXResult(fop.getDefaultHandler());

			transformer.transform(xmlSource, res);

			return out.toByteArray();
		} catch (final Exception e) {
			throw new FopReportException("Can not create PDF report", e);
		}
	}
}
