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
import dev.javai18n.fx.FXLogger;
import dev.javai18n.fx.LocalizableStage;

/**
 * A LocalizableStage subclass used as the top-level window in unit tests.
 * Its associated bundle ({@code AppStageBundle.json} / {@code _fr.json})
 * provides locale-specific stage properties such as the window title.
 */
public class AppStage extends LocalizableStage
{
    static
    {
        FXTestModuleRegistrar.ensureRegistered();
    }

    /**
     * Create and initialize a new AppStage.
     * Must be called on the JavaFX Application Thread.
     *
     * @return An AppStage with locale-sensitive attributes updated from its bundle.
     */
    public static AppStage create()
    {
        AppStage stage = new AppStage();
        stage.setBundleLocale(Locale.ROOT);
        stage.updateLocaleSpecificValues();
        return stage;
    }

    @Override
    public Locale[] getAvailableLocales()
    {
        return FXLogger.FX_LOGGER.getAvailableLocales();
    }
}
