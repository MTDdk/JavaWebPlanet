package net.javapla.jawn.core;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Result {
    
    /**
     * Http status code
     */
    protected Status status = Status.OK;
    
    /**
     * Something like: "text/html" or "application/json"
     */
    protected MediaType contentType = MediaType.PLAIN;

    /**
     * Object to be handled by a rendering engine
     */
    protected Object renderable;
    
    /**
     * Something like: "utf-8" => will be appended to the content-type. eg
     * "text/html; charset=utf-8"
     */
    protected Charset charset; //= "UTF-8";
    
    /**
     * A list of content types this result will handle. If you got a general
     * person object you can render it via application/json and application/xml
     * without changing anything inside your controller for instance.
     */
    //protected final List<String> supportedContentTypes = new ArrayList<>();
    
    // TODO README: using a hashmap of course prevents us from having multiple values for a single header
    // this could be implemented with a map<string, object> instead, where object CAN be an iterable,
    // and as such set a list of values in the response
    protected Map<String, String> headers;
    
    public Status status() {
        return status;
    }
    
    public Result status(final int status) {
        return status(Status.valueOf(status));
    }
    
    public Result status(final Status status) {
        if (status == null) throw new IllegalArgumentException("Status may not be null");
        this.status = status;
        return this;
    }
    
    public MediaType contentType() {
        return /*Optional.ofNullable*/(contentType);
    }
    
    public Result contentType(final MediaType type) {
        if (type == null) throw new IllegalArgumentException("ContentType may not be null");
        this.contentType = type;
        return this;
    }
    
    public Result contentType(final String type) {
        return contentType(MediaType.valueOf(type));
    }

    public Optional<Object> renderable() {
        return Optional.ofNullable(renderable);
    }
    
    public Result renderable(final Object content) {
        this.renderable = content;
        return this;
    }
    
    public Optional<Charset> charset() {
        return Optional.ofNullable(charset);
    }
    
    public Result charset(final Charset cs) {
        this.charset = cs;
        return this;
    }
    
    public Result charset(final String cs) {
        return charset(Charset.forName(cs));
    }
    
    public Optional<Map<String, String>> headers() {
        if (headers == null) return Optional.empty();
        return Optional.of(Collections.unmodifiableMap(headers));
    }
    
    public Result header(final String name, final String value) {
        if (headers == null) headers = new HashMap<>();
        headers.put(name, value);
        return this;
    }
    
    @Override
    public String toString() {
        return MessageFormat.format("status [{0}], type [{1}], char [{2}], obj [{3}]", status, contentType, charset, renderable);
    }
}
