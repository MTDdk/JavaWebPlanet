package net.javapla.jawn.core.parsers;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.javapla.jawn.core.MediaType;
import net.javapla.jawn.core.Up;

@Singleton
final class XmlParserEngine implements ParserEngine {
    
    private final XmlMapper mapper;
    
    @Inject
    XmlParserEngine(XmlMapper mapper) {
        this.mapper = mapper;
    }
    
    /*@Override
    public <T> T invoke(Reader reader, Class<T> clazz) throws Up.ParsableError {
        try (Reader r = reader) {
            return mapper.readValue(r, clazz);
        } catch (IOException e) {
            throw new Up.ParsableError(e);
        }
    }*/
    @Override
    public <T> T invoke(InputStream stream, Class<T> clazz) throws Up.ParsableError {
        try (InputStream s = stream) {
            return mapper.readValue(s, clazz);
        } catch (IOException e) {
            throw new Up.ParsableError(e);
        }
    }
    
    @Override
    public <T> T invoke(byte[] arr, Class<T> clazz) throws Up.ParsableError {
        try {
            return mapper.readValue(arr, clazz);
        } catch (IOException e) {
            throw new Up.ParsableError(e);
        }
    }
    
    /*@Override
    public <T> T invoke(Parsable parsable, Class<T> type) throws ParsableError {
        try {
            return mapper.readValue(parsable.bytes(), type);
        } catch (IOException e) {
            throw new Up.ParsableError(e);
        }
    }*/
    
    @Override
    public MediaType[] getContentType() {
        return new MediaType[]{ MediaType.XML };
    }

}
