package com.github.gkislin.common.web;


import com.github.gkislin.common.ExceptionSource;
import com.github.gkislin.common.ExceptionType;

import javax.xml.ws.WebFault;

/**
 * User: gkislin
 * Date: 28.01.14
 */
@WebFault(name = "webStateException", targetNamespace = "http://web.common.gkislin.github.com/")

public class WebStateException extends Exception {
    private FaultInfo faultInfo;

    public WebStateException(String message, FaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public WebStateException(String message, ExceptionType type, ExceptionSource source, Throwable cause) {
        super(message, cause);
        this.faultInfo = new FaultInfo(type, source);
    }

    public WebStateException(Exception e) {
        super(e);
        faultInfo = new FaultInfo(ExceptionType.SYSTEM, null);
    }

    public FaultInfo getFaultInfo() {
        return faultInfo;
    }
}
