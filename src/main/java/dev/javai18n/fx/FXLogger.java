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

import static java.lang.System.LoggerFinder.getLoggerFinder;
import dev.javai18n.core.LocalizableLogger;

/**
 * A LocalizableLogger for the i18n-fx module. Log messages are resolved
 * from the FXLoggerBundle resource bundle in this module.
 */
public class FXLogger extends LocalizableLogger
{
    static
    {
        FXModuleRegistrar.ensureRegistered();
    }

    /**
     * The logger used internally by the i18n-fx module.
     */
    public static final FXLogger FX_LOGGER = createFXLogger("dev.javai18n.fx");

    /**
     * A factory method that returns a FXLogger for the specified name in the default Locale.
     *
     * @param name The name of the logger.
     * @return A FXLogger for the specified name in the default Locale.
     */
    public static FXLogger createFXLogger(String name)
    {
        FXLogger fxLogger = new FXLogger(name);
        fxLogger.logger = getLoggerFinder().getLocalizedLogger(
                name, fxLogger.getResourceBundle(), fxLogger.getClass().getModule());
        return fxLogger;
    }

    /**
     * Constructs a new FXLogger for the specified name with the default Locale.
     *
     * @param name The name of the logger.
     */
    protected FXLogger(String name)
    {
        super(name);
    }
}
