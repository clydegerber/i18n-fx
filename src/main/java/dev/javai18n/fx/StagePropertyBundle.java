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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.scene.image.Image;

/**
 * Provides a collection of localizable property values for a {@link LocalizableStage},
 * extending {@link NodePropertyBundle} with a title and a list of icon images.
 */
public class StagePropertyBundle extends NodePropertyBundle
{
    /** Creates a new StagePropertyBundle. */
    public StagePropertyBundle()
    {
    }

    /**
     * A key for the title of the Stage.
     */
    public static final String TITLE = "Title";

    /**
     * A key for the list of icon images of the Stage.
     */
    public static final String ICONS = "Icons";

    private static final Set<String> OWN_ATTRIBUTE_NAMES = Set.of(TITLE, ICONS);

    /**
     * Returns the String associated with the TITLE key.
     *
     * @return The String associated with the TITLE key.
     */
    public String getTitle()
    {
        return getString(TITLE);
    }

    /**
     * Sets the String associated with the TITLE key.
     *
     * @param title The String to associate with the TITLE key.
     */
    public void setTitle(String title)
    {
        put(TITLE, title);
    }

    /**
     * Returns the list of icon images associated with the ICONS key.
     *
     * @return The list of icon images, or an empty list if none are set.
     */
    @SuppressWarnings("unchecked") // AttributeCollection stores Object values; the ICONS slot is
                                   // always written by addIcon(), which only ever puts List<Image>.
    public List<Image> getIcons()
    {
        List<Image> icons = (List<Image>) get(ICONS);
        return icons != null ? icons : new ArrayList<>();
    }

    /**
     * Adds an icon image to the list associated with the ICONS key.
     *
     * @param image The Image to add to the icons list.
     */
    public void addIcon(Image image)
    {
        @SuppressWarnings("unchecked") // same reason as getIcons()
        List<Image> icons = (List<Image>) get(ICONS);
        if (icons == null)
        {
            icons = new ArrayList<>();
            put(ICONS, icons);
        }
        icons.add(image);
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue)
    {
        if (attributeName.equals(TITLE))
        {
            setTitle((String) attributeValue);
            return;
        }
        if (attributeName.equals(ICONS))
        {
            if (attributeValue instanceof String[] iconPaths)
            {
                for (String path : iconPaths)
                {
                    addIcon(FXImageResourceLoader.getFXImageResource(path));
                }
            }
            else if (attributeValue instanceof String iconPath)
            {
                addIcon(FXImageResourceLoader.getFXImageResource(iconPath));
            }
            return;
        }
        super.setAttribute(attributeName, attributeValue);
    }

    /**
     * Returns the set of valid attribute names for a StagePropertyBundle.
     *
     * @return The set of valid attribute names for a StagePropertyBundle.
     */
    @Override
    public Set<String> validNames()
    {
        Set<String> nameSet = super.validNames();
        nameSet.addAll(OWN_ATTRIBUTE_NAMES);
        return nameSet;
    }
}
