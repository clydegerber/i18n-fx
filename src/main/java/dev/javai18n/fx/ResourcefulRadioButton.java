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
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import dev.javai18n.core.Localizable;
import dev.javai18n.core.Localizable.LocaleEventListener;
import dev.javai18n.core.Resource;
import dev.javai18n.core.Resourceful;
import static dev.javai18n.fx.FXLogger.FX_LOGGER;

/**
 * A RadioButton that supports localizing the name (id), style, text, graphic, tooltip,
 * accessible text, accessible help, and accessible role description.
 */
public class ResourcefulRadioButton extends RadioButton implements Resourceful, LocaleEventListener
{
    static
    {
        FXModuleRegistrar.ensureRegistered();
    }

    /**
     * Construct a ResourcefulRadioButton with the specified Resource.
     * @param resource A Resource containing a ButtonBasePropertyBundle.
     * @return A ResourcefulRadioButton with updated Locale-specific values that is registered
     *         to listen to LocaleEvents generated from the specified Resource's source.
     */
    public static ResourcefulRadioButton create(Resource resource)
    {
        ResourcefulRadioButton radioButton = new ResourcefulRadioButton(resource);
        radioButton.initialize();
        return radioButton;
    }

    private Locale locale = Locale.getDefault();
    private final FXResourcefulDelegate delegate;

    /**
     * Constructs a RadioButton bound to the given resource. Use {@link #create(Resource)}
     * for an initialized instance.
     * @param resource The resource identifying the locale source and bundle key.
     */
    protected ResourcefulRadioButton(Resource resource)
    {
        this.delegate = new FXResourcefulDelegate(resource, this::setLocale, this::updateLocaleSpecificValues);
    }

    /**
     * No-arg constructor for FXML instantiation.
     * Call {@link #initialize(Resource)} from the FXML controller before use.
     */
    public ResourcefulRadioButton()
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

    /**
     * Registers this component as a locale-event listener on its resource source and
     * applies the initial locale-specific values from the resource bundle.
     */
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

    /**
     * Sets the locale used for resource lookups on this component. Called by the delegate
     * when a locale change event is processed.
     * @param locale The new locale.
     */
    protected void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * Returns the current locale of this component, used for resource lookups.
     * @return the current locale.
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Applies locale-specific values from the associated resource bundle to this component.
     */
    protected void updateLocaleSpecificValues()
    {
        try
        {
            ButtonBasePropertyBundle props = (ButtonBasePropertyBundle) getResource().getObject();
            String name = props.getName(); if (null != name) setId(name);
            String style = props.getStyle(); if (null != style) setStyle(style);
            String text = props.getText(); if (null != text) setText(text);
            Image graphic = props.getGraphic();
            if (graphic != null)
            {
                javafx.scene.Node current = getGraphic();
                if (!(current instanceof ImageView iv) || iv.getImage() != graphic)
                    setGraphic(new ImageView(graphic));
            }
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
            FX_LOGGER.log(System.Logger.Level.WARNING, "missing.resource.for.radio.button", ex.getMessage(), ex);
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
