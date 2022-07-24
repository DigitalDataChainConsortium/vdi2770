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
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity DocumentClassification.
 * </p>
 * <p>
 * <em>DocumentClassification</em> one assigned document class of a document
 * classification system (for a document).
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(of = { "classId", "classificationSystem" })
@Data
@FieldNameConstants
public class DocumentClassification implements ModelEntity {

	private static final String ENTITY = "DocumentClassification";

	private String classId;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private final List<TranslatableString> className;

	public List<TranslatableString> getClassName() {
		return new ArrayList<>(this.className);
	}

	public void setClassName(final List<TranslatableString> classNames) {
		this.className.clear();
		if (classNames != null && !classNames.isEmpty()) {
			this.className.addAll(classNames);
		}
	}

	public void addClassName(final TranslatableString className) {
		Preconditions.checkArgument(className != null);

		this.className.add(className);
	}

	public void removeClassName(final TranslatableString className) {
		Preconditions.checkArgument(className != null);

		this.className.remove(className);
	}

	private String classificationSystem;

	/**
	 * Create a new instance of a {@link DocumentClassification}.
	 */
	public DocumentClassification() {
		this.className = new ArrayList<>();
	}

	/**
	 * Create a new instance for {@link DocumentClassification} (without a class
	 * name).
	 *
	 * @param classId              An identifier for a class within a classification
	 *                             system.
	 * @param classificationSystem A classification system.
	 */
	public DocumentClassification(final String classId, final String classificationSystem) {

		this();

		this.classId = classId;
		this.classificationSystem = classificationSystem;
	}

	/**
	 * Validate this instance.
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

		// classId must not be null or empty
		if (Strings.isNullOrEmpty(this.classId)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.classId, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL5"));
			faults.add(fault);
		}

		// className can be null or empty
		if (!CollectionUtils.isEmpty(this.className)) {

			faults.addAll(ValidationHelper.validateEntityList(this.className, ENTITY,
					Fields.className, locale, strict));

			// check for duplicate languages
			if (this.className.stream().map(TranslatableString::getLanguage)
					.collect(Collectors.toSet()).size() != this.className.size()) {
				final ValidationFault fault = new ValidationFault(ENTITY, Fields.className, parent,
						FaultLevel.ERROR, FaultType.HAS_DUPLICATE_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
				faults.add(fault);
			}
		}

		// classificationSystem must not be null or empty
		if (Strings.isNullOrEmpty(this.classificationSystem)) {
			final ValidationFault fault = new ValidationFault(ENTITY, Fields.classificationSystem,
					parent, FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL6"));
			faults.add(fault);
		}

		// check class ID and classification system
		if (!Strings.isNullOrEmpty(this.classId)
				&& !Strings.isNullOrEmpty(this.classificationSystem)) {

			// if classification system is VDI 2770
			if (Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME.equals(this.classificationSystem)) {

				// classId values must be conform to class ids defined in VDI
				// 2770
				if (!Constants.getVdi2770CategoryIds().contains(this.classId)) {
					final ValidationFault fault = new ValidationFault(ENTITY, Fields.classId,
							parent, FaultLevel.ERROR, FaultType.IS_INCONSISTENT);
					fault.setMessage(
							MessageFormat.format(bundle.getString(ENTITY + "_VAL2"), this.classId));
					faults.add(fault);
				}
			}
		}

		// check class names
		if (!Strings.isNullOrEmpty(this.classId)
				&& !Strings.isNullOrEmpty(this.classificationSystem)
				&& !CollectionUtils.isEmpty(this.className)) {

			if (Constants.VDI2770_CLASSIFICATIONSYSTEM_NAME.equals(this.classificationSystem)) {

				for (int i = 0; i < this.className.size(); i++) {

					final TranslatableString name = this.className.get(i);

					// check German names
					if (!Strings.isNullOrEmpty(name.getLanguage())
							&& ("de".equalsIgnoreCase(name.getLanguage())
									|| "de-de".equalsIgnoreCase(name.getLanguage()))) {

						// German names are specified in VDI 2770 guideline
						if (!Constants.isVdi2770GermanCategoryName(name.getText(), strict)) {

							final ValidationFault fault = new ValidationFault(ENTITY,
									Fields.className, FaultLevel.ERROR,
									FaultType.HAS_INVALID_VALUE);
							fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
							fault.setIndex(i);
							fault.setOriginalValue(name.getText());
							faults.add(fault);
						}
					}

					// check English names
					if (!Strings.isNullOrEmpty(name.getLanguage())
							&& ("en".equalsIgnoreCase(name.getLanguage())
									|| "en-US".equalsIgnoreCase(name.getLanguage()))) {

						// English names are specified in VDI 2770 guideline
						if (!Constants.isVdi2770EnglishCategoryName(name.getText(), strict)) {

							final ValidationFault fault = new ValidationFault(ENTITY,
									Fields.className, FaultLevel.ERROR,
									FaultType.HAS_INVALID_VALUE);
							fault.setMessage(bundle.getString(ENTITY + "_VAL4"));
							fault.setIndex(i);
							fault.setOriginalValue(name.getText());
							faults.add(fault);
						}
					}
				}
			}
		}

		return faults;
	}
}
