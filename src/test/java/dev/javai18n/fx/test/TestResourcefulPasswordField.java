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

package dev.javai18n.fx.test;

import java.util.Locale;
import dev.javai18n.core.Resource;
import dev.javai18n.fx.ResourcefulPasswordField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ResourcefulPasswordField.
 */
public class TestResourcefulPasswordField
{
    @BeforeAll
    static void init()
    {
        FXTestHelper.ensureStarted();
    }

    @Test
    void testInitialProperties() throws Exception
    {
        FXTestHelper.runAndWait(() ->
        {
            TestComponentSource source = TestComponentSource.create();
            ResourcefulPasswordField passwordField = ResourcefulPasswordField.create(new Resource(source, "TestPasswordField"));
            assertEquals("Enter password", passwordField.getPromptText());
            assertEquals("Test password field tooltip", passwordField.getTooltip().getText());
        });
    }

    @Test
    void testLocaleChange() throws Exception
    {
        TestComponentSource source = TestComponentSource.create();
        ResourcefulPasswordField[] holder = new ResourcefulPasswordField[1];
        FXTestHelper.runAndWait(() ->
        {
            holder[0] = ResourcefulPasswordField.create(new Resource(source, "TestPasswordField"));
        });
        source.setBundleLocale(Locale.FRANCE);
        FXTestHelper.runAndWait(() -> {}); // drain FX queue
        assertEquals("Entrez le mot de passe", holder[0].getPromptText());
        assertEquals("Info-bulle champ mot de passe test", holder[0].getTooltip().getText());
    }
}
