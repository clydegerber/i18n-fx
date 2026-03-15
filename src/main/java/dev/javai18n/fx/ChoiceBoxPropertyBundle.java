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
 * Provides a collection of localizable property values for a JavaFX {@link javafx.scene.control.ChoiceBox},
 * extending {@link NodePropertyBundle} with item values.
 */
public class ChoiceBoxPropertyBundle extends NodePropertyBundle
{
    /** Creates a new ChoiceBoxPropertyBundle. */
    public ChoiceBoxPropertyBundle()
    {
    }

    /**
     * A key for the string array of item values for the ChoiceBox.
     */
    public static final String VALUES = "Values";

    private static final Set<String> OWN_ATTRIBUTE_NAMES = Set.of(VALUES);

    /**
     * Returns the String array associated with the VALUES key.
     *
     * @return The String array associated with the VALUES key, or null if not set.
     */
    public String[] getValues()
    {
        return (String[]) get(VALUES);
    }

    /**
     * Sets the String array associated with the VALUES key.
     *
     * @param values The String array to associate with the VALUES key.
     */
    public void setValues(String[] values)
    {
        put(VALUES, values);
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue)
    {
        if (attributeName.equals(VALUES))
        {
            setValues((String[]) attributeValue);
            return;
        }
        super.setAttribute(attributeName, attributeValue);
    }

    /**
     * Returns the set of valid attribute names for a ChoiceBoxPropertyBundle.
     *
     * @return The set of valid attribute names for a ChoiceBoxPropertyBundle.
     */
    @Override
    public Set<String> validNames()
    {
        Set<String> nameSet = super.validNames();
        nameSet.addAll(OWN_ATTRIBUTE_NAMES);
        return nameSet;
    }
}
