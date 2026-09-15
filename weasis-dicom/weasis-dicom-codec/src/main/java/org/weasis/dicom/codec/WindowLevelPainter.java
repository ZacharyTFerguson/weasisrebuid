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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.SimpleOpManager;
import org.weasis.core.api.image.WindowAndPresetsOp;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.dicom.codec.utils.DicomMediaUtils;
import org.weasis.dicom.codec.utils.LutPipeline;

/**
 * Applies the View2d W/L node to Explicit VR LE MONOCHROME2 pixels and paints 8-bit grey.
 * Uncompressed path is pure Java (OpenCV not required).
 */
public final class WindowLevelPainter {

  private WindowLevelPainter() {}

  public static BufferedImage paintMonochrome2(Attributes dcm) {
    return paintMonochrome2(dcm, DicomMediaUtils.windowLevel(dcm, 400, 40));
  }

  public static BufferedImage paintMonochrome2(Attributes dcm, double window, double level) {
    return paintMonochrome2(dcm, new WindLevelParameters(window, level));
  }

  public static BufferedImage paintMonochrome2(Attributes dcm, WindLevelParameters voi) {
    if (dcm == null || !DicomMediaUtils.isMonochrome2(dcm)) {
      throw new IllegalArgumentException("MONOCHROME2 dataset required");
    }
    int rows = dcm.getInt(Tag.Rows, 0);
    int cols = dcm.getInt(Tag.Columns, 0);
    int[] pixels = dcm.getInts(Tag.PixelData);
    if (rows <= 0 || cols <= 0 || pixels == null || pixels.length < rows * cols) {
      throw new IllegalArgumentException("pixel data");
    }
    WindLevelParameters wl = voi == null ? DicomMediaUtils.windowLevel(dcm, 400, 40) : voi;
    SimpleOpManager chain = SimpleOpManager.view2dChain();
    chain.setParamValue("op.window.presets", WindowAndPresetsOp.P_WINDOW, wl.getWindow());
    chain.setParamValue("op.window.presets", WindowAndPresetsOp.P_LEVEL, wl.getLevel());
    chain.setParamValue("op.window.presets", WindowAndPresetsOp.P_VOI_LUT_SHAPE, wl.getLutShape());
    int pad = DicomMediaUtils.pixelPaddingValue(dcm);
    BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_BYTE_GRAY);
    byte[] out = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    for (int i = 0; i < rows * cols; i++) {
      out[i] = (byte) sampleDisplay(dcm, pixels[i], pad, wl);
    }
    return image;
  }

  static int sampleDisplay(Attributes dcm, int raw, int pad, WindLevelParameters wl) {
    int stored = DicomMediaUtils.storedPixel(dcm, raw);
    if (stored == pad) {
      return 0;
    }
    double modality = LutPipeline.modalityValue(dcm, stored);
    return LutPipeline.applyPresentationIdentity(LutPipeline.applyVoi(modality, wl));
  }
}
