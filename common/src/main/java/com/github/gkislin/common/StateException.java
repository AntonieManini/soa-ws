package com.github.gkislin.common;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.SocketException;

public class StateException extends RuntimeException {

    private ExceptionType type;
    private ExceptionSource source;

    public StateException() {
    }

    public StateException(ExceptionType type, Throwable e) {
        super(e);
        setSource(e);
        this.type = type;
    }

    public StateException(String message, ExceptionType type, Throwable e) {
        super(message, e);
        setSource(e);
        this.type = type;
    }

    public StateException(String msg, ExceptionType type) {
        super(msg);
        this.type = type;
    }

    public StateException(String message, ExceptionType type, ExceptionSource source) {
        this(message, type);
        this.source = source;
    }

    private void setSource(Throwable e) {
        if (e instanceof SocketException) {
            source = ExceptionSource.NETWORK;
        } else if (e instanceof IOException) {
            source = ExceptionSource.IO;
        } else if (e instanceof JAXBException) {
            source = ExceptionSource.XML;
        } else if(e instanceof SecurityException){
            source = ExceptionSource.SECURITY;
        }
    }

    public ExceptionType getType() {
        return type;
    }

    public ExceptionSource getSource() {
        return source;
    }

    @Override
    public String toString() {
        return type.getDescr() + " (" + getMessage() + ")";
    }
}