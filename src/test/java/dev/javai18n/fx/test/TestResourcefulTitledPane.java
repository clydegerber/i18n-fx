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
import dev.javai18n.fx.ResourcefulTitledPane;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ResourcefulTitledPane.
 */
public class TestResourcefulTitledPane
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
            ResourcefulTitledPane titledPane = ResourcefulTitledPane.create(new Resource(source, "TestTitledPane"));
            assertEquals("Test Title", titledPane.getText());
            assertEquals("Test titled pane tooltip", titledPane.getTooltip().getText());
        });
    }

    @Test
    void testLocaleChange() throws Exception
    {
        TestComponentSource source = TestComponentSource.create();
        ResourcefulTitledPane[] holder = new ResourcefulTitledPane[1];
        FXTestHelper.runAndWait(() ->
        {
            holder[0] = ResourcefulTitledPane.create(new Resource(source, "TestTitledPane"));
        });
        source.setBundleLocale(Locale.FRANCE);
        FXTestHelper.runAndWait(() -> {}); // drain FX queue
        assertEquals("Titre Test", holder[0].getText());
        assertEquals("Info-bulle volet titre test", holder[0].getTooltip().getText());
    }
}
