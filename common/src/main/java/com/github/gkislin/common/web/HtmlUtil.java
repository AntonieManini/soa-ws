package com.github.gkislin.common.web;


import com.github.gkislin.common.StateException;

/**
 * User: gkislin
 * Date: 11.02.14
 */
public class HtmlUtil {

    public static String toHtml(Exception e) {
        return (((e instanceof StateException)) ? "<h2>" + ((StateException) e).getType().getDescr() + "</h2>" : "") + e.toString();
    }
}
