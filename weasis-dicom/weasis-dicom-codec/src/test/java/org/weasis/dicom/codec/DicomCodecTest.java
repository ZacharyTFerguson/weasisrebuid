/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.nio.file.Path;
import java.util.Hashtable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.media.data.MediaReader;
import org.weasis.dicom.codec.utils.SyntheticCtWriter;

class DicomCodecTest {

  private final DicomCodec codec = new DicomCodec();

  @Test
  void exposesReaderAndWriterMetadata() {
    assertEquals("DicomCodec", codec.getCodecName());
    assertTrue(codec.getReaderExtensions().length >= 1);
    assertEquals(DicomMime.APPLICATION_DICOM, codec.getWriterMIMETypes()[0]);
    assertEquals("dcm", codec.getWriterExtensions()[0]);
    assertTrue(codec.isMimeTypeSupported(DicomMime.IMAGE_DICOM));
    assertTrue(codec.isMimeTypeSupported(DicomMime.APPLICATION_DICOM));
    assertTrue(codec.isMimeTypeSupported(DicomMime.SERIES_DICOM));
    assertTrue(codec.isMimeTypeSupported(DicomMime.KO_DICOM));
    assertTrue(codec.isMimeTypeSupported(DicomMime.SEG_DICOM));
    assertTrue(codec.isMimeTypeSupported(DicomMime.PR_DICOM));
    assertFalse(codec.isMimeTypeSupported(null));
    assertFalse(codec.isMimeTypeSupported("text/plain"));
  }

  @Test
  void getMediaIoOpensLocalPart10(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    SyntheticCtWriter.write(file.toFile(), 4, 40, 400);
    URI uri = file.toUri();
    MediaReader reader = codec.getMediaIO(uri, DicomMime.IMAGE_DICOM, new Hashtable<>());
    assertNotNull(reader);
    assertTrue(reader instanceof DicomMediaIO);
    assertEquals(DicomMime.IMAGE_DICOM, ((DicomMediaIO) reader).mimeType());
  }

  @Test
  void getMediaIoRejectsNonFileSchemes() {
    assertNull(codec.getMediaIO(URI.create("http://example/ct.dcm"), DicomMime.IMAGE_DICOM, null));
    assertNull(codec.getMediaIO(null, DicomMime.IMAGE_DICOM, null));
  }

  @Test
  void getMediaIoReturnsNullForMissingFile(@TempDir Path dir) {
    URI uri = dir.resolve("missing.dcm").toUri();
    assertNull(codec.getMediaIO(uri, DicomMime.IMAGE_DICOM, new Hashtable<>()));
  }
}
