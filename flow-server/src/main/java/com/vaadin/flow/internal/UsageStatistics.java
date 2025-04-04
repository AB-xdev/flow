/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.vaadin.flow.server.Version;

/**
 * Collects entries that should be exported for vaadin-usage-statistics when
 * running in development mode.
 * <p>
 * Statistics gathering is automatically disabled and excluded in production
 * mode.
 * <p>
 * For details and to opt-out, see
 * https://github.com/vaadin/vaadin-usage-statistics.
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public class UsageStatistics {
    /**
     * A usage statistics entry.
     */
    public static class UsageEntry {
        private final String name;
        private final String version;

        private UsageEntry(String name, String version) {
            this.name = name;
            this.version = version;
        }

        /**
         * Gets the feature name.
         *
         * @return the feature name, not <code>null</code>
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the version string.
         *
         * @return the version string, not <code>null</code>
         */
        public String getVersion() {
            if (version == null) {
                return Version.getFullVersion();
            }
            return version;
        }
    }

    private static final ConcurrentHashMap<String, UsageEntry> entries = new ConcurrentHashMap<>();
    static {
        setupDefaultEntries();
    }

    private UsageStatistics() {
        // Only static methods here, no need to create an instance
    }

    /**
     * Registers a new entry with the given feature name and version string if
     * no previous entry has been registered for the same name.
     *
     * @param name
     *            the feature name, not <code>null</code>
     * @param version
     *            the version, or <code>null</code> to use the current Flow
     *            version
     */
    public static void markAsUsed(String name, String version) {
        assert name != null;

        entries.computeIfAbsent(name, ignore -> new UsageEntry(name, version));
    }

    /**
     * Gets a stream of the current usage entries.
     *
     * @return a stream of entries, not <code>null</code>
     */
    public static Stream<UsageEntry> getEntries() {
        return entries.values().stream();
    }

    /**
     * Remove a entry of the current usage entries.
     *
     * @param name
     *            the feature name want to be removed, not <code>null</code>
     */
    public static void removeEntry(String name) {
        entries.remove(name);
    }

    /**
     * Reset the usage entries.
     */
    public static void resetEntries() {
        entries.clear();
        setupDefaultEntries();
    }

    private static void setupDefaultEntries() {
        String version = System.getProperty("java.version");

        if (version != null) {
            // Ignore pre, build and opt fields
            version = version.replaceAll("[-_+].*", "");
        } else {
            version = "unknown";
        }

        markAsUsed("java", version);
    }
}
