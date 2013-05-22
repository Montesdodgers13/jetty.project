//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.websocket.api;

import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.api.util.QuoteUtil;

public class UpgradeRequest
{
    private URI requestURI;
    private List<String> subProtocols = new ArrayList<>();
    private List<ExtensionConfig> extensions = new ArrayList<>();
    private List<HttpCookie> cookies = new ArrayList<>();
    private Map<String, List<String>> headers = new HashMap<>();
    private Map<String, List<String>> parameters = new HashMap<>();
    private Object session;
    private String httpVersion;
    private String method;
    private String host;

    protected UpgradeRequest()
    {
        /* anonymous, no requestURI, upgrade request */
    }

    public UpgradeRequest(String requestURI)
    {
        this(URI.create(requestURI));
    }

    public UpgradeRequest(URI requestURI)
    {
        this();
        setRequestURI(requestURI);
    }

    public void addExtensions(ExtensionConfig... configs)
    {
        for (ExtensionConfig config : configs)
        {
            extensions.add(config);
        }
    }

    public void addExtensions(String... configs)
    {
        for (String config : configs)
        {
            extensions.add(ExtensionConfig.parse(config));
        }
    }

    public void clearHeaders()
    {
        headers.clear();
    }

    public List<HttpCookie> getCookies()
    {
        return cookies;
    }

    public List<ExtensionConfig> getExtensions()
    {
        return extensions;
    }

    public String getHeader(String name)
    {
        List<String> values = headers.get(name.toLowerCase(Locale.ENGLISH));
        // no value list
        if (values == null)
        {
            return null;
        }
        int size = values.size();
        // empty value list
        if (size <= 0)
        {
            return null;
        }
        // simple return
        if (size == 1)
        {
            return values.get(0);
        }
        // join it with commas
        boolean needsDelim = false;
        StringBuilder ret = new StringBuilder();
        for (String value : values)
        {
            if (needsDelim)
            {
                ret.append(", ");
            }
            QuoteUtil.quoteIfNeeded(ret,value,QuoteUtil.ABNF_REQUIRED_QUOTING);
            needsDelim = true;
        }
        return ret.toString();
    }

    public int getHeaderInt(String name)
    {
        List<String> values = headers.get(name.toLowerCase(Locale.ENGLISH));
        // no value list
        if (values == null)
        {
            return -1;
        }
        int size = values.size();
        // empty value list
        if (size <= 0)
        {
            return -1;
        }
        // simple return
        if (size == 1)
        {
            return Integer.parseInt(values.get(0));
        }
        throw new NumberFormatException("Cannot convert multi-value header into int");
    }

    public Map<String, List<String>> getHeaders()
    {
        return headers;
    }

    public List<String> getHeaders(String name)
    {
        return headers.get(name);
    }

    public String getHost()
    {
        return host;
    }

    public String getHttpVersion()
    {
        return httpVersion;
    }

    public String getMethod()
    {
        return method;
    }

    public String getOrigin()
    {
        return getHeader("Origin");
    }

    /**
     * Returns a map of the query parameters of the request.
     * 
     * @return a unmodifiable map of query parameters of the request.
     */
    public Map<String, List<String>> getParameterMap()
    {
        return Collections.unmodifiableMap(parameters);
    }

    public String getQueryString()
    {
        return requestURI.getQuery();
    }

    public URI getRequestURI()
    {
        return requestURI;
    }

    /**
     * Access the Servlet HTTP Session (if present)
     * <p>
     * Note: Never present on a Client UpgradeRequest.
     * 
     * @return the Servlet HTTPSession on server side UpgradeRequests
     */
    public Object getSession()
    {
        return session;
    }

    public List<String> getSubProtocols()
    {
        return subProtocols;
    }

    public boolean hasSubProtocol(String test)
    {
        for (String protocol : subProtocols)
        {
            if (protocol.equalsIgnoreCase(test))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isOrigin(String test)
    {
        return test.equalsIgnoreCase(getOrigin());
    }

    public void setCookies(List<HttpCookie> cookies)
    {
        this.cookies = cookies;
    }

    public void setHeader(String name, List<String> values)
    {
        headers.put(name.toLowerCase(Locale.ENGLISH),values);
    }

    public void setHeader(String name, String value)
    {
        List<String> values = new ArrayList<>();
        values.add(value);
        setHeader(name.toLowerCase(Locale.ENGLISH),values);
    }

    public void setHeaders(Map<String, List<String>> headers)
    {
        clearHeaders();

        for (Map.Entry<String, List<String>> entry : headers.entrySet())
        {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            setHeader(name,values);
        }
    }

    public void setHttpVersion(String httpVersion)
    {
        this.httpVersion = httpVersion;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    protected void setParameterMap(Map<String, List<String>> parameters)
    {
        this.parameters.clear();
        this.parameters.putAll(parameters);
    }

    public void setRequestURI(URI uri)
    {
        this.requestURI = uri;
        this.host = this.requestURI.getHost();
        this.parameters.clear();
    }

    public void setSession(Object session)
    {
        this.session = session;
    }

    public void setSubProtocols(List<String> subProtocols)
    {
        this.subProtocols.clear();
        if (subProtocols != null)
        {
            this.subProtocols.addAll(subProtocols);
        }
    }

    /**
     * Set Sub Protocol request list.
     * 
     * @param protocols
     *            the sub protocols desired
     */
    public void setSubProtocols(String... protocols)
    {
        this.subProtocols.clear();
        for (String protocol : protocols)
        {
            this.subProtocols.add(protocol);
        }
    }
}
