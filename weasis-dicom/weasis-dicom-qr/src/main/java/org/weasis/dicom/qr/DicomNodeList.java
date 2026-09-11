/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import java.util.ArrayList;
import java.util.List;

/** Prefs DICOM node list shared by Q/R and Send. */
public final class DicomNodeList {

  public record Node(String name, String ae, String host, int port, boolean dicomweb) {}

  private final List<Node> nodes = new ArrayList<>();

  public void add(Node node) {
    nodes.add(node);
  }

  public List<Node> nodes() {
    return List.copyOf(nodes);
  }

  public List<Node> sendDestinations() {
    return nodes();
  }
}
