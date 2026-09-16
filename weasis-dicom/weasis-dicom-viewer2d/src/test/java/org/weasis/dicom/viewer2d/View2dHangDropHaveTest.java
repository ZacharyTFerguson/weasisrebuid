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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.media.data.TagW;

class View2dHangDropHaveTest {

  @Test
  void layoutCellAtMapsBottomLeftOfTwoByTwo() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(4);
    assertSame(container.getLayoutViews().get(2), container.layoutCellAt(50, 150, 200, 200));
    assertSame(container.getLayoutViews().get(3), container.layoutCellAt(150, 150, 200, 200));
    assertSame(container.getLayoutViews().get(0), container.layoutCellAt(50, 50, 200, 200));
    assertSame(
        container.getLayoutViews().get(2),
        View2dContainer.firstView(
            container.layoutCellAt(50, 150, 200, 200), container.getLayoutViews().get(0), null));
  }

  @Test
  void hangCellCopiesPaintedImageWhenExplorerSeriesHasNoUri() {
    View2dContainer container = twoByTwoChest();
    BufferedImage img = gray();
    container.getLayoutViews().get(0).setSourceImage(img);
    Series<MediaElement> explorer = new Series<>("2.25.paint");
    explorer.addMedia(new MediaElement());
    View2d bottomLeft = container.getLayoutViews().get(2);
    bottomLeft.setSourceImage(new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY));
    container.hangCell(bottomLeft, explorer);
    assertSame(img, bottomLeft.getSourceImage());
    Object uid = bottomLeft.getSeries().getTagValue(TagW.SeriesInstanceUID);
    assertEquals("2.25.paint", String.valueOf(uid));
  }

  @Test
  void hangCellCopiesPaintWhenExplorerUriMatchesHungFile() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(4);
    BufferedImage img = gray();
    Series<MediaElement> hung = new Series<>("2.25.paint");
    MediaElement hungMedia = new MediaElement();
    hungMedia.setMediaURI(java.net.URI.create("file:///tmp/chest-hang.dcm"));
    hung.addMedia(hungMedia);
    View2d primary = container.getLayoutViews().get(0);
    primary.setSeries(hung);
    primary.setSourceImage(img);
    Series<MediaElement> explorer = new Series<>("2.25.other-uid");
    MediaElement drop = new MediaElement();
    drop.setMediaURI(java.net.URI.create("file:///tmp/chest-hang.dcm"));
    explorer.addMedia(drop);
    View2d bottomLeft = container.getLayoutViews().get(2);
    container.hangCell(bottomLeft, explorer);
    assertSame(img, bottomLeft.getSourceImage());
  }

  @Test
  void dropOntoUnpaintedBottomLeftCopiesPixelsNotOnlySeries() {
    View2dContainer container = twoByTwoChest();
    BufferedImage img = gray();
    container.getLayoutViews().get(0).setSourceImage(img);
    View2d bottomLeft = container.getLayoutViews().get(2);
    Series<MediaElement> explorer = new Series<>("2.25.paint");
    explorer.addMedia(new MediaElement());
    container.dropSeries(explorer, bottomLeft);
    assertSame(img, bottomLeft.getSourceImage());
    Object uid = bottomLeft.getSeries().getTagValue(TagW.SeriesInstanceUID);
    assertEquals("2.25.paint", String.valueOf(uid));
  }

  static View2dContainer twoByTwoChest() {
    View2dContainer container = new View2dContainer();
    container.setLayoutCount(4);
    Series<MediaElement> hung = new Series<>("2.25.paint");
    container.getLayoutViews().get(0).setSeries(hung);
    return container;
  }

  static BufferedImage gray() {
    return new BufferedImage(4, 4, BufferedImage.TYPE_BYTE_GRAY);
  }
}
