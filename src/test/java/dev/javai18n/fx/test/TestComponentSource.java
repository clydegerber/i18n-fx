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
import dev.javai18n.core.LocalizableImpl;

/**
 * A lightweight {@link dev.javai18n.core.Localizable} source used as the locale-event
 * source in unit tests for Resourceful JavaFX components. Its associated bundle
 * ({@code TestComponentSourceBundle.json} / {@code _fr.json}) holds only the property
 * entries needed by the tests, keeping the demo-app bundle (AppStageBundle) free of
 * test-only content.
 */
public class TestComponentSource extends LocalizableImpl
{
    static
    {
        FXTestModuleRegistrar.ensureRegistered();
    }

    /**
     * Create and initialize a new TestComponentSource with {@link Locale#ROOT} as the initial
     * locale, regardless of the JVM default locale.
     *
     * @return A TestComponentSource with locale-sensitive attributes updated from its bundle.
     */
    public static TestComponentSource create()
    {
        TestComponentSource source = new TestComponentSource();
        source.setBundleLocale(Locale.ROOT);
        return source;
    }

    @Override
    public Locale[] getAvailableLocales()
    {
        return new Locale[]{ Locale.ROOT, Locale.FRANCE };
    }
}
