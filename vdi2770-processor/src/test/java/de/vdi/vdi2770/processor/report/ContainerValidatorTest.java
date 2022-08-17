package de.vdi.vdi2770.processor.report;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class ContainerValidatorTest {

	@Test
	public void issue16test() {

		assertTrue(ContainerValidator.mimeTypeEquals("image/vnd.dxf; format=ascii",
				"image/vnd.dxf; format=ascii"));
		assertTrue(ContainerValidator.mimeTypeEquals("image/vnd.dxf;format=ascii",
				"image/vnd.dxf; format=ascii"));
		assertTrue(ContainerValidator.mimeTypeEquals("image/vnd.dxf;format=\"ascii\"",
				"image/vnd.dxf; format=ascii"));
		assertTrue(
				ContainerValidator.mimeTypeEquals("image/vnd.dxf", "image/vnd.dxf; format=ascii"));
		assertTrue(
				ContainerValidator.mimeTypeEquals("image/vnd.dxf", "image/vnd.dxf;format=ascii"));
		assertTrue(
				ContainerValidator.mimeTypeEquals("image/vnd.dxf;format=ascii", "image/vnd.dxf"));
		assertTrue(
				ContainerValidator.mimeTypeEquals("image/vnd.dxf; format=ascii", "image/vnd.dxf"));
	}

}
