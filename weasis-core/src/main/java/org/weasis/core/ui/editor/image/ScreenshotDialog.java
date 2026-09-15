/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Capture the focused 2D view (PNG/JPEG, optional overlays). */
public class ScreenshotDialog extends JDialog {

  public enum Format {
    PNG,
    JPEG
  }

  public enum Scope {
    CURRENT_VIEW,
    NATIVE_PIXELS
  }

  private final JComboBox<Format> formatBox = new JComboBox<>(Format.values());
  private final JComboBox<Scope> scopeBox = new JComboBox<>(Scope.values());
  private final JCheckBox overlaysBox = new JCheckBox("Include overlays", true);

  public ScreenshotDialog() {
    this(null);
  }

  public ScreenshotDialog(Frame owner) {
    super(owner, "Screenshot", true);
    JPanel form = new JPanel();
    form.add(new JLabel("Format"));
    form.add(formatBox);
    form.add(new JLabel("Scope"));
    form.add(scopeBox);
    form.add(overlaysBox);
    getContentPane().add(form);
    setSize(420, 120);
  }

  public Format format() {
    Format f = (Format) formatBox.getSelectedItem();
    return f == null ? Format.PNG : f;
  }

  public void setFormat(Format format) {
    formatBox.setSelectedItem(format == null ? Format.PNG : format);
  }

  public Scope scope() {
    Scope s = (Scope) scopeBox.getSelectedItem();
    return s == null ? Scope.CURRENT_VIEW : s;
  }

  public void setScope(Scope scope) {
    scopeBox.setSelectedItem(scope == null ? Scope.CURRENT_VIEW : scope);
  }

  public boolean includeOverlays() {
    return overlaysBox.isSelected();
  }

  public void setIncludeOverlays(boolean includeOverlays) {
    overlaysBox.setSelected(includeOverlays);
  }

  public String imageIoFormat() {
    return format() == Format.JPEG ? "jpeg" : "png";
  }

  public BufferedImage render(DefaultView2d<?> view) {
    if (view == null) {
      throw new IllegalArgumentException("view");
    }
    if (scope() == Scope.NATIVE_PIXELS && view.getSourceImage() != null) {
      BufferedImage src = view.getSourceImage();
      BufferedImage copy =
          new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
      Graphics2D g = copy.createGraphics();
      try {
        g.drawImage(src, 0, 0, null);
        if (includeOverlays()) {
          view.paintDecorations(g);
        }
      } finally {
        g.dispose();
      }
      return copy;
    }
    int w = Math.max(1, view.getWidth());
    int h = Math.max(1, view.getHeight());
    if (w <= 1 || h <= 1) {
      BufferedImage src = view.getSourceImage();
      w = src == null ? 1 : Math.max(1, src.getWidth());
      h = src == null ? 1 : Math.max(1, src.getHeight());
    }
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    try {
      view.paintView(g, includeOverlays());
    } finally {
      g.dispose();
    }
    return img;
  }

  public Path write(DefaultView2d<?> view, Path file) throws IOException {
    BufferedImage img = render(view);
    if (!ImageIO.write(img, imageIoFormat(), file.toFile())) {
      throw new IOException("write " + format().name().toLowerCase(Locale.ROOT));
    }
    return file;
  }
}
