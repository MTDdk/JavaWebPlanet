package net.javapla.jawn.core.internal.reflection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import net.javapla.jawn.core.util.URLCodec;

public class JarLoader implements AutoCloseable {
    
    private final String scannedPath;
    private final ArrayList<JarFileTuple> jarfiles;
    
    public JarLoader(final ClassLoader classLoader, final String scannedPath) throws ZipException, IOException {
        this.scannedPath = scannedPath;
        jarfiles = new ArrayList<>();
        
        
        Enumeration<URL> resources = classLoader.getResources(scannedPath);
        while (resources.hasMoreElements()) {
            URL resource = (URL) resources.nextElement();
            
        
            String jarFile = resource.getFile();
            jarFile = URLCodec.decode(jarFile, StandardCharsets.UTF_8); // Perhaps this is only necessary on windows systems, 
                                                                        // but it seems to be crucial to decode spaces
        
        
            String jarFileName = extractJarFileFromClassPathFilename(jarFile);
            jarfiles.add(new JarFileTuple(jarFileName, new JarFile(jarFileName)));
        }
        jarfiles.trimToSize();
    }

    public Enumeration<URL> getResourcesFromJarFile() {
        final ArrayList<URL> retval = new ArrayList<>(jarfiles.size());
        
        for (JarFileTuple jarFile : jarfiles) {
            final Enumeration<JarEntry> e = jarFile.file.entries();
            
            while(e.hasMoreElements()){
                final JarEntry ze = (JarEntry) e.nextElement();
                if (ze.isDirectory()) continue;
                
                final String classname = ze.getName();
                if (classname.startsWith(scannedPath)) {
                    try {
                        URL url = new URL(String.format("jar:file:%s!/%s", jarFile.fileName, classname));
                        retval.add(url);
                    } catch (MalformedURLException e1) {
                        throw new Error(e1);
                    }
                }
            }
        }

        return Collections.enumeration(retval);
    }

    @Override
    public void close() throws IOException {
        IOException up = null;
        for (JarFileTuple jarFile : jarfiles) {
            try {
                jarFile.file.close();
            } catch (IOException e) {
                if (up != null) up = e;
            }
        }
        if (up != null) throw up;
    }

    private static String extractJarFileFromClassPathFilename(String file) {
        return file.substring("file:".length(), file.indexOf('!'));
    }
    
    private static final class JarFileTuple {
        public final String fileName;
        public final JarFile file;
        public JarFileTuple(String fileName, JarFile file) {
            this.fileName = fileName;
            this.file = file;
        }
    }

}
