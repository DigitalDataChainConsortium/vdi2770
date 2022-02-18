<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:date="http://exslt.org/dates-and-times"
	extension-element-prefixes="date"
	exclude-result-prefixes="fo">
	
	<xsl:output method="xml" version="1.0"
		omit-xml-declaration="no" indent="yes" />
	<xsl:param name="versionParam" select="'1.0'" />
	<xsl:param name="LOGO">
		UNDEFINED
	</xsl:param>
	<xsl:param name="LOGOHEIGHT">
		2.5cm
	</xsl:param>
	<xsl:param name="LOGOTITLEHEIGHT">
		5cm
	</xsl:param>
	<xsl:param name="TITLE">
		VDI Validation Report
	</xsl:param>
	<xsl:param name="SECOVERVIEW" />
	<xsl:param name="FILENAME" />
	<xsl:param name="ERRORCOUNT" />
	<xsl:param name="WARNCOUNT" />
	<xsl:param name="SECREPORT" />
	<xsl:param name="SECERRORS" />
	<xsl:param name="SECWARNINGS" />
	<xsl:param name="SECINFOS" />
	<xsl:param name="NOMESSAGE" />
	<xsl:param name="SECSUBREPORTS" />
	<xsl:param name="DATE" />
	<xsl:param name="PAGE" />
	
	<xsl:param name="LANG">en</xsl:param>
	<xsl:param name="FONTFAMILY">Roboto,BabelStoneHan</xsl:param>

	<xsl:param name="RENDER_INFOS" />
	<xsl:param name="RENDER_WARNINGS" />
	<xsl:param name="RENDER_SHA256" />

	<xsl:param name="AUTHOR">
		Universität Leipzig
	</xsl:param>

	<xsl:param name="HEADINGCOLOR">
		#b02f2c
	</xsl:param>
	<xsl:param name="TITLECOLOR">
		#b02f2c
	</xsl:param>
	<xsl:param name="BORDERCOLOR">
		#b02f2c
	</xsl:param>
	<xsl:param name="FONTFOLOR">
		#262a31
	</xsl:param>
	<xsl:param name="LINKCOLOR">
		darkblue
	</xsl:param>
	
	<xsl:variable name="ALT_LOGO">A logo image</xsl:variable>
	<xsl:if test="LANG = 'de'">
		<xsl:variable name="ALT_LOGO">Eine Abbildung eines Logos</xsl:variable>
	</xsl:if>
	
	<xsl:variable name="ALT_BUG">An image of a bug representing an error</xsl:variable>
	<xsl:if test="LANG = 'de'">
		<xsl:variable name="ALT_BUG">Eine Abbildung eines Käfers zur Symbolisierung eines Fehlers</xsl:variable>
	</xsl:if>
	
	<xsl:variable name="ALT_EXCLAMATION">An image of an exclamation mark representing a warning</xsl:variable>
	<xsl:if test="LANG = 'de'">
		<xsl:variable name="ALT_EXCLAMATION">Eine Abbildung eines Ausrufezeichens zur Symbolisierung einer Warnung</xsl:variable>
	</xsl:if>
	
	<xsl:variable name="ALT_INFO">An image of a 'i' representing an information</xsl:variable>
	<xsl:if test="LANG = 'de'">
		<xsl:variable name="ALT_INFO">Eine Abbildung eines 'i' zur Symbolisierung einer Information</xsl:variable>
	</xsl:if>
	
	<xsl:variable name="ALT_CIRCLE">A circle symbol</xsl:variable>
	<xsl:if test="LANG = 'de'">
		<xsl:variable name="ALT_CIRCLE">Ein Kreis als Aufzählungszeichen</xsl:variable>
	</xsl:if>

	<xsl:variable name="ALT_SECTIONLINK">A Link to a report section</xsl:variable>
	<xsl:if test="LANG = 'de'">
		<xsl:variable name="ALT_SECTIONLINK">Link auf einen Reportabschnitt</xsl:variable>
	</xsl:if>

	<xsl:template match="XmlReportContent">
		<fo:root 
			xml:lang="{$LANG}" 
			xmlns:fo="http://www.w3.org/1999/XSL/Format"
			xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
			xmlns:pdf="http://xmlgraphics.apache.org/fop/‌extensions/pdf"
			font-family="{$FONTFAMILY}" font-size="12pt" color="{$FONTFOLOR}">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="first"
					page-height="297mm" page-width="210mm" margin-top="35mm"
					margin-bottom="35mm" margin-left="25mm" margin-right="25mm">
					<fo:region-body margin-bottom="25mm"
						region-name="xsl-region-body-first" />
				</fo:simple-page-master>
				<fo:simple-page-master master-name="even"
					page-height="297mm" page-width="210mm" margin-left="25mm" margin-right="25mm">
					<fo:region-body margin-top="45mm" margin-bottom="35mm" />
					<fo:region-before region-name="header-even" extent="55mm"/>
					<fo:region-after region-name="footer-even" extent="30mm"/>
				</fo:simple-page-master>
				<fo:simple-page-master master-name="odd"
					page-height="297mm" page-width="210mm" margin-left="25mm" margin-right="25mm">
					<fo:region-body margin-top="45mm" margin-bottom="35mm" />					
					<fo:region-before region-name="header-odd" extent="55mm"/>
					<fo:region-after region-name="footer-odd" extent="30mm"/>
				</fo:simple-page-master>
				<fo:page-sequence-master
					master-name="document">
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference
							odd-or-even="even" master-reference="even" />
						<fo:conditional-page-master-reference
							odd-or-even="odd" master-reference="odd" />
						<fo:conditional-page-master-reference
							blank-or-not-blank="not-blank" 
							page-position="first"
							master-reference="first" />
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
			</fo:layout-master-set>
			<fo:declarations xmlns:fo="http://www.w3.org/1999/XSL/Format" 
							 xmlns:pdfaid="http://www.aiim.org/pdfa/ns/id/" 
			  				 xmlns:dc="http://purl.org/dc/elements/1.1/" 
							 xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
				<pdf:catalog xmlns:pdf="http://xmlgraphics.apache.org/fop/extensions/pdf">
				  <pdf:dictionary type="normal" key="ViewerPreferences">
					<pdf:boolean key="DisplayDocTitle">true</pdf:boolean>
				  </pdf:dictionary>
				  <pdf:string key="Lang"><xsl:value-of select="$LANG" /></pdf:string>
				</pdf:catalog>
				<x:xmpmeta xmlns:x="adobe:ns:meta/" id="xmp_meta">
					<rdf:RDF>
						<rdf:Description xmlns:xmp="http://ns.adobe.com/xap/1.0/" rdf:about="">
							<xmp:CreatorTool>VDI 2770 Processor</xmp:CreatorTool>
							<dc:language>
								<rdf:Bag>
									<rdf:li><xsl:value-of select="$LANG" /></rdf:li>
								</rdf:Bag>
							</dc:language>
							<dc:title>
								<rdf:Alt>
									<rdf:li xml:lang="{$LANG}"><xsl:value-of select="$TITLE" /></rdf:li>
								</rdf:Alt>
							</dc:title>
							<dc:creator>
								<rdf:Seq>
									<rdf:li><xsl:value-of select="$AUTHOR" /></rdf:li>
								</rdf:Seq>
							</dc:creator>
							<dc:description>
								<rdf:Alt>
									<rdf:li xml:lang="{$LANG}">VDI 2770 Validation</rdf:li>
								</rdf:Alt>
							</dc:description>
							<dc:date>
								<rdf:Seq>
									<rdf:li><xsl:value-of select="date:format-date(date:date(), 'yyyy-MM-dd')" /></rdf:li>
								</rdf:Seq>
							</dc:date>
						</rdf:Description>
						<rdf:Description rdf:about=""
							 xmlns:xmp="http://ns.adobe.com/xap/1.0/">
						   <xmp:CreatorTool>VDI 2770 Processor by Leipzig University</xmp:CreatorTool>
						</rdf:Description>
					</rdf:RDF>
				</x:xmpmeta>
			</fo:declarations>
			<fo:bookmark-tree>
				<xsl:for-each select="Reports/Report">
					<xsl:variable name="blockId" select="id" />
					<fo:bookmark internal-destination="{$blockId}">
						<fo:bookmark-title>
							<xsl:value-of select="$SECREPORT" />&#160;<xsl:value-of select="fileName" />
						</fo:bookmark-title>
					</fo:bookmark>
				</xsl:for-each>
			</fo:bookmark-tree>
			<fo:page-sequence master-reference="first">
				<fo:flow flow-name="xsl-region-body-first">
					<xsl:call-template name="title_page" />
				</fo:flow>
			</fo:page-sequence>
			<fo:page-sequence master-reference="document">
				<fo:static-content flow-name="footer-even" role="artifact">
					<fo:block text-align-last="justify" role="artifact"> 
						<fo:block border-top="1pt solid"
							border-color="{$BORDERCOLOR}" role="artifact">
						</fo:block>
						<fo:block margin-top="12pt" role="artifact">
							<fo:inline>
								<xsl:value-of select="$PAGE" />&#160;<fo:page-number />
							</fo:inline>
							<fo:leader leader-length.maximum="100%"
								leader-pattern="space" />
							<fo:inline><xsl:value-of select="$AUTHOR" /></fo:inline>
						</fo:block>
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="footer-odd" role="artifact">
					<fo:block text-align-last="justify" role="artifact"> 
						<fo:block border-top="1pt solid"
							border-color="{$BORDERCOLOR}" role="artifact">
						</fo:block>
						<fo:block margin-top="12pt" role="artifact">
							<fo:inline><xsl:value-of select="$AUTHOR" /></fo:inline>
							<fo:leader leader-length.maximum="100%"
								leader-pattern="space" />
							<fo:inline>
								<xsl:value-of select="$PAGE" />&#160;<fo:page-number />
							</fo:inline>
						</fo:block>
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="header-odd" role="artifact">
					<fo:block margin-top="20mm" padding-bottom="6pt" role="artifact">
						<xsl:value-of select="$TITLE" />
						<fo:leader leader-length.maximum="100%"
							leader-pattern="space" />
						<xsl:if test="contains($LOGO,'file:')">
							<fo:external-graphic src="{$LOGO}"
								fox:alt-text="{$ALT_LOGO}"
								content-height="{$LOGOHEIGHT}" />
						</xsl:if>
					</fo:block>
					<fo:block text-align-last="justify" border-bottom="1pt solid"
						border-color="{$BORDERCOLOR}" role="artifact">
					</fo:block>
				</fo:static-content>
				<fo:static-content flow-name="header-even" role="artifact">
					<fo:block margin-top="20mm" padding-bottom="6pt" role="artifact">
						<xsl:if test="contains($LOGO,'file:')">
							<fo:external-graphic src="{$LOGO}"
								fox:alt-text="{$ALT_LOGO}"
								content-height="{$LOGOHEIGHT}" />
							<fo:leader leader-length.maximum="100%"
								leader-pattern="space" />
						</xsl:if>
						<xsl:value-of select="$TITLE" />
					</fo:block>
					<fo:block text-align-last="justify" border-bottom="1pt solid"
						border-color="{$BORDERCOLOR}" role="artifact">
					</fo:block>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body">
					<xsl:apply-templates select="Reports" />
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template name="title_page" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions">
		<fo:block page-break-after="always">
			<fo:block margin-top="8cm" margin-bottom="12pt"
				text-align="right">
				<xsl:if test="contains($LOGO,'file:')">
					<fo:external-graphic src="{$LOGO}" 
						fox:alt-text="{$ALT_LOGO}"
						content-height="{$LOGOTITLEHEIGHT}" />
				</xsl:if>
			</fo:block>
			<fo:block font-size="24pt" text-align="right"
				margin-bottom="12pt" font-weight="bold" color="{$TITLECOLOR}" role="H1">
				<xsl:value-of select="$TITLE" />
			</fo:block>
			<fo:block font-size="14pt" text-align="right"
				margin-bottom="12pt" font-weight="normal" color="{$TITLECOLOR}">
				<xsl:value-of select="$DATE" />
			</fo:block>
		</fo:block>
	</xsl:template>
	<xsl:template name="heading1">
		<xsl:param name="text">
			Heading1
		</xsl:param>
		<fo:block role="H1" font-size="16pt" margin-bottom="12pt"
			margin-top="12pt" font-weight="bold" color="{$HEADINGCOLOR}">
			<xsl:value-of select="$text" />
		</fo:block>
	</xsl:template>
	<xsl:template name="heading2">
		<xsl:param name="text">
			Heading2
		</xsl:param>
		<fo:block role="H2" font-size="14pt" margin-bottom="10pt"
			margin-top="10pt" font-weight="bold" color="{$HEADINGCOLOR}">
			<xsl:value-of select="$text" />
		</fo:block>
	</xsl:template>
	<xsl:template name="heading3">
		<xsl:param name="text">
			Heading3
		</xsl:param>
		<fo:block role="H3" font-size="12pt" margin-bottom="6pt"
			margin-top="6pt" font-weight="bold" color="{$HEADINGCOLOR}">
			<xsl:value-of select="$text" />
		</fo:block>
	</xsl:template>
	<xsl:template match="Errors">
		<xsl:if test="count(error) > 0">
			<xsl:call-template name="heading3">
				<xsl:with-param name="text">
					<xsl:value-of select="$SECERRORS" />
				</xsl:with-param>
			</xsl:call-template>
			<fo:list-block
				provisional-distance-between-starts="8mm" end-indent="1mm"
				start-indent="1mm">
				<xsl:apply-templates select="error" />
			</fo:list-block>
		</xsl:if>
	</xsl:template>
	<xsl:template match="error|warning|info" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions">
		<fo:list-item margin-bottom="9pt">
			<fo:list-item-label end-indent="label-end()">
				<fo:block>
					<xsl:if test="starts-with(name(.), 'error')">
						<fo:block margin-left="3pt">
							<fo:external-graphic src="./bug.svg"
								content-height="9pt" 
								content-width="9pt" height="9pt" width="9pt"
								fox:alt-text="{$ALT_BUG}"/>
						</fo:block>
					</xsl:if>
					<xsl:if test="starts-with(name(.), 'warning')">
						<fo:block margin-left="3pt">
							<fo:external-graphic src="./exclamation-triangle.svg"
								content-height="9pt" 
								content-width="9pt" height="9pt" width="9pt"
								fox:alt-text="{$ALT_EXCLAMATION}"/>
						</fo:block>
					</xsl:if>
					<xsl:if test="starts-with(name(.), 'info')">
						<fo:block margin-left="3pt">
							<fo:external-graphic src="./info.svg"
								content-height="9pt" 
								content-width="9pt" height="9pt" width="9pt"
								fox:alt-text="{$ALT_INFO}"/>
						</fo:block>
					</xsl:if>
				</fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()">
				<fo:block>
					<xsl:value-of select="." />
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>
	<xsl:template match="Warnings">
		<xsl:if test="count(warning) > 0">
			<xsl:call-template name="heading3">
				<xsl:with-param name="text">
					<xsl:value-of select="$SECWARNINGS" />
				</xsl:with-param>
			</xsl:call-template>
			<fo:list-block
				provisional-distance-between-starts="8mm" end-indent="1mm"
				start-indent="1mm">
				<xsl:apply-templates select="warning" />
			</fo:list-block>
		</xsl:if>
	</xsl:template>
	<xsl:template match="Infos">
		<xsl:if test="count(info) > 0">
			<xsl:call-template name="heading3">
				<xsl:with-param name="text">
					<xsl:value-of select="$SECINFOS" />
				</xsl:with-param>
			</xsl:call-template>
			<fo:list-block
				provisional-distance-between-starts="8mm" end-indent="1mm"
				start-indent="1mm">
				<xsl:apply-templates select="info" />
			</fo:list-block>
		</xsl:if>
	</xsl:template>
	<xsl:template match="Reports" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions">
		<xsl:call-template name="heading2">
			<xsl:with-param name="text">
				<xsl:value-of select="$SECOVERVIEW" />
			</xsl:with-param>
		</xsl:call-template>
		<fo:table inline-progression-dimension="auto"
			table-layout="fixed" width="100%" border-collapse="separate"
			border-style="solid">
			<fo:table-column column-width="70%" fox:header="true"
				border-style="solid" border-width="thin" />
			<fo:table-column column-width="15%"
				border-style="solid" border-width="thin" />
			<fo:table-column column-width="15%"
				border-style="solid" border-width="thin" />
			<fo:table-header>
				<fo:table-row>
					<fo:table-cell border-style="solid" text-align="left"
						border-width="thin" padding-after="3pt" padding-before="3pt"
						padding-left="3pt">
						<fo:block font-weight="bold">
							<xsl:value-of select="$FILENAME" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="solid"
						text-align="center" border-width="thin" padding-after="3pt"
						padding-before="3pt" padding-left="3pt">
						<fo:block font-weight="bold">
							<xsl:value-of select="$ERRORCOUNT" />
						</fo:block>
					</fo:table-cell>
					<fo:table-cell border-style="solid"
						text-align="center" border-width="thin" padding-after="3pt"
						padding-before="3pt" padding-left="3pt">
						<fo:block font-weight="bold">
							<xsl:value-of select="$WARNCOUNT" />
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-header>
			<fo:table-body>
				<xsl:apply-templates select="Report" />
			</fo:table-body>
		</fo:table>
		<fo:block>
			<xsl:for-each select="Report">
				<xsl:variable name="blockId" select="id" />
				<fo:block id="{$blockId}">
					<xsl:call-template name="heading2">
						<xsl:with-param name="text">
							<xsl:value-of select="$SECREPORT" /> &#160;<xsl:value-of select="fileName" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:if test="$RENDER_SHA256 = 'ON'">
						<fo:block font-size="10pt">
							SHA265 <xsl:value-of select="fileHash" />
						</fo:block>
					</xsl:if>
					<xsl:apply-templates select="Errors" />
					<xsl:if test="$RENDER_WARNINGS = 'ON'">
						<xsl:apply-templates select="Warnings" />
					</xsl:if>
					<xsl:if test="$RENDER_INFOS = 'ON'">
						<xsl:apply-templates select="Infos" />
					</xsl:if>
					<xsl:if test="count(Errors/error) = 0 and $RENDER_WARNINGS = 'OFF' and $RENDER_INFOS = 'OFF'">
						<xsl:value-of select="$NOMESSAGE" />
					</xsl:if>
					<xsl:apply-templates select="SubReports" />
				</fo:block>
			</xsl:for-each>
		</fo:block>
	</xsl:template>
	<xsl:template match="SubReports">
		<xsl:if test="count(subReport)>0">
			<fo:block>
				<xsl:call-template name="heading2">
					<xsl:with-param name="text">
						<xsl:value-of select="$SECSUBREPORTS" />
					</xsl:with-param>
				</xsl:call-template>
			</fo:block>
			<fo:list-block
				provisional-distance-between-starts="8mm" end-indent="1mm"
				start-indent="1mm">
				<xsl:apply-templates select="subReport" />
			</fo:list-block>
		</xsl:if>
	</xsl:template>
	<xsl:template match="subReport" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions">
		<fo:list-item margin-bottom="9pt">
			<fo:list-item-label end-indent="label-end()">
				<fo:block margin-left="3pt">
					<fo:external-graphic src="./circle.svg"
								content-height="6pt" 
								content-width="9pt" height="9pt" width="9pt" 
								fox:alt-text="{$ALT_INFO}" />
				</fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()">
				<fo:block>
					<xsl:variable name="blockId" select="id" />
					<fo:basic-link internal-destination="{$blockId}"
						color="{$LINKCOLOR}" fox:alt-text="${ALT_SECTIONLINK}">
						<xsl:value-of select="fileName" />
					</fo:basic-link>
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>
	<xsl:template match="Report" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions">
		<fo:table-row>
			<fo:table-cell border-style="solid" border-width="thin"
				padding-after="3pt" padding-before="3pt" padding-left="3pt" role="TD">
				<fo:block>
					<xsl:variable name="blockId" select="id" />
					<fo:basic-link internal-destination="{$blockId}"
						color="{$LINKCOLOR}" fox:alt-text="${ALT_SECTIONLINK}">
						<xsl:value-of select="fileName" />
					</fo:basic-link>
				</fo:block>
			</fo:table-cell>
			<fo:table-cell border-style="solid" text-align="center"
				border-width="thin">
				<fo:block>
					<xsl:value-of select="errorCount" />
				</fo:block>
			</fo:table-cell>
			<fo:table-cell border-style="solid" text-align="center"
				border-width="thin">
				<fo:block>
					<xsl:value-of select="warnCount" />
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</xsl:template>
</xsl:stylesheet>
