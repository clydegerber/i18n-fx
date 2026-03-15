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
import java.util.concurrent.atomic.AtomicReference;
import dev.javai18n.core.Resource;
import dev.javai18n.fx.ResourcefulTreeTableView;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ResourcefulTreeTableView.
 */
public class TestResourcefulTreeTableView
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
            var treeTableView = ResourcefulTreeTableView.create(new Resource(source, "TestTreeTableView"));
            assertEquals("Test tree table view tooltip", treeTableView.getTooltip().getText());
        });
    }

    @Test
    void testLocaleChange() throws Exception
    {
        TestComponentSource source = TestComponentSource.create();
        AtomicReference<ResourcefulTreeTableView<?>> ref = new AtomicReference<>();
        FXTestHelper.runAndWait(() ->
            ref.set(ResourcefulTreeTableView.create(new Resource(source, "TestTreeTableView"))));
        source.setBundleLocale(Locale.FRANCE);
        FXTestHelper.runAndWait(() -> {}); // drain FX queue
        assertEquals("Info-bulle vue arbre-tableau test", ref.get().getTooltip().getText());
    }
}
