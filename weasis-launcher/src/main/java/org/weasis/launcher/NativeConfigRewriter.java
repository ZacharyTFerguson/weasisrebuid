/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rewrites Maven-dev {@code file:${maven.localRepository}/.../artifact.jar} URLs to native-zip
 * {@code file:${weasis.resources.path}/bundle/artifact.jar}. {@code maven.local.repo} stays
 * <em>dev-only</em>; the packager never reads {@code ~/.m2}.
 */
public final class NativeConfigRewriter {

  public static final String RESOURCES_PATH = "weasis.resources.path";
  public static final String I18N_DIR = "weasis.i18n.dir";

  private static final Pattern MAVEN_FILE =
      Pattern.compile(
          "file:\\$\\{(?:maven\\.localRepository|maven\\.local\\.repo|settings\\.localRepository)}/\\S+?/([^\\s\"]+\\.jar)");

  private NativeConfigRewriter() {}

  public static String rewriteFileUrls(String json) {
    if (json == null || json.isBlank()) {
      return json;
    }
    Matcher matcher = MAVEN_FILE.matcher(json);
    StringBuilder out = new StringBuilder();
    while (matcher.find()) {
      matcher.appendReplacement(
          out,
          Matcher.quoteReplacement("file:${" + RESOURCES_PATH + "}/bundle/" + matcher.group(1)));
    }
    matcher.appendTail(out);
    return out.toString();
  }

  public static boolean usesMavenRepo(String json) {
    if (json == null) {
      return false;
    }
    return json.contains("${maven.localRepository}")
        || json.contains("${maven.local.repo}")
        || json.contains("${settings.localRepository}");
  }
}
