/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.dicom.codec.PhotometricInterpretation;
import org.weasis.dicom.geom.ImageOrientation;
import org.weasis.dicom.geom.Vector3;

/** Helpers over a Part-10 dataset (tagged-equivalent of {@code DicomMediaUtils}). */
public final class DicomMediaUtils {

  private DicomMediaUtils() {}

  public static boolean isSignedPixel(Attributes dcm) {
    return dcm != null && dcm.getInt(Tag.PixelRepresentation, 0) == 1;
  }

  /**
   * Interprets a dcm4che {@code getInts(PixelData)} sample using {@code PixelRepresentation},
   * {@code BitsAllocated}, and {@code BitsStored}. Unsigned 8-bit values must not sign-wrap.
   */
  public static int storedPixel(Attributes dcm, int raw) {
    int allocated = dcm == null ? 16 : dcm.getInt(Tag.BitsAllocated, 16);
    int stored = dcm == null ? allocated : dcm.getInt(Tag.BitsStored, allocated);
    if (stored <= 0) {
      stored = allocated <= 0 ? 16 : allocated;
    }
    if (stored > 32) {
      stored = 32;
    }
    int mask = stored >= 32 ? 0xffffffff : (1 << stored) - 1;
    if (!isSignedPixel(dcm)) {
      int unsigned = allocated <= 8 ? raw & 0xff : raw & 0xffff;
      return unsigned & mask;
    }
    int u = raw & mask;
    int sign = 1 << (stored - 1);
    if ((u & sign) != 0) {
      return u | ~mask;
    }
    return u;
  }

  public static String photometricInterpretation(Attributes dcm) {
    return dcm == null ? "" : dcm.getString(Tag.PhotometricInterpretation, "");
  }

  public static WindLevelParameters windowLevel(
      Attributes dcm, double defaultWindow, double defaultLevel) {
    if (dcm == null) {
      return new WindLevelParameters(defaultWindow, defaultLevel);
    }
    WindLevelParameters header = headerWindowLevel(dcm, defaultWindow, defaultLevel);
    if (header != null) {
      return header;
    }
    WindLevelParameters lut = firstVoiLut(dcm);
    if (lut != null) {
      return lut;
    }
    WindLevelParameters fromData = dataRangeWindowLevel(dcm);
    return fromData != null ? fromData : new WindLevelParameters(defaultWindow, defaultLevel);
  }

  static WindLevelParameters headerWindowLevel(
      Attributes dcm, double defaultWindow, double defaultLevel) {
    if (!dcm.containsValue(Tag.WindowWidth) || !dcm.containsValue(Tag.WindowCenter)) {
      return null;
    }
    double window = dcm.getDouble(Tag.WindowWidth, defaultWindow);
    double level = dcm.getDouble(Tag.WindowCenter, defaultLevel);
    WindLevelParameters p = new WindLevelParameters(window > 0 ? window : defaultWindow, level);
    p.setLutShape(LutPipeline.voiFunction(dcm));
    return p;
  }

  static WindLevelParameters firstVoiLut(Attributes dcm) {
    List<WindLevelParameters> items = voiLutItems(dcm);
    return items.isEmpty() ? null : items.getFirst();
  }

  /**
   * Linear Window Center/Width presets (keys 1–9) then VOI LUT Sequence items. Empty when the
   * dataset has no VOI; callers then keep {@link #windowLevel} (data-range or first WindowCenter).
   */
  public static List<WindLevelParameters> voiPresets(Attributes dcm) {
    if (dcm == null) {
      return List.of();
    }
    List<WindLevelParameters> out = new ArrayList<>();
    out.addAll(
        pairPresets(
            dcm.getDoubles(Tag.WindowWidth),
            dcm.getDoubles(Tag.WindowCenter),
            LutPipeline.voiFunction(dcm)));
    out.addAll(voiLutItems(dcm));
    return List.copyOf(out);
  }

  static List<WindLevelParameters> pairPresets(double[] widths, double[] centers, String shape) {
    if (widths == null || centers == null) {
      return List.of();
    }
    return copyValidPairs(widths, centers, shape);
  }

  static List<WindLevelParameters> copyValidPairs(double[] widths, double[] centers, String shape) {
    int n = Math.min(widths.length, centers.length);
    List<WindLevelParameters> out = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      addIfValid(out, widths[i], centers[i], shape);
    }
    return List.copyOf(out);
  }

  static void addIfValid(List<WindLevelParameters> out, double window, double level, String shape) {
    if (window > 0) {
      WindLevelParameters p = new WindLevelParameters(window, level);
      p.setLutShape(shape);
      out.add(p);
    }
  }

  static List<WindLevelParameters> voiLutItems(Attributes dcm) {
    Sequence seq = dcm.getSequence(Tag.VOILUTSequence);
    if (seq == null || seq.isEmpty()) {
      return List.of();
    }
    return copyLutItems(seq);
  }

  static List<WindLevelParameters> copyLutItems(Sequence seq) {
    List<WindLevelParameters> out = new ArrayList<>();
    for (Attributes item : seq) {
      WindLevelParameters p = lutItem(item);
      if (p != null) {
        out.add(p);
      }
    }
    return out;
  }

  static WindLevelParameters lutItem(Attributes item) {
    if (item == null) {
      return null;
    }
    return lutPreset(item.getInts(Tag.LUTDescriptor), item.getInts(Tag.LUTData));
  }

  static WindLevelParameters lutPreset(int[] desc, int[] data) {
    if (missingLut(desc, data)) {
      return null;
    }
    int n = desc[0] == 0 ? 65536 : desc[0];
    int first = desc[1];
    WindLevelParameters p = new WindLevelParameters(Math.max(1, n), first + n / 2.0);
    p.setVoiLut(toDisplayLut(data, n, desc[2]), first);
    p.setLutShape(LutPipeline.SHAPE_NON_LINEAR);
    return p;
  }

  static boolean missingLut(int[] desc, int[] data) {
    return desc == null || desc.length < 3 || data == null || data.length == 0;
  }

  static int[] toDisplayLut(int[] data, int n, int bits) {
    int[] out = new int[Math.max(1, n)];
    int max = bits > 8 ? (1 << Math.min(16, bits)) - 1 : 255;
    for (int i = 0; i < out.length; i++) {
      int v = i < data.length ? data[i] : data[data.length - 1];
      out[i] = scaleLutEntry(v, max);
    }
    return out;
  }

  static int scaleLutEntry(int v, int max) {
    if (max <= 255) {
      return LutPipeline.clamp8(v);
    }
    return LutPipeline.clamp8((int) Math.round(v * 255.0 / max));
  }

  public static WindLevelParameters dataRangeWindowLevel(Attributes dcm) {
    int[] pixels = dcm.getInts(Tag.PixelData);
    if (pixels == null || pixels.length == 0) {
      return null;
    }
    int pad = pixelPaddingValue(dcm);
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    for (int raw : pixels) {
      int stored = storedPixel(dcm, raw);
      if (stored == pad) {
        continue;
      }
      double modality = LutPipeline.modalityValue(dcm, stored);
      if (modality < min) {
        min = modality;
      }
      if (modality > max) {
        max = modality;
      }
    }
    if (!Double.isFinite(min) || !Double.isFinite(max)) {
      return null;
    }
    double window = Math.max(1.0, max - min);
    double level = min + window / 2.0;
    return new WindLevelParameters(window, level);
  }

  public static int pixelPaddingValue(Attributes dcm) {
    if (dcm == null || !dcm.contains(Tag.PixelPaddingValue)) {
      return Integer.MIN_VALUE;
    }
    return dcm.getInt(Tag.PixelPaddingValue, Integer.MIN_VALUE);
  }

  public static boolean isMonochrome2(Attributes dcm) {
    return PhotometricInterpretation.MONOCHROME2
        == PhotometricInterpretation.parse(photometricInterpretation(dcm)).orElse(null);
  }

  /**
   * Uses weasis-dicom-tools {@link ImageOrientation} when row/col direction cosines are present.
   */
  public static String planLabel(Attributes dcm) {
    if (dcm == null || !dcm.contains(Tag.ImageOrientationPatient)) {
      return "";
    }
    double[] iop = dcm.getDoubles(Tag.ImageOrientationPatient);
    if (iop == null || iop.length < 6) {
      return "";
    }
    Vector3 row = new Vector3(iop[0], iop[1], iop[2]);
    Vector3 col = new Vector3(iop[3], iop[4], iop[5]);
    return ImageOrientation.getPlan(row, col).name();
  }
}
