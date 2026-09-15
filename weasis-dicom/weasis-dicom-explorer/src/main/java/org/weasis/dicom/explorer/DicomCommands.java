/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.command.Option;
import org.weasis.core.api.command.Options;
import org.weasis.dicom.explorer.rs.RsQueryParams;

/**
 * Gogo {@code dicom:get}, {@code dicom:rs}, {@code dicom:close}. Local {@code -l} uses {@link
 * LoadLocalDicom}; remote flags are parsed for Have tests without a live PACS.
 */
@Component(
    immediate = true,
    service = DicomCommands.class,
    property = {
      "osgi.command.scope=dicom",
      "osgi.command.function=get",
      "osgi.command.function=rs",
      "osgi.command.function=close"
    })
public class DicomCommands {

  public String get(String... args) {
    Option opt = Options.compile("l(local)w(wado)r(remote)z(zip)p(portable)i(iwado)").parse(args);
    List<String> lines = new ArrayList<>();
    for (String local : opt.values("l")) {
      if (local == null || local.isBlank()) {
        continue;
      }
      try {
        LoadLocalDicom.ImportResult result =
            LoadLocalDicom.importPath(new File(local), null, null, null);
        lines.add("local " + local + " imported=" + result.imported().size());
      } catch (IOException e) {
        lines.add("local " + local + " error=" + e.getMessage());
      }
    }
    for (String wado : opt.values("w")) {
      lines.add("manifest " + wado);
    }
    for (String remote : opt.values("r")) {
      lines.add("remote " + remote);
    }
    for (String zip : opt.values("z")) {
      lines.add("zip " + zip);
    }
    if (opt.isSet("p")) {
      lines.add("portable");
    }
    for (String iwado : opt.values("i")) {
      lines.add("iwado " + iwado);
    }
    if (lines.isEmpty()) {
      return "dicom:get -l PATH -w URI -r URI -z URI -p -i DATA";
    }
    return String.join("\n", lines);
  }

  public String rs(String... args) {
    Option opt = Options.compile("u(url)r(request)").parse(args);
    String url = firstNonBlank(opt.get("u"), opt.get("url"));
    String request = opt.get("r");
    RsQueryParams params = new RsQueryParams();
    if (url != null) {
      params.setUrl(url);
    }
    if (request != null) {
      params.setRawQueryParams(request);
    }
    if (params.url() == null) {
      return "dicom:rs --url URL -r QUERYPARAMS";
    }
    return params.buildQidoStudiesUrl();
  }

  public String close(String... args) {
    Option opt = Options.compile("a(all)p(patient)y(study)s(series)").parse(args);
    if (opt.isSet("a") || opt.isSet("all")) {
      return "close-all";
    }
    if (opt.isSet("p")) {
      return "close-patient " + opt.get("p");
    }
    if (opt.isSet("y")) {
      return "close-study " + opt.get("y");
    }
    if (opt.isSet("s")) {
      return "close-series " + opt.get("s");
    }
    return "dicom:close -a | -p ID | -y UID | -s UID";
  }

  private static String firstNonBlank(String a, String b) {
    if (a != null && !a.isBlank()) {
      return a;
    }
    return b;
  }
}
