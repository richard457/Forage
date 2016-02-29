package io.github.plastix.forage.ui.map;

import dagger.Subcomponent;
import io.github.plastix.forage.ui.ActivityScope;

/**
 * Dagger component to inject all required dependencies into {@link MapFragment}.
 */
@ActivityScope
@Subcomponent(
        modules = {
                MapModule.class
        }
)
public interface MapComponent {
    void injectTo(MapFragment mapFragment);
}
