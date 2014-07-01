package com.github.gkislin.common.web;


import com.github.gkislin.common.io.ReadableFile;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.developer.JAXWSProperties;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * User: gkislin
 * Date: 06.08.12
 */
public class WsClient<T> {

    private final Service SERVICE;
    private final Class<T> serviceClass;

    private volatile String endpointAddress;

    public WsClient(String wsdlFile, QName qname, Class<T> serviceClass) {
        this.serviceClass = serviceClass;
        URL url = ReadableFile.getResourceUrl(wsdlFile);
        SERVICE = Service.create(url, qname);
    }

    public void setEndpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }

    public T getPort() {
        T port = SERVICE.getPort(serviceClass);
        BindingProvider bp = (BindingProvider) port;
        Map<String, Object> requestContext = bp.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
        return port;
    }

    public static <T> void setHandler(T port, Handler handler) {
        Binding binding = ((BindingProvider) port).getBinding();
        List<Handler> handlerList = binding.getHandlerChain();
        handlerList.add(handler);
        binding.setHandlerChain(handlerList);
    }

    public static <T> void setAuth(T port, String user, String password) {
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProvider.USERNAME_PROPERTY, user);
        requestContext.put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    // Timeout in millis
    public static <T> void setTimeout(T port, int timeout) {
        Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, timeout);
        requestContext.put(JAXWSProperties.CONNECT_TIMEOUT, timeout);
        requestContext.put("com.sun.xml.internal.ws.connect.timeout", timeout);
        requestContext.put("com.sun.xml.internal.ws.request.timeout", timeout);
    }
}
