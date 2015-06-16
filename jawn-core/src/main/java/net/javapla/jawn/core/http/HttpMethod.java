/*
Copyright 2009-2014 Igor Polevoy

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/
package net.javapla.jawn.core.http;


/**
 * @author Igor Polevoy
 */
public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD;

    /**
     * Detects an HTTP method from a request.
     */
    public static HttpMethod getMethod(Context context/*HttpServletRequest request*/){
        String methodParam = context.getParameter("_method");
        String requestMethod = context.method();
        requestMethod = requestMethod.equalsIgnoreCase("POST") && methodParam != null && methodParam.equalsIgnoreCase("DELETE")? "DELETE" : requestMethod;
        requestMethod = requestMethod.equalsIgnoreCase("POST") && methodParam != null && methodParam.equalsIgnoreCase("PUT")? "PUT" : requestMethod;
        return HttpMethod.valueOf(requestMethod.toUpperCase());
    }
}
