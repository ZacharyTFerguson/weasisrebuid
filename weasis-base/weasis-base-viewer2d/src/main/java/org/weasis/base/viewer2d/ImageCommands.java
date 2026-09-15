/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.command.Option;
import org.weasis.core.api.command.Options;
import org.weasis.core.api.media.MimeInspector;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaReader;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.TagW;
import org.weasis.imageio.codec.ImageioCodec;

/**
 * Gogo {@code image:get} and {@code image:close}. Local {@code -f} uses ImageIO via {@link
 * ImageioCodec}; {@code -u} http(s) records the URI without a live fetch.
 */
@Component(
    immediate = true,
    service = ImageCommands.class,
    property = {
      "osgi.command.scope=image",
      "osgi.command.function=get",
      "osgi.command.function=close"
    })
public class ImageCommands {

  static final String GET_USAGE = "image:get ([-f FILE]... [-u URL]...)";
  static final String CLOSE_USAGE = "image:close (-a | ([-g UID]... [-s UID]...))";

  private final Codec codec;
  private final Supplier<View2dContainer> viewer;
  private final List<Opened> opened = new ArrayList<>();

  public ImageCommands() {
    this(new ImageioCodec(), View2dContainer::selected);
  }

  public ImageCommands(Codec codec, Supplier<View2dContainer> viewer) {
    this.codec = codec == null ? new ImageioCodec() : codec;
    this.viewer = viewer == null ? () -> null : viewer;
  }

  public String get(String... args) {
    Option opt = Options.compile("f(file)u(url)").parse(args);
    List<String> lines = new ArrayList<>();
    for (String file : opt.values("f")) {
      lines.add(openFile(file));
    }
    for (String url : opt.values("u")) {
      lines.add(openUrl(url));
    }
    if (lines.isEmpty()) {
      return GET_USAGE;
    }
    return String.join("\n", lines);
  }

  public String close(String... args) {
    Option opt = Options.compile("a(all)g(group)s(series)").parse(args);
    if (opt.isSet("a") || opt.isSet("all")) {
      closeAll();
      return "close-all";
    }
    List<String> lines = new ArrayList<>();
    for (String group : opt.values("g")) {
      int n = closeGroup(group);
      lines.add("close-group " + group + " count=" + n);
    }
    for (String series : opt.values("s")) {
      boolean removed = closeSeries(series);
      lines.add("close-series " + series + (removed ? "" : " missing"));
    }
    if (lines.isEmpty()) {
      return CLOSE_USAGE;
    }
    return String.join("\n", lines);
  }

  List<Opened> opened() {
    return List.copyOf(opened);
  }

  private String openFile(String spec) {
    if (spec == null || spec.isBlank()) {
      return "file error=empty";
    }
    File file = new File(spec);
    if (!file.isFile()) {
      return "file " + spec + " error=missing";
    }
    return openUri(file.toURI(), MimeInspector.getMimeType(file), "file " + spec, true);
  }

  private String openUrl(String spec) {
    if (spec == null || spec.isBlank()) {
      return "url error=empty";
    }
    URI uri;
    try {
      uri = URI.create(spec.trim());
    } catch (IllegalArgumentException e) {
      return "url " + spec + " error=invalid";
    }
    if (ImageioCodec.localFile(uri) != null) {
      File file = ImageioCodec.localFile(uri);
      if (file != null && file.isFile()) {
        return openUri(file.toURI(), MimeInspector.getMimeType(file), "file " + spec, true);
      }
    }
    String mime = ImageioCodec.guessMime(uri);
    return openUri(uri, mime, "url " + spec, false);
  }

  private String openUri(URI uri, String mime, String prefix, boolean requirePixels) {
    MediaReader reader = codec.getMediaIO(uri, mime, null);
    if (reader == null) {
      return prefix + " error=unsupported";
    }
    MediaElement preview = reader.getPreview();
    @SuppressWarnings("unchecked")
    MediaSeries<MediaElement> series = (MediaSeries<MediaElement>) reader.getMediaSeries();
    ImageElement image = preview instanceof ImageElement el ? el : null;
    if (requirePixels && (image == null || image.getImage() == null)) {
      return prefix + " error=unreadable";
    }
    String seriesUid = seriesUid(series, uri);
    String groupUid = groupUid(uri);
    opened.add(new Opened(groupUid, seriesUid, uri, image, series));
    View2dContainer container = viewer.get();
    if (container != null) {
      container.addSeries(series);
    }
    String pixels = "";
    if (image != null && image.getImage() != null) {
      pixels = " pixels=" + image.getImage().getWidth() + "x" + image.getImage().getHeight();
    }
    return prefix + " series=" + seriesUid + " group=" + groupUid + pixels;
  }

  private void closeAll() {
    View2dContainer container = viewer.get();
    for (Opened item : List.copyOf(opened)) {
      if (container != null) {
        container.removeSeries(item.series());
      }
    }
    opened.clear();
    if (container != null) {
      container.getView2d().setSourceImage(null);
    }
  }

  private int closeGroup(String groupUid) {
    int count = 0;
    View2dContainer container = viewer.get();
    Iterator<Opened> it = opened.iterator();
    while (it.hasNext()) {
      Opened item = it.next();
      if (groupUid != null && groupUid.equals(item.groupUid())) {
        if (container != null) {
          container.removeSeries(item.series());
        }
        it.remove();
        count++;
      }
    }
    return count;
  }

  private boolean closeSeries(String seriesUid) {
    View2dContainer container = viewer.get();
    Iterator<Opened> it = opened.iterator();
    while (it.hasNext()) {
      Opened item = it.next();
      if (seriesUid != null && seriesUid.equals(item.seriesUid())) {
        if (container != null) {
          container.removeSeries(item.series());
        }
        it.remove();
        return true;
      }
    }
    return false;
  }

  static String seriesUid(MediaSeries<?> series, URI uri) {
    if (series != null) {
      Object v = series.getTagValue(TagW.SeriesInstanceUID);
      if (v != null && !v.toString().isBlank()) {
        return v.toString();
      }
    }
    return uri == null ? "" : uri.toString();
  }

  static String groupUid(URI uri) {
    File local = ImageioCodec.localFile(uri);
    if (local != null) {
      File parent = local.getAbsoluteFile().getParentFile();
      return parent == null ? local.getAbsolutePath() : parent.getAbsolutePath();
    }
    if (uri == null) {
      return "";
    }
    String host = uri.getHost();
    return host == null || host.isBlank() ? String.valueOf(uri.getScheme()) : host;
  }

  record Opened(
      String groupUid,
      String seriesUid,
      URI uri,
      ImageElement element,
      MediaSeries<MediaElement> series) {}
}
