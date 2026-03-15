/*
 * Copyright 2026 Clyde Gerber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.javai18n.fx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;
import static dev.javai18n.fx.FXLogger.FX_LOGGER;

/**
 * A helper class that provides a static method to load a JavaFX {@link Image} from a specified location.
 *
 * <p><b>Security note:</b> The {@link #getFXImageResource(String)} method will attempt to
 * read from the local filesystem as a last resort.  Callers are responsible for ensuring that
 * image path strings in resource bundles originate from trusted sources.</p>
 */
public final class FXImageResourceLoader
{
    private FXImageResourceLoader() {}

    /**
     * Cache of resource package names to their corresponding Modules, to avoid repeated StackWalker lookups.
     */
    private static final Map<String, Module> MODULE_CACHE = new ConcurrentHashMap<>();

    /**
     * Construct a JavaFX {@link Image} from the location specified by str, first trying it as a local
     * module resource, if that fails as a URL, and if that fails as a file URI.
     *
     * @param str The location (module resource path, URL, or file path) of the image.
     * @return The Image found at the location specified by str.
     * @throws IllegalArgumentException if the image cannot be loaded from any of the attempted locations.
     */
    public static Image getFXImageResource(String str)
    {
        try
        {
            String resourcePkg = "";
            if (str.lastIndexOf('/') >= 0)
            {
                resourcePkg = str.startsWith("/") ?
                    str.substring(1, str.lastIndexOf('/')) :
                    str.substring(0, str.lastIndexOf('/'));
            }
            resourcePkg = resourcePkg.replace("/", ".");
            Module module = FXImageResourceLoader.class.getModule();
            if (module.isNamed() && (resourcePkg.length() > 0))
            {
                module = MODULE_CACHE.computeIfAbsent(resourcePkg, pkg ->
                {
                    // Match the package of the resource to a package in the
                    // call stack to find the appropriate Module to load the resource.
                    // If there's not a match, use the Module for this Class.
                    Module defaultModule = FXImageResourceLoader.class.getModule();
                    StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
                    return walker.walk(s ->
                        s.filter(f -> f.getDeclaringClass().getPackage().getName().equals(pkg))
                         .map(f -> f.getDeclaringClass().getModule())
                         .findFirst()
                         .orElse(defaultModule));
                });
            }
            try (InputStream is = module.getResourceAsStream(str))
            {
                if (null != is)
                {
                    Image image = new Image(is);
                    if (!image.isError())
                    {
                        return image;
                    }
                }
            }
        }
        catch (IOException ex)
        {
            FX_LOGGER.log(System.Logger.Level.DEBUG, "failed.to.load.image.as.resource", str, ex);
        }
        try
        {
            Image image = new Image(str);
            if (!image.isError())
            {
                return image;
            }
        }
        catch (IllegalArgumentException ex)
        {
            FX_LOGGER.log(System.Logger.Level.DEBUG, "failed.to.load.image.as.url", str, ex);
        }
        try
        {
            java.io.File file = new java.io.File(str);
            if (file.exists())
            {
                Image image = new Image(file.toURI().toString());
                if (!image.isError())
                {
                    return image;
                }
            }
        }
        catch (Exception ex)
        {
            FX_LOGGER.log(System.Logger.Level.DEBUG, "failed.to.load.image.as.file", str, ex);
        }
        FX_LOGGER.log(System.Logger.Level.WARNING, "failed.to.load.image", str);
        throw new IllegalArgumentException("Unable to load image at " + str);
    }
}
