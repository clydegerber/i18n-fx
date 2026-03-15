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
import java.util.Optional;
import java.util.function.Consumer;
import javafx.application.Platform;
import dev.javai18n.core.Localizable.LocaleEvent;
import dev.javai18n.core.Localizable.LocaleEventListener;
import dev.javai18n.core.Resource;
import dev.javai18n.core.ResourcefulDelegate;

/**
 * A JavaFX-specific wrapper around {@link ResourcefulDelegate} that adds locale-setting,
 * property update callbacks, and JavaFX Application Thread dispatch via {@link Platform#runLater}.
 *
 * <p>This class centralizes the JavaFX-specific orchestration that all {@code Resourceful}
 * JavaFX components share: optionally setting the component's locale, updating locale-specific
 * properties, and ensuring that locale event processing occurs on the JavaFX Application Thread.</p>
 *
 * <p>Four constructors are provided in two groups:
 * <ul>
 *   <li><b>Factory-path constructors</b> accept a {@link Resource} at construction time.
 *       The caller then invokes {@link #initialize()} to complete setup. This is the path
 *       used by the {@code static create(Resource)} factory methods on each component.</li>
 *   <li><b>FXML-path constructors</b> defer the {@link Resource} until
 *       {@link #initialize(Resource)} is called. This supports FXML two-phase initialization
 *       where FXMLLoader instantiates the component via its public no-arg constructor and a
 *       controller subsequently calls {@code initialize(Resource)}.</li>
 * </ul>
 * Within each group, the three-argument form is for {@link javafx.scene.Node} subclasses
 * that expose a {@code setLocale()} method; the two-argument form is for non-Node types
 * (MenuItem, Tab, TableColumn, Tooltip, ContextMenu) that do not.</p>
 */
class FXResourcefulDelegate implements LocaleEventListener
{
    /**
     * The core delegate that handles resource ownership and listener registration.
     * Null when created via an FXML-path constructor until {@link #initialize(Resource)} is called.
     */
    private ResourcefulDelegate delegate;

    /**
     * An optional callback that sets the locale on the owning JavaFX component.
     * Empty for non-Node types that do not expose a setLocale() method.
     */
    private final Optional<Consumer<Locale>> setLocale;

    /**
     * A callback that updates locale-specific properties on the owning JavaFX component.
     */
    private final Runnable updateCallback;

    /**
     * True after {@link #initialize()} or {@link #initialize(Resource)} has been called.
     */
    private boolean initialized;

    // -------------------------------------------------------------------------
    // Factory-path constructors (Resource provided at construction time)
    // -------------------------------------------------------------------------

    /**
     * Construct a FXResourcefulDelegate for a {@link javafx.scene.Node} subclass.
     * Use with {@link #initialize()} via the {@code static create(Resource)} factory.
     *
     * @param resource       The Resource for the owning component.
     * @param setLocale      A callback that sets the locale on the owning component.
     * @param updateCallback A callback that updates locale-specific properties.
     */
    FXResourcefulDelegate(Resource resource, Consumer<Locale> setLocale, Runnable updateCallback)
    {
        this.setLocale = Optional.of(setLocale);
        this.updateCallback = updateCallback;
        this.delegate = new ResourcefulDelegate(resource, this);
    }

    /**
     * Construct a FXResourcefulDelegate for a non-Node type (MenuItem, Tab, TableColumn, Tooltip).
     * Use with {@link #initialize()} via the {@code static create(Resource)} factory.
     *
     * @param resource       The Resource for the owning component.
     * @param updateCallback A callback that updates locale-specific properties.
     */
    FXResourcefulDelegate(Resource resource, Runnable updateCallback)
    {
        this.setLocale = Optional.empty();
        this.updateCallback = updateCallback;
        this.delegate = new ResourcefulDelegate(resource, this);
    }

    // -------------------------------------------------------------------------
    // FXML-path constructors (Resource deferred until initialize(Resource))
    // -------------------------------------------------------------------------

    /**
     * Construct a FXResourcefulDelegate for a {@link javafx.scene.Node} subclass without a
     * {@link Resource}. Use with {@link #initialize(Resource)} from an FXML controller.
     *
     * @param setLocale      A callback that sets the locale on the owning component.
     * @param updateCallback A callback that updates locale-specific properties.
     */
    FXResourcefulDelegate(Consumer<Locale> setLocale, Runnable updateCallback)
    {
        this.setLocale = Optional.of(setLocale);
        this.updateCallback = updateCallback;
    }

    /**
     * Construct a FXResourcefulDelegate for a non-Node type without a {@link Resource}.
     * Use with {@link #initialize(Resource)} from an FXML controller.
     *
     * @param updateCallback A callback that updates locale-specific properties.
     */
    FXResourcefulDelegate(Runnable updateCallback)
    {
        this.setLocale = Optional.empty();
        this.updateCallback = updateCallback;
    }

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------

    /**
     * Initialize the owning component for the factory path.
     * Sets the component's initial locale, applies locale-specific values, and registers
     * this delegate as a {@link LocaleEventListener} on the resource's source.
     * Must be called exactly once after a factory-path constructor.
     *
     * @throws IllegalStateException if called more than once.
     */
    void initialize()
    {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        initialized = true;
        setLocale.ifPresent(sl -> sl.accept(delegate.getResource().getSource().getBundleLocale()));
        updateCallback.run();
        delegate.registerListener();
    }

    /**
     * Initialize the owning component for the FXML path.
     * Creates the internal {@link ResourcefulDelegate} for the given resource, then behaves
     * identically to {@link #initialize()}. Must be called exactly once after an FXML-path
     * constructor, typically from an FXML controller's {@code initialize()} method.
     *
     * @param resource The Resource to associate with the owning component.
     * @throws IllegalStateException if called more than once.
     */
    void initialize(Resource resource)
    {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        delegate = new ResourcefulDelegate(resource, this);
        initialize();
    }

    /**
     * Return whether this delegate has been initialized.
     *
     * @return true if {@link #initialize()} or {@link #initialize(Resource)} has been called.
     */
    boolean isInitialized()
    {
        return initialized;
    }

    // -------------------------------------------------------------------------
    // LocaleEventListener
    // -------------------------------------------------------------------------

    /**
     * Process a LocaleEvent by scheduling a locale update and property refresh on the JavaFX
     * Application Thread. No-op if called before initialization (safety net only; cannot occur
     * in normal use since this delegate is not registered until {@link #initialize()} completes).
     *
     * @param event The LocaleEvent that has been raised.
     */
    @Override
    public void processLocaleEvent(LocaleEvent event)
    {
        if (!initialized)
            return;
        Platform.runLater(() ->
        {
            setLocale.ifPresent(sl -> sl.accept(event.getLocalizableSource().getBundleLocale()));
            updateCallback.run();
        });
    }

    // -------------------------------------------------------------------------
    // Resource access
    // -------------------------------------------------------------------------

    /**
     * Get the Resource holding locale-specific values for the owning component.
     *
     * @return The Resource holding locale-specific values for the owning component.
     * @throws IllegalStateException if called before initialization.
     */
    Resource getResource()
    {
        requireInitialized();
        return delegate.getResource();
    }

    /**
     * Set the Resource holding locale-specific values for the owning component.
     * Delegates to the core {@link ResourcefulDelegate} for listener management,
     * then schedules a property refresh on the JavaFX Application Thread.
     *
     * @param resource The new Resource holding locale-specific values for the owning component.
     * @throws IllegalStateException if called before initialization.
     */
    void setResource(Resource resource)
    {
        requireInitialized();
        delegate.setResource(resource);
        Platform.runLater(() ->
        {
            setLocale.ifPresent(sl -> sl.accept(delegate.getResource().getSource().getBundleLocale()));
            updateCallback.run();
        });
    }

    /**
     * Unregister this delegate as a {@link dev.javai18n.core.Localizable.LocaleEventListener} on
     * the resource's source. No-op if called before initialization. Call this when the owning
     * component is being permanently discarded so that it is no longer retained by the source's
     * listener list.
     */
    void dispose()
    {
        if (!initialized)
            return;
        delegate.getResource().getSource().removeLocaleEventListener(delegate);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private void requireInitialized()
    {
        if (!initialized)
            throw new IllegalStateException(
                "Not yet initialized \u2014 call initialize(Resource) before use");
    }
}
