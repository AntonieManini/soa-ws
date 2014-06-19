package com.github.gkislin.common.web;

import com.github.gkislin.common.ExceptionSource;
import com.github.gkislin.common.ExceptionType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * User: gkislin
 * Date: 11.02.14
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FaultInfo {
    @XmlElement(required = true)
    private ExceptionType type;

    private ExceptionSource source;

    public FaultInfo() {
    }

    public FaultInfo(ExceptionType type, ExceptionSource source) {
        this.type = type;
        this.source = source;
    }

    public ExceptionType getType() {
        return type;
    }

    public ExceptionSource getSource() {
        return source;
    }
}
