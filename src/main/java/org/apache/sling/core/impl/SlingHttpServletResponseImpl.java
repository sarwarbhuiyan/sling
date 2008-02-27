/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.core.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.core.impl.request.RequestData;
import org.apache.sling.core.servlets.ErrorHandler;

/**
 * The <code>SlingHttpServletResponseImpl</code> TODO
 */
public class SlingHttpServletResponseImpl extends HttpServletResponseWrapper implements SlingHttpServletResponse {

    private final RequestData requestData;

    public SlingHttpServletResponseImpl(RequestData requestData,
            HttpServletResponse response) {
        super(response);
        this.requestData = requestData;
    }

    protected final RequestData getRequestData() {
        return requestData;
    }

    @Override
    public void flushBuffer() throws IOException {
        getRequestData().getContentData().flushBuffer();
    }

    @Override
    public int getBufferSize() {
        return getRequestData().getContentData().getBufferSize();
    }

    @Override
    public Locale getLocale() {
        // TODO Should use our Locale Resolver and not let the component set the locale, right ??
        return getResponse().getLocale();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return getRequestData().getBufferProvider().getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return getRequestData().getBufferProvider().getWriter();
    }

    @Override
    public boolean isCommitted() {
        // TODO: integrate with our output catcher
        return getResponse().isCommitted();
    }

    @Override
    public void reset() {
        // TODO: integrate with our output catcher
        getResponse().reset();
    }

    @Override
    public void resetBuffer() {
        getRequestData().getContentData().resetBuffer();
    }

    @Override
    public void setBufferSize(int size) {
        getRequestData().getContentData().setBufferSize(size);
    }

    // ---------- Redirection support through PathResolver --------------------

    @Override
    public String encodeURL(String url) {
        // make the path absolute
        url = makeAbsolutePath(url);

        // resolve the url to as if it would be a resource path
        url = map(url);

        // have the servlet container to further encodings
        return super.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        // make the path absolute
        url = makeAbsolutePath(url);

        // resolve the url to as if it would be a resource path
        url = map(url);

        // have the servlet container to further encodings
        return super.encodeRedirectURL(url);
    }

    @Override
    @Deprecated
    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    @Override
    @Deprecated
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    // ---------- Error handling through Sling Error Resolver -----------------

    @Override
    public void sendError(int sc) throws IOException {
        sendError(sc, null);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        checkCommitted();

        ErrorHandler eh = getRequestData().getSlingMainServlet().getErrorHandler();
        eh.handleError(sc, msg, requestData.getSlingRequest(), this);
    }

    @Override
    public void setStatus(int sc, String sm) {
        checkCommitted();
        super.setStatus(sc, sm);
    }

    @Override
    public void setStatus(int sc) {
        checkCommitted();
        super.setStatus(sc);
    }

    // ---------- Internal helper ---------------------------------------------

    private void checkCommitted() {
        if (isCommitted()) {
            throw new IllegalStateException(
                "Response has already been committed");
        }
    }

    private String makeAbsolutePath(String path) {
        if (path.startsWith("/")) {
            return path;
        }

        String base = getRequestData().getContentData().getResource().getPath();
        int lastSlash = base.lastIndexOf('/');
        if (lastSlash >= 0) {
            path = base.substring(0, lastSlash+1) + path;
        } else {
            path = "/" + path;
        }

        return path;
    }

    private String map(String url) {
        return getRequestData().getResourceResolver().map(url);
    }
}
