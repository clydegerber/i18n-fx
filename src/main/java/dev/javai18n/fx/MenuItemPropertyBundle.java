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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javafx.scene.image.Image;
import dev.javai18n.core.AttributeCollection;

/**
 * Provides a collection of localizable property values for a JavaFX {@link javafx.scene.control.MenuItem}.
 *
 * <p>MenuItem does not extend {@link javafx.scene.Node}, so this bundle does not extend
 * {@link NodePropertyBundle}. It provides text and graphic properties directly.</p>
 */
public class MenuItemPropertyBundle implements AttributeCollection
{
    /** Creates a new MenuItemPropertyBundle. */
    public MenuItemPropertyBundle()
    {
    }

    /**
     * A key for the text of the MenuItem.
     */
    public static final String TEXT = "Text";

    /**
     * A key for the graphic of the MenuItem (stored as a resolved {@link Image}).
     */
    public static final String GRAPHIC = "Graphic";

    private static final Set<String> VALID_ATTRIBUTE_NAMES = Set.of(TEXT, GRAPHIC);

    private final HashMap<String, Object> map = new HashMap<>();

    /**
     * Returns the value associated with the specified key, or {@code null} if no
     * mapping exists for the key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The value associated with the specified key, or {@code null}.
     */
    public Object get(String key)
    {
        return map.get(key);
    }

    /**
     * Associates the specified value with the specified key in this bundle.
     *
     * @param key   The key with which the specified value is to be associated.
     * @param value The value to be associated with the specified key.
     */
    protected void put(String key, Object value)
    {
        map.put(key, value);
    }

    /**
     * Returns {@code true} if this bundle contains a mapping for the specified key.
     *
     * @param key The key whose presence is to be tested.
     * @return {@code true} if this bundle contains a mapping for the specified key.
     */
    public boolean containsKey(String key)
    {
        return map.containsKey(key);
    }

    /**
     * Returns the number of attribute mappings currently stored in this bundle.
     *
     * @return The number of attribute mappings in this bundle.
     */
    public int size()
    {
        return map.size();
    }

    /**
     * Returns the String associated with the TEXT key.
     *
     * @return The String associated with the TEXT key.
     */
    public String getText()
    {
        return (String) get(TEXT);
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
        throw new IllegalArgumentException("Unrecognized attribute name: " + attributeName);
    }

    /**
     * Returns the set of valid attribute names for a MenuItemPropertyBundle.
     *
     * @return The set of valid attribute names for a MenuItemPropertyBundle.
     */
    public Set<String> validNames()
    {
        return new HashSet<>(VALID_ATTRIBUTE_NAMES);
    }
}
