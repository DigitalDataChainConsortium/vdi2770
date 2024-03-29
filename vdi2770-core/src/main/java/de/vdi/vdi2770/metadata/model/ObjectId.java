/*******************************************************************************
 * Copyright (C) 2021-2023 Johannes Schmidt
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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.vdi.vdi2770.metadata.common.FaultLevel;
import de.vdi.vdi2770.metadata.common.FaultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * <p>
 * Information model entity ObjectId.
 * </p>
 * <p>
 * <em>ObjectId</em> represents identification information for a
 * {@link ReferencedObject}. The object can be of type individual or type.
 * </p>
 * <p>
 * For more information on this entity see VDI 2770 guideline.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
@ToString(includeFieldNames = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(of = { "id", "objectType" })
public class ObjectId implements ModelEntity {

	private static final String ENTITY = "ObjectId";

	private ObjectType objectType;

	private String id;

	private Boolean isGloballyBiunique;

	/**
	 * Is this {@link ObjectId} globally biunique.
	 *
	 * @return <code>True</code>, if the {@link ObjectId} is globally biunique.
	 */
	public Boolean getIsGloballyBiunique() {
		if (this.isGloballyBiunique == null) {
			return Boolean.FALSE;
		}

		return this.isGloballyBiunique;
	}

	private String refType;

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

		// objectType is required
		if (this.objectType == null) {
			ValidationFault fault = new ValidationFault(ENTITY, Fields.objectType, parent,
					FaultLevel.ERROR, FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL1"));
			faults.add(fault);
		}

		// id is required
		if (Strings.isNullOrEmpty(this.id)) {
			ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent, FaultLevel.ERROR,
					FaultType.IS_EMPTY);
			fault.setMessage(bundle.getString(ENTITY + "_VAL2"));
			faults.add(fault);
		}

		if (!Strings.isNullOrEmpty(this.refType) && !Strings.isNullOrEmpty(this.id)
				&& StringUtils.equals(RefType.DIN_SPEC_91406_ID, refType)) {

			faults.addAll(validateUrl(this.id, bundle, parent));
		}

		return faults;
	}

	/**
	 * Validate an object ID that is encodes as URL.
	 * 
	 * URL specification is provided in DIN SPEC 91406:2019-12. In 2022, the
	 * international standard prEN IEC 61406:2022 "Identification Link" has been
	 * published that is based on the DIN SPEC.
	 * 
	 * This code is based on the reference implementation provided by Brezel31 at
	 * <a href="https://github.com/Brezel31/URL_Check_IEC61406">github</a>, which is
	 * published under the MIT License.
	 * 
	 * @param url    The object ID encodes as URL
	 * @param bundle The {@link ResourceBundle} used for translation
	 * @param parent The parent entity
	 * @return A {@link List} of {@link ValidationFault}s. If this {@link List} is
	 *         empty or does not contain entries with {@link FaultLevel#ERROR}, the
	 *         given URL is valid.
	 * 
	 * @since 0.9.9
	 * 
	 * @author Johannes Schmidt (Leipzig University, Institute for Applied
	 *         Informatics InfAI) and Peter Geiger
	 * 
	 */
	private static List<ValidationFault> validateUrl(final String url, final ResourceBundle bundle,
			final String parent)  {

		final List<ValidationFault> faults = new ArrayList<>();

		try {

			// append scheme if missing
			// see RFC 3986 
			String toParse = url;
			if (!toParse.contains("://")) {
				toParse = "http://" + toParse;
			}
			
			// see Requirement 5.1 in DIN SPEC 91406
			// parse the URI string
			// note: java.net.URL throws exception in case of unknown scheme,
			// so, we use URI
			URI parsedUri = new URI(toParse);

			final String host = parsedUri.getHost();
			if (!Strings.isNullOrEmpty(host)) {

				// Requirement 5.3 in DIN SPEC 91406
				if (!StringUtils.equals(parsedUri.getHost(), parsedUri.getHost().toLowerCase())) {
					ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent,
							FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
					fault.setMessage(bundle.getString(ENTITY + "_VAL4"));
					faults.add(fault);
				}
			} else {
				ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent, FaultLevel.ERROR,
						FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
				faults.add(fault);
			}

			// see Requirement 4 in DIN SPEC 91406
			if (url.length() > 100) {
				ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent,
						FaultLevel.WARNING, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL5"));
				faults.add(fault);
			}

			// see Requirement 4 in DIN SPEC 91406
			if (url.length() > 255) {
				ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent,
						FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL6"));
				faults.add(fault);
			}

			if (url.toLowerCase().contains("xn--")) {
				ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent,
						FaultLevel.WARNING, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(bundle.getString(ENTITY + "_VAL7"));
				faults.add(fault);
			}

			// see Requirement 5.2 in DIN SPEC 91406
			Pattern p = Pattern.compile("[^A-Za-z0-9#,$&'()*+\\-./~\\[\\]=?:;!_@]");
			Matcher m = p.matcher(url);
			while (m.find()) {
				ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent,
						FaultLevel.ERROR, FaultType.HAS_INVALID_VALUE);
				fault.setMessage(MessageFormat.format(bundle.getString(ENTITY + "_VAL8"), m.start(),
						m.end()));
				faults.add(fault);
			}

		} catch (final URISyntaxException e) {
			ValidationFault fault = new ValidationFault(ENTITY, Fields.id, parent, FaultLevel.ERROR,
					FaultType.HAS_INVALID_VALUE);
			fault.setMessage(bundle.getString(ENTITY + "_VAL3"));
			faults.add(fault);
		}

		return faults;
	}
}
