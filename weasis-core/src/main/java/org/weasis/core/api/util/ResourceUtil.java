/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.util;

import java.net.URL;
import javax.swing.ImageIcon;

public final class ResourceUtil {
  private ResourceUtil() {}

  public static URL getResourceURL(String resource, Class<?> owner) {
    if (resource == null) {
      return null;
    }
    Class<?> c = owner == null ? ResourceUtil.class : owner;
    URL url = c.getResource(resource);
    return url != null ? url : c.getClassLoader().getResource(resource);
  }

  public static ImageIcon getIcon(String resource) {
    URL url = getResourceURL(resource, ResourceUtil.class);
    return url == null ? null : new ImageIcon(url);
  }
}
