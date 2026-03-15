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
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for LocalizableStage (via AppStage).
 */
public class TestLocalizableStage
{
    @BeforeAll
    static void init()
    {
        FXTestHelper.ensureStarted();
    }

    @Test
    void testInitialTitle() throws Exception
    {
        FXTestHelper.runAndWait(() ->
        {
            AppStage stage = AppStage.create();
            assertEquals("Test FX Application", stage.getTitle());
        });
    }

    @Test
    void testLocaleChange() throws Exception
    {
        AppStage[] holder = new AppStage[1];
        FXTestHelper.runAndWait(() ->
        {
            holder[0] = AppStage.create();
        });
        holder[0].setBundleLocale(Locale.FRANCE);
        FXTestHelper.runAndWait(() -> {}); // drain FX queue
        assertEquals("Application FX de Test", holder[0].getTitle());
    }
}
