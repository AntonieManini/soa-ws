package com.github.gkislin.common;

/**
 * User: gkislin
 * Date: 8/26/11
 */
public enum ExceptionSource {
    NETWORK("Ошибка соединения по сети"),
    IO("Ошибка ввода-вывода"),
    XML("Ошибка при работе с XML"),
    SECURITY("Ошибка безопасности");

    final private String descr;

    ExceptionSource(String title) {
        this.descr = title;
    }

    public String getDescr() {
        return descr;
    }
}
