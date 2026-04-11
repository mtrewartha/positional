package io.trewartha.positional

import android.app.Application
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@SingleIn(AppScope::class)
@DependencyGraph(AppScope::class)
public interface AppGraph : MetroAppComponentProviders, ViewModelGraph {

    @DependencyGraph.Factory
    public interface Factory {
        public fun create(@Provides application: Application): AppGraph
    }
}
