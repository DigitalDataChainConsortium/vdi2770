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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * This class defines some constants that are used in the metadata according to
 * VDI 2770 guideline. These constants refer to VDI 2770 classification
 * identifier and some String constants.
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */
public final class Constants {

	/**
	 * Name of the IEC 61355 classification system.
	 */
	public static final String IEC61355_CLASSIFICATION_NAME = "IEC61355";

	/**
	 * name of the VDI 2770 classification system.
	 */
	public static final String VDI2770_CLASSIFICATIONSYSTEM_NAME = "VDI2770:2018";

	/**
	 * Class ID of the identification category according to VDI 2770.
	 */
	public static final String VDI2770_IDENTIFICATION_CATEGORY = "01-01";

	/**
	 * Class ID of the technical specification category according to VDI 2770.
	 */
	public static final String VDI2770_TECHNICAL_SPECIFICATON_CATEGORY = "02-01";

	/**
	 * Class ID of the drawings category according to VDI 2770.
	 */
	public static final String VDI2770_DRAWINGS_CATEGORY = "02-02";

	/**
	 * Class ID of the assembly category according to VDI 2770.
	 */
	public static final String VDI2770_ASSEMBLY_CATEGORY = "02-03";

	/**
	 * Class ID of the certificates category according to VDI 2770.
	 */
	public static final String VDI2770_CERTIFICATE_CATEGORY = "02-04";

	/**
	 * Class ID of the mount category according to VDI 2770.
	 */
	public static final String VDI2770_MOUNT_CATEGORY = "03-01";

	/**
	 * Class ID of the operation category according to VDI 2770.
	 */
	public static final String VDI2770_OPERATION_CATEGORY = "03-02";

	/**
	 * Class ID of the safety category according to VDI 2770.
	 */
	public static final String VDI2770_SAFETY_CATEGORY = "03-03";

	/**
	 * Class ID of the maintenance category according to VDI 2770.
	 */
	public static final String VDI2770_MAINTENANCE_CATEGORY = "03-04";

	/**
	 * Class ID of the repair category according to VDI 2770.
	 */
	public static final String VDI2770_REPAIR_CATEGORY = "03-05";

	/**
	 * Class ID of the spareparts category according to VDI 2770.
	 */
	public static final String VDI2770_SPAREPARTS_CATEGORY = "03-06";

	/**
	 * Class ID of the contracts category according to VDI 2770.
	 */
	public static final String VDI2770_CONTRACT_CATEGORY = "04-01";

	/**
	 * Get a {@link List} of all VDI 2770 category IDs.
	 *
	 * @return A {@link List} of category IDs.
	 */
	public static List<String> getVdi2770CategoryIds() {

		return Lists.newArrayList(VDI2770_IDENTIFICATION_CATEGORY,
				VDI2770_TECHNICAL_SPECIFICATON_CATEGORY, VDI2770_DRAWINGS_CATEGORY,
				VDI2770_ASSEMBLY_CATEGORY, VDI2770_CERTIFICATE_CATEGORY, VDI2770_MOUNT_CATEGORY,
				VDI2770_OPERATION_CATEGORY, VDI2770_SAFETY_CATEGORY, VDI2770_SAFETY_CATEGORY,
				VDI2770_MAINTENANCE_CATEGORY, VDI2770_REPAIR_CATEGORY, VDI2770_REPAIR_CATEGORY,
				VDI2770_SPAREPARTS_CATEGORY, VDI2770_CONTRACT_CATEGORY);
	}

	/**
	 * Get a {@link List} of all German VDI 2770 category names.
	 *
	 * @return A {@link List} of category names.
	 */
	public static Map<String, String> getVdi2770GermanCategoryNames() {

		final Map<String, String> map = new HashMap<>();

		map.put(VDI2770_IDENTIFICATION_CATEGORY, "Identifikation");
		map.put(VDI2770_TECHNICAL_SPECIFICATON_CATEGORY, "Technische Spezifikation");
		map.put(VDI2770_DRAWINGS_CATEGORY, "Zeichnungen, Pläne");
		map.put(VDI2770_ASSEMBLY_CATEGORY, "Bauteile");
		map.put(VDI2770_CERTIFICATE_CATEGORY, "Zeugnisse, Zertifikate, Bescheinigungen");
		map.put(VDI2770_MOUNT_CATEGORY, "Montage, Demontage");
		map.put(VDI2770_OPERATION_CATEGORY, "Bedienung");
		map.put(VDI2770_SAFETY_CATEGORY, "Allgemeine Sicherheit");
		map.put(VDI2770_MAINTENANCE_CATEGORY, "Inspektion, Wartung, Prüfung");
		map.put(VDI2770_REPAIR_CATEGORY, "Instandsetzung");
		map.put(VDI2770_SPAREPARTS_CATEGORY, "Ersatzteile");
		map.put(VDI2770_CONTRACT_CATEGORY, "Vertragsunterlagen");

		return map;
	}

	public static boolean isVdi2770GermanCategoryName(final String category, boolean strict) {
		return isCategoryName(getVdi2770GermanCategoryNames().values(), category, strict);
	}

	/**
	 * Get English category name (defined in VDI 2770 guideline) as map. The key is
	 * the constant for the category and the value is the English name.
	 *
	 * @return A map containing English category names.
	 */
	public static Map<String, String> getVdi2770EnglishCategoryNames() {

		final Map<String, String> map = new HashMap<>();

		map.put(VDI2770_IDENTIFICATION_CATEGORY, "Identification");
		map.put(VDI2770_TECHNICAL_SPECIFICATON_CATEGORY, "Technical specification");
		map.put(VDI2770_DRAWINGS_CATEGORY, "Drawings, plans");
		map.put(VDI2770_ASSEMBLY_CATEGORY, "Components");
		map.put(VDI2770_CERTIFICATE_CATEGORY, "Certificates");
		map.put(VDI2770_MOUNT_CATEGORY, "Assembly, disassembly");
		map.put(VDI2770_OPERATION_CATEGORY, "Operation");
		map.put(VDI2770_SAFETY_CATEGORY, "General safety");
		map.put(VDI2770_MAINTENANCE_CATEGORY, "Inspection, maintenance");
		map.put(VDI2770_REPAIR_CATEGORY, "Repair");
		map.put(VDI2770_SPAREPARTS_CATEGORY, "Spare parts");
		map.put(VDI2770_CONTRACT_CATEGORY, "contract documents");

		return map;
	}

	public static boolean isVdi2770EnglishCategoryName(final String category, boolean strict) {

		Preconditions.checkArgument(!Strings.isNullOrEmpty(category), "category is empty");

		return isCategoryName(getVdi2770EnglishCategoryNames().values(), category, strict);
	}

	private static boolean isCategoryName(final Collection<String> categories,
			final String category, boolean strict) {

		Preconditions.checkArgument(categories != null, "categories is empty");

		if (Strings.isNullOrEmpty(category)) {
			return false;
		}

		if (strict) {
			return categories.contains(category);
		}

		return categories.stream().map(v -> v.toLowerCase()).collect(Collectors.toList())
				.contains(StringUtils.lowerCase(category));
	}

	/**
	 * Get a {@link List} of all ISO language codes.
	 *
	 * @return {@link List} of language codes.
	 */
	public static List<String> getIsoLanguageCodes() {

		return Lists.newArrayList("ab", "aa", "af", "ak", "sq", "am", "ar", "an", "hy", "as", "av",
				"ae", "ay", "az", "bm", "ba", "eu", "be", "bn", "bh", "bi", "bs", "br", "bg", "my",
				"ca", "ch", "ce", "ny", "zh", "cv", "kw", "co", "cr", "hr", "cs", "da", "dv", "nl",
				"dz", "en", "eo", "et", "ee", "fo", "fj", "fi", "fr", "ff", "gl", "ka", "de", "el",
				"gn", "gu", "ht", "ha", "he", "hz", "hi", "ho", "hu", "ia", "id", "ie", "ga", "ig",
				"ik", "io", "is", "it", "iu", "ja", "jv", "kl", "kn", "kr", "ks", "kk", "km", "ki",
				"rw", "ky", "kv", "kg", "ko", "ku", "kj", "la", "lb", "lg", "li", "ln", "lo", "lt",
				"lu", "lv", "gv", "mk", "mg", "ms", "ml", "mt", "mi", "mr", "mh", "mn", "na", "nv",
				"nd", "ne", "ng", "nb", "nn", "no", "ii", "nr", "oc", "oj", "cu", "om", "or", "os",
				"pa", "pi", "fa", "pl", "ps", "pt", "qu", "rm", "rn", "ro", "ru", "sa", "sc", "sd",
				"se", "sm", "sg", "sr", "gd", "sn", "si", "sk", "sl", "so", "st", "es", "su", "sw",
				"ss", "sv", "ta", "te", "tg", "th", "ti", "bo", "tk", "tl", "tn", "to", "tr", "ts",
				"tt", "tw", "ty", "ug", "uk", "ur", "uz", "ve", "vi", "vo", "wa", "cy", "wo", "fy",
				"xh", "yi", "yo", "za", "zu", "abk", "aar", "afr", "aka", "sqi", "amh", "ara",
				"arg", "hye", "asm", "ava", "ave", "aym", "aze", "bam", "bak", "eus", "bel", "ben",
				"bih", "bis", "bos", "bre", "bul", "mya", "cat", "cha", "che", "nya", "zho", "chv",
				"cor", "cos", "cre", "hrv", "ces", "dan", "div", "nld", "dzo", "eng", "epo", "est",
				"ewe", "fao", "fij", "fin", "fra", "ful", "glg", "kat", "deu", "ell", "grn", "guj",
				"hat", "hau", "heb", "her", "hin", "hmo", "hun", "ina", "ind", "ile", "gle", "ibo",
				"ipk", "ido", "isl", "ita", "iku", "jpn", "jav", "kal", "kan", "kau", "kas", "kaz",
				"khm", "kik", "kin", "kir", "kom", "kon", "kor", "kur", "kua", "lat", "ltz", "lug",
				"lim", "lin", "lao", "lit", "lub", "lav", "glv", "mkd", "mlg", "msa", "mal", "mlt",
				"mri", "mar", "mah", "mon", "nau", "nav", "nde", "nep", "ndo", "nob", "nno", "nor",
				"iii", "nbl", "oci", "oji", "chu", "orm", "ori", "oss", "pan", "pli", "fas", "pol",
				"pus", "por", "que", "roh", "run", "ron", "rus", "san", "srd", "snd", "sme", "smo",
				"sag", "srp", "gla", "sna", "sin", "slk", "slv", "som", "sot", "spa", "sun", "swa",
				"ssw", "swe", "tam", "tel", "tgk", "tha", "tir", "bod", "tuk", "tgl", "tsn", "ton",
				"tur", "tso", "tat", "twi", "tah", "uig", "ukr", "urd", "uzb", "ven", "vie", "vol",
				"wln", "cym", "wol", "fry", "xho", "yid", "yor", "zha", "zul", "alb", "arm", "baq",
				"bur", "chi", "cze", "dut", "fre", "geo", "ger", "gre", "ice", "mac", "may", "mao",
				"per", "rum", "slo", "tib", "wel");
	}

}
