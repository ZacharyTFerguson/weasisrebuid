/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.codec.DicomSpecialElement;

class StructureSetTest {

  @Test
  void parsesClosedPlanarContoursByPlaneAndLargestRoi() {
    Attributes rt = structureSet();
    StructureSet set = StructureSet.from(rt);
    assertEquals("SYNTH", set.label());
    assertEquals(2, set.regions().size());
    StructRegion ptv = set.region(1);
    assertEquals("PTV", ptv.name());
    assertEquals(Color.RED, ptv.color());
    assertEquals(1, ptv.contours().size());
    StructContour square = ptv.contours().get(0);
    assertTrue(square.closedPlanar());
    assertEquals(4, square.pointCount());
    assertEquals(0.0, square.z());
    assertEquals(0.0, square.points().get(0)[0]);
    assertEquals(10.0, square.points().get(2)[0]);
    assertEquals("1.2.CT.1", square.referencedSopInstanceUid());

    PlaneContourLoader planes = new PlaneContourLoader(set);
    assertEquals(1, planes.contoursAt(0).size());
    assertEquals(4, planes.contoursAt(0).get(0).pointCount());
    assertEquals(1, planes.contoursAt(5).size());
    assertEquals(3, planes.contoursAt(5).get(0).pointCount());
    assertEquals(4, LargestContour.of(set.region(1).contours()).pointCount());

    DicomMediaIO io = new DicomMediaIO(rt, UID.ExplicitVRLittleEndian);
    assertInstanceOf(DicomSpecialElement.class, io.getPreview());
    Series<MediaElement> series = new Series<>();
    series.setMimeType(DicomMime.RT_DICOM);
    series.addMedia(new RtSpecialElement(io));
    RtDisplayTool tool = new RtDisplayTool();
    tool.addSeries(series);
    assertEquals(2, tool.tree().regionCount());
    assertEquals("PTV", tool.structureSet().region(1).name());
    assertInstanceOf(RtSpecialElement.class, new RtDisplayToolFactory().buildInstance(io));
  }

  @Test
  void planDoseAndDvh() {
    Attributes plan = new Attributes();
    plan.setString(Tag.Modality, VR.CS, "RTPLAN");
    plan.setString(Tag.RTPlanLabel, VR.LO, "PLAN-A");
    Attributes dose = new Attributes();
    dose.setString(Tag.Modality, VR.CS, "RTDOSE");
    dose.setDouble(Tag.DoseGridScaling, VR.DS, 0.001);
    Sequence dvhSeq = dose.newSequence(Tag.DVHSequence, 1);
    Attributes dvh = new Attributes();
    dvh.setString(Tag.DVHType, VR.CS, "CUMULATIVE");
    dvh.setDouble(Tag.DVHMeanDose, VR.DS, 45.0);
    dvhSeq.add(dvh);
    RtDisplayTool tool = new RtDisplayTool();
    tool.display(plan);
    tool.display(dose);
    assertEquals("PLAN-A", tool.rtSet().plan().label());
    assertEquals(0.001, tool.rtSet().dose().gridScaling());
    assertEquals(45.0, tool.rtSet().dose().dvhs().get(0).meanDose());
    assertEquals(Color.RED, new DoseLut().colorForPercent(95));
    assertEquals(3.0, new PolynomialFunction(new double[] {1, 2}).value(1.0));
  }

  static Attributes structureSet() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.RTStructureSetStorage);
    dcm.setString(Tag.Modality, VR.CS, "RTSTRUCT");
    dcm.setString(Tag.StructureSetLabel, VR.LO, "SYNTH");
    Sequence rois = dcm.newSequence(Tag.StructureSetROISequence, 2);
    rois.add(roi(1, "PTV"));
    rois.add(roi(2, "OAR"));
    Sequence contours = dcm.newSequence(Tag.ROIContourSequence, 2);
    contours.add(roiContour(1, new int[] {255, 0, 0}, closedSquare(0), "1.2.CT.1"));
    contours.add(roiContour(2, new int[] {0, 0, 255}, triangle(5), "1.2.CT.2"));
    return dcm;
  }

  static Attributes roi(int number, String name) {
    Attributes item = new Attributes();
    item.setInt(Tag.ROINumber, VR.IS, number);
    item.setString(Tag.ROIName, VR.LO, name);
    return item;
  }

  static Attributes roiContour(int number, int[] rgb, double[] xyz, String sop) {
    Attributes item = new Attributes();
    item.setInt(Tag.ReferencedROINumber, VR.IS, number);
    item.setInt(Tag.ROIDisplayColor, VR.US, rgb);
    Sequence seq = item.newSequence(Tag.ContourSequence, 1);
    Attributes contour = new Attributes();
    contour.setString(Tag.ContourGeometricType, VR.CS, "CLOSED_PLANAR");
    contour.setInt(Tag.NumberOfContourPoints, VR.IS, xyz.length / 3);
    contour.setDouble(Tag.ContourData, VR.DS, xyz);
    Sequence images = contour.newSequence(Tag.ContourImageSequence, 1);
    Attributes ref = new Attributes();
    ref.setString(Tag.ReferencedSOPInstanceUID, VR.UI, sop);
    images.add(ref);
    seq.add(contour);
    return item;
  }

  static double[] closedSquare(double z) {
    return new double[] {0, 0, z, 10, 0, z, 10, 10, z, 0, 10, z};
  }

  static double[] triangle(double z) {
    return new double[] {0, 0, z, 4, 0, z, 2, 3, z};
  }
}
