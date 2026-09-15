/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.sr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import javax.swing.JEditorPane;

/** Prints an HTML {@link JEditorPane} SR view (headless-safe render, no live printer). */
public class EditorPanePrinter implements Printable {

  public static final int PAGE_WIDTH = 612;
  public static final int PAGE_HEIGHT = 792;

  private final JEditorPane editor;

  public EditorPanePrinter(JEditorPane editor) {
    this.editor = editor;
  }

  public EditorPanePrinter(String html) {
    this.editor = new JEditorPane("text/html", html == null ? "" : html);
    this.editor.setEditable(false);
    this.editor.setSize(PAGE_WIDTH, PAGE_HEIGHT);
  }

  public JEditorPane editor() {
    return editor;
  }

  public String html() {
    return editor == null || editor.getText() == null ? "" : editor.getText();
  }

  public int pageCount() {
    return html().isBlank() ? 0 : 1;
  }

  @Override
  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0 || editor == null || html().isBlank()) {
      return NO_SUCH_PAGE;
    }
    Graphics2D g2 = (Graphics2D) graphics;
    double x = pageFormat == null ? 0 : pageFormat.getImageableX();
    double y = pageFormat == null ? 0 : pageFormat.getImageableY();
    int w =
        pageFormat == null
            ? Math.max(1, editor.getWidth())
            : Math.max(1, (int) pageFormat.getImageableWidth());
    int h =
        pageFormat == null
            ? Math.max(1, editor.getHeight())
            : Math.max(1, (int) pageFormat.getImageableHeight());
    g2.translate(x, y);
    editor.setSize(w, h);
    editor.print(g2);
    return PAGE_EXISTS;
  }

  public BufferedImage render() {
    int w = editor == null || editor.getWidth() <= 0 ? PAGE_WIDTH : editor.getWidth();
    int h = editor == null || editor.getHeight() <= 0 ? PAGE_HEIGHT : editor.getHeight();
    if (editor != null) {
      editor.setSize(w, h);
    }
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, w, h);
    if (editor != null) {
      editor.print(g);
    }
    g.dispose();
    return image;
  }
}
