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

import org.dcm4che3.data.UID;

/** MIME families from ARCHITECTURE / CHECKLIST §2.5. */
public final class DicomMime {

  public static final String APPLICATION_DICOM = "application/dicom";
  public static final String IMAGE_DICOM = "image/dicom";
  public static final String VIDEO_DICOM = "video/dicom";
  public static final String SERIES_DICOM = "series/dicom";
  public static final String PR_DICOM = "pr/dicom";
  public static final String KO_DICOM = "ko/dicom";
  public static final String SEG_DICOM = "seg/dicom";
  public static final String SR_DICOM = "sr/dicom";
  public static final String AU_DICOM = "au/dicom";
  public static final String WAVE_DICOM = "wave/dicom";
  public static final String RT_DICOM = "rt/dicom";
  public static final String ENCAP_DICOM = "encap/dicom";
  public static final String UNREADABLE_DICOM = "unreadable/dicom";

  private DicomMime() {}

  public static String fromSopClass(String sopClassUid) {
    if (sopClassUid == null || sopClassUid.isBlank()) {
      return UNREADABLE_DICOM;
    }
    if (isPresentationState(sopClassUid)) {
      return PR_DICOM;
    }
    if (UID.KeyObjectSelectionDocumentStorage.equals(sopClassUid)) {
      return KO_DICOM;
    }
    if (UID.SegmentationStorage.equals(sopClassUid)
        || UID.SurfaceSegmentationStorage.equals(sopClassUid)) {
      return SEG_DICOM;
    }
    if (isEncapsulatedDocument(sopClassUid)) {
      return ENCAP_DICOM;
    }
    if (isVideoSop(sopClassUid)) {
      return VIDEO_DICOM;
    }
    if (isStructuredReport(sopClassUid)) {
      return SR_DICOM;
    }
    if (isAudio(sopClassUid)) {
      return AU_DICOM;
    }
    if (isWaveform(sopClassUid)) {
      return WAVE_DICOM;
    }
    if (isRt(sopClassUid)) {
      return RT_DICOM;
    }
    return IMAGE_DICOM;
  }

  static boolean isStructuredReport(String uid) {
    return UID.BasicTextSRStorage.equals(uid)
        || UID.EnhancedSRStorage.equals(uid)
        || UID.ComprehensiveSRStorage.equals(uid)
        || UID.Comprehensive3DSRStorage.equals(uid)
        || UID.MammographyCADSRStorage.equals(uid)
        || UID.ChestCADSRStorage.equals(uid)
        || UID.XRayRadiationDoseSRStorage.equals(uid)
        || UID.ExtensibleSRStorage.equals(uid);
  }

  static boolean isAudio(String uid) {
    return UID.BasicVoiceAudioWaveformStorage.equals(uid)
        || UID.GeneralAudioWaveformStorage.equals(uid);
  }

  static boolean isWaveform(String uid) {
    return UID.TwelveLeadECGWaveformStorage.equals(uid)
        || UID.GeneralECGWaveformStorage.equals(uid)
        || UID.AmbulatoryECGWaveformStorage.equals(uid)
        || UID.HemodynamicWaveformStorage.equals(uid)
        || UID.CardiacElectrophysiologyWaveformStorage.equals(uid);
  }

  static boolean isRt(String uid) {
    return UID.RTStructureSetStorage.equals(uid)
        || UID.RTPlanStorage.equals(uid)
        || UID.RTDoseStorage.equals(uid)
        || UID.RTImageStorage.equals(uid)
        || UID.RTIonPlanStorage.equals(uid)
        || UID.RTIonBeamsTreatmentRecordStorage.equals(uid);
  }

  static boolean isPresentationState(String uid) {
    return UID.GrayscaleSoftcopyPresentationStateStorage.equals(uid)
        || UID.ColorSoftcopyPresentationStateStorage.equals(uid)
        || UID.PseudoColorSoftcopyPresentationStateStorage.equals(uid)
        || UID.BlendingSoftcopyPresentationStateStorage.equals(uid)
        || UID.XAXRFGrayscaleSoftcopyPresentationStateStorage.equals(uid)
        || uid.startsWith("1.2.840.10008.10.0.2.2.1.11.");
  }

  static boolean isEncapsulatedDocument(String uid) {
    return UID.EncapsulatedPDFStorage.equals(uid)
        || UID.EncapsulatedCDAStorage.equals(uid)
        || "1.2.840.10008.10.0.2.2.1.104.3".equals(uid)
        || uid.startsWith("1.2.840.10008.10.0.2.2.1.104.");
  }

  static boolean isVideoSop(String uid) {
    return UID.VideoEndoscopicImageStorage.equals(uid)
        || UID.VideoMicroscopicImageStorage.equals(uid)
        || UID.VideoPhotographicImageStorage.equals(uid);
  }
}
