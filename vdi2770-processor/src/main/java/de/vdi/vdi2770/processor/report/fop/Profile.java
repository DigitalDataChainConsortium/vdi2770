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

import java.io.File;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.transform.Transformer;

import org.apache.fop.apps.FOUserAgent;

import com.google.common.base.Preconditions;

import de.vdi.vdi2770.processor.ProcessorException;
import de.vdi.vdi2770.processor.common.ProcessorConfiguration;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Profile {

	protected final ResourceBundle bundle;

	protected final Locale locale;

	public Profile(final Locale locale) {

		Preconditions.checkArgument(locale != null, "locale is null");

		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
		this.locale = (Locale) locale.clone();
	}

	protected void configure(final Transformer transformer, final FOUserAgent agent) {

		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, this.locale);
		Date now = new Date();
		String strDate = df.format(now);
		transformer.setParameter("DATE", strDate);

		transformer.setParameter("PAGE", this.bundle.getString("RD_LABEL_PAGE"));

		final String title = this.bundle.getString("RD_LABEL_TITLE");
		transformer.setParameter("TITLE", title);

		transformer.setParameter("LANG", this.locale.getISO3Language());

		// using google font Roboto
		// https://fonts.google.com/specimen/Roboto
		// using font https://www.cufonfonts.com/font/babelstone-han
		// see also https://www.babelstone.co.uk/Fonts/Han.html
		String fontFamily = "Roboto,BabelStoneHan";

		transformer.setParameter("FONTFAMILY", fontFamily);

		transformer.setParameter("SECOVERVIEW", this.bundle.getString("RD_LABEL_SEC_OVERVIEW"));
		transformer.setParameter("FILENAME",
				this.bundle.getString("RD_LABEL_OVERVIEW_HEADER_FILE"));
		transformer.setParameter("ERRORCOUNT",
				this.bundle.getString("RD_LABEL_OVERVIEW_HEADER_ERRORCOUNT"));
		transformer.setParameter("WARNCOUNT",
				this.bundle.getString("RD_LABEL_OVERVIEW_HEADER_WARNCOUNT"));
		transformer.setParameter("SECREPORT", this.bundle.getString("RD_LABEL_SEC_REPORT"));
		transformer.setParameter("SECERRORS", this.bundle.getString("RD_LABEL_SEC_ERRORS"));
		transformer.setParameter("SECWARNINGS", this.bundle.getString("RD_LABEL_SEC_WARNINGS"));
		transformer.setParameter("SECINFOS", this.bundle.getString("RD_LABEL_SEC_INFO"));
		transformer.setParameter("NOMESSAGE", this.bundle.getString("RD_LABEL_NOMESSAGE"));
		transformer.setParameter("SECSUBREPORTS", this.bundle.getString("RD_LABEL_SEC_SUPREPORTS"));

		final ProcessorConfiguration config = ProcessorConfiguration.getInstance(this.locale);

		final String author = config.getReportAuthor();
		transformer.setParameter("AUTHOR", author);
		agent.setAuthor(author);

		transformer.setParameter("HEADINGCOLOR", config.getReportHeadingColor());
		transformer.setParameter("TITLECOLOR", config.getReportTitleColor());
		transformer.setParameter("BORDERCOLOR", config.getReportTableBorderColor());
		transformer.setParameter("FONTFOLOR", config.getReportFontColor());
		transformer.setParameter("LINKCOLOR", config.getReportLinkColor());
		transformer.setParameter("LOGOHEIGHT", config.getReportLogoHeight());
		transformer.setParameter("LOGOTITLEHEIGHT", config.getReportLogoTitleHeight());

		try {
			final File logoFile = config.getLogoFile();
			if (logoFile != null && logoFile.exists()) {
				transformer.setParameter("LOGO", logoFile.toURI().toURL());
			}
		} catch (ProcessorException e) {
			log.error("Can not set logo file", e);
		} catch (MalformedURLException e) {
			log.error("Can not set logo file", e);
		}

	}

}
