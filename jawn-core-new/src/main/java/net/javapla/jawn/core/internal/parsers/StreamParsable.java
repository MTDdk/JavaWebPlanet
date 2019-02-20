package net.javapla.jawn.core.internal.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import net.javapla.jawn.core.parsers.ParserEngine;
import net.javapla.jawn.core.util.StreamUtil;

public class StreamParsable implements ParserEngine.Parsable {
    
    private final long length;
    //private final Charset cs;
    
    private File file;
    private byte[] bytes;
    
    public StreamParsable(final long length/*, final Charset cs*/, final File file, final InputStream in, final int bufferSize) throws IOException {
        this.length = length;
        //this.cs = cs;
        
        if (length < bufferSize) {
            this.bytes = StreamUtil.bytes(in);
        } else {
            StreamUtil.saveTo(file, in);
            this.file = file;
        }
    }

    @Override
    public long length() {
        return length;
    }

    @Override
    public byte[] bytes() throws IOException {
        if (bytes == null)
            return Files.readAllBytes(file.toPath());
        return bytes;
    }

    /*@Override
    public String text() throws IOException {
        if (bytes != null) return new String(bytes, cs);
        return new String(bytes(), cs);
    }*/

}