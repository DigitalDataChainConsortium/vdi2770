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
package de.vdi.vdi2770.metadata.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity DocumentDescription.
 * </p>
 * <p>
 * <em>DocumentDescription</em> contains informative metadata for a document
 * version including a title, summary and keywords.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(of = { "title" })
@Data
@FieldNameConstants
@AllArgsConstructor
public class DocumentDescription implements ModelEntity {

	private static final String ENTITY = "DocumentDescription";

	private String language;

	private String title;

	private String subTitle;

	private String summary;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<String> keyWords;

	public List<String> getKeyWords() {
		return new ArrayList<>(this.keyWords);
	}

	public void setKeyWords(final List<String> keyWords) {
		this.keyWords.clear();
		if (keyWords != null && !keyWords.isEmpty()) {
			this.keyWords.addAll(keyWords);
		}
	}

	public void addKeyWord(final String keyWord) {
		Preconditions.checkArgument(keyWord != null);

		this.keyWords.add(keyWord);
	}

	public void removeKeyWord(final String keyWord) {
		Preconditions.checkArgument(keyWord != null);

		this.keyWords.remove(keyWord);
	}

	/**
	 * Create a new instance of a {@link DocumentDescription}.
	 */
	public DocumentDescription() {
		this.keyWords = new ArrayList<>();
	}

	/**
	 * Validate this instance of <em>DocumentDescription</em>.
	 *
	 * @param parent The name of a parent element. Can be <code>null</code>.
	 * @param locale Desired {@link Locale} for validation messages.
	 * @param strict If <code>true</code>, strict validation is enabled.
	 * @return A {@link List} of {@link ValidationFault}s indicating validation
	 *         errors, warnings or information.
	 */
	@Override
	public List<ValidationFault> validate(final String parent, final Locale locale,
			boolean strict) {

		Preconditions.checkArgument(locale != null);

		final ResourceBundle bundle = ResourceBundle.getBundle("i8n.metadata", locale);

		final List<ValidationFault> faults = new ArrayList<>();

		// language is required
		if (Strings.isNullOrEmpty(this.language)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.language, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
			faults.add(fault);
		} else {
			// language format is according to ISO language code?
			if (!Constants.getIsoLanguageCodes().contains(this.language.toLowerCase())) {
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.language, parent,
						FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
				fault.setOriginalValue(this.language);
				fault.setMessage(
						MessageFormat.format(bundle.getString(ENTITY + "_VAL2"), this.language));
				faults.add(fault);
			}
		}

		// title is required
		if (Strings.isNullOrEmpty(this.title)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.title,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
			faults.add(fault);
		}

		// summary is required
		if (Strings.isNullOrEmpty(this.summary)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.summary,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL4"));
			faults.add(fault);
		}

		// At least one keyword is required
		if (CollectionUtils.isEmpty(this.keyWords)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.keyWords,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL5"));
			faults.add(fault);
		} else {
			// Validate the list of keywords
			faults.addAll(ValidationHelper.validateStrings(this.keyWords, ENTITY, Fields.keyWords,
					locale));
		}

		return faults;
	}
}
