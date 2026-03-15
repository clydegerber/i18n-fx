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
import dev.javai18n.fx.ResourcefulHyperlink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ResourcefulHyperlink.
 */
public class TestResourcefulHyperlink
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
            ResourcefulHyperlink hyperlink = ResourcefulHyperlink.create(new Resource(source, "TestHyperlink"));
            assertEquals("Test Hyperlink", hyperlink.getText());
            assertEquals("Test hyperlink tooltip", hyperlink.getTooltip().getText());
        });
    }

    @Test
    void testLocaleChange() throws Exception
    {
        TestComponentSource source = TestComponentSource.create();
        ResourcefulHyperlink[] holder = new ResourcefulHyperlink[1];
        FXTestHelper.runAndWait(() ->
        {
            holder[0] = ResourcefulHyperlink.create(new Resource(source, "TestHyperlink"));
        });
        source.setBundleLocale(Locale.FRANCE);
        FXTestHelper.runAndWait(() -> {}); // drain FX queue
        assertEquals("Lien Test", holder[0].getText());
        assertEquals("Info-bulle lien test", holder[0].getTooltip().getText());
    }
}
