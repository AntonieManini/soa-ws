package com.github.gkislin.mail;

/**
 * User: gkislin
 * Date: 16.09.13
 */
public class ByteAttach extends Attach {
    private byte[] data;

    public ByteAttach() {
    }

    public ByteAttach(String name, byte[] data) {
        super(name, null);
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
