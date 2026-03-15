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

/**
 * Provides a collection of localizable property values for a JavaFX {@link javafx.scene.control.Control},
 * extending {@link NodePropertyBundle} with a tooltip text property.
 */
public class ControlPropertyBundle extends NodePropertyBundle
{
    /** Creates a new ControlPropertyBundle. */
    public ControlPropertyBundle()
    {
    }

    /**
     * A key for the tooltip text of the Control.
     */
    public static final String TOOLTIP_TEXT = "TooltipText";

    private static final Set<String> OWN_ATTRIBUTE_NAMES = Set.of(TOOLTIP_TEXT);

    /**
     * Returns the String associated with the TOOLTIP_TEXT key.
     *
     * @return The String associated with the TOOLTIP_TEXT key.
     */
    public String getTooltipText()
    {
        return getString(TOOLTIP_TEXT);
    }

    /**
     * Sets the String associated with the TOOLTIP_TEXT key.
     *
     * @param tooltipText The String to associate with the TOOLTIP_TEXT key.
     */
    public void setTooltipText(String tooltipText)
    {
        put(TOOLTIP_TEXT, tooltipText);
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue)
    {
        if (attributeName.equals(TOOLTIP_TEXT))
        {
            setTooltipText((String) attributeValue);
            return;
        }
        super.setAttribute(attributeName, attributeValue);
    }

    /**
     * Returns the set of valid attribute names for a ControlPropertyBundle.
     *
     * @return The set of valid attribute names for a ControlPropertyBundle.
     */
    @Override
    public Set<String> validNames()
    {
        Set<String> nameSet = super.validNames();
        nameSet.addAll(OWN_ATTRIBUTE_NAMES);
        return nameSet;
    }
}
