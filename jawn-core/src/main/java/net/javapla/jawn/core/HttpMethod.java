package net.javapla.jawn.core;

import java.util.Optional;
import java.util.function.Supplier;

import net.javapla.jawn.core.server.FormItem;
import net.javapla.jawn.core.util.MultiList;
import net.javapla.jawn.core.util.StringUtil;


/**
 * @author MTD
 */
public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS/*, PATCH, CONNECT, TRACE*//*, WS*/;
    
    public static final String AJAX_METHOD_PARAMETER = "_method";
    
    @FunctionalInterface
    public interface MultiListFormItemSupplier extends Supplier<MultiList<FormItem>> {}
    
    /** Detects an HTTP method from a request. */
    public static HttpMethod getMethod(final CharSequence requestMethod, Supplier<MultiList<? extends CharSequence>> params) {
        return _getMethod(requestMethod, () -> params.get().first(AJAX_METHOD_PARAMETER));
    }
    
    /** Detects an HTTP method from a request. */
    public static HttpMethod getMethod(final CharSequence requestMethod, MultiListFormItemSupplier params) { // we need MultiListFormItemSupplier or else it clashes with the other getMethod(String, Supplier)
        return _getMethod(requestMethod, () -> 
                                            params
                                                .get()
                                                .firstOptionally(AJAX_METHOD_PARAMETER)
                                                .map(FormItem::value)
                                                .map(Optional::get)
                                                .orElse(null)
                                            );
    }
    
    // Using a supplier in order to postpone calculation to later if necessary
    public static HttpMethod _getMethod(final CharSequence requestMethod, Supplier<CharSequence> _method) {
        char first = requestMethod.charAt(0);
        switch (first) {
            case 'G':
                return HttpMethod.GET;
            case 'D':
                return HttpMethod.DELETE;
            case 'H':
                return HttpMethod.HEAD;
            case 'O':
                return HttpMethod.OPTIONS;
            case 'P':
                if (requestMethod.charAt(1) == 'U') return HttpMethod.PUT;
                
                // assume POST
                
                // Sometimes an ajax request can only be sent as GET or POST.
                // We can emulate PUT and DELETE by sending a parameter '_method=PUT' or '_method=DELETE'.
                // Under the assumption that a request method always is sent in upper case
                final CharSequence methodParam = _method.get();
                if (methodParam != null) {
                    // assume DELETE
                    if (StringUtil.startsWith(methodParam, 'D', 'E', 'L')) return HttpMethod.DELETE;
                    // PUT
                    if (StringUtil.startsWith(methodParam, 'P', 'U', 'T')) return HttpMethod.PUT;
                }
                return HttpMethod.POST;
        }
        
        throw new IllegalArgumentException();
    }

}
