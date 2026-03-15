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
 * Provides a collection of localizable property values for a JavaFX {@link javafx.scene.control.TextInputControl},
 * extending {@link ControlPropertyBundle} with a prompt text property.
 */
public class TextInputPropertyBundle extends ControlPropertyBundle
{
    /** Creates a new TextInputPropertyBundle. */
    public TextInputPropertyBundle()
    {
    }

    /**
     * A key for the prompt text of the TextInputControl.
     */
    public static final String PROMPT_TEXT = "PromptText";

    private static final Set<String> OWN_ATTRIBUTE_NAMES = Set.of(PROMPT_TEXT);

    /**
     * Returns the String associated with the PROMPT_TEXT key.
     *
     * @return The String associated with the PROMPT_TEXT key.
     */
    public String getPromptText()
    {
        return getString(PROMPT_TEXT);
    }

    /**
     * Sets the String associated with the PROMPT_TEXT key.
     *
     * @param promptText The String to associate with the PROMPT_TEXT key.
     */
    public void setPromptText(String promptText)
    {
        put(PROMPT_TEXT, promptText);
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue)
    {
        if (attributeName.equals(PROMPT_TEXT))
        {
            setPromptText((String) attributeValue);
            return;
        }
        super.setAttribute(attributeName, attributeValue);
    }

    /**
     * Returns the set of valid attribute names for a TextInputPropertyBundle.
     *
     * @return The set of valid attribute names for a TextInputPropertyBundle.
     */
    @Override
    public Set<String> validNames()
    {
        Set<String> nameSet = super.validNames();
        nameSet.addAll(OWN_ATTRIBUTE_NAMES);
        return nameSet;
    }
}
