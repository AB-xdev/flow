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
package com.vaadin.client;

import com.vaadin.client.bootstrap.ErrorMessage;

/**
 * Application configuration data.
 * <p>
 * This class is effectively immutable although setters exist to assign the
 * values during construction.
 *
 * @author Vaadin Ltd
 * @since 1.0
 */
public class ApplicationConfiguration {
    private String applicationId;
    private String contextRootUrl;
    private String serviceUrl;
    private int uiId;
    private ErrorMessage sessionExpiredError;
    private int heartbeatInterval;
    private int maxMessageSuspendTimeout;

    private boolean productionMode;
    private boolean requestTiming;
    private boolean webComponentMode;

    private String servletVersion;
    private String atmosphereVersion;
    private String atmosphereJSVersion;
    private String[] exportedWebComponents;

    private boolean devToolsEnabled;
    private String liveReloadUrl;
    private String liveReloadBackend;
    private String springBootLiveReloadPort;

    /**
     * Gets the id generated for the application.
     *
     * @return the id for the application
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Sets the id generated for the application.
     *
     * @param applicationId
     *            the id for the application
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Gets the URL to the server-side VaadinService.
     *
     * @return the URL to the server-side service as a string
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Sets the URL to the server-side VaadinService.
     *
     * @param serviceUrl
     *            the URL to the server-side service as a string
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    /**
     * Gets the URL of the context root on the server.
     *
     * @return the URL of the context root, ending with a "/"
     */
    public String getContextRootUrl() {
        return contextRootUrl;
    }

    /**
     * Sets the URL of the context root on the server.
     *
     * @param contextRootUrl
     *            the URL of the context root, ending with a "/"
     */
    public void setContextRootUrl(String contextRootUrl) {
        assert contextRootUrl.endsWith("/");
        this.contextRootUrl = contextRootUrl;
    }

    /**
     * Checks whether the application is running as a web-component in the page.
     *
     * @return true in case the app is a WC
     */
    public boolean isWebComponentMode() {
        return webComponentMode;
    }

    /**
     * Sets whether the application is running as a web-component in the page.
     *
     * @param mode
     *            set to true if it's a WC
     */
    public void setWebComponentMode(boolean mode) {
        this.webComponentMode = mode;
    }

    /**
     * Gets the UI id of the server-side UI associated with this client-side
     * instance. The UI id should be included in every request originating from
     * this instance in order to associate the request with the right UI
     * instance on the server.
     *
     * @return the UI id
     */
    public int getUIId() {
        return uiId;
    }

    /**
     * Sets the UI id of the server-side UI associated with this client-side
     * instance.
     *
     * @param uiId
     *            the UI id
     */
    public void setUIId(int uiId) {
        this.uiId = uiId;
    }

    /**
     * Gets the interval for heartbeat requests.
     *
     * @return The interval in seconds between heartbeat requests, or -1 if
     *         heartbeat is disabled.
     */
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    /**
     * Sets the interval for heartbeat requests.
     *
     * @param heartbeatInterval
     *            The interval in seconds between heartbeat requests, or -1 if
     *            heartbeat is disabled.
     */
    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    /**
     * Gets the maximum message suspension delay.
     *
     * @return The maximum time, in milliseconds, to suspend out-of-order
     *         messages waiting for their predecessor before resynchronizing.
     */
    public int getMaxMessageSuspendTimeout() {
        return maxMessageSuspendTimeout;
    }

    /**
     * Sets the maximum message suspension delay.
     *
     * @param maxMessageSuspendTimeout
     *            The maximum time, in milliseconds, to suspend out-of-order
     *            messages waiting for their predecessor before resynchronizing.
     */
    public void setMaxMessageSuspendTimeout(int maxMessageSuspendTimeout) {
        this.maxMessageSuspendTimeout = maxMessageSuspendTimeout;
    }

    /**
     * Gets the message used when a session expiration error occurs.
     *
     * @return the session expiration error message
     */
    public ErrorMessage getSessionExpiredError() {
        return sessionExpiredError;
    }

    /**
     * Sets the message used when a session expiration error occurs.
     *
     * @param sessionExpiredError
     *            the session expiration error message
     */
    public void setSessionExpiredError(ErrorMessage sessionExpiredError) {
        this.sessionExpiredError = sessionExpiredError;
    }

    /**
     * Gets the Vaadin servlet version in use.
     *
     * @return the Vaadin servlet version in use
     */
    public String getServletVersion() {
        return servletVersion;
    }

    /**
     * Sets the Vaadin servlet version in use.
     *
     * @param servletVersion
     *            the Vaadin servlet version in use
     */
    public void setServletVersion(String servletVersion) {
        this.servletVersion = servletVersion;
    }

    /**
     * Gets the Atmosphere runtime version in use.
     *
     * @return the Atmosphere runtime version in use
     */
    public String getAtmosphereVersion() {
        return atmosphereVersion;
    }

    /**
     * Sets the Atmosphere runtime version in use.
     *
     * @param atmosphereVersion
     *            the Atmosphere runtime version in use
     */
    public void setAtmosphereVersion(String atmosphereVersion) {
        this.atmosphereVersion = atmosphereVersion;
    }

    /**
     * Gets the Atmosphere JavaScript version in use.
     *
     * @return the Atmosphere JavaScript version in use
     */
    public String getAtmosphereJSVersion() {
        return atmosphereJSVersion;
    }

    /**
     * Sets the Atmosphere JavaScript version in use.
     *
     * @param atmosphereJSVersion
     *            the Atmosphere JavaScript version in use
     */
    public void setAtmosphereJSVersion(String atmosphereJSVersion) {
        this.atmosphereJSVersion = atmosphereJSVersion;
    }

    /**
     * Checks if we are running in production mode.
     * <p>
     * With production mode disabled, a lot more information is logged to the
     * browser console. In production you should always enable production mode,
     * because logging and other debug features can have a significant
     * performance impact.
     *
     * @return {@code true} if production mode is enabled, {@code false}
     *         otherwise
     */
    public boolean isProductionMode() {
        return productionMode;
    }

    /**
     * Checks if request timing info should be made available.
     *
     * @return {@code true} if request timing info should be made availble,
     *         {@code false} otherwise
     */
    public boolean isRequestTiming() {
        return requestTiming;
    }

    /**
     * Sets whether we are running in production mode.
     * <p>
     * With production mode disabled, a lot more information is logged to the
     * browser console. In production you should always enable production mode,
     * because logging and other debug features can have a significant
     * performance impact.
     *
     * @param productionMode
     *            {@code true} if production mode is enabled, {@code false}
     *            otherwise
     */
    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
        Console.setProductionMode(productionMode);
    }

    /**
     * Sets whether request timing info should be made available.
     *
     * @param requestTiming
     *            {@code true} if request timing info should be made available,
     *            {@code false} otherwise
     */
    public void setRequestTiming(boolean requestTiming) {
        this.requestTiming = requestTiming;
    }

    /**
     * Sets the exported web components.
     *
     * @param exportedWebComponents
     *            the exported web components
     */
    public void setExportedWebComponents(String[] exportedWebComponents) {
        this.exportedWebComponents = exportedWebComponents;
    }

    /**
     * Gets the exported web components.
     *
     * @return the exported web components
     */
    public String[] getExportedWebComponents() {
        return exportedWebComponents;
    }

    /**
     * Gets if development tools should be added to the page.
     *
     * @return whether development tools should be added
     */
    public boolean isDevToolsEnabled() {
        return devToolsEnabled;
    }

    /**
     *
     * Sets if development tools should be added to the page.
     *
     * @param devToolsEnabled
     *            whether development tools should be added
     */
    public void setDevToolsEnabled(boolean devToolsEnabled) {
        this.devToolsEnabled = devToolsEnabled;
    }

    /**
     * Gets the URL for the live reload websocket connection.
     *
     * @return URL for the live reload websocket connection
     */
    public String getLiveReloadUrl() {
        return liveReloadUrl;
    }

    /**
     * Sets the URL for the live reload websocket connection.
     *
     * @param liveReloadUrl
     *            URL for the live reload websocket connection
     */
    public void setLiveReloadUrl(String liveReloadUrl) {
        this.liveReloadUrl = liveReloadUrl;
    }

    /**
     * Gets the the live reload backend technology identifier.
     *
     * @return the live reload backend technology identifier
     */
    public String getLiveReloadBackend() {
        return liveReloadBackend;
    }

    /**
     * Sets the live reload backend technology identifier.
     *
     * @param liveReloadBackend
     *            the live reload backend technology identifier
     */
    public void setLiveReloadBackend(String liveReloadBackend) {
        this.liveReloadBackend = liveReloadBackend;
    }

    /**
     * Gets the Spring boot live reload port.
     *
     * @return the Spring boot live reload port
     */
    public String getSpringBootLiveReloadPort() {
        return springBootLiveReloadPort;
    }

    /**
     * Sets the Spring boot live reload port.
     *
     * @param springBootLiveReloadPort
     *            the Spring boot live reload port
     */
    public void setSpringBootLiveReloadPort(String springBootLiveReloadPort) {
        this.springBootLiveReloadPort = springBootLiveReloadPort;
    }
}
