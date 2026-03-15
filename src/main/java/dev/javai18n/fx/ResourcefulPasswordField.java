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
import java.util.MissingResourceException;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;
import dev.javai18n.core.Localizable;
import dev.javai18n.core.Localizable.LocaleEventListener;
import dev.javai18n.core.Resource;
import dev.javai18n.core.Resourceful;
import static dev.javai18n.fx.FXLogger.FX_LOGGER;

/**
 * A PasswordField that supports localizing the name (id), style, prompt text, tooltip,
 * accessible text, accessible help, and accessible role description.
 */
public class ResourcefulPasswordField extends PasswordField implements Resourceful, LocaleEventListener
{
    static
    {
        FXModuleRegistrar.ensureRegistered();
    }

    /**
     * Construct a ResourcefulPasswordField with the specified Resource.
     * @param resource A Resource containing a TextInputPropertyBundle.
     * @return A ResourcefulPasswordField with updated Locale-specific values that is registered
     *         to listen to LocaleEvents generated from the specified Resource's source.
     */
    public static ResourcefulPasswordField create(Resource resource)
    {
        ResourcefulPasswordField passwordField = new ResourcefulPasswordField(resource);
        passwordField.initialize();
        return passwordField;
    }

    private Locale locale = Locale.getDefault();
    private final FXResourcefulDelegate delegate;

    protected ResourcefulPasswordField(Resource resource)
    {
        this.delegate = new FXResourcefulDelegate(resource, this::setLocale, this::updateLocaleSpecificValues);
    }

    /**
     * No-arg constructor for FXML instantiation.
     * Call {@link #initialize(Resource)} from the FXML controller before use.
     */
    public ResourcefulPasswordField()
    {
        this.delegate = new FXResourcefulDelegate(this::setLocale, this::updateLocaleSpecificValues);
        sceneProperty().addListener(new javafx.beans.value.ChangeListener<javafx.scene.Scene>()
        {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends javafx.scene.Scene> obs,
                                javafx.scene.Scene old, javafx.scene.Scene scene)
            {
                if (scene != null)
                {
                    if (!delegate.isInitialized())
                        throw new IllegalStateException(
                            getClass().getSimpleName() + " added to scene before initialize(Resource) was called");
                    sceneProperty().removeListener(this);
                }
            }
        });
    }

    protected final void initialize()
    {
        delegate.initialize();
    }

    /**
     * Initialize this component for the FXML path.
     * Associates the given {@link Resource} and applies its locale-specific values.
     * Must be called exactly once from an FXML controller after FXMLLoader instantiation.
     *
     * @param resource The Resource to associate with this component.
     * @throws IllegalStateException if called more than once.
     */
    public void initialize(Resource resource)
    {
        delegate.initialize(resource);
    }

    protected void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public Locale getLocale()
    {
        return locale;
    }

    protected void updateLocaleSpecificValues()
    {
        try
        {
            TextInputPropertyBundle props = (TextInputPropertyBundle) getResource().getObject();
            String name = props.getName(); if (null != name) setId(name);
            String style = props.getStyle(); if (null != style) setStyle(style);
            String promptText = props.getPromptText(); if (null != promptText) setPromptText(promptText);
            String tooltipText = props.getTooltipText();
            if (tooltipText != null)
            {
                Tooltip current = getTooltip();
                if (current == null || !tooltipText.equals(current.getText()))
                    setTooltip(new Tooltip(tooltipText));
            }
            else if (getTooltip() != null)
            {
                setTooltip(null);
            }
            String at = props.getAccessibleText(); if (null != at) setAccessibleText(at);
            String ah = props.getAccessibleHelp(); if (null != ah) setAccessibleHelp(ah);
            String ard = props.getAccessibleRoleDescription(); if (null != ard) setAccessibleRoleDescription(ard);
        }
        catch (MissingResourceException | ClassCastException ex)
        {
            FX_LOGGER.log(System.Logger.Level.WARNING, "missing.resource.for.password.field", ex.getMessage(), ex);
        }
    }

    @Override
    public void processLocaleEvent(Localizable.LocaleEvent event)
    {
        delegate.processLocaleEvent(event);
    }

    @Override
    public Resource getResource()
    {
        return delegate.getResource();
    }

    @Override
    public void setResource(Resource resource)
    {
        delegate.setResource(resource);
    }

    /**
     * Unregister this component as a locale-event listener on its resource's source.
     * Call this when the component is permanently removed from use to prevent it from
     * being retained in the source's listener list.
     */
    public void dispose()
    {
        delegate.dispose();
    }
}
