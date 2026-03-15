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
import dev.javai18n.fx.ResourcefulSeparator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ResourcefulSeparator.
 */
public class TestResourcefulSeparator
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
            ResourcefulSeparator separator = ResourcefulSeparator.create(new Resource(source, "TestSeparator"));
            assertEquals("Test separator tooltip", separator.getTooltip().getText());
        });
    }

    @Test
    void testLocaleChange() throws Exception
    {
        TestComponentSource source = TestComponentSource.create();
        ResourcefulSeparator[] holder = new ResourcefulSeparator[1];
        FXTestHelper.runAndWait(() ->
        {
            holder[0] = ResourcefulSeparator.create(new Resource(source, "TestSeparator"));
        });
        source.setBundleLocale(Locale.FRANCE);
        FXTestHelper.runAndWait(() -> {}); // drain FX queue
        assertEquals("Info-bulle séparateur test", holder[0].getTooltip().getText());
    }
}
