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
package net.javapla.jawn.templatemanagers.freemarker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.javapla.jawn.exceptions.InitException;
import net.javapla.jawn.exceptions.ViewException;
import net.javapla.jawn.exceptions.ViewMissingException;
import net.javapla.jawn.templatemanagers.TemplateManager;
import net.javapla.jawn.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author Igor Polevoy
 */
public class FreeMarkerTemplateManager implements TemplateManager {

    private Configuration config;
    private String defaultLayout;

    private String location;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    public FreeMarkerTemplateManager(AbstractFreeMarkerConfig freeMarkerConfig) {
        config = new Configuration();
        config.setObjectWrapper(new DefaultObjectWrapper());
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setSharedVariable("link_to", new LinkToTag());
        config.setSharedVariable("form", new FormTag());
        config.setSharedVariable("content", new ContentForTag());
        config.setSharedVariable("yield", new YieldTag());
        config.setSharedVariable("flash", new FlashTag());
        config.setSharedVariable("render", new RenderTag());
        config.setSharedVariable("confirm", new ConfirmationTag());
        config.setSharedVariable("wrap", new WrapTag());
        config.setSharedVariable("debug", new DebugTag());
        config.setSharedVariable("select", new SelectTag());
        config.setSharedVariable("message", new MessageTag());

//        AbstractFreeMarkerConfig freeMarkerConfig = (AbstractFreeMarkerConfig) net.javapla.jawn.Configuration.getTemplateConfig(getTemplateConfigClass());
        if(freeMarkerConfig != null){
//            freeMarkerConfig.setConfiguration(config);
            freeMarkerConfig.init(config);
        }
    }

    public void merge(Map<String, Object> values, String template, Writer writer) {
        merge(values, template, defaultLayout, null, writer);
    }
    
    @Override
    public void merge(Map<String, Object> values, String template, String layout, String format, String language, Writer writer) {
        merge(values, template, writer);
    }

    public void merge(Map<String, Object> input, String template, String layout, String format, Writer writer) {

        try {

            if(net.javapla.jawn.trash.Configuration.getEnv().equals("development")){
                config.clearTemplateCache();
            }
            ContentTL.reset();
            Template pageTemplate;

            try{
                String templateName = StringUtil.blank(format)? template + ".ftl" : template + "." + format + ".ftl";
                pageTemplate = config.getTemplate(templateName);
            }catch(FileNotFoundException e){
                logger.warn("Current location: " + new File(".").getCanonicalPath());
                throw e;
            }

            if(layout == null){//no layout
                pageTemplate.process(input, writer);
            }else{ // with layout
                 //Generate the template itself
                StringWriter pageWriter = new StringWriter();
                pageTemplate.process(input, pageWriter);

                Map<String, Object> values = new HashMap<>(input);
                values.put("page_content", pageWriter.toString());
                Map<String, List<String>>  assignedValues = ContentTL.getAllContent();

                for(String name: assignedValues.keySet()){
                    values.put(name, StringUtil.join(assignedValues.get(name), " "));
                }

                Template layoutTemplate = config.getTemplate(layout + ".ftl");
                layoutTemplate.process(values, writer);

                FreeMarkerTL.setEnvironment(null);
                logger.info("Rendered template: '" + template + "' with layout: '" + layout + "'");
            }
        }
        catch(FileNotFoundException e){
            throw new ViewMissingException(errorMessage(layout, template), e);
        }
        catch(ViewException e){
            logger.error(errorMessage(layout, template));
            throw e;
        }
        catch (Exception e) {
            throw new ViewException(errorMessage(layout, template), e);
        }
    }
    private String errorMessage(String layout, String template){
        return "Failed to render template: '" +(location != null? location:"") +  template + ".ftl" +
                (layout == null? "', without layout" : "', with layout: '" +(location != null? location:"") + layout + "'");

    }
    
    public void setServletContext(ServletContext ctx) {
        if(location == null)
            config.setServletContextForTemplateLoading(ctx, "WEB-INF/views/");
    }

    /**
     * This method exists for testing.
     *
     * @param path path to directory with test templates.
     * @throws IOException exception if directory not present.
     */
    public void setTemplateClassPath(String path) throws IOException {  
        config.setClassForTemplateLoading(this.getClass(), path);
    }

    public void setDefaultLayout(String layoutPath) {
        defaultLayout = layoutPath;
    }



    public void setTemplateLocation(String templateLocation) {
        location = templateLocation;

        try{
            config.setDirectoryForTemplateLoading(new File(templateLocation));
        }
        catch(Exception e){throw new InitException(e);}
    }


    /**
     * Registers an application-specific tag.
     *
     * @param name name of tag.
     * @param tag tag instance.
     */
    public void registerTag(String name, FreeMarkerTag tag){
       config.setSharedVariable(name, tag);
    }

     /**
     * Returns an instance of {@link FreeMarkerTag}. Use this method
     * to further configure specific tags.
     *
     * @param tagName name of tag as used in a template
     * @return instance of registered tag
     */
    public FreeMarkerTag getTag(String tagName){
        return (FreeMarkerTag) config.getSharedVariable(tagName);
    }
    
}
