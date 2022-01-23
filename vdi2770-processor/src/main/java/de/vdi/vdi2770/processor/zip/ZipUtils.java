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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import de.vdi.vdi2770.metadata.xml.FileNames;
import de.vdi.vdi2770.metadata.xml.XmlReader;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.processor.ProcessorException;
import de.vdi.vdi2770.processor.common.Check;
import de.vdi.vdi2770.processor.common.Message;
import de.vdi.vdi2770.processor.common.MessageLevel;
import de.vdi.vdi2770.processor.common.ProcessorConfiguration;
import de.vdi.vdi2770.processor.report.Report;
import lombok.extern.log4j.Log4j2;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

/**
 * This utility class provides some methods to read and write ZIP files and ZIP
 * entries.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 *
 */
@Log4j2
public class ZipUtils {

	// prefix is ZU
	private final ResourceBundle bundle;
	private final Locale locale;

	/**
	 * media type for ZIP files
	 */
	public static final List<String> ZIP_CONTENT_TYPE = Collections
			.unmodifiableList(Arrays.asList("application/x-zip-compressed", "application/zip"));

	/**
	 * ctor
	 * 
	 * @param locale Desired {@link Locale} for validation messages; must not be
	 *               <code>null</code>.
	 */
	public ZipUtils(final Locale locale) {
		super();

		Preconditions.checkArgument(locale != null, "locale is null");

		this.bundle = ResourceBundle.getBundle("i8n.processor", locale);
		this.locale = (Locale) locale.clone();
	}

	/**
	 * Add a list of given file(names) to a new ZIP archive.
	 *
	 * @param zipFileName Name of the ZIP archive {@link File} to create; must not
	 *                    be <code>null</code> or empty.
	 * @param filesToZip  Non-null {@link List} of file paths to existing files.
	 * @throws ProcessorException Error processing the ZIP file. For example, some
	 *                            files might not exist or there was an error while
	 *                            zipping these files.
	 */

	public void zip(final String zipFileName, final Collection<String> filesToZip)
			throws ProcessorException {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(zipFileName));
		Preconditions.checkArgument(filesToZip != null);

		final List<File> fileList = new ArrayList<>();
		for (final String path : filesToZip) {
			fileList.add(new File(path));
		}

		final File zipFile = new File(zipFileName);

		zip(zipFile, fileList);

	}

	/**
	 * Create ZIP file with a given {@link Collection} of {@link File}s.
	 * 
	 * @param zipFile    A {@link File} to create as ZIP file.
	 * @param filesToZip A {@link Collection} of {@link File} that shall be zipped.
	 * @throws ProcessorException An error occurred while zipping the file.
	 */
	public void zip(final File zipFile, final Collection<File> filesToZip)
			throws ProcessorException {

		Preconditions.checkArgument(zipFile != null);
		Preconditions.checkArgument(filesToZip != null);

		if (filesToZip.isEmpty()) {
			log.warn("No file to zip given. Return.");
			return;
		}

		if (log.isInfoEnabled()) {
			log.info("Trying to ZIP " + filesToZip.size() + " to file " + zipFile.getName());
		}

		Check check = new Check(this.locale);

		final List<File> zipContent = new ArrayList<>();

		for (final File fileToZip : filesToZip) {
			if (log.isInfoEnabled()) {
				log.info("Zipping " + fileToZip);
			}

			// check files
			check.fileExists(fileToZip, "ZU_EXCEPTION_001");
			check.fileIsNotDirectory(fileToZip, "ZU_EXCEPTION_016");

			zipContent.add(fileToZip);
		}

		try (final ZipFile zipResult = new ZipFile(zipFile)) {
			zipResult.addFiles(zipContent);
		} catch (final ZipException e) {
			throw new ProcessorException(this.bundle.getString("ZU_EXCEPTION_002"), e);
		} catch (final IOException e) {
			throw new ProcessorException(this.bundle.getString("ZU_EXCEPTION_002"), e);
		}
	}

	/**
	 * Check, whether a given file is a document (ZIP) container file.
	 *
	 * <p>
	 * A document container must be a ZIP file that contains at least on PDF/A file
	 * and a XML meta data file.
	 * </p>
	 *
	 * @param zipFile The file to be checked; must not be <code>null</code>.
	 * @return <code>true</code>, if the file is a document container file.
	 * @throws ProcessorException
	 */
	public boolean isDocumentContainer(final File zipFile) throws ProcessorException {

		Preconditions.checkArgument(zipFile != null, "file is null");

		// is ZIP file
		if (!isZipFile(zipFile)) {
			return false;
		}

		// contains XML meta data file for a
		// document
		return zipFileContainsFile(zipFile, FileNames.METADATA_XML_FILE_NAME);
	}

	/**
	 * Check, whether a given file is a documentation (ZIP) container file.
	 *
	 * <p>
	 * A documentation container must be a ZIP file that contains at least on PDF/A
	 * file for the main document, a XML meta data file and at least one document
	 * container.
	 * </p>
	 *
	 * @param zipFile     The file to be checked; must not be <code>null</code>.
	 * @param checkForPdf Check for PDF document for the main document
	 * @return <code>true</code>, if the file is a documentation container file.
	 * @throws ProcessorException There was an error reading the container file.
	 */
	public boolean isDocumentationContainer(final File zipFile, boolean checkForPdf)
			throws ProcessorException {

		Preconditions.checkArgument(zipFile != null, "file is null");

		if (!isZipFile(zipFile)) {
			return false;
		}

		if (!zipFileContainsFile(zipFile, FileNames.MAIN_DOCUMENT_XML_FILE_NAME)) {
			return false;
		}

		if (checkForPdf) {
			if (!zipFileContainsFile(zipFile, FileNames.MAIN_DOCUMENT_PDF_FILE_NAME)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Check, if a given ZIP file is a document container file or a documentation
	 * container file
	 * 
	 * @param zipFile     An existing ZIP file; must not be <code>null</code>.
	 * @param checkForPdf Check for PDF document for the main document
	 * @return <code>True</code>, if the ZIP file is a container file
	 * @throws ProcessorException Error while reading the ZIP file.
	 */
	public boolean isContainer(final File zipFile, boolean checkForPdf) throws ProcessorException {

		return isDocumentationContainer(zipFile, checkForPdf) || isDocumentContainer(zipFile);
	}

	/**
	 * Check, whether a given file is a ZIP file.
	 *
	 * @param file A file to be checked; must not be <code>null</code>.
	 * @return <code>true</code>, if the file is a ZIP file.
	 */
	public static boolean isZipFile(final File file) {

		Preconditions.checkArgument(file != null, "file is null");

		if (!file.exists()) {
			if (log.isWarnEnabled()) {
				log.warn("File " + file.getAbsolutePath() + " does not exist.");
			}
			return false;
		}

		// check the MIME type of the file
		try {
			if (ZIP_CONTENT_TYPE.contains(Files.probeContentType(file.toPath()))) {
				return true;
			}
		} catch (final IOException e) {
			log.error("Can not probe content type of file " + file.getName(), e);
		}

		return false;
	}

	/**
	 * Check, whether a {@link File} is a valid ZIP file.
	 * 
	 * @param file A ZIP {@link File}
	 * @return True, if true ZIP {@link File} is valid.
	 */
	public static boolean isValidZipFile(final File file) {

		Preconditions.checkArgument(file != null, "file is null");
		if (!file.exists()) {
			return false;
		}

		try (final ZipFile zip = new ZipFile(file)) {
			return zip.isValidZipFile();
		} catch (IOException e) {
			log.warn("Error reading file " + file.getAbsolutePath(), e);
		}

		return false;
	}

	/**
	 * Check, if ZIP file is a ZIP Bomb
	 * <p>
	 * A maximum compression factor of 100 is allowed.
	 * <p>
	 * See https://en.wikipedia.org/wiki/Zip_bomb and
	 * https://thesecurityvault.com/appsec/attacks-with-zip-files-and-mitigations/
	 * for more information.
	 * </p>
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isBomb(final File file) {

		Preconditions.checkArgument(file != null, "file is null");
		if (!file.exists()) {
			return false;
		}

		if (isValidZipFile(file)) {

			ProcessorConfiguration config = ProcessorConfiguration.getInstance(Locale.getDefault());
			int maxCompressionFactor = config.getMaxZipCompressionFactor();
			int maxFileSize = config.getMaxZipFileSize();

			try (final ZipFile zip = new ZipFile(file)) {

				// we only need to validate the first layer, because ZIP
				// in ZIP files are extracted manually
				for (final FileHeader fileInZip : zip.getFileHeaders()) {

					if (!fileInZip.isDirectory()) {

						long compressedSize = fileInZip.getCompressedSize();
						if (compressedSize == 0 && log.isWarnEnabled()) {
							log.warn("File " + fileInZip.getFileName()
									+ " has compressed size of zero.");
						}

						long uncompressedSize = fileInZip.getUncompressedSize();

						// check for invalid size values
						if (compressedSize < 0 || uncompressedSize < 0) {
							log.error("Security Error: Size of of file in ZIP is less than zero in "
									+ file.getAbsolutePath());
							return true;
						}

						// calculate compression factor
						// check maximal factor if defined
						// -1 = do not check
						if (maxCompressionFactor > 0 && compressedSize > 0
								&& uncompressedSize / compressedSize > maxCompressionFactor) {
							log.error(
									"Security Error: Maximum compression rate exceeded in ZIP file "
											+ file.getAbsolutePath());
							return true;
						}

						// check maximal uncompressed file size
						// -1 = do not check
						if (maxFileSize > 0 && uncompressedSize > maxFileSize) {
							log.error(
									"Security Error: Maximum uncompressed file size of entry in ZIP file "
											+ file.getAbsolutePath());
							return true;
						}
					}
				}
			} catch (IOException e) {
				log.warn("Error reading file " + file.getAbsolutePath(), e);
			}
		}

		return false;
	}

	/**
	 * Check, whether a given ZIP {@link File} is encrypted
	 * 
	 * @param file A ZIP {@link File}
	 * @return true, if the ZIP {@link File} is encrypted, otherwise false. If the
	 *         given {@link File} is not a ZIP {@link File}, <code>false</code> will
	 *         return.
	 */
	public static boolean isEncryptedZipFile(final File file) {

		Preconditions.checkArgument(file != null, "file is null");

		try (final ZipFile zip = new ZipFile(file)) {
			return zip.isEncrypted();
		} catch (final ZipException e) {
			log.error("Can read encryption of ZIP file " + file.getName(), e);
		} catch (IOException e) {
			log.warn("Error reading file " + file.getAbsolutePath(), e);
		}

		return false;
	}

	/**
	 * Get a list of file names (not file paths) in a ZIP file.
	 *
	 * @param zipFile A ZIP file; must not be <code>null</code> and must exist.
	 * @return A {@link List} of all file names in the ZIP file.
	 * @throws ProcessorException There was an error reading the ZIP file.
	 */
	public List<String> listAllFilesInZip(final File zipFile) throws ProcessorException {

		Preconditions.checkArgument(zipFile != null, "zip file is null");

		Check check = new Check(this.locale);
		check.fileExists(zipFile, "ZU_EXCEPTION_003");

		final Path tempFolder = unzipToTemperaryFolder(zipFile, true);

		// read the file names
		final List<String> result = new ArrayList<>();
		result.addAll(getFileNamesInFolder(tempFolder, true));

		try {
			FileUtils.deleteDirectory(tempFolder.toFile());
		} catch (final IOException e) {
			log.warn("Can not delete folder " + tempFolder.toFile().getAbsolutePath(), e);
		}

		return result;
	}

	/**
	 * Get a {@link List} of file names (not path to the files) for a given
	 * {@link Path}.
	 *
	 * @param path                  A folder {@link Path}; must not be
	 *                              <code>null</code>.
	 * @param includeSubDirectories If <code>true</code>, file of sub directories
	 *                              will be included.
	 * @return A {@link List} of file names; can be empty.
	 * @throws ProcessorException There was an error reading file names.
	 */
	private List<String> getFileNamesInFolder(final Path path, final boolean includeSubDirectories)
			throws ProcessorException {

		final List<String> files = new ArrayList<>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {

			for (final Path entry : stream) {
				final Path entryFileName = entry.getFileName();
				if (entryFileName != null) {
					files.add(entryFileName.toString());
				}

				if (Files.isDirectory(entry) && includeSubDirectories) {
					files.addAll(getFileNamesInFolder(entry, includeSubDirectories));
				}
			}
		} catch (final IOException e) {
			throw new ProcessorException(MessageFormat
					.format(this.bundle.getString("ZU_EXCEPTION_007"), path.toString()), e);
		}

		return files;
	}

	/**
	 * Unzip a ZIP file to a temporary folder.
	 *
	 * <p>
	 * Included ZIP files that are documentation container or document container can
	 * be unzipped, too (see parameter extractZipsAndDelete).
	 * </p>
	 *
	 * @param zipFile              A ZIP file; must not be <code>null</code> and
	 *                             must exist.
	 * @param extractZipsAndDelete If <code>true</code>, all including container
	 *                             files according to VDI 2770 will be extracted and
	 *                             the origin ZIP container files will be deleted.
	 * @return The {@link Path} to the temporary folder
	 * @throws ProcessorException There was an error while unzipping the ZIP file.
	 */
	public Path unzipToTemperaryFolder(final File zipFile, final boolean extractZipsAndDelete)
			throws ProcessorException {
		return unzipToTemperaryFolder(zipFile, extractZipsAndDelete, null);
	}

	/**
	 * Unzip a ZIP file to a temporary folder.
	 *
	 * <p>
	 * Included ZIP files that are documentation container or document container can
	 * be unzipped, too (see parameter extractZipsAndDelete).
	 * </p>
	 *
	 * @param zipFile              A ZIP file; must not be <code>null</code> and
	 *                             must exist.
	 * @param extractZipsAndDelete If <code>true</code>, all including container
	 *                             files according to VDI 2770 will be extracted and
	 *                             the origin ZIP container files will be deleted.
	 * @param report               {@link Report} instances to protocol messages
	 * @return The {@link Path} to the temporary folder
	 * @throws ProcessorException There was an error while unzipping the ZIP file.
	 */
	public Path unzipToTemperaryFolder(final File zipFile, final boolean extractZipsAndDelete,
			final Report report) throws ProcessorException {

		Preconditions.checkArgument(zipFile != null, "zip file is null");

		final File tmpFile = createTemporaryFolder();
		final File targetFile = new File(tmpFile, FilenameUtils.removeExtension(zipFile.getName()));
		targetFile.deleteOnExit();

		unzip(zipFile, targetFile, extractZipsAndDelete, report);

		return targetFile.toPath();
	}

	/**
	 * Create a new temporary folder (e.g. to unzip a ZIP file).
	 *
	 * @return A {@link File} representing the new temporary folder.
	 * @throws ProcessorException There was an error creating the new folder.
	 */
	public File createTemporaryFolder() throws ProcessorException {

		try {
			final Path tempFolder = Files.createTempDirectory("vdi2770_");

			final File tmpFile = tempFolder.toFile();
			tmpFile.deleteOnExit();

			return tmpFile;

		} catch (final IOException e) {
			throw new ProcessorException(this.bundle.getString("ZU_EXCEPTION_015"), e);
		}
	}

	public void unzip(final File zipFile, final File targetDir, final boolean extractZipsAndDelete)
			throws ProcessorException {
		unzip(zipFile, targetDir, extractZipsAndDelete, null);
	}

	/**
	 * Unzip a ZIP file to a folder.
	 *
	 * <p>
	 * Included ZIP files that are documentation container or document container can
	 * be unzipped, too (see parameter extractZipsAndDelete).
	 * </p>
	 *
	 * @param zipFile              A ZIP file; must not be <code>null</code> and
	 *                             must exist.
	 * @param targetDir            The target directory; must not be
	 *                             <code>null</code>.
	 * @param extractZipsAndDelete If <code>true</code>, all including container
	 *                             files according to VDI 2770 will be extracted and
	 *                             the origin ZIP container files will be deleted.
	 * @param report               A {@link Report} to log messages (may be
	 *                             <code>null</code>).
	 * @throws ProcessorException There was an error while unzipping the ZIP file.
	 */
	public void unzip(final File zipFile, final File targetDir, final boolean extractZipsAndDelete,
			final Report report) throws ProcessorException {

		Preconditions.checkArgument(zipFile != null, "zip file is null");
		Preconditions.checkArgument(targetDir != null, "target dir is null");

		Check check = new Check(this.locale);
		check.fileExists(zipFile, "ZU_EXCEPTION_004");
		check.isZipFile(zipFile, "ZU_EXCEPTION_004");
		check.isValidZipFile(zipFile, "ZU_EXCEPTION_004");
		check.isNotEncryptedZipFile(zipFile, "ZU_EXCEPTION_004");
		if (ZipUtils.isBomb(zipFile)) {
			throw new ProcessorException(MessageFormat
					.format(this.bundle.getString("ZU_EXCEPTION_004"), zipFile.getAbsolutePath()));
		}

		// create the target directory if necessary.
		if (!targetDir.exists()) {
			try {
				Files.createDirectory(targetDir.toPath());
			} catch (final IOException e) {
				throw new ProcessorException(MessageFormat.format(
						this.bundle.getString("ZU_EXCEPTION_004"), targetDir.getAbsolutePath()), e);
			}
		} else {
			// targetDir must be a directory
			check.isDirectory(targetDir, "ZU_EXCEPTION_004");
		}

		// read the ZIP file
		try (final ZipFile zip = new ZipFile(zipFile)) {
			if (report != null) {
				report.addMessage(new Message(MessageLevel.INFO,
						MessageFormat.format(this.bundle.getString("ZU_MESSAGE_005"),
								zipFile.getName(), Long.valueOf(zipFile.length() / 1024))));
			}

			zip.extractAll(targetDir.getAbsolutePath());

			if (extractZipsAndDelete) {

				final File[] extractedZips = targetDir
						.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
				if (extractedZips != null && extractedZips.length > 0) {

					for (final File sub : extractedZips) {

						// do not check for VDI2770_Main.pdf file in the container
						if (isContainer(sub, false)) {

							if (log.isDebugEnabled()) {
								log.debug("Extraction ZIP file " + sub.getName());
							}

							final File subTargetDir = new File(sub.getParent(),
									com.google.common.io.Files
											.getNameWithoutExtension(sub.getName()));

							Report subReport = null;
							if (report != null) {
								subReport = report.createSubReport(sub);
							}
							unzip(sub, subTargetDir, extractZipsAndDelete, subReport);

							if (sub.delete() == false) {
								log.warn("Can not delete directory " + sub.getAbsolutePath());
							}
						}

					}
				}

			}

		} catch (final ZipException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_004"), zipFile.getAbsolutePath()), e);
		} catch (final IOException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_004"), zipFile.getAbsolutePath()), e);
		}
	}

	/**
	 * Check, whether a ZIP file contains a file identified by name.
	 *
	 * @param zipFile  A ZIP file
	 * @param fileName A file name to look for in the ZIP file.
	 * @return <code>true</code>, if there is a file with the given name in the ZIP
	 *         file.
	 * @throws ProcessorException There was an error reading the ZIP file.
	 */
	public boolean zipFileContainsFile(final File zipFile, final String fileName)
			throws ProcessorException {

		Preconditions.checkArgument(zipFile != null, "file is null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(fileName), "file name is null or empty");

		Check check = new Check(this.locale);
		check.fileExists(zipFile, "ZU_EXCEPTION_003");
		check.isZipFile(zipFile, "ZU_EXCEPTION_009");
		check.isValidZipFile(zipFile, "ZU_EXCEPTION_017");
		check.isNotEncryptedZipFile(zipFile, "ZU_EXCEPTION_018");

		try (final ZipFile zip = new ZipFile(zipFile)) {
			final List<FileHeader> fileHeaders = zip.getFileHeaders();
			return fileHeaders.stream().filter(e -> StringUtils.equals(e.getFileName(), fileName))
					.findFirst().isPresent();
		} catch (final ZipException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_010"), zipFile.getAbsolutePath()), e);
		} catch (IOException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_010"), zipFile.getAbsolutePath()), e);
		}
	}

	/**
	 * Check whether a given existing ZIP file contains a XML metadata file conform
	 * to VDI 2770.
	 * <p>
	 * This ZIP file must contain a XML file named
	 * {@link FileNames#METADATA_XML_FILE_NAME}.
	 * </p>
	 *
	 * @param zipFile An existing ZIP file.
	 * @return <code>true</code>, if the ZIP file contains a XML metadata file.
	 * @throws ProcessorException
	 */
	public boolean zipFileContainsMetadataXml(final File zipFile) throws ProcessorException {

		Preconditions.checkArgument(zipFile != null, "file is null");

		// get the XML file from ZIP
		final File metadataFile = getMetadataFileFromDocumentContainer(zipFile);

		if (metadataFile == null) {
			return false;
		}

		final XmlReader reader = new XmlReader(this.locale);

		// check the XML file
		return reader.isMetadataFile(metadataFile);
	}

	/**
	 * Get the XML metadata file from a document container.
	 *
	 * @param containerFile A document container a ZIP file.
	 * @return The XML metadata file.
	 * @throws ProcessorException There was an error reading the container file.
	 */
	public File getMetadataFileFromDocumentContainer(final File containerFile)
			throws ProcessorException {

		Preconditions.checkArgument(containerFile != null, "file is null");

		Check check = new Check(this.locale);
		check.isZipFile(containerFile, "ZU_EXCEPTION_009");
		if (!isDocumentContainer(containerFile)) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_011"), containerFile.getAbsolutePath()));
		}

		final File metadataFile = getgetMetadataFileFromContainer(containerFile,
				FileNames.METADATA_XML_FILE_NAME);

		if (metadataFile == null) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_012"), containerFile.getAbsolutePath()));
		}

		return metadataFile;
	}

	/**
	 * Get the XML metadata file from a documentation container.
	 *
	 * @param containerFile A documentation container a ZIP file; must not be
	 *                      <code>null</code>.
	 * @return The XML metadata file.
	 * @throws ProcessorException There was an error reading the container file.
	 */
	public File getMetadataFileFromDocumentationContainer(final File containerFile)
			throws ProcessorException {

		Preconditions.checkArgument(containerFile != null, "file is null");

		Check check = new Check(this.locale);
		check.isZipFile(containerFile, "ZU_EXCEPTION_009");
		if (!isDocumentationContainer(containerFile, false)) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_013"), containerFile.getAbsolutePath()));
		}

		final File metadataFile = getgetMetadataFileFromContainer(containerFile,
				FileNames.MAIN_DOCUMENT_XML_FILE_NAME);

		if (metadataFile == null) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_014"), containerFile.getAbsolutePath()));
		}

		return metadataFile;
	}

	private File getgetMetadataFileFromContainer(final File containerFile,
			final String metadataFileName) throws ProcessorException {

		try (final ZipFile zipFile = new ZipFile(containerFile)) {
			final FileHeader fileHeader = zipFile.getFileHeader(metadataFileName);
			try (InputStream inputStream = zipFile.getInputStream(fileHeader)) {
				final File metadataFile = extractTemporary(inputStream);
				return metadataFile;
			}
		} catch (final ZipException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_003"), containerFile.getAbsolutePath()), e);
		} catch (final IOException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_010"), containerFile.getAbsolutePath()), e);
		}

	}

	private static File extractTemporary(final InputStream stream) throws IOException {

		Preconditions.checkArgument(stream != null);

		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(stream, baos);
		final byte[] bytes = baos.toByteArray();

		final File file = File.createTempFile("vdi2770", ".zip");
		file.deleteOnExit();
		FileUtils.writeByteArrayToFile(file, bytes);

		return file;
	}

	/**
	 * Validate a given ZIP {@link File}
	 * 
	 * <p>
	 * The file must exist and must be of ZIP application type. It also must not be
	 * encrypted.
	 * </p>
	 * 
	 * @param zipFile An existing {@link File}.
	 * @return A {@link List} of {@link ZipFault}s indicating problems or warnings.
	 * @throws ProcessorException There was an error while reading the ZIP file.
	 */
	public List<ZipFault> validateZipFile(final File zipFile) throws ProcessorException {

		Preconditions.checkArgument(zipFile != null, "zip file is null");

		Check check = new Check(this.locale);
		check.fileExists(zipFile, "ZU_EXCEPTION_001");

		final List<ZipFault> faults = new ArrayList<>();

		try {

			// check if file is a ZIP file
			if (!ZipUtils.isZipFile(zipFile)) {
				final ZipFault fault = new ZipFault(FaultLevel.ERROR, zipFile.getName(),
						FaultType.HAS_INVALID_VALUE);
				fault.setMessage(this.bundle.getString("ZU_MESSAGE_002"));
				faults.add(fault);
				return faults;
			}

			// validate ZIP by zip4j
			if (!ZipUtils.isValidZipFile(zipFile)) {
				final ZipFault fault = new ZipFault(FaultLevel.ERROR, zipFile.getName(),
						FaultType.HAS_INVALID_VALUE);
				fault.setMessage(this.bundle.getString("ZU_MESSAGE_003"));
				faults.add(fault);
				return faults;
			}

			// ZIP files must not be encrypted
			if (ZipUtils.isEncryptedZipFile(zipFile)) {
				final ZipFault fault = new ZipFault(FaultLevel.ERROR, zipFile.getName(),
						FaultType.HAS_INVALID_VALUE);
				fault.setMessage(this.bundle.getString("ZU_MESSAGE_004"));
				faults.add(fault);
				return faults;
			}

			try (final ZipFile zip = new ZipFile(zipFile)) {
				final List<FileHeader> fileHeaders = zip.getFileHeaders();
				for (FileHeader header : fileHeaders) {
					if (header.isDirectory()) {

						// VDI 2770 itself does not support directories in ZIP files.
						// This will be reported as WARNING only, because other related container
						// files like iiRDS contain folders.
						final ZipFault fault = new ZipFault(FaultLevel.WARNING,
								header.getFileName(), FaultType.HAS_INVALID_VALUE);
						fault.setMessage(MessageFormat.format(
								this.bundle.getString("ZU_MESSAGE_001"), header.getFileName()));
						faults.add(fault);
					}
				}
			}

			return faults;

		} catch (final ZipException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_004"), zipFile.getAbsolutePath()), e);
		} catch (IOException e) {
			throw new ProcessorException(MessageFormat.format(
					this.bundle.getString("ZU_EXCEPTION_004"), zipFile.getAbsolutePath()), e);
		}

	}
}
