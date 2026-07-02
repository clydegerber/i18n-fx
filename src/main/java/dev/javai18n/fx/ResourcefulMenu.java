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

import java.util.MissingResourceException;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import dev.javai18n.core.Localizable;
import dev.javai18n.core.Localizable.LocaleEventListener;
import dev.javai18n.core.Resource;
import dev.javai18n.core.Resourceful;
import static dev.javai18n.fx.FXLogger.FX_LOGGER;

/**
 * A Menu that supports localizing the text and graphic.
 */
public class ResourcefulMenu extends Menu implements Resourceful, LocaleEventListener
{
    static
    {
        FXModuleRegistrar.ensureRegistered();
    }

    /**
     * Construct a ResourcefulMenu with the specified Resource.
     *
     * @param resource A Resource containing a MenuItemPropertyBundle.
     * @return A ResourcefulMenu with updated Locale-specific values that is registered
     *         to listen to LocaleEvents generated from the specified Resource's source.
     */
    public static ResourcefulMenu create(Resource resource)
    {
        ResourcefulMenu menu = new ResourcefulMenu(resource);
        menu.initialize();
        return menu;
    }

    private final FXResourcefulDelegate delegate;

    /**
     * Constructs a Menu bound to the given resource. Use {@link #create(Resource)}
     * for an initialized instance.
     * @param resource The resource identifying the locale source and bundle key.
     */
    protected ResourcefulMenu(Resource resource)
    {
        this.delegate = new FXResourcefulDelegate(resource, this::updateLocaleSpecificValues);
    }

    /**
     * No-arg constructor for FXML instantiation.
     * Call {@link #initialize(Resource)} from the FXML controller before use.
     */
    public ResourcefulMenu()
    {
        this.delegate = new FXResourcefulDelegate(this::updateLocaleSpecificValues);
        parentMenuProperty().addListener(new javafx.beans.value.ChangeListener<javafx.scene.control.Menu>()
        {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends javafx.scene.control.Menu> obs,
                                javafx.scene.control.Menu old, javafx.scene.control.Menu menu)
            {
                if (menu != null)
                {
                    if (!delegate.isInitialized())
                        throw new IllegalStateException(
                            getClass().getSimpleName() + " added to Menu before initialize(Resource) was called");
                    parentMenuProperty().removeListener(this);
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
     * Applies locale-specific values from the associated resource bundle to this component.
     */
    protected void updateLocaleSpecificValues()
    {
        try
        {
            MenuItemPropertyBundle props = (MenuItemPropertyBundle) getResource().getObject();
            String text = props.getText(); if (null != text) setText(text);
            Image graphic = props.getGraphic();
            if (graphic != null)
            {
                javafx.scene.Node current = getGraphic();
                if (!(current instanceof ImageView iv) || iv.getImage() != graphic)
                    setGraphic(new ImageView(graphic));
            }
        }
        catch (MissingResourceException | ClassCastException ex)
        {
            FX_LOGGER.log(System.Logger.Level.WARNING, "missing.resource.for.menu", ex.getMessage(), ex);
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
     */
    public void dispose()
    {
        delegate.dispose();
    }
}
