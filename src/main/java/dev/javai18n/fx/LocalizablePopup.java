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

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.stage.Popup;
import dev.javai18n.core.Localizable;
import dev.javai18n.core.LocalizationDelegate;
import dev.javai18n.core.NoCallbackRegisteredForModuleException;

/**
 * A LocalizablePopup class that extends {@link Popup} and implements {@link Localizable}.
 *
 * <p>LocalizablePopup serves as the locale-event source for all Resourceful components it
 * contains. It dispatches locale events on the JavaFX Application Thread when
 * {@link #setBundleLocale(Locale)} is called. All unowned floating-window use cases are covered
 * by this single class; it can be subclassed to add locale-specific content.</p>
 *
 * <p>{@link #updateLocaleSpecificValues()} is a no-op in this base class because {@code Popup}
 * has no title or icon images of its own. Subclasses may override it to apply locale-specific
 * content to any nodes they place inside the popup.</p>
 */
public class LocalizablePopup extends Popup implements Localizable
{
    static
    {
        FXModuleRegistrar.ensureRegistered();
    }

    /**
     * Returns a LocalizablePopup.
     *
     * @return A LocalizablePopup ready to be shown.
     */
    public static LocalizablePopup create()
    {
        LocalizablePopup popup = new LocalizablePopup();
        popup.updateLocaleSpecificValues();
        return popup;
    }

    /**
     * Constructs a LocalizablePopup.
     */
    protected LocalizablePopup()
    {
    }

    /**
     * Updates locale-specific properties.
     *
     * <p>Popup has no localizable properties of its own; override in subclasses to apply
     * locale-specific content.</p>
     */
    protected void updateLocaleSpecificValues()
    {
        // Popup has no localizable properties of its own; override in subclasses to apply locale-specific content.
    }

    /**
     * A delegate for Localizable functionality.
     */
    private final LocalizationDelegate loc = new LocalizationDelegate(this);

    /**
     * Get the Locale for ResourceBundles provided by this object.
     *
     * @return The Locale for ResourceBundles provided by this object.
     */
    @Override
    public Locale getBundleLocale()
    {
        return loc.getBundleLocale();
    }

    /**
     * Set the Locale for ResourceBundles provided by this object.
     * Fires a LocaleEvent to all registered listeners, then updates this popup's own
     * locale-specific properties on the JavaFX Application Thread.
     *
     * @param locale The Locale for ResourceBundles provided by this object.
     */
    @Override
    public void setBundleLocale(Locale locale)
    {
        loc.setBundleLocale(locale);
        Platform.runLater(this::updateLocaleSpecificValues);
    }

    /**
     * The available Locales for this object.
     *
     * @return An array of the available Locales for this object.
     */
    @Override
    public Locale[] getAvailableLocales()
    {
        return loc.getAvailableLocales();
    }

    /**
     * Returns the ResourceBundle for the Locale that is currently set for this object.
     *
     * @return The ResourceBundle for the Locale that is currently set for this object.
     * @throws NoCallbackRegisteredForModuleException if a getBundle callback has not been registered
     *         for the module.
     */
    @Override
    public ResourceBundle getResourceBundle() throws NoCallbackRegisteredForModuleException
    {
        return loc.getResourceBundle();
    }

    /**
     * Registers a listener to receive LocaleEvents when the bundle locale changes.
     *
     * @param listener The listener that will receive LocaleEvents.
     */
    @Override
    public void addLocaleEventListener(LocaleEventListener listener)
    {
        loc.addLocaleEventListener(listener);
    }

    /**
     * Unregisters a listener from receiving LocaleEvents.
     *
     * @param listener The listener to remove.
     */
    @Override
    public void removeLocaleEventListener(LocaleEventListener listener)
    {
        loc.removeLocaleEventListener(listener);
    }
}
