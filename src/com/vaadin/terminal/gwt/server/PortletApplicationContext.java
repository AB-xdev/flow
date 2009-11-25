/**
 * 
 */
package com.vaadin.terminal.gwt.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSession;

import com.vaadin.Application;

/**
 * @author marc
 * @deprecated Use Portlet 2.0 class {@link PortletApplicationContext2} instead.
 */
@SuppressWarnings({"serial", "unchecked"})
@Deprecated
public class PortletApplicationContext extends WebApplicationContext implements
        Serializable {

    protected PortletSession portletSession;

    protected Map portletListeners = new HashMap();

    protected Map portletToApplication = new HashMap();

    PortletApplicationContext() {

    }

    static public PortletApplicationContext getApplicationContext(
            PortletSession session) {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName(),
                        PortletSession.APPLICATION_SCOPE);
        if (cx == null) {
            cx = new PortletApplicationContext();
        }
        if (!(cx instanceof PortletApplicationContext)) {
            // TODO Should we even try this? And should we leave original as-is?
            PortletApplicationContext pcx = new PortletApplicationContext();
            pcx.applications.addAll(cx.applications);
            cx.applications.clear();
            pcx.browser = cx.browser;
            cx.browser = null;
            pcx.listeners = cx.listeners;
            cx.listeners = null;
            pcx.session = cx.session;
            cx = pcx;
        }
        if (((PortletApplicationContext) cx).portletSession == null) {
            ((PortletApplicationContext) cx).portletSession = session;
        }
        session.setAttribute(WebApplicationContext.class.getName(), cx,
                PortletSession.APPLICATION_SCOPE);
        return (PortletApplicationContext) cx;
    }

    static public WebApplicationContext getApplicationContext(
            HttpSession session) {
        WebApplicationContext cx = (WebApplicationContext) session
                .getAttribute(WebApplicationContext.class.getName());
        if (cx == null) {
            cx = new PortletApplicationContext();
        }
        if (cx.session == null) {
            cx.session = session;
        }
        session.setAttribute(WebApplicationContext.class.getName(), cx);
        return cx;
    }

    @Override
    protected void removeApplication(Application application) {
        portletListeners.remove(application);
        for (Iterator it = portletToApplication.keySet().iterator(); it
                .hasNext();) {
            Object key = it.next();
            if (key == application) {
                portletToApplication.remove(key);
            }
        }
        super.removeApplication(application);
    }

    public void setPortletApplication(Portlet portlet, Application app) {
        portletToApplication.put(portlet, app);
    }

    public Application getPortletApplication(Portlet portlet) {
        return (Application) portletToApplication.get(portlet);
    }

    public PortletSession getPortletSession() {
        return portletSession;
    }

    public void addPortletListener(Application app, PortletListener listener) {
        Set l = (Set) portletListeners.get(app);
        if (l == null) {
            l = new LinkedHashSet();
            portletListeners.put(app, l);
        }
        l.add(listener);
    }

    public void removePortletListener(Application app, PortletListener listener) {
        Set l = (Set) portletListeners.get(app);
        if (l != null) {
            l.remove(listener);
        }
    }

    public static void dispatchRequest(Portlet portlet, RenderRequest request,
            RenderResponse response) {
        PortletApplicationContext ctx = getApplicationContext(request
                .getPortletSession());
        if (ctx != null) {
            ctx.firePortletRenderRequest(portlet, request, response);
        }
    }

    public static void dispatchRequest(Portlet portlet, ActionRequest request,
            ActionResponse response) {
        PortletApplicationContext ctx = getApplicationContext(request
                .getPortletSession());
        if (ctx != null) {
            ctx.firePortletActionRequest(portlet, request, response);
        }
    }

    public void firePortletRenderRequest(Portlet portlet,
            RenderRequest request, RenderResponse response) {
        Application app = getPortletApplication(portlet);
        Set listeners = (Set) portletListeners.get(app);
        if (listeners != null) {
            for (Iterator it = listeners.iterator(); it.hasNext();) {
                PortletListener l = (PortletListener) it.next();
                l.handleRenderRequest(request, new RestrictedRenderResponse(
                        response));
            }
        }
    }

    public void firePortletActionRequest(Portlet portlet,
            ActionRequest request, ActionResponse response) {
        Application app = getPortletApplication(portlet);
        Set listeners = (Set) portletListeners.get(app);
        if (listeners != null) {
            for (Iterator it = listeners.iterator(); it.hasNext();) {
                PortletListener l = (PortletListener) it.next();
                l.handleActionRequest(request, response);
            }
        }
    }

    public interface PortletListener extends Serializable {
        public void handleRenderRequest(RenderRequest request,
                RenderResponse response);

        public void handleActionRequest(ActionRequest request,
                ActionResponse response);
    }

}
