/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.wado;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.ArcQuery;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Manifest;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Patient;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Study;

/**
 * Plans WADO/WADO-RS/DirectDownload jobs and runs them under MX-10 / MX-11 caps. Unit tests never
 * open a socket.
 */
public class DownloadManager {

  private final SeriesDownloadManager slots;
  private final LoadRemoteDicomURL urls;

  public DownloadManager() {
    this(new SeriesDownloadManager(), new LoadRemoteDicomURL());
  }

  public DownloadManager(SeriesDownloadManager slots, LoadRemoteDicomURL urls) {
    this.slots = slots == null ? new SeriesDownloadManager() : slots;
    this.urls = urls == null ? new LoadRemoteDicomURL() : urls;
  }

  public SeriesDownloadManager slots() {
    return slots;
  }

  public List<LoadSeries> plan(Manifest manifest) {
    List<LoadSeries> jobs = new ArrayList<>();
    if (manifest == null) {
      return jobs;
    }
    for (ArcQuery arc : manifest.arcQueries()) {
      for (Patient patient : arc.patients()) {
        for (Study study : patient.studies()) {
          for (Series series : study.series()) {
            jobs.add(LoadSeries.from(arc, study, series, urls));
          }
        }
      }
    }
    jobs.sort(Comparator.comparing(LoadSeries::priority));
    return jobs;
  }

  public List<String> collectUrls(List<LoadSeries> jobs) {
    List<String> sink = new ArrayList<>();
    if (jobs == null) {
      return sink;
    }
    for (LoadSeries job : jobs) {
      if (job.bulk()) {
        sink.add(job.bulkUrl());
      } else {
        sink.addAll(job.instanceUrls());
      }
    }
    return sink;
  }

  public List<String> downloadAll(List<LoadSeries> jobs) {
    List<String> sink = new ArrayList<>();
    if (jobs == null || jobs.isEmpty()) {
      return sink;
    }
    ExecutorService pool = Executors.newFixedThreadPool(Math.max(2, jobs.size()));
    List<Future<?>> futures = new ArrayList<>();
    try {
      for (LoadSeries job : jobs) {
        futures.add(pool.submit(() -> download(job, sink)));
      }
      for (Future<?> future : futures) {
        try {
          future.get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new IllegalStateException(e);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        }
      }
    } finally {
      pool.shutdownNow();
    }
    return List.copyOf(sink);
  }

  void download(LoadSeries job, List<String> sink) {
    slots.runSeries(
        () -> {
          if (job.bulk()) {
            synchronized (sink) {
              sink.add(job.bulkUrl());
            }
            return;
          }
          List<String> instances = job.instanceUrls();
          if (instances.isEmpty()) {
            return;
          }
          AtomicInteger idx = new AtomicInteger();
          slots.runImagesInSeries(
              instances.size(),
              () -> {
                int i = idx.getAndIncrement();
                String url = instances.get(i);
                synchronized (sink) {
                  sink.add(url);
                }
              });
        });
  }
}
