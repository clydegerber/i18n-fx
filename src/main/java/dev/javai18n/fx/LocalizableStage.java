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

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import dev.javai18n.core.Localizable;
import dev.javai18n.core.LocalizationDelegate;
import dev.javai18n.core.NoCallbackRegisteredForModuleException;
import static dev.javai18n.fx.FXLogger.FX_LOGGER;

/**
 * A LocalizableStage class that extends {@link Stage} and implements {@link Localizable}.
 *
 * <p>LocalizableStage serves as the locale-event source for all Resourceful components it contains.
 * It dispatches locale events on the JavaFX Application Thread when {@link #setBundleLocale(Locale)}
 * is called. All top-level window use cases are covered by this single class:</p>
 * <ul>
 *   <li>Standard application window: {@code LocalizableStage.create()}</li>
 *   <li>Decorated window with specific style: {@code LocalizableStage.create(StageStyle)}</li>
 *   <li>Modal dialog: call {@code initModality(Modality.APPLICATION_MODAL)} after creation</li>
 *   <li>Undecorated window: call {@code LocalizableStage.create(StageStyle.UNDECORATED)}</li>
 * </ul>
 */
public class LocalizableStage extends Stage implements Localizable
{
    static
    {
        FXModuleRegistrar.ensureRegistered();
    }

    /**
     * The bundle key used to retrieve the Stage's localizable properties.
     */
    public static final String STAGE_PROPERTIES_KEY = "stageProperties";

    /**
     * Returns a LocalizableStage with the default decorated style.
     *
     * <p>The resource bundle associated with the concrete class must contain an entry with the
     * key {@value #STAGE_PROPERTIES_KEY} whose value is a {@link StagePropertyBundle}.  If the
     * key is absent a {@link java.util.MissingResourceException} is logged at WARNING level and
     * swallowed; if it is present but has the wrong type a {@link ClassCastException} is logged
     * and swallowed, so partial initialization always succeeds.</p>
     *
     * @return A LocalizableStage with locale-sensitive attributes updated from an associated ResourceBundle.
     */
    public static LocalizableStage create()
    {
        LocalizableStage stage = new LocalizableStage();
        stage.updateLocaleSpecificValues();
        return stage;
    }

    /**
     * Returns a LocalizableStage with the specified style.
     *
     * @param style The style for this stage.
     * @return A LocalizableStage with locale-sensitive attributes updated from an associated ResourceBundle.
     */
    public static LocalizableStage create(StageStyle style)
    {
        LocalizableStage stage = new LocalizableStage(style);
        stage.updateLocaleSpecificValues();
        return stage;
    }

    /**
     * Constructs a LocalizableStage with the default decorated style.
     */
    protected LocalizableStage()
    {
    }

    /**
     * Constructs a LocalizableStage with the specified style.
     *
     * @param style The style for this stage.
     */
    protected LocalizableStage(StageStyle style)
    {
        super(style);
    }

    /**
     * Updates locale-specific properties (title and icon images) by reading a
     * {@link StagePropertyBundle} from the resource bundle under the key {@value #STAGE_PROPERTIES_KEY}.
     *
     * <p>Called by the {@code create} factory methods at construction time and by
     * {@link #setBundleLocale(Locale)} on each locale change. Subclasses may override this method
     * to apply additional locale-specific values, and should call
     * {@code super.updateLocaleSpecificValues()} first. A {@link MissingResourceException}
     * (e.g. if the key is absent from the bundle) is logged at WARNING level and silently
     * absorbed so that partial initialization still succeeds.</p>
     */
    protected void updateLocaleSpecificValues()
    {
        try
        {
            StagePropertyBundle props = (StagePropertyBundle) getResourceBundle().getObject(STAGE_PROPERTIES_KEY);
            String title = props.getTitle();
            if (null != title) setTitle(title);
            List<javafx.scene.image.Image> icons = props.getIcons();
            if (!icons.isEmpty()) getIcons().setAll(icons);
        }
        catch (MissingResourceException | ClassCastException e)
        {
            FX_LOGGER.log(System.Logger.Level.WARNING, "missing.resource.for.stage", e.getMessage(), e);
        }
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
     * Fires a LocaleEvent to all registered listeners, then updates this stage's own
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
