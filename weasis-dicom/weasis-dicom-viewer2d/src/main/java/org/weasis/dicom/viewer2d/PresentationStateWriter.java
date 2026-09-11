/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.GraphicKind;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/**
 * GSPS writer. Exported PR contains drawings/measurements <strong>only</strong> — zoom, spatial
 * calibration, window/level, and LUT are not written (SRS §4.4 / CHECKLIST §4.4).
 */
public final class PresentationStateWriter {

  private PresentationStateWriter() {}

  public static Attributes create(Attributes sourceImage, List<Graphic> drawings) {
    Attributes pr = new Attributes();
    String sop = UIDUtils.createUID("2.25");
    pr.setString(Tag.SOPClassUID, VR.UI, UID.GrayscaleSoftcopyPresentationStateStorage);
    pr.setString(Tag.SOPInstanceUID, VR.UI, sop);
    pr.setString(Tag.Modality, VR.CS, "PR");
    pr.setString(Tag.Manufacturer, VR.LO, "Weasis rebuild");
    pr.setString(Tag.ContentLabel, VR.CS, "DRAWINGS");
    pr.setString(Tag.ContentDescription, VR.LO, "graphics and measurements only");
    if (sourceImage != null) {
      copyPatientStudy(sourceImage, pr);
      addReferencedSeries(sourceImage, pr);
    }
    Sequence layers = pr.newSequence(Tag.GraphicLayerSequence, 1);
    Attributes layer = new Attributes();
    layer.setString(Tag.GraphicLayer, VR.CS, "MEASURE");
    layer.setInt(Tag.GraphicLayerOrder, VR.IS, 1);
    layers.add(layer);
    Sequence annSeq = pr.newSequence(Tag.GraphicAnnotationSequence, 1);
    Attributes ann = new Attributes();
    ann.setString(Tag.GraphicLayer, VR.CS, "MEASURE");
    Sequence objs =
        ann.newSequence(Tag.GraphicObjectSequence, drawings == null ? 0 : drawings.size());
    Sequence texts = ann.newSequence(Tag.TextObjectSequence, 1);
    if (drawings != null) {
      for (Graphic g : drawings) {
        if (!g.isMeasurement() && GraphicKind.of(g) != GraphicKind.ANNOTATION) {
          continue;
        }
        Attributes obj = toGraphicObject(g);
        if (obj != null) {
          objs.add(obj);
        }
        if (g.getLabel() != null && g.getLabel().length > 0) {
          Attributes text = new Attributes();
          text.setString(Tag.UnformattedTextValue, VR.ST, String.join("\n", g.getLabel()));
          if (!g.getPts().isEmpty()) {
            Point2D.Double p = g.getPts().get(0);
            text.setFloat(Tag.AnchorPoint, VR.FL, (float) p.x, (float) p.y);
          }
          texts.add(text);
        }
      }
    }
    annSeq.add(ann);
    // Intentionally omit WindowCenter/Width, VOI LUT, Presentation LUT, Displayed Area, Pixel
    // Spacing.
    return pr;
  }

  public static void write(File dest, Attributes sourceImage, List<Graphic> drawings)
      throws IOException {
    Attributes pr = create(sourceImage, drawings);
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, pr.getString(Tag.SOPClassUID));
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, pr.getString(Tag.SOPInstanceUID));
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, pr);
    }
  }

  static void copyPatientStudy(Attributes src, Attributes dest) {
    dest.setString(Tag.PatientName, VR.PN, src.getString(Tag.PatientName, "SYNTHETIC"));
    dest.setString(Tag.PatientID, VR.LO, src.getString(Tag.PatientID, "SYN"));
    dest.setString(Tag.StudyInstanceUID, VR.UI, src.getString(Tag.StudyInstanceUID));
    dest.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID("2.25"));
  }

  static void addReferencedSeries(Attributes src, Attributes pr) {
    Sequence series = pr.newSequence(Tag.ReferencedSeriesSequence, 1);
    Attributes s = new Attributes();
    s.setString(Tag.SeriesInstanceUID, VR.UI, src.getString(Tag.SeriesInstanceUID));
    Sequence imgs = s.newSequence(Tag.ReferencedImageSequence, 1);
    Attributes img = new Attributes();
    img.setString(Tag.ReferencedSOPClassUID, VR.UI, src.getString(Tag.SOPClassUID));
    img.setString(Tag.ReferencedSOPInstanceUID, VR.UI, src.getString(Tag.SOPInstanceUID));
    imgs.add(img);
    series.add(s);
  }

  static Attributes toGraphicObject(Graphic g) {
    List<Point2D.Double> pts = g.getPts();
    if (pts.isEmpty()) {
      return null;
    }
    Attributes obj = new Attributes();
    obj.setString(Tag.GraphicAnnotationUnits, VR.CS, "PIXEL");
    float[] data = new float[pts.size() * 2];
    for (int i = 0; i < pts.size(); i++) {
      data[i * 2] = (float) pts.get(i).x;
      data[i * 2 + 1] = (float) pts.get(i).y;
    }
    obj.setFloat(Tag.GraphicData, VR.FL, data);
    obj.setInt(Tag.NumberOfGraphicPoints, VR.US, pts.size());
    obj.setString(Tag.GraphicFilled, VR.CS, Boolean.TRUE.equals(g.getFilled()) ? "Y" : "N");
    if (g instanceof EllipseGraphic) {
      obj.setString(Tag.GraphicType, VR.CS, "ELLIPSE");
    } else if (g instanceof LineGraphic && pts.size() == 2) {
      obj.setString(Tag.GraphicType, VR.CS, "POLYLINE");
    } else if (g instanceof PolylineGraphic || pts.size() >= 2) {
      obj.setString(Tag.GraphicType, VR.CS, "POLYLINE");
    } else {
      obj.setString(Tag.GraphicType, VR.CS, "POINT");
    }
    return obj;
  }
}
