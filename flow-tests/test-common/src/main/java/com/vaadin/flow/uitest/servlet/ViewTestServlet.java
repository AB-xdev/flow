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
package com.vaadin.flow.uitest.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import com.vaadin.flow.server.VaadinServlet;

@WebServlet(asyncSupported = true, urlPatterns = { "/view/*" })
public class ViewTestServlet extends VaadinServlet {

    private static ViewClassLocator viewLocator;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        if (getService() != null) {
            viewLocator = new ViewClassLocator(getService().getClassLoader());
        }
    }

    static ViewClassLocator getViewLocator() {
        return viewLocator;
    }

}
