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
package net.javapla.jawn.controller_filters;

import net.javapla.jawn.Context;

/**
 * This is a simple filter which logs time it took to process a controller.
 *
 * @author Igor Polevoy
 */
public class TimingFilter extends AbstractLoggingFilter {
    
    //must be threadlocal - filters are NOT thread safe!
    private static ThreadLocal<Long> time = new ThreadLocal<Long>();

    @Override
    public void before(Context context) {
        time.set(System.currentTimeMillis());
    }

    @Override
    protected String getMessage(Context context) {
        return "Processed request in: " + (System.currentTimeMillis() - time.get() + " milliseconds, path: " + context.path() + ", method: " + context.method());
    }
}
