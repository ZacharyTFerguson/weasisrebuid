/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.dcm4che3.data.UID;
import org.weasis.dicom.codec.DicomMime;

/**
 * Import end Information (unsupported SOP Class). Don't-show-again is an explorer pref, not Pass.
 */
public final class SkipUnsupportedSopNotifier {

  public static final String PREF_DONT_SHOW = "weasis.import.skip.unsupported.hide";

  private static final Set<String> IMAGE_SOP =
      Set.of(
          UID.CTImageStorage,
          UID.MRImageStorage,
          UID.UltrasoundImageStorage,
          UID.UltrasoundMultiFrameImageStorage,
          UID.ComputedRadiographyImageStorage,
          UID.DigitalXRayImageStorageForPresentation,
          UID.DigitalXRayImageStorageForProcessing,
          UID.SecondaryCaptureImageStorage,
          UID.MultiFrameTrueColorSecondaryCaptureImageStorage,
          UID.PositronEmissionTomographyImageStorage,
          UID.EnhancedCTImageStorage,
          UID.EnhancedMRImageStorage,
          UID.XRayAngiographicImageStorage,
          UID.XRayRadiofluoroscopicImageStorage);

  private final List<String> skipped = new ArrayList<>();
  private boolean dontShowAgain;

  public SkipUnsupportedSopNotifier() {
    this(false);
  }

  public SkipUnsupportedSopNotifier(boolean dontShowAgain) {
    this.dontShowAgain = dontShowAgain;
  }

  public static boolean isSupportedImageSop(String sopClassUid) {
    return sopClassUid != null && IMAGE_SOP.contains(sopClassUid);
  }

  public boolean offer(ImportedInstance inst) {
    if (inst == null) {
      return false;
    }
    String mime = DicomMime.fromSopClass(inst.sopClassUid());
    if (DicomMime.UNREADABLE_DICOM.equals(mime)
        || DicomMime.ENCAP_DICOM.equals(mime)
        || DicomMime.VIDEO_DICOM.equals(mime)) {
      skipped.add(inst.sopClassUid() + " " + inst.sopUid());
      return false;
    }
    if (!isSupportedImageSop(inst.sopClassUid())
        && (DicomMime.PR_DICOM.equals(mime)
            || DicomMime.KO_DICOM.equals(mime)
            || DicomMime.SEG_DICOM.equals(mime))) {
      skipped.add(inst.sopClassUid() + " " + inst.sopUid());
      return false;
    }
    return true;
  }

  public List<String> skipped() {
    return Collections.unmodifiableList(skipped);
  }

  public String informationMessage() {
    if (dontShowAgain || skipped.isEmpty()) {
      return "";
    }
    return "Unsupported SOP Class (" + skipped.size() + ")";
  }

  public void setDontShowAgain(boolean dontShowAgain) {
    this.dontShowAgain = dontShowAgain;
  }

  public boolean isDontShowAgain() {
    return dontShowAgain;
  }
}
