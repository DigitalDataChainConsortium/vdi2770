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
package de.vdi.vdi2770.processor.zip;

import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import de.vdi.vdi2770.metadata.MetadataException;
import de.vdi.vdi2770.metadata.common.Fault;
import de.vdi.vdi2770.metadata.model.DigitalFile;
import de.vdi.vdi2770.metadata.model.Document;
import de.vdi.vdi2770.metadata.model.DocumentId;
import de.vdi.vdi2770.metadata.model.ValidationFault;
import de.vdi.vdi2770.metadata.xml.FileNames;
import de.vdi.vdi2770.metadata.xml.XmlProcessingException;
import de.vdi.vdi2770.metadata.xml.XmlReader;
import de.vdi.vdi2770.processor.ProcessorException;
import lombok.extern.log4j.Log4j2;

/**
 * This processor creates documentation and document container files for
 * hierarchical file structures. For each folder, the VDI 2770 xml meta data
 * file is processed and the referenced digital files are included as a document
 * or documentation container.
 * 
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class ContainerProcessor {

	// Prefix is CP
	private final ResourceBundle bundle;
	private final Locale locale;
	private final boolean isStrictMode;

	/**
	 * ctor
	 * 
	 * @param locale Desired {@link Locale} for validation messages.
	 */
	public ContainerProcessor(final Locale locale) {
		this(locale, false);
	}

	/**
	 * ctor
	 *
	 * @param locale       Desired {@link Locale} for validation messages.
	 * @param isStrictMode Enable or disable strict validation.
	 */
	public ContainerProcessor(final Locale locale, final boolean isStrictMode) {
		super();

		Preconditions.checkArgument(locale != null);

		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
		this.locale = (Locale) locale.clone();
		this.isStrictMode = isStrictMode;
	}

	/**
	 * <p>
	 * Create a document or documentation container depending on the type of meta
	 * data.
	 * </p>
	 * 
	 * <p>
	 * Every sub folder is analyzed.
	 * </p>
	 * 
	 * @param folder An existing folder containing a meta data XML file and may be
	 *               sub-folders.
	 * @return The zipped container file.
	 * @throws ProcessorException There was an error processing the folder or
	 *                            digital files.
	 * @throws MetadataException  The XML meta data files could not be processed
	 */
	public File createContainer(final File folder) throws ProcessorException, MetadataException {

		Preconditions.checkArgument(folder != null);

		if (!folder.exists()) {
			throw new ProcessorException(MessageFormat
					.format(this.bundle.getString("CP_EXCEPTION_001"), folder.getAbsolutePath()));
		}

		// include sub-folder recursively
		// sub-folders are processed first to include this zipped container files in
		// this container
		List<File> subFolders = getSubFolders(folder);
		if (subFolders.size() > 0) {
			for (final File subFolder : subFolders) {
				File zip = createContainer(subFolder);
				if (zip != null) {
					zip.deleteOnExit();
				}
			}
		}

		if (isDocumentationContainerFolder(folder)) {
			return createDocumentationContainer(folder);
		}

		if (isDocumentContainerFolder(folder)) {
			return createDocumentContainer(folder);
		}

		if (log.isInfoEnabled()) {
			log.info("Folder " + folder.getAbsolutePath() + " is not a container.");
		}

		return null;

	}

	/**
	 * Read sup-folders of the next level
	 * 
	 * @param folder
	 * @return
	 */
	private static List<File> getSubFolders(final File folder) {

		File[] subFolders = folder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		if (subFolders != null && subFolders.length > 0) {
			return Arrays.asList(subFolders);
		}

		return new ArrayList<>();
	}

	/**
	 * Get VDI 2770 meta data XML files from sub-folders
	 * 
	 * @param folder
	 * @return
	 */
	private static List<File> getMetadataFilesFromSubfolders(final File folder) {

		final List<File> metaDataFiles = new ArrayList<>();

		final List<File> subFolders = getSubFolders(folder);
		if (subFolders.isEmpty()) {
			return metaDataFiles;
		}

		for (final File sub : subFolders) {

			File documentFile = new File(sub, FileNames.METADATA_XML_FILE_NAME);
			if (documentFile.exists()) {
				metaDataFiles.add(documentFile);
			}

			File documentationFile = new File(sub, FileNames.MAIN_DOCUMENT_XML_FILE_NAME);
			if (documentationFile.exists()) {
				metaDataFiles.add(documentationFile);
			}
		}

		return metaDataFiles;
	}

	private static boolean isDocumentContainerFolder(final File folder) {
		File documentContainerMetadataFile = new File(folder, FileNames.METADATA_XML_FILE_NAME);
		return documentContainerMetadataFile.exists();
	}

	private static boolean isDocumentationContainerFolder(final File folder) {
		File documentationContainerMetadataFile = new File(folder,
				FileNames.MAIN_DOCUMENT_XML_FILE_NAME);
		return documentationContainerMetadataFile.exists();
	}

	/**
	 * Zip all digital files and meta data files into a container file
	 * 
	 * @param folder folder to process
	 * @return Zip file of the created container
	 * @throws ProcessorException
	 */
	private File createDocumentContainer(File folder) throws ProcessorException {

		Preconditions.checkArgument(folder != null);

		final File documentContainerMetadataFile = new File(folder,
				FileNames.METADATA_XML_FILE_NAME);

		// read meta data and process as POJOs
		final Document metaData = readMetadata(documentContainerMetadataFile);

		final List<DigitalFile> digitalFiles = getDigitalFiles(metaData);
		if (digitalFiles.isEmpty()) {
			// digital files are required
			throw new ProcessorException(
					MessageFormat.format(this.bundle.getString("CP_EXCEPTION_002"),
							documentContainerMetadataFile.getAbsolutePath()));
		}

		final List<File> filesToZip = new ArrayList<>();

		for (final DigitalFile digitalFile : digitalFiles) {

			File content = new File(folder, digitalFile.getFileName());
			if (!content.exists()) {
				throw new ProcessorException(MessageFormat.format(
						this.bundle.getString("CP_EXCEPTION_003"), content.getAbsolutePath(),
						documentContainerMetadataFile.getAbsolutePath()));
			}

			filesToZip.add(content);
		}

		// add xml meta data file to zip files
		filesToZip.add(documentContainerMetadataFile);

		File zipFile = getZipFile(metaData, folder);
		if (log.isInfoEnabled()) {
			log.info("Writing document container file " + zipFile);
		}

		ZipUtils zipUtils = new ZipUtils(this.locale);
		zipUtils.zip(zipFile, filesToZip);

		return zipFile;
	}

	/**
	 * Get document IDs of a {@link Document}.
	 * 
	 * @param document
	 * @return
	 */
	private static DocumentId getDocumentId(final Document document) {

		Preconditions.checkArgument(document != null);

		final List<DocumentId> primaryIds = document.getDocumentId().stream()
				.filter(i -> i.getIsPrimary().booleanValue()).collect(Collectors.toList());

		DocumentId id = null;
		if (primaryIds.size() > 0) {
			id = primaryIds.get(0);
		} else {
			id = document.getDocumentId().get(0);
		}

		return id;
	}

	/**
	 * <p>
	 * Create a documentation container zip file
	 * </p>
	 * 
	 * @param folder
	 * @return
	 * @throws ProcessorException
	 * @throws MetadataException
	 */
	private File createDocumentationContainer(File folder)
			throws ProcessorException, MetadataException {

		Preconditions.checkArgument(folder != null);

		final File documentationContainerMetadataFile = new File(folder,
				FileNames.MAIN_DOCUMENT_XML_FILE_NAME);
		final Document metaData = readMetadata(documentationContainerMetadataFile);

		final List<DigitalFile> digitalFiles = getDigitalFiles(metaData);
		if (digitalFiles.isEmpty()) {
			// digital files are required
			throw new ProcessorException(
					MessageFormat.format(this.bundle.getString("CP_EXCEPTION_004"),
							documentationContainerMetadataFile.getAbsolutePath()));
		}

		final List<File> filesToZip = new ArrayList<>();

		List<File> subordinatedFiles = getMetadataFilesFromSubfolders(folder);

		for (final DigitalFile digitalFile : digitalFiles) {

			File content = new File(folder, digitalFile.getFileName());
			if (!content.exists()) {
				throw new ProcessorException(MessageFormat.format(
						this.bundle.getString("CP_EXCEPTION_003"), content.getAbsolutePath(),
						documentationContainerMetadataFile.getAbsolutePath()));
			}

			filesToZip.add(content);
		}

		filesToZip.add(documentationContainerMetadataFile);

		// include related document container and documentation container
		for (final File sub : subordinatedFiles) {
			if (isReferencedByMainDocument(metaData, sub)) {
				filesToZip.add(getZipFile(sub));
			} else {
				log.warn(
						"Found file " + sub.getName() + " thas is not referenced by main document");
			}
		}

		File zipFile = getZipFile(metaData, folder);
		if (log.isInfoEnabled()) {
			log.info("Writing document container file " + zipFile);
		}

		ZipUtils zipUtils = new ZipUtils(this.locale);
		zipUtils.zip(zipFile, filesToZip);

		return zipFile;
	}

	/**
	 * Check, whether a container file is referenced by a main document.
	 *
	 * <p>
	 * The file might be a documentation container or a document container.
	 * </p>
	 * </p>
	 * A document can refer to other documents using {@link DocumentRelationship}s.
	 * The related {@link DocumentId}s of a main document are compared to
	 * {@link DocumentId}s contained in a container (document container or
	 * documentation container) file.
	 * <p>
	 *
	 * @param mainDocument    A {@link MainDocument} containing document
	 *                        relationships.
	 * @param xmlMetadataFile A container file
	 * @return <code>true</code>, if the container wraps a {@link Document} with a
	 *         {@link DocumentId} that is referenced by the main document.
	 * @throws MetadataException  There was an error reading the document IDs from
	 *                            the metadata.
	 * @throws ProcessorException There was an error processing the container file.
	 */
	private boolean isReferencedByMainDocument(final Document mainDocument,
			final File xmlMetadataFile) throws MetadataException, ProcessorException {

		Preconditions.checkArgument(mainDocument != null, "Parameter mainDocument is null");
		Preconditions.checkArgument(xmlMetadataFile != null, "Parameter containerFile is null");
		Preconditions.checkArgument(xmlMetadataFile.exists(), "xmlMetadataFile does not exist");

		final List<DocumentId> documentIds = getDocumentIds(xmlMetadataFile);

		final List<DocumentId> refersTo = mainDocument.getDocumentVersion().stream()
				.map(v -> v.getDocumentRelationship()).flatMap(Collection::stream)
				.map(r -> r.getDocumentId()).collect(Collectors.toList());

		// for each document relationship in the current main document
		for (final DocumentId referencedDocumentId : refersTo) {

			// for each document id defined in the container
			for (final DocumentId documentIdInContainer : documentIds) {

				if (referencedDocumentId.equals(documentIdInContainer)) {
					return true;
				}
			}
		}

		return false;
	}

	private List<DocumentId> getDocumentIds(final File metadataFile)
			throws MetadataException, ProcessorException {

		// reader to read XML metadata file
		final XmlReader reader = new XmlReader(this.locale);

		// read the document
		final Document document = reader.read(metadataFile);
		if (document == null) {
			throw new ProcessorException(this.bundle.getString("CP_EXCEPTION_005"));
		}

		// return list of document ids of the (main) document
		return document.getDocumentId();
	}

	private File getZipFile(final File metadataFile) throws ProcessorException {

		Preconditions.checkArgument(metadataFile != null);

		final Document metaData = readMetadata(metadataFile);

		return getZipFile(metaData, metadataFile.getParentFile());
	}

	private static File getZipFile(final Document document, final File folder) {

		Preconditions.checkArgument(document != null);

		DocumentId id = getDocumentId(document);
		File zipFile = new File(folder, id.getId() + ".zip");
		return zipFile;

	}

	/**
	 * Extract {@link List} of {@link DigitalFile} from a {@link Document}
	 * 
	 * @param document A {@link Document} instance
	 * @return A {@link List} of {@link DigitalFile}
	 */
	private static List<DigitalFile> getDigitalFiles(final Document document) {

		Preconditions.checkArgument(document != null);

		return document.getDocumentVersion().stream().map(v -> v.getDigitalFile())
				.flatMap(e -> e.stream()).collect(Collectors.toList());
	}

	private Document readMetadata(final File metadataFile) throws ProcessorException {

		final XmlReader reader = new XmlReader(this.locale);

		try {
			Document document = reader.read(metadataFile);

			final List<ValidationFault> errors = document.validate(this.locale, this.isStrictMode);
			if (Fault.hasErrors(errors)) {
				throw new ProcessorException(MessageFormat.format(
						this.bundle.getString("CP_EXCEPTION_006"), metadataFile.getAbsolutePath()));
			}

			return document;
		} catch (final XmlProcessingException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("CP_EXCEPTION_007"), metadataFile.getAbsolutePath()), e);
		}

	}

}
