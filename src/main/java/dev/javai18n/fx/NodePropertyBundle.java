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
import dev.javai18n.core.AttributeCollection;

/**
 * Provides a collection of localizable property values for a JavaFX {@link javafx.scene.Node}.
 *
 * <p>Properties are stored in a private map and accessed via typed getter and setter
 * methods. Subclasses add their own keys by delegating to the protected
 * {@link #get(String)} and {@link #put(String, Object)} methods.</p>
 *
 * <p><b>Font:</b> Stored as a CSS style string via the {@value #STYLE} key and applied
 * via {@code Node.setStyle()}.  For example: {@code "-fx-font: bold 14pt Arial"}.
 * This makes locale-specific fonts — such as CJK font families — straightforward to
 * configure without requiring a {@code javafx.scene.text.Font} object in the bundle.</p>
 */
public class NodePropertyBundle implements AttributeCollection
{
    /** Creates a new NodePropertyBundle. */
    public NodePropertyBundle()
    {
    }

    /**
     * A key for the accessible name of the Node.
     */
    public static final String NAME = "Name";

    /**
     * A key for the CSS style string of the Node (e.g. {@code "-fx-font: bold 14pt Arial"}).
     */
    public static final String STYLE = "Style";

    /**
     * A key for the accessible text of the Node.
     */
    public static final String ACCESSIBLE_TEXT = "AccessibleText";

    /**
     * A key for the accessible help text of the Node.
     */
    public static final String ACCESSIBLE_HELP = "AccessibleHelp";

    /**
     * A key for the accessible role description of the Node.
     */
    public static final String ACCESSIBLE_ROLE_DESCRIPTION = "AccessibleRoleDescription";

    private static final Set<String> OWN_ATTRIBUTE_NAMES = Set.of(
        NAME, STYLE, ACCESSIBLE_TEXT, ACCESSIBLE_HELP, ACCESSIBLE_ROLE_DESCRIPTION);

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
     * Associates the specified value with the specified key in this bundle,
     * replacing any previous mapping for the key.
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
     * Returns the String associated with the specified key.
     *
     * @param key The key to the desired String property.
     * @return The String associated with the specified key or null if not found.
     */
    public String getString(String key)
    {
        return (String) get(key);
    }

    /**
     * Returns the String associated with the NAME key.
     *
     * @return The String associated with the NAME key.
     */
    public String getName()
    {
        return getString(NAME);
    }

    /**
     * Sets the String associated with the NAME key.
     *
     * @param name The String to associate with the NAME key.
     */
    public void setName(String name)
    {
        put(NAME, name);
    }

    /**
     * Returns the CSS style string associated with the STYLE key.
     *
     * @return The CSS style string associated with the STYLE key.
     */
    public String getStyle()
    {
        return getString(STYLE);
    }

    /**
     * Sets the CSS style string associated with the STYLE key.
     *
     * @param style The CSS style string to associate with the STYLE key.
     */
    public void setStyle(String style)
    {
        put(STYLE, style);
    }

    /**
     * Returns the String associated with the ACCESSIBLE_TEXT key.
     *
     * @return The String associated with the ACCESSIBLE_TEXT key.
     */
    public String getAccessibleText()
    {
        return getString(ACCESSIBLE_TEXT);
    }

    /**
     * Sets the String associated with the ACCESSIBLE_TEXT key.
     *
     * @param text The String to associate with the ACCESSIBLE_TEXT key.
     */
    public void setAccessibleText(String text)
    {
        put(ACCESSIBLE_TEXT, text);
    }

    /**
     * Returns the String associated with the ACCESSIBLE_HELP key.
     *
     * @return The String associated with the ACCESSIBLE_HELP key.
     */
    public String getAccessibleHelp()
    {
        return getString(ACCESSIBLE_HELP);
    }

    /**
     * Sets the String associated with the ACCESSIBLE_HELP key.
     *
     * @param help The String to associate with the ACCESSIBLE_HELP key.
     */
    public void setAccessibleHelp(String help)
    {
        put(ACCESSIBLE_HELP, help);
    }

    /**
     * Returns the String associated with the ACCESSIBLE_ROLE_DESCRIPTION key.
     *
     * @return The String associated with the ACCESSIBLE_ROLE_DESCRIPTION key.
     */
    public String getAccessibleRoleDescription()
    {
        return getString(ACCESSIBLE_ROLE_DESCRIPTION);
    }

    /**
     * Sets the String associated with the ACCESSIBLE_ROLE_DESCRIPTION key.
     *
     * @param description The String to associate with the ACCESSIBLE_ROLE_DESCRIPTION key.
     */
    public void setAccessibleRoleDescription(String description)
    {
        put(ACCESSIBLE_ROLE_DESCRIPTION, description);
    }

    /**
     * Set the specified attribute name to the specified attribute value.
     *
     * @param attributeName  The name of the attribute.
     * @param attributeValue The value of the attribute.
     * @throws IllegalArgumentException if {@code attributeName} is not a recognized attribute
     *         for this bundle type.
     */
    @Override
    public void setAttribute(String attributeName, Object attributeValue)
    {
        if (attributeName.equals(NAME))
        {
            setName((String) attributeValue);
            return;
        }
        if (attributeName.equals(STYLE))
        {
            setStyle((String) attributeValue);
            return;
        }
        if (attributeName.equals(ACCESSIBLE_TEXT))
        {
            setAccessibleText((String) attributeValue);
            return;
        }
        if (attributeName.equals(ACCESSIBLE_HELP))
        {
            setAccessibleHelp((String) attributeValue);
            return;
        }
        if (attributeName.equals(ACCESSIBLE_ROLE_DESCRIPTION))
        {
            setAccessibleRoleDescription((String) attributeValue);
            return;
        }
        throw new IllegalArgumentException("Unrecognized attribute name: " + attributeName);
    }

    /**
     * Returns the set of valid attribute names for a NodePropertyBundle.
     *
     * @return The set of valid attribute names for a NodePropertyBundle.
     */
    public Set<String> validNames()
    {
        HashSet<String> nameSet = new HashSet<>(OWN_ATTRIBUTE_NAMES);
        return nameSet;
    }
}
