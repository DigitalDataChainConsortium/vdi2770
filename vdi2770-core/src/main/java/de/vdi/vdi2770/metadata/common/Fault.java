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
package de.vdi.vdi2770.metadata.common;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * <p>
 * This class represents a fault.
 * </p>
 *
 * @author Johannes Schmidt (Leipzig University, Institute for Applied
 *         Informatics InfAI)
 */

@Data
@ToString(includeFieldNames = true)
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public abstract class Fault {

	/**
	 * The severity of the fault.
	 */
	@NonNull
	private FaultLevel level;

	/**
	 * The type of fault.
	 */
	@NonNull
	private FaultType type;

	/**
	 * Index of an validated object in a list of objects.
	 */
	private Integer index;

	/**
	 * The validation message.
	 */
	private String message;

	/**
	 * The original value that is faulty.
	 */
	private String originalValue;

	/**
	 * Check, if a {@link List} of {@link Fault}s contains errors.
	 *
	 * @param faults List of {@link Fault}s. Must not be <code>null</code>.
	 * @return <code>True</code>, if the {@link List} contains {@link Fault} with
	 *         {@link FaultLevel} {@link FaultLevel#ERROR}.
	 */
	public static boolean hasErrors(final List<? extends Fault> faults) {

		Preconditions.checkArgument(faults != null);

		return filter(faults, FaultLevel.ERROR).size() > 0;
	}

	/**
	 * Check, if a {@link List} of {@link Fault}s contains errors or warnings.
	 *
	 * @param faults List of {@link Fault}s. Must not be <code>null</code>.
	 * @return <code>True</code>, if the {@link List} contains {@link Fault} with
	 *         {@link FaultLevel} {@link FaultLevel#ERROR} or @link
	 *         {@link FaultLevel#WARNING}.
	 */
	public static boolean hasWarnings(final List<? extends Fault> faults) {

		Preconditions.checkArgument(faults != null);

		return filter(faults, FaultLevel.WARNING).size() > 0;
	}

	/**
	 * Filter a {@link List} of {@link Fault}s by a given {@link FaultLevel}.
	 *
	 * @param faults List of {@link Fault}s. Must not be <code>null</code>.
	 * @param level  {@link FaultLevel} to filer.
	 * @return Filtered {@link Fault}.
	 */
	public static List<? extends Fault> filter(final List<? extends Fault> faults,
			final FaultLevel level) {

		Preconditions.checkArgument(faults != null);

		if (level == FaultLevel.WARNING) {
			return faults.stream().filter(
					f -> f.getLevel() == FaultLevel.WARNING || f.getLevel() == FaultLevel.ERROR)
					.collect(Collectors.toList());
		}

		if (level == FaultLevel.ERROR) {
			return faults.stream().filter(f -> f.getLevel() == FaultLevel.ERROR)
					.collect(Collectors.toList());
		}

		return faults;
	}

}
