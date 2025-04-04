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

import org.jsoup.nodes.TextNode;

/**
 * Helpers for HTML related aspects.
 *
 * <p>
 * For internal use only. May be renamed or removed in a future release.
 *
 * @author Vaadin Ltd
 * @since 1.0
 *
 */
public final class HtmlUtils {

    private HtmlUtils() {
        // avoid instantiation
    }

    /**
     * Escape a string which may contain html.
     *
     * @param maybeHtml
     *            the text to escape
     * @return escaped string
     */
    public static String escape(String maybeHtml) {
        return new TextNode(maybeHtml).outerHtml();
    }
}
