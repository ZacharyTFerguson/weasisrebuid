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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

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
  private DefaultView2d<?> view;
  private JDialog window;
  private JComboBox<Format> formatBox;
  private JComboBox<Scope> scopeBox;
  private JCheckBox overlaysBox;
  private final JTextField pathField = new JTextField();
  private final JLabel status = new JLabel(" ");

  public ScreenshotDialog() {
    this(null);
  }

  public ScreenshotDialog(Frame owner) {
    this.owner = owner;
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
  }

  public DefaultView2d<?> boundView() {
    return view;
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

  public void setPath(String path) {
    pathField.setText(path == null ? "" : path);
  }

  public String path() {
    String text = pathField.getText();
    return text == null ? "" : text.trim();
  }

  public String statusText() {
    return status.getText();
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
      return copyNative(view);
    }
    return paintCurrent(view);
  }

  BufferedImage copyNative(DefaultView2d<?> view) {
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

  BufferedImage paintCurrent(DefaultView2d<?> view) {
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

  public void save() {
    if (view == null) {
      status.setText("No view");
      return;
    }
    if (path().isBlank()) {
      status.setText("Choose a destination file");
      return;
    }
    try {
      Path file = write(view, Path.of(path()));
      status.setText("Saved " + file);
    } catch (Exception e) {
      status.setText("Error " + e.getMessage());
    }
  }

  public JDialog ensureWindow() {
    if (window == null) {
      window = buildWindow();
    }
    return window;
  }

  JDialog buildWindow() {
    JDialog dialog = new JDialog(owner, "Screenshot", false);
    dialog.setName("screenshot-dialog");
    dialog.add(formPanel(), BorderLayout.CENTER);
    dialog.add(southPanel(), BorderLayout.SOUTH);
    dialog.setSize(520, 180);
    return dialog;
  }

  JPanel formPanel() {
    formatBox = new JComboBox<>(Format.values());
    formatBox.setName("screenshot-format");
    formatBox.setSelectedItem(format());
    formatBox.addActionListener(e -> setFormat((Format) formatBox.getSelectedItem()));
    scopeBox = new JComboBox<>(Scope.values());
    scopeBox.setName("screenshot-scope");
    scopeBox.setSelectedItem(scope());
    scopeBox.addActionListener(e -> setScope((Scope) scopeBox.getSelectedItem()));
    overlaysBox = new JCheckBox("Include overlays", includeOverlays);
    overlaysBox.setName("screenshot-overlays");
    overlaysBox.addActionListener(e -> includeOverlays = overlaysBox.isSelected());
    pathField.setName("screenshot-path");
    JButton browse = new JButton("Browse…");
    browse.setName("screenshot-browse");
    browse.addActionListener(e -> browse());
    JPanel form = new JPanel();
    form.add(new JLabel("Format"));
    form.add(formatBox);
    form.add(new JLabel("Scope"));
    form.add(scopeBox);
    form.add(overlaysBox);
    form.add(pathField);
    form.add(browse);
    return form;
  }

  JPanel southPanel() {
    JButton save = new JButton("Save");
    save.setName("screenshot-run");
    save.addActionListener(e -> save());
    status.setName("screenshot-status");
    JPanel south = new JPanel(new BorderLayout());
    south.add(save, BorderLayout.WEST);
    south.add(status, BorderLayout.CENTER);
    return south;
  }

  void browse() {
    JFileChooser chooser = newFileChooser();
    if (chooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
      File selected = chooser.getSelectedFile();
      if (selected != null) {
        setPath(selected.getAbsolutePath());
      }
    }
  }

  JFileChooser newFileChooser() {
    UIManager.put("FileChooser.useShellFolder", Boolean.FALSE);
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setDialogTitle("Screenshot");
    return chooser;
  }
}
