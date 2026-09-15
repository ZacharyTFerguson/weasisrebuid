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
import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/** Grayscale Softcopy Presentation State from 2D graphics (GSPS). */
public class PRManager {

  public static final String ROOT_UID = KOManager.ROOT_UID;

  public Attributes buildPresentationState(Attributes sourceImage, List<Graphic> graphics) {
    Attributes src = sourceImage == null ? new Attributes() : sourceImage;
    List<Graphic> list = graphics == null ? List.of() : graphics;
    Attributes pr = new Attributes();
    pr.setString(Tag.SOPClassUID, VR.UI, UID.GrayscaleSoftcopyPresentationStateStorage);
    pr.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID(ROOT_UID));
    pr.setString(
        Tag.StudyInstanceUID,
        VR.UI,
        src.getString(Tag.StudyInstanceUID, UIDUtils.createUID(ROOT_UID)));
    pr.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID(ROOT_UID));
    pr.setString(Tag.Modality, VR.CS, "PR");
    pr.setString(Tag.Manufacturer, VR.LO, "Weasis");
    Sequence refSeries = pr.newSequence(Tag.ReferencedSeriesSequence, 1);
    Attributes series = new Attributes();
    series.setString(
        Tag.SeriesInstanceUID,
        VR.UI,
        src.getString(Tag.SeriesInstanceUID, UIDUtils.createUID(ROOT_UID)));
    Sequence sops = series.newSequence(Tag.ReferencedImageSequence, 1);
    Attributes img = new Attributes();
    img.setString(
        Tag.ReferencedSOPClassUID, VR.UI, src.getString(Tag.SOPClassUID, UID.CTImageStorage));
    img.setString(
        Tag.ReferencedSOPInstanceUID,
        VR.UI,
        src.getString(Tag.SOPInstanceUID, UIDUtils.createUID(ROOT_UID)));
    sops.add(img);
    refSeries.add(series);

    Sequence ann = pr.newSequence(Tag.GraphicAnnotationSequence, 1);
    Attributes layer = new Attributes();
    layer.setString(Tag.GraphicLayer, VR.CS, "MEASURE");
    Sequence objs = layer.newSequence(Tag.GraphicObjectSequence, list.size());
    for (Graphic g : list) {
      Attributes go = new Attributes();
      go.setString(Tag.GraphicAnnotationUnits, VR.CS, "PIXEL");
      List<Point2D.Double> pts = g.getPts();
      float[] data = new float[pts.size() * 2];
      for (int i = 0; i < pts.size(); i++) {
        Point2D.Double p = pts.get(i);
        data[i * 2] = (float) p.x;
        data[i * 2 + 1] = (float) p.y;
      }
      String type = "POLYLINE";
      if (g instanceof LineGraphic && !(g instanceof PolylineGraphic) && pts.size() == 2) {
        type = "POLYLINE";
      }
      go.setString(Tag.GraphicType, VR.CS, type);
      go.setFloat(Tag.GraphicData, VR.FL, data);
      go.setInt(Tag.NumberOfGraphicPoints, VR.US, pts.size());
      go.setString(Tag.GraphicFilled, VR.CS, Boolean.TRUE.equals(g.getFilled()) ? "Y" : "N");
      objs.add(go);
    }
    ann.add(layer);
    return pr;
  }

  public File writePr(File dest, Attributes sourceImage, List<Graphic> graphics)
      throws IOException {
    Attributes pr =
        buildPresentationState(sourceImage, graphics == null ? new ArrayList<>() : graphics);
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(
        Tag.MediaStorageSOPClassUID, VR.UI, UID.GrayscaleSoftcopyPresentationStateStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, pr.getString(Tag.SOPInstanceUID));
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    fmi.setString(Tag.ImplementationVersionName, VR.SH, "WEASISREBUILD");
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, pr);
    }
    return dest;
  }
}
