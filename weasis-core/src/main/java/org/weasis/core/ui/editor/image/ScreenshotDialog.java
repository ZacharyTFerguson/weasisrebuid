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
import java.awt.GraphicsEnvironment;
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

/**
 * Capture the focused 2D view (PNG/JPEG, optional overlays). Capture state lives on fields so
 * {@link #write} works without a display; the Swing dialog is created only when headed.
 */
public class ScreenshotDialog {

  public enum Format {
    PNG,
    JPEG
  }

  public enum Scope {
    CURRENT_VIEW,
    NATIVE_PIXELS
  }

  private Format format = Format.PNG;
  private Scope scope = Scope.CURRENT_VIEW;
  private boolean includeOverlays = true;
  private final Frame owner;
  private JDialog window;
  private JComboBox<Format> formatBox;
  private JComboBox<Scope> scopeBox;
  private JCheckBox overlaysBox;

  public ScreenshotDialog() {
    this(null);
  }

  public ScreenshotDialog(Frame owner) {
    this.owner = owner;
  }

  public Format format() {
    return format == null ? Format.PNG : format;
  }

  public void setFormat(Format format) {
    this.format = format == null ? Format.PNG : format;
    if (formatBox != null) {
      formatBox.setSelectedItem(this.format);
    }
  }

  public Scope scope() {
    return scope == null ? Scope.CURRENT_VIEW : scope;
  }

  public void setScope(Scope scope) {
    this.scope = scope == null ? Scope.CURRENT_VIEW : scope;
    if (scopeBox != null) {
      scopeBox.setSelectedItem(this.scope);
    }
  }

  public boolean includeOverlays() {
    return includeOverlays;
  }

  public void setIncludeOverlays(boolean includeOverlays) {
    this.includeOverlays = includeOverlays;
    if (overlaysBox != null) {
      overlaysBox.setSelected(includeOverlays);
    }
  }

  public String imageIoFormat() {
    return format() == Format.JPEG ? "jpeg" : "png";
  }

  public void setVisible(boolean visible) {
    if (!visible) {
      if (window != null) {
        window.setVisible(false);
      }
      return;
    }
    if (GraphicsEnvironment.isHeadless()) {
      return;
    }
    ensureWindow().setVisible(true);
  }

  public boolean isHeadless() {
    return GraphicsEnvironment.isHeadless();
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

  JDialog ensureWindow() {
    if (window == null) {
      window = new JDialog(owner, "Screenshot", false);
      formatBox = new JComboBox<>(Format.values());
      formatBox.setSelectedItem(format());
      formatBox.addActionListener(
          e -> {
            Format selected = (Format) formatBox.getSelectedItem();
            format = selected == null ? Format.PNG : selected;
          });
      scopeBox = new JComboBox<>(Scope.values());
      scopeBox.setSelectedItem(scope());
      scopeBox.addActionListener(
          e -> {
            Scope selected = (Scope) scopeBox.getSelectedItem();
            scope = selected == null ? Scope.CURRENT_VIEW : selected;
          });
      overlaysBox = new JCheckBox("Include overlays", includeOverlays);
      overlaysBox.addActionListener(e -> includeOverlays = overlaysBox.isSelected());
      JPanel form = new JPanel();
      form.add(new JLabel("Format"));
      form.add(formatBox);
      form.add(new JLabel("Scope"));
      form.add(scopeBox);
      form.add(overlaysBox);
      window.getContentPane().add(form);
      window.setSize(420, 120);
    }
    return window;
  }
}
