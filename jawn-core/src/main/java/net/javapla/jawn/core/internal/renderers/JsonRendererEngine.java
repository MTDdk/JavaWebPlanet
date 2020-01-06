package net.javapla.jawn.core.internal.renderers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.javapla.jawn.core.Context;
import net.javapla.jawn.core.MediaType;
import net.javapla.jawn.core.renderers.RendererEngine;

@Singleton
final class JsonRendererEngine implements RendererEngine {

    private final ObjectMapper mapper;
    
    @Inject
    JsonRendererEngine(final ObjectMapper mapper) {
        this.mapper = mapper;
    }
    
    @Override
    public void invoke(final Context context, final Object obj) throws Exception {
        if (obj instanceof byte[]) {
            context.resp().send((byte[])obj);
        } else if (obj instanceof String) {
            context.resp().send( /*(String)obj );/*/((String) obj).getBytes(context.resp().charset()));
        } else {
            context.resp().send(mapper.writeValueAsBytes(obj));
            //mapper.writeValue(context.resp().outputStream(), obj);
        }
    }
    
    @Override
    public MediaType[] getContentType() {
        return new MediaType[]{ MediaType.JSON };
    }
}