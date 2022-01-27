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
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import de.vdi.vdi2770.metadata.MetadataException;
import de.vdi.vdi2770.metadata.model.Constants;
import de.vdi.vdi2770.metadata.model.DigitalFile;
import de.vdi.vdi2770.metadata.model.Document;
import de.vdi.vdi2770.metadata.model.DocumentClassification;
import de.vdi.vdi2770.metadata.model.DocumentId;
import de.vdi.vdi2770.metadata.model.DocumentRelationship;
import de.vdi.vdi2770.metadata.model.DocumentVersion;
import de.vdi.vdi2770.metadata.model.MainDocument;
import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.model.ObjectId;
import de.vdi.vdi2770.metadata.model.ValidationFault;
import de.vdi.vdi2770.metadata.xml.FileNames;
import de.vdi.vdi2770.metadata.xml.XmlProcessingException;
import de.vdi.vdi2770.metadata.xml.XmlReader;
import de.vdi.vdi2770.metadata.xml.XmlValidationFault;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;

import de.vdi.vdi2770.processor.ProcessorException;
import de.vdi.vdi2770.processor.common.Check;
import de.vdi.vdi2770.processor.common.ContainerType;
import de.vdi.vdi2770.processor.common.Message;
import de.vdi.vdi2770.processor.common.MessageLevel;
import de.vdi.vdi2770.processor.pdf.PdfValidationException;
import de.vdi.vdi2770.processor.pdf.PdfValidator;
import de.vdi.vdi2770.processor.zip.ZipFault;
import de.vdi.vdi2770.processor.zip.ZipUtils;
import lombok.extern.log4j.Log4j2;

/**
 * This file implements Container validation and reporting according to VDI 2770
 * specification.
 *
 * <p>
 * A documentation or document container can be validated. Every contained sub
 * container are validated, too.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class ContainerValidator {

	// prefix is REP
	private final ResourceBundle bundle;
	private final Locale locale;
	private boolean isStrictMode;

	/**
	 * ctor
	 * 
	 * @param locale Desired {@link Locale} for validation messages; must not be
	 *               <code>null</code>.
	 */
	public ContainerValidator(final Locale locale) {
		super();

		Preconditions.checkArgument(locale != null);

		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
		this.locale = (Locale) locale.clone();
		this.isStrictMode = false;
	}

	/**
	 * ctor
	 * 
	 * @param isStrictMode Enable or disable strict validation.
	 * 
	 * @param locale       Desired {@link Locale} for validation messages; must not
	 *                     be <code>null</code>.
	 */
	public ContainerValidator(final Locale locale, final boolean isStrictMode) {
		super();

		Preconditions.checkArgument(locale != null);

		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
		this.locale = (Locale) locale.clone();
		this.isStrictMode = isStrictMode;
	}

	/**
	 * Validate ZIP container and report the content as well as validation errors.
	 *
	 * @param zipFileName A path to a ZIP container file.
	 * @return A {@link Report} containing information and error {@link Message}s.
	 * @throws ProcessorException Error while processing the container.
	 * @throws MetadataException  Error reading XML metadata in the container.
	 */
	public Report validate(final String zipFileName) throws ProcessorException, MetadataException {
		return validate(zipFileName, MessageLevel.INFO);
	}

	/**
	 * Validate ZIP container and report the content as well as validation errors.
	 *
	 * @param zipFileName A path to a ZIP container file.
	 * @param logLevel    A minimal logging threshold
	 * @return A {@link Report} containing information and error {@link Message}s.
	 * @throws ProcessorException Error while processing the container.
	 * @throws MetadataException  Error reading XML metadata in the container.
	 */
	public Report validate(final String zipFileName, final MessageLevel logLevel)
			throws ProcessorException, MetadataException {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(zipFileName),
				"Zip file name is null or empty.");
		Preconditions.checkArgument(logLevel != null, "logLevel is null");

		final File zipFile = new File(zipFileName);

		Check check = new Check(this.locale);
		check.fileExists(zipFile, "REP_EXCEPTION_001");
		check.isZipFile(zipFile, "REP_EXCEPTION_002");

		return validate(zipFile, logLevel);
	}

	/**
	 * Validate ZIP container and report the content as well as validation errors.
	 *
	 * @param zipFile        A ZIP {@link File}; must not be <code>null</code> and
	 *                       must exist.
	 * @param minReportLevel The logging threshold.
	 * @return A {@link Report} containing information and error {@link Message}s.
	 * @throws ProcessorException Error while processing the container.
	 * @throws MetadataException  Error reading XML metadata in the container.
	 */
	public Report validate(final File zipFile, final MessageLevel minReportLevel)
			throws MetadataException, ProcessorException {

		Preconditions.checkArgument(zipFile != null, "file is null");
		Preconditions.checkArgument(minReportLevel != null, "minReportLevel is null");

		Check check = new Check(this.locale);
		check.fileExists(zipFile, "REP_EXCEPTION_001");
		check.isZipFile(zipFile, "REP_EXCEPTION_002");

		final ZipUtils zip = new ZipUtils(this.locale);

		final Report report = new Report(this.locale, zipFile, minReportLevel);

		List<ZipFault> zipFaults = zip.validateZipFile(zipFile);
		zipFaults.forEach(f -> report.addMessage(zipFaultToMessage(f, 0)));

		if (report.hasErrors()) {
			log.info("Found ZIP validation errors.");
			return report;
		}

		// unzip the first level ZIP file
		final Path tmpPath = zip.unzipToTemperaryFolder(zipFile, true, report);
		final File tmpDir = tmpPath.toFile();
		if (log.isDebugEnabled()) {
			log.debug("Temp Path Created: " + tmpDir.getParentFile().getAbsolutePath());
		}
		tmpDir.deleteOnExit();

		Report result = validateUnzippedContainer(tmpDir, report);

		try {
			if (log.isDebugEnabled()) {
				log.debug("Delete Temp Path: " + tmpDir.getParentFile().getAbsolutePath());
			}
			FileUtils.deleteDirectory(tmpDir.getParentFile());
		} catch (final IOException e) {
			log.warn("Can not delete dir " + tmpDir.getParentFile().getAbsolutePath(), e);
		}

		return result;
	}

	/**
	 * Validate the content of a container file that has been extracted to a folder
	 * 
	 * @param folder The folder that contains the extracted content; must not be
	 *               <code>null</code> and the folder must exist
	 * @param report A {@link Report} instance
	 * @return A {@link Report} for this folder
	 * @throws MetadataException  An error occurred reading XML meta data
	 * @throws ProcessorException There was an error while processing the folder
	 */
	public Report validateUnzippedContainer(final File folder, final Report report)
			throws MetadataException, ProcessorException {

		Preconditions.checkArgument(folder != null, "folder is null");
		Preconditions.checkArgument(report != null, "report is null");

		Check check = new Check(this.locale);
		check.fileExists(folder, "REP_EXCEPTION_001");
		check.isDirectory(folder, "REP_EXCEPTION_003");

		final Collection<File> xmlFiles = FileUtils.listFiles(folder, new String[] { "xml" }, true);

		final XmlReader reader = new XmlReader(this.locale);

		// filter to meta data XML files
		final List<File> metaDataFiles = xmlFiles.stream().filter(f -> reader.isMetadataFile(f))
				.collect(Collectors.toList());

		final Map<File, Document> documents = new HashMap<>();
		for (File metaDataFile : metaDataFiles) {
			// read the document
			final Document document = reader.tryRead(metaDataFile);
			if (document != null) {
				documents.put(metaDataFile, document);
			}
		}

		// process the file in the ZIP
		process(folder, report, null, documents, 0);

		return report;
	}

	private void validateObjectRelations(final Document current, final Document parent,
			final Report report, final int indentLevel) {

		Preconditions.checkArgument(report != null, "report is null");

		if (current == null) {
			if (log.isWarnEnabled()) {
				log.warn("Document instance not found for file " + report.getFileHash());
			}

			report.addMessage(
					new Message(MessageLevel.WARN, this.bundle.getString("REP_MESSAGE_028")));

		} else {
			if (parent == null) {
				// there is no super ordinated file
				report.addMessage(
						new Message(MessageLevel.INFO, this.bundle.getString("REP_MESSAGE_028")));
			} else {

				// validate object ID (match) only, of parent is a main document and
				// current document is a primitive document
				if (parent.isMainDocument() && !current.isMainDocument()) {
					final ValidationFault fault = current.validateObjects(parent, this.locale);
					if (fault != null) {
						reportFault(fault, report, indentLevel);
					} else {
						report.addMessage(new Message(MessageLevel.INFO,
								this.bundle.getString("REP_MESSAGE_032")));
					}
				}
			}
		}
	}

	private void validateDocumentRelations(final File dir, final Report report,
			final Map<File, Document> documents, final int indentLevel) {

		Preconditions.checkArgument(documents != null, "documents is null");
		Preconditions.checkArgument(dir != null, "dir is null");
		Preconditions.checkArgument(report != null, "report is null");

		File current = new File(dir, FileNames.MAIN_DOCUMENT_XML_FILE_NAME);
		if (!current.exists()) {
			current = new File(dir, FileNames.METADATA_XML_FILE_NAME);
			if (!current.exists()) {
				log.warn("No XML meta data file found. Can not validate");
				return;
			}
		}

		final Document currentDocument = documents.get(current);
		if (currentDocument != null) {

			if (currentDocument.getDocumentVersion().stream().map(v -> v.getDocumentRelationship())
					.flatMap(Collection::stream).count() == 0) {

				// Document has not relationsships
				return;
			}

			Map<File, Document> tmp = new HashMap<>(documents);
			tmp.remove(current);
			List<Document> otherDocuments = new ArrayList<>(tmp.values());

			final List<ValidationFault> faults = currentDocument
					.validateDocumentRelations(otherDocuments, true, this.locale);

			if (Fault.hasWarnings(faults)) {
				reportFaults(faults, report, indentLevel);
			} else {
				report.addMessage(
						new Message(MessageLevel.INFO, this.bundle.getString("REP_MESSAGE_031")));
			}
		} else {
			log.warn("Can not validate realtions, because 'currentDocument' is null");
			report.addMessage(
					new Message(MessageLevel.WARN, this.bundle.getString("REP_MESSAGE_027")));
		}
	}

	private static void reportFaults(final List<? extends ValidationFault> faults,
			final Report report, final int indentLevel) {

		Preconditions.checkArgument(faults != null, "faults is null");
		Preconditions.checkArgument(report != null, "report is null");

		if (faults.isEmpty()) {
			return;
		}

		for (final ValidationFault fault : faults) {

			reportFault(fault, report, indentLevel);
		}
	}

	private static void reportFault(ValidationFault fault, final Report report,
			final int indentLevel) {

		Preconditions.checkArgument(fault != null, "fault is null");
		Preconditions.checkArgument(report != null, "report is null");

		if (fault.getLevel() == FaultLevel.ERROR) {
			report.addMessage(new Message(MessageLevel.ERROR, fault.getMessage(), indentLevel));
		} else {
			report.addMessage(new Message(MessageLevel.WARN, fault.getMessage(), indentLevel));
		}
	}

	private File getMetadataFile(final List<File> files, final ContainerType type,
			final Report report, final int indentLevel) throws ProcessorException {

		Preconditions.checkArgument(files != null, "files is null");
		Preconditions.checkArgument(type != null, "type is null");
		Preconditions.checkArgument(report != null, "report is null");

		final Optional<File> metadataFile;

		final XmlReader reader = new XmlReader(this.locale);

		if (type.equals(ContainerType.DOCUMENT_CONTAINER)) {

			metadataFile = files.stream().filter(f -> reader.isMetadataFileForDocument(f))
					.findFirst();
		} else {

			metadataFile = files.stream().filter(f -> reader.isMetadataFileForMainDocument(f))
					.findFirst();
		}

		if (!metadataFile.isPresent()) {
			report.addMessage(new Message(MessageLevel.ERROR,
					MessageFormat.format(this.bundle.getString("REP_MESSAGE_004"),
							getMetadataFileNameForContainerType(type)),
					indentLevel));
			return null;
		}

		return metadataFile.get();
	}

	private String getMetadataFileNameForContainerType(final ContainerType type)
			throws ProcessorException {

		Preconditions.checkArgument(type != null, "type is null");

		switch (type) {
		case DOCUMENT_CONTAINER:
			return FileNames.METADATA_XML_FILE_NAME;
		case DOCUMENTATION_CONTAINER:
			return FileNames.MAIN_DOCUMENT_XML_FILE_NAME;
		default:
			throw new ProcessorException(this.bundle.getString("REP_EXCEPTION_005"));
		}
	}

	private void process(final File folder, final Report report, final Document parentDocument,
			final Map<File, Document> allKnownDocuments, final int indentLevel)
			throws ProcessorException, MetadataException {

		Preconditions.checkArgument(folder != null, "folder is null");
		Preconditions.checkArgument(report != null, "report is null");
		Preconditions.checkArgument(allKnownDocuments != null, "documents is null");

		Check check = new Check(this.locale);
		check.isDirectory(folder, "REP_EXCEPTION_003");

		// list files in folder
		final File[] files = folder.listFiles();

		// no files in folder
		if (files == null) {
			report.addMessage(new Message(
					MessageLevel.WARN, MessageFormat
							.format(this.bundle.getString("REP_MESSAGE_002"), folder.getName()),
					indentLevel));
			return;
		}

		// add strict mode enabled / disabled to report
		reportStrictMode(report, indentLevel);

		// convert to array to support streams and so on
		final List<File> filesInFolder = Lists.newArrayList(files).stream()
				.filter(f -> !f.isDirectory()).collect(Collectors.toList());
		for (final File file : filesInFolder) {
			// remove the files, because they are unzipped into a temporary folder
			file.deleteOnExit();
		}

		// output the XML metadata file name
		reportExistingVdiXmlFile(folder, report, indentLevel);

		File vdiXmlFile = null;
		ContainerType type = null;
		try {
			type = getContainerType(filesInFolder);
			report.setContainerType(type);
			report.addMessage(new Message(
					MessageFormat.format(this.bundle.getString("REP_MESSAGE_003"), type),
					indentLevel));

			// try to find XML meta data file
			vdiXmlFile = getMetadataFile(filesInFolder, type, report, indentLevel);

		} catch (final ProcessorException ex) {
			report.addMessage(new Message(MessageLevel.ERROR, ex.getMessage(), indentLevel));

			// finish validation in strict mode
			if (this.isStrictMode) {
				return;
			}

			final File[] xmlFiles = folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(final File dir, final String name) {
					return name.toLowerCase().endsWith(".xml");
				}
			});

			// not XML files found
			if (xmlFiles == null || xmlFiles.length == 0) {
				return;
			}

			if (xmlFiles.length > 1) {
				report.addMessage(
						new Message(this.bundle.getString("REP_MESSAGE_033"), indentLevel));
				return;
			}

			vdiXmlFile = xmlFiles[0];
		}

		// process only, if file found try to parse sub container although this error
		if (vdiXmlFile != null) {

			report.addMessage(new Message(MessageFormat.format(
					this.bundle.getString("REP_MESSAGE_005"), vdiXmlFile.getName()), indentLevel));

			// process and validate the XML file
			validateAndReportVdiXmlFile(vdiXmlFile, report, indentLevel);
		} else {
			report.addMessage(new Message(MessageLevel.ERROR,
					this.bundle.getString("REP_MESSAGE_026"), indentLevel));
			return;
		}

		final Document currentDocument = allKnownDocuments.get(vdiXmlFile);

		// validate relations between objects
		validateObjectRelations(currentDocument, parentDocument, report, indentLevel);

		if (parentDocument != null && currentDocument != null
				&& currentDocument.getDocumentId().size() > 0) {

			String target = this.bundle.getString("MD_LABEL_D");
			if (currentDocument.isMainDocument()) {
				target = this.bundle.getString("MD_LABEL_MD");
			}

			// main documents shall be referenced by higher level main documents
			if (!isKnownByParent(currentDocument, parentDocument)) {
				report.addMessage(new Message(MessageLevel.ERROR,
						MessageFormat.format(this.bundle.getString("REP_MESSAGE_029"), target,
								currentDocument.getDocumentId().get(0).getAsText())));
			} else {
				report.addMessage(new Message(MessageLevel.INFO,
						MessageFormat.format(this.bundle.getString("REP_MESSAGE_030"), target,
								currentDocument.getDocumentId().get(0).getAsText())));
			}
		}

		// validate relations between documents
		validateDocumentRelations(folder, report, allKnownDocuments, indentLevel);

		// only process other ZIP files, if the container is documentation
		// container file
		if (type == ContainerType.DOCUMENTATION_CONTAINER || (!this.isStrictMode && type == null)) {

			// check VDI2770_Main.pdf file
			validateMainDocumentPdf(folder, report, indentLevel);

			// search for other ZIP files
			final List<File> zipFiles = filesInFolder.stream().filter(f -> ZipUtils.isZipFile(f))
					.collect(Collectors.toList());

			// process other ZIP files that were included in the container
			processEmbeddedZipFiles(folder, zipFiles, report, currentDocument, allKnownDocuments,
					indentLevel + 1);
		}

		if (this.isStrictMode && type == null) {
			report.addMessage(new Message(MessageLevel.WARN,
					this.bundle.getString("REP_MESSAGE_034"), indentLevel));
		}
	}

	private void reportExistingVdiXmlFile(final File folder, final Report report, int indentLevel) {

		Preconditions.checkArgument(folder != null, "folder is null");
		Preconditions.checkArgument(report != null, "report is null");

		File fileToCheck = new File(folder, FileNames.MAIN_DOCUMENT_XML_FILE_NAME);
		try {
			if (!fileToCheck.exists() || !fileToCheck.getCanonicalPath()
					.endsWith(FileNames.MAIN_DOCUMENT_XML_FILE_NAME)) {
				fileToCheck = new File(folder, FileNames.METADATA_XML_FILE_NAME);
				if (!fileToCheck.exists() || !fileToCheck.getCanonicalPath()
						.endsWith(FileNames.METADATA_XML_FILE_NAME)) {
					report.addMessage(new Message(MessageLevel.ERROR,
							this.bundle.getString("REP_MESSAGE_035"), indentLevel));
				}
			}
		} catch (final Exception e) {
			log.warn("Error while checking XML metadata file", e);
		}
	}

	private void reportStrictMode(final Report report, int indentLevel) {

		Preconditions.checkArgument(report != null, "report is null");

		if (this.isStrictMode) {
			report.addMessage(new Message(this.bundle.getString("REP_MESSAGE_036"), indentLevel));
		} else {
			report.addMessage(new Message(this.bundle.getString("REP_MESSAGE_037"), indentLevel));
		}

	}

	private static boolean isKnownByParent(final Document current, final Document parent) {

		final List<DocumentId> parentRelations = parent.getDocumentVersion().stream()
				.map(v -> v.getDocumentRelationship()).flatMap(Collection::stream)
				.map(r -> r.getDocumentId()).collect(Collectors.toList());

		return parentRelations.stream().anyMatch(current.getDocumentId()::contains);
	}

	private void validateMainDocumentPdf(final File folder, final Report report,
			final int indentLevel) {

		final File mainPdfFile = new File(folder, FileNames.MAIN_DOCUMENT_PDF_FILE_NAME);
		if (!mainPdfFile.exists()) {
			report.addMessage(new Message(MessageLevel.ERROR,
					this.bundle.getString("REP_MESSAGE_025"), indentLevel));
		}
	}

	private ContainerType getContainerType(final List<File> filesInContainer)
			throws ProcessorException {

		final XmlReader reader = new XmlReader(this.locale);

		Preconditions.checkArgument(filesInContainer != null, "filesInContainer is null");

		final ContainerType type;
		if (filesInContainer.stream().filter(f -> reader.isMetadataFileForMainDocument(f))
				.count() > 0) {
			log.info("Processing a documentation container.");
			type = ContainerType.DOCUMENTATION_CONTAINER;
		} else if (filesInContainer.stream().filter(f -> reader.isMetadataFileForDocument(f))
				.count() > 0) {
			log.info("Processing a document container.");
			type = ContainerType.DOCUMENT_CONTAINER;
		} else {
			throw new ProcessorException(this.bundle.getString("REP_EXCEPTION_004"));
		}

		return type;
	}

	private void processEmbeddedZipFiles(final File folder, final Collection<File> zipFiles,
			final Report report, final Document parentDocument,
			final Map<File, Document> allKnownDocuments, final int indentLevel)
			throws ProcessorException, MetadataException {

		Preconditions.checkArgument(allKnownDocuments != null, "allKnownDocuments is null");
		Preconditions.checkArgument(folder != null, "folder is null");
		Preconditions.checkArgument(zipFiles != null, "zipFiles null");
		Preconditions.checkArgument(report != null, "report is null");

		File[] directories = folder.listFiles(File::isDirectory);
		if (directories != null && directories.length > 0) {
			for (final File sub : directories) {

				final Report subReport = report.getSubReport(sub);
				process(sub, subReport, parentDocument, allKnownDocuments, indentLevel + 1);
			}
		}
	}

	/**
	 * Validate an XML meta data file and report the content
	 * 
	 * @param xmlFile     A XML meta data file (must not be <code>null</code> and
	 *                    exist)
	 * @param report      A {@link Report} instance to log messages
	 * @param indentLevel An indent level for formatting
	 */
	public void validateAndReportVdiXmlFile(final File xmlFile, final Report report,
			final int indentLevel) {
		validateAndReportVdiXmlFile(xmlFile, report, indentLevel, true);
	}

	/**
	 * Validate an XML meta data file and report the content
	 * 
	 * @param xmlFile         A XML meta data file (must not be <code>null</code>
	 *                        and exist)
	 * @param report          A {@link Report} instance to log messages
	 * @param indentLevel     An indent level for formatting
	 * @param checkFilesExist If true, check stored files.
	 */
	public void validateAndReportVdiXmlFile(final File xmlFile, final Report report,
			final int indentLevel, boolean checkFilesExist) {

		Preconditions.checkArgument(xmlFile != null, "xmlFile is null");
		Preconditions.checkArgument(report != null, "report is null");
		Preconditions.checkArgument(xmlFile.exists(), "xmlFile does not exist");

		validateVdiXmlFile(xmlFile, report, indentLevel);
		reportVdiXmlFile(xmlFile, report, indentLevel, checkFilesExist);
	}

	private void reportVdiXmlFile(final File xmlFile, final Report report, final int indentLevel,
			boolean checkFilesExist) {

		Preconditions.checkArgument(xmlFile != null, "xml file is null");
		Preconditions.checkArgument(report != null, "report is null");

		final XmlReader reader = new XmlReader(this.locale);

		List<ValidationFault> faults = null;
		try {
			Document document = reader.read(xmlFile);
			if (document.isMainDocument()) {
				MainDocument main = new MainDocument(document);
				faults = main.validate(this.locale);
			} else {
				document.validate(null, this.locale);
				faults = document.validate(null, this.locale);
			}

			// add validation messages to report
			faults.stream().forEach(f -> report.addMessage(new Message(f, indentLevel)));

			reportDocumentIds(document, report, indentLevel);
			reportObjectIds(document, report, indentLevel);
			reportClassifications(document, report, indentLevel);
			reportRelationships(document, report, indentLevel);
			if (checkFilesExist) {
				reportStoredDocumentRepresentations(document, xmlFile.getParent(), report,
						indentLevel);
			}

		} catch (final ProcessorException e) {
			report.addMessage(new Message(MessageLevel.ERROR, e.getMessage()));
		} catch (final XmlProcessingException e) {
			if (log.isInfoEnabled()) {
				log.info("Error while XML validation", e);
			}
			report.addMessage(
					new Message(MessageLevel.ERROR, this.bundle.getString("REP_MESSAGE_039")));
		}
	}

	private void reportStoredDocumentRepresentations(final Document document, final String basePath,
			final Report report, final int indentLevel) throws ProcessorException {

		Preconditions.checkArgument(document != null, "document is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(basePath), "basepath is null or empty");
		Preconditions.checkArgument(report != null, "report is null");

		// files defined in the XML metadata
		final List<DigitalFile> storedFiles = document.getDocumentVersion().stream()
				.map(v -> v.getDigitalFile()).flatMap(d -> d.stream()).collect(Collectors.toList());

		// check for additional files
		final File[] localFiles = new File(basePath).listFiles();
		if (localFiles != null) {
			final ZipUtils zip = new ZipUtils(this.locale);
			for (final File file : localFiles) {
				if (!file.isDirectory() && !zip.isContainer(file, false)
						&& !FileNames.isMetadataFile(file)) {

					if (storedFiles.stream()
							.filter(f -> StringUtils.equals(f.getFileName(), file.getName()))
							.count() == 0) {

						report.addMessage(new Message(MessageLevel.WARN, MessageFormat
								.format(this.bundle.getString("REP_MESSAGE_006"), file.getName()),
								indentLevel));
					}
				}
			}
		}

		boolean allowPdfA1Only = document.getDocumentClassification().stream()
				.filter(s -> StringUtils.equals(s.getClassificationSystem(),
						Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME))
				.map(s -> s.getClassId())
				.filter(i -> StringUtils.equals(i, Constants.VDI2770_CERTIFICATE_CATEGORY))
				.count() > 0;

		// look for files that are defined in the metadata but do not exist
		for (final DigitalFile storedFile : storedFiles) {

			final String fileName = storedFile.getFileName();
			final File localFile = new File(basePath, fileName);

			if (!localFile.exists()) {
				report.addMessage(new Message(MessageLevel.ERROR,
						MessageFormat.format(this.bundle.getString("REP_MESSAGE_007"), fileName),
						indentLevel));
			} else {
				report.addMessage(new Message(
						MessageFormat.format(this.bundle.getString("REP_MESSAGE_008"), fileName),
						indentLevel));

				reportContentType(storedFile, localFile, allowPdfA1Only, report, indentLevel);
			}
		}
	}

	private void reportContentType(final DigitalFile storedFile, final File localFile,
			boolean allowAModeOnly, final Report report, final int indentLevel) {

		Preconditions.checkArgument(storedFile != null);
		Preconditions.checkArgument(localFile != null);
		Preconditions.checkArgument(localFile.exists());
		Preconditions.checkArgument(localFile != null);

		try {
			final String contentType = storedFile.getFileFormat();
			if (contentType != null) {

//				final String contentType = Files.probeContentType(localFile.toPath());

				final Tika tika = new Tika();
				final String fileMimeType = tika.detect(localFile);

				if (!StringUtils.equalsIgnoreCase(fileMimeType, contentType)) {
					report.addMessage(
							new Message(MessageLevel.WARN,
									MessageFormat.format(this.bundle.getString("REP_MESSAGE_018"),
											localFile.getName(), contentType, fileMimeType),
									indentLevel));
				}

				if (contentType.equals(MediaType.PDF.toString())) {
					report.addMessages(validatePdfFile(localFile, allowAModeOnly, indentLevel));
				}
			} else {
				report.addMessage(new Message(MessageLevel.ERROR, MessageFormat
						.format(this.bundle.getString("REP_MESSAGE_014"), localFile.getName()),
						indentLevel));
			}
		} catch (final IOException e) {
			report.addMessage(new Message(
					MessageLevel.ERROR, MessageFormat
							.format(this.bundle.getString("REP_MESSAGE_017"), localFile.getName()),
					indentLevel));
			if (log.isDebugEnabled()) {
				log.debug("Error reading PDF/A level", e);
			}
		}
	}

	/**
	 * Validate a PDF file.
	 *
	 * <p>
	 * According to VDI 2770, a PDF file must has the type PDF/A-2 or PDF/A-3.
	 * Containers in PDF are not allowed.
	 * </p>
	 *
	 * @param pdfFile        A PDF {@link File}; must not be <code>null</code> and
	 *                       exist.
	 * @param allowAModeOnly In general, only PDF/A-1A, PDF/A-2A and PDF/A-3A files
	 *                       are allowed.
	 * @param indentLevel    Level of indent.
	 * @return A {@link List} of {@link Message} including Information, warnings and
	 *         errors.
	 */
	public List<Message> validatePdfFile(final File pdfFile, boolean allowAModeOnly,
			final int indentLevel) {

		Preconditions.checkArgument(pdfFile != null, "pdfFile is null");
		Preconditions.checkArgument(pdfFile.exists(), "pdfFile does not exist");

		final List<Message> messages = new ArrayList<>();

		String pdfVersion = null;
		try {

			PdfValidator pdfValidator = new PdfValidator(this.locale);
			pdfVersion = pdfValidator.getPdfAVersion(pdfFile);

			messages.add(new Message(MessageFormat.format(this.bundle.getString("REP_MESSAGE_015"),
					pdfFile.getName(), pdfVersion), indentLevel));

			if (allowAModeOnly && !pdfVersion.toLowerCase().endsWith("a")) {
				messages.add(new Message(MessageLevel.ERROR,
						this.bundle.getString("REP_MESSAGE_038"), indentLevel));
			}

		} catch (final PdfValidationException e) {
			messages.add(new Message(
					MessageLevel.ERROR, MessageFormat
							.format(this.bundle.getString("REP_MESSAGE_017"), pdfFile.getName()),
					indentLevel));
			if (log.isWarnEnabled()) {
				log.warn("Error reading PDF/A level", e);
			}
			return messages;
		}

		return messages;
	}

	private void reportRelationships(final Document document, final Report report,
			final int indentLevel) {

		Preconditions.checkArgument(document != null, "document is null");
		Preconditions.checkArgument(report != null, "report is null");

		for (final DocumentVersion version : document.getDocumentVersion()) {

			for (final DocumentRelationship rel : version.getDocumentRelationship()) {
				reportDocumentRelation(rel, report, indentLevel);
			}
		}
	}

	private void reportDocumentRelation(final DocumentRelationship relation, final Report report,
			final int indentLevel) {

		Preconditions.checkArgument(relation != null, "relation is null");
		Preconditions.checkArgument(report != null, "report is null");

		if (!relation.getDocumentVersionId().isEmpty()) {

			final StringBuilder builder = new StringBuilder();

			builder.append(relation.getDocumentId().getAsText());
			builder.append(" / ");
			for (String versionId : relation.getDocumentVersionId()) {
				builder.append(versionId);
			}

			report.addMessage(new Message(MessageFormat.format(
					this.bundle.getString("REP_MESSAGE_012"), builder.toString()), indentLevel));

		} else {
			report.addMessage(
					new Message(MessageFormat.format(this.bundle.getString("REP_MESSAGE_012"),
							relation.getDocumentId().getAsText()), indentLevel));
		}

	}

	private static String getDocumentClassAsString(final DocumentClassification classification) {

		Preconditions.checkArgument(classification != null, "classification is null");

		final Map<String, String> categoryNames = Constants.getVdi2770GermanCategoryNames();

		final StringBuilder builder = new StringBuilder();
		builder.append(classification.getClassificationSystem());
		builder.append(" / ");
		builder.append(classification.getClassId());

		if (categoryNames.containsKey(classification.getClassId())) {
			builder.append(" [de: ");
			builder.append(categoryNames.get(classification.getClassId()));
			builder.append("]");
		}

		return builder.toString();
	}

	private void reportClassifications(final Document document, final Report report,
			final int indentLevel) {

		Preconditions.checkArgument(document != null, "docment is null");
		Preconditions.checkArgument(report != null, "report is null");

		final List<String> documentClassIds = document.getDocumentClassification().stream()
				.map(c -> getDocumentClassAsString(c)).collect(Collectors.toList());

		for (final String id : documentClassIds) {
			report.addMessage(
					new Message(MessageFormat.format(this.bundle.getString("REP_MESSAGE_011"), id),
							indentLevel));
		}
	}

	private void reportObjectIds(final Document document, final Report report,
			final int indentLevel) {

		Preconditions.checkArgument(document != null, "docment is null");
		Preconditions.checkArgument(report != null, "report is null");

		final List<ObjectId> ids = document.getReferencedObject().stream().map(o -> o.getObjectId())
				.flatMap(o -> o.stream()).collect(Collectors.toList());

		for (final ObjectId id : ids) {
			report.addMessage(
					new Message(
							MessageFormat.format(this.bundle.getString("REP_MESSAGE_010"),
									id.getObjectType().toString(), getObjectIdAsString(id)),
							indentLevel));
		}
	}

	private static String getObjectIdAsString(final ObjectId id) {

		Preconditions.checkArgument(id != null, "id is null");

		final StringBuilder builder = new StringBuilder();
		builder.append(id.getId());
		builder.append(";");
		builder.append(id.getRefType());
		builder.append(";");
		builder.append(id.getIsGloballyBiunique());

		return builder.toString();
	}

	private void reportDocumentIds(final Document document, final Report report,
			final int indentLevel) {

		Preconditions.checkArgument(document != null, "docment is null");
		Preconditions.checkArgument(report != null, "report is null");

		Preconditions.checkArgument(document != null, "document is null");
		Preconditions.checkArgument(report != null, "report is null");

		final List<String> documentIds = document.getDocumentId().stream().map(id -> id.getAsText())
				.collect(Collectors.toList());

		for (final String id : documentIds) {
			report.addMessage(
					new Message(MessageFormat.format(this.bundle.getString("REP_MESSAGE_009"), id),
							indentLevel));
		}
	}

	/**
	 * Check, if a XML is a valid meta data file according to VDI 2770
	 * 
	 * @param xmlFile     An existing XML {@link Files}. Must not be
	 *                    <code>null</code>
	 * @param report      A {@link Report} instance to log information and errors
	 * @param indentLevel An indent level for message indent
	 */
	public void validateVdiXmlFile(final File xmlFile, final Report report, final int indentLevel) {

		Preconditions.checkArgument(xmlFile != null, "xmlFile is null");
		Preconditions.checkArgument(report != null, "report is null");

		final XmlReader reader = new XmlReader(this.locale);

		try {

			final List<XmlValidationFault> errors = reader.validate(xmlFile);
			if (!Fault.hasWarnings(errors)) {
				report.addMessage(
						new Message(this.bundle.getString("REP_MESSAGE_020"), indentLevel));
			} else {
				report.addMessage(new Message(MessageLevel.WARN,
						MessageFormat.format(this.bundle.getString("REP_MESSAGE_016"),
								Integer.valueOf(errors.size())),
						indentLevel));
				for (XmlValidationFault fault : errors) {
					Message message = errorToMessage(fault, indentLevel);
					report.addMessage(message);
				}
			}
		} catch (final XmlProcessingException e) {
			report.addMessage(new Message(MessageLevel.ERROR, e.getMessage()));
		}
	}

	private Message errorToMessage(XmlValidationFault error, final int indentLevel) {

		// if error is of level ERROR
		if (error.getLevel() == FaultLevel.ERROR) {
			final String text = MessageFormat.format(this.bundle.getString("REP_MESSAGE_023"),
					Integer.valueOf(error.getLine()), error.getMessage(), error.getType());

			Message result = new Message(text);
			result.setIndent(indentLevel);
			result.setLevel(MessageLevel.ERROR);

			return result;
		}

		// Handle Warnings and Information
		final String text = MessageFormat.format(this.bundle.getString("REP_MESSAGE_024"),
				Integer.valueOf(error.getLine()), error.getLevel(), error.getMessage(),
				error.getType());

		Message result = new Message(text);
		result.setIndent(indentLevel);

		return result;
	}

	private static Message zipFaultToMessage(final ZipFault fault, final int indentLevel) {

		Message result = new Message(fault.getMessage());
		result.setIndent(indentLevel);

		// if error is of level ERROR
		if (fault.getLevel() == FaultLevel.ERROR) {
			result.setLevel(MessageLevel.ERROR);
		} else if (fault.getLevel() == FaultLevel.WARNING) {
			result.setLevel(MessageLevel.WARN);
		} else {
			result.setLevel(MessageLevel.INFO);
		}

		return result;
	}
}
