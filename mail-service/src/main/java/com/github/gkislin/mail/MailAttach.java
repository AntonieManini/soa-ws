package com.github.gkislin.mail;

import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.io.ReadableFile;
import org.apache.commons.io.IOUtils;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.*;
import java.util.UUID;

/**
 * User: gkislin
 * Date: 16.09.13
 */
public class MailAttach {
    private static final LoggerWrapper LOGGER = LoggerWrapper.get(MailAttach.class);

    String name;
    DataSource dataSource;
    String description;
    String fileName;

    public MailAttach(String name, DataSource dataSource, String description) {
        this.description = description;
        this.name = name;
        this.dataSource = dataSource;
    }

    public MailAttach(String attach) {
        int idx = attach.indexOf('<');
        if (idx == -1) {
            throw LOGGER.getIllegalStateException("Attach format " + attach + " is illegal");
        } else {
            this.fileName = attach.substring(0, idx).trim();
            this.name = attach.substring(idx + 1, attach.length() - 1).trim();
        }
        dataSource = new FileDataSource(getFile(false));
    }

    public void save() {
        fileName = UUID.randomUUID().toString();
        File file = getFile(true);
        try (InputStream is = dataSource.getInputStream(); OutputStream os = new FileOutputStream(file)) {
            IOUtils.copy(is, os);
        } catch (IOException e) {
            LOGGER.warn("Attach " + this + " couldn't be saved", e);
        }
    }

    private ReadableFile getFile(boolean create) {
        return new ReadableFile(MailConfig.get().attachDir, fileName, create);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void delete() {
        File file = getFile(false);
        if (!file.delete()) {
            LOGGER.warn("Attach file " + file.getAbsoluteFile() + " couldn't be deleted");
        }
    }

    @Override
    public String toString() {
        return fileName == null ? name : fileName + " <" + name + '>';
    }
}
