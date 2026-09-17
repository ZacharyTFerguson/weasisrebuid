/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferUShort;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Signed 16-bit cache for a derived MPR slice. */
public class RawImageIO {

  private int width;
  private int height;
  private short[] pixels = new short[0];

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setSamples(double[][] samples) {
    if (samples == null || samples.length == 0 || samples[0].length == 0) {
      width = 0;
      height = 0;
      pixels = new short[0];
      return;
    }
    height = samples.length;
    width = samples[0].length;
    pixels = new short[width * height];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        long v = Math.round(samples[y][x]);
        if (v > Short.MAX_VALUE) {
          v = Short.MAX_VALUE;
        } else if (v < Short.MIN_VALUE) {
          v = Short.MIN_VALUE;
        }
        pixels[y * width + x] = (short) v;
      }
    }
  }

  public double[][] samples() {
    if (width <= 0 || height <= 0) {
      return new double[0][0];
    }
    double[][] out = new double[height][width];
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        out[y][x] = pixels[y * width + x];
      }
    }
    return out;
  }

  public BufferedImage getImage() {
    if (width <= 0 || height <= 0) {
      return null;
    }
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
    short[] data = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
    for (int i = 0; i < pixels.length; i++) {
      int v = pixels[i];
      data[i] = (short) (v < 0 ? 0 : v);
    }
    return image;
  }

  public void write(Path file) throws IOException {
    try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
      out.writeInt(width);
      out.writeInt(height);
      for (short pixel : pixels) {
        out.writeShort(pixel);
      }
    }
  }

  public static RawImageIO read(Path file) throws IOException {
    RawImageIO io = new RawImageIO();
    try (DataInputStream in = new DataInputStream(Files.newInputStream(file))) {
      io.width = in.readInt();
      io.height = in.readInt();
      if (io.width < 0 || io.height < 0) {
        throw new IOException("invalid raw dimensions");
      }
      int n = io.width * io.height;
      io.pixels = new short[n];
      for (int i = 0; i < n; i++) {
        io.pixels[i] = in.readShort();
      }
    }
    return io;
  }
}
