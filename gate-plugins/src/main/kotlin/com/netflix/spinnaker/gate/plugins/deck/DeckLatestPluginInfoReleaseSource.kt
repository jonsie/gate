/*
 * Copyright 2020 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate.plugins.deck

import com.netflix.spinnaker.kork.plugins.update.SpinnakerUpdateManager
import com.netflix.spinnaker.kork.plugins.update.release.PluginInfoRelease
import com.netflix.spinnaker.kork.plugins.update.release.source.PluginInfoReleaseSource
import org.pf4j.update.PluginInfo
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered

class DeckLatestPluginInfoReleaseSource(
  private val updateManager: SpinnakerUpdateManager
) : PluginInfoReleaseSource {

  private val log by lazy { LoggerFactory.getLogger(javaClass) }

  override fun getReleases(pluginInfo: List<PluginInfo>): Set<PluginInfoRelease> {
    return pluginInfo.mapNotNull { pluginInfoRelease(it) }.toSet()
  }

  private fun pluginInfoRelease(pluginInfo: PluginInfo): PluginInfoRelease? {
    val latestRelease = updateManager.getLastPluginRelease(pluginInfo.id,
      DECK_REQUIREMENT)
    return if (latestRelease != null) {
      log.info("Latest release version {} for plugin {}", latestRelease.version, pluginInfo.id)
      PluginInfoRelease(pluginInfo.id, latestRelease)
    } else {
      log.info("Latest release version not found for plugin {}", pluginInfo.id)
      null
    }
  }

  /**
   * Ensures this runs first in
   * [com.netflix.spinnaker.kork.plugins.update.release.provider.AggregatePluginInfoReleaseProvider]
   */
  override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE

  companion object {
    internal const val DECK_REQUIREMENT = "deck"
  }
}
