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

import java.util.Set;
import javafx.scene.image.Image;

/**
 * Provides a collection of localizable property values for a JavaFX {@link javafx.scene.control.TitledPane},
 * extending {@link ControlPropertyBundle} with title text and graphic properties.
 */
public class TitledPanePropertyBundle extends ControlPropertyBundle
{
    /** Creates a new TitledPanePropertyBundle. */
    public TitledPanePropertyBundle()
    {
    }

    /**
     * A key for the title text of the TitledPane.
     */
    public static final String TEXT = "Text";

    /**
     * A key for the graphic of the TitledPane (stored as a resolved {@link Image}).
     */
    public static final String GRAPHIC = "Graphic";

    private static final Set<String> OWN_ATTRIBUTE_NAMES = Set.of(TEXT, GRAPHIC);

    /**
     * Returns the String associated with the TEXT key.
     *
     * @return The String associated with the TEXT key.
     */
    public String getText()
    {
        return getString(TEXT);
    }

    /**
     * Sets the String associated with the TEXT key.
     *
     * @param text The String to associate with the TEXT key.
     */
    public void setText(String text)
    {
        put(TEXT, text);
    }

    /**
     * Returns the Image associated with the GRAPHIC key.
     *
     * @return The Image associated with the GRAPHIC key, or null if none is set.
     */
    public Image getGraphic()
    {
        return (Image) get(GRAPHIC);
    }

    /**
     * Sets the Image associated with the GRAPHIC key.
     *
     * @param image The Image to associate with the GRAPHIC key.
     */
    public void setGraphic(Image image)
    {
        put(GRAPHIC, image);
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue)
    {
        if (attributeName.equals(TEXT))
        {
            setText((String) attributeValue);
            return;
        }
        if (attributeName.equals(GRAPHIC))
        {
            setGraphic(FXImageResourceLoader.getFXImageResource((String) attributeValue));
            return;
        }
        super.setAttribute(attributeName, attributeValue);
    }

    /**
     * Returns the set of valid attribute names for a TitledPanePropertyBundle.
     *
     * @return The set of valid attribute names for a TitledPanePropertyBundle.
     */
    @Override
    public Set<String> validNames()
    {
        Set<String> nameSet = super.validNames();
        nameSet.addAll(OWN_ATTRIBUTE_NAMES);
        return nameSet;
    }
}
