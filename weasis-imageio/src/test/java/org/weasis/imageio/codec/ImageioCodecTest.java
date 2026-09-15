/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.imageio.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaReader;

class ImageioCodecTest {

  @Test
  void jpegMimeIsSupported() {
    ImageioCodec codec = new ImageioCodec();
    assertTrue(codec.isMimeTypeSupported("image/jpeg"));
    assertEquals("ImageioCodec", codec.getCodecName());
  }

  @Test
  void zipAndOpenCvProvidersRegisterMimeTables() {
    assertTrue(new DicomZipCodec().isMimeTypeSupported("application/dicom+zip"));
    assertTrue(new NativeOpenCVCodec().isMimeTypeSupported("image/x-hdr"));
  }

  @Test
  void getMediaIoReadsLocalPng(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("codec.png");
    BufferedImage img = new BufferedImage(5, 2, BufferedImage.TYPE_INT_RGB);
    img.setRGB(1, 0, 0x00FF00);
    ImageIO.write(img, "png", png.toFile());
    ImageioCodec codec = new ImageioCodec();
    MediaReader reader = codec.getMediaIO(png.toUri(), "image/png", null);
    assertNotNull(reader);
    assertTrue(reader.getPreview() instanceof ImageElement);
    ImageElement image = (ImageElement) reader.getPreview();
    assertNotNull(image.getImage());
    assertEquals(5, image.getImage().getWidth());
    assertEquals(2, image.getImage().getHeight());
    assertEquals(1, reader.getMediaSeries().size());
    assertEquals(codec, reader.getCodec());
  }

  @Test
  void getMediaIoDoesNotFetchRemoteUri() {
    URI remote = URI.create("https://example.invalid/remote.jpg");
    MediaReader reader = new ImageioCodec().getMediaIO(remote, "image/jpeg", null);
    assertNotNull(reader);
    assertEquals(remote, reader.getUri());
    assertTrue(reader.getPreview() instanceof ImageElement);
    assertNull(((ImageElement) reader.getPreview()).getImage());
  }

  @Test
  void getMediaIoRejectsUnsupportedMime() {
    assertNull(
        new ImageioCodec().getMediaIO(URI.create("file:///tmp/a.dcm"), "application/dicom", null));
  }
}
