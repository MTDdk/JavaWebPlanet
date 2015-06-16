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
public class SessionHelper {
    /**
     * Returns all session attributes in a map.
     *
     * @return all session attributes in a map.
     */
     /*public static Map<String, Object> getSessionAttributes(HttpServletRequest request){
        //TODO: cache session attributes map since this method can be called multiple times during a request.

        HttpSession session = request.getSession(true);
        Enumeration<String> names = session.getAttributeNames();
        Map<String, Object> values = new HashMap<String, Object>();
        while (names.hasMoreElements()) {
            Object name = names.nextElement();
            values.put(name.toString(), session.getAttribute(name.toString()));
        }
        return values;
    }*/
}
