package com.github.gkislin.common.io;

import com.github.gkislin.common.ExceptionType;
import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.util.Util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: gkislin
 * Date: 7/27/11
 * Replace ${placeholder}  return existed file
 */
public class ReadableFile extends File {
    private static final LoggerWrapper LOGGER = LoggerWrapper.get(ReadableFile.class);
    private static final Directory ROOT_FILE = new Directory("${SOA_WS2_ROOT}/resources");

    public ReadableFile(String pathname) {
        this(pathname, true);
    }

    public ReadableFile(String pathname, boolean check) {
        super(Util.resolveReplacement(pathname));
        if (check) {
            check();
        }
    }

    public ReadableFile(File parent, String child, boolean create) {
        super(parent, child);
        createOrCheck(create);
    }

    private void createOrCheck(boolean create) {
        if (create) {
            try {
                if (!createNewFile()) {
                    throw LOGGER.getStateException("File " + getAbsolutePath() + " already exists", ExceptionType.FILE);
                }
            } catch (IOException e) {
                throw LOGGER.getStateException("File " + getAbsolutePath() + " couldn't be created", ExceptionType.FILE, e);
            }
        } else if (!isFile()) {
            throw LOGGER.getStateException("File " + getAbsolutePath() + " not found", ExceptionType.FILE);
        }
    }

    public static URL getResourceUrl(String relativePath) {
        try {
            return getResource(relativePath).toURI().toURL();
        } catch (MalformedURLException e) {
            throw LOGGER.getIllegalArgumentException(relativePath + " malformed URL converting", e);
        }
    }

    public static ReadableFile getResource(String relativePath) {
        return new ReadableFile(ROOT_FILE, relativePath);
    }

    public ReadableFile(File parent, String child) {
        super(parent, child);
        check();
    }

    private void check() {
        if (!isFile()) {
            throw LOGGER.getStateException(getAbsolutePath() + " не найден", ExceptionType.FILE);
        }
    }

    public FileReader getReader() {
        try {
            return new FileReader(this);
        } catch (FileNotFoundException e) {
            throw LOGGER.getStateException(getAbsolutePath() + " не найден", ExceptionType.FILE);
        }
    }

    public FileInputStream getInputStream() {
        try {
            return new FileInputStream(this);
        } catch (FileNotFoundException e) {
            throw LOGGER.getStateException(getAbsolutePath() + " не найден", ExceptionType.FILE);
        }
    }
}
