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
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.TagW;
import org.weasis.dicom.codec.geometry.GeometryOfSlice;
import org.weasis.dicom.codec.utils.DicomMediaUtils;

public class DicomImageElement extends ImageElement implements DicomElement {

  private final DcmMediaReader mediaIO;

  public DicomImageElement(DcmMediaReader mediaIO) {
    this.mediaIO = mediaIO;
    if (mediaIO != null) {
      setMediaURI(mediaIO.getUri());
      Attributes dcm = mediaIO.getDicomObject();
      if (dcm != null) {
        setMimeType(DicomMime.fromSopClass(dcm.getString(Tag.SOPClassUID, "")));
        setTag(TagW.SOPInstanceUID, dcm.getString(Tag.SOPInstanceUID, ""));
        setTag(TagW.SOPClassUID, dcm.getString(Tag.SOPClassUID, ""));
        setTag(TagW.Modality, dcm.getString(Tag.Modality, ""));
        setTag(TagW.SeriesInstanceUID, dcm.getString(Tag.SeriesInstanceUID, ""));
        setTag(TagW.InstanceNumber, dcm.getInt(Tag.InstanceNumber, 0));
        WindLevelParameters wl = DicomMediaUtils.windowLevel(dcm, 400, 40);
        setWindowLevel(wl.getWindow(), wl.getLevel());
        double[] ps = dcm.getDoubles(Tag.PixelSpacing);
        if (ps != null && ps.length >= 2 && ps[0] > 0 && ps[1] > 0) {
          setPixelSize(ps[1], ps[0]);
          setPixelSpacingUnit(Unit.MILLIMETER);
        }
      }
    }
  }

  @Override
  public DcmMediaReader getMediaReader() {
    return mediaIO;
  }

  @Override
  public String getKey() {
    Object v = getTagValue(TagW.SOPInstanceUID);
    return v == null ? "" : v.toString();
  }

  @Override
  public BufferedImage getImage() {
    if (mediaIO instanceof DicomMediaIO io) {
      try {
        return io.paintWindowLevel();
      } catch (RuntimeException e) {
        return super.getImage();
      }
    }
    return super.getImage();
  }

  public GeometryOfSlice getDispSliceGeometry() {
    Attributes dcm = getDicomObject();
    return dcm == null ? null : GeometryOfSlice.fromDataset(dcm);
  }
}
