/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.imageio.codec;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;

public final class ImageioUtil {
  private ImageioUtil() {}

  public static void registerServiceProvider(Class<?> clazz) {
    if (clazz == null) {
      return;
    }
    try {
      Object spi = clazz.getDeclaredConstructor().newInstance();
      IIORegistry.getDefaultInstance().registerServiceProvider(spi);
    } catch (Exception ignored) {
      // optional SPI
    }
  }

  public static void deregisterServiceProvider(Class<?> clazz) {
    if (clazz == null) {
      return;
    }
    IIORegistry reg = IIORegistry.getDefaultInstance();
    if (ImageReaderSpi.class.isAssignableFrom(clazz)) {
      deregister(reg, clazz, ImageReaderSpi.class);
    }
    if (ImageWriterSpi.class.isAssignableFrom(clazz)) {
      deregister(reg, clazz, ImageWriterSpi.class);
    }
  }

  private static <T> void deregister(IIORegistry reg, Class<?> clazz, Class<T> category) {
    try {
      @SuppressWarnings("unchecked")
      T spi = (T) clazz.getDeclaredConstructor().newInstance();
      reg.deregisterServiceProvider(spi, category);
    } catch (Exception ignored) {
      // ignore
    }
  }

  public static String[] readerFormats() {
    return ImageIO.getReaderFormatNames();
  }
}
