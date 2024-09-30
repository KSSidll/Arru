package com.kssidll.arru.helper

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.window.core.layout.WindowWidthSizeClass

object BetterNavigationSuiteScaffoldDefaults {
    fun calculateFromAdaptiveInfo(adaptiveInfo: WindowAdaptiveInfo): NavigationSuiteType {
        return with(adaptiveInfo) {
            if (
                windowPosture.isTabletop ||
                windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
            ) {
                NavigationSuiteType.NavigationBar
            } else if (
                windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
                windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
            ) {
                NavigationSuiteType.NavigationRail
            } else {
                NavigationSuiteType.NavigationBar
            }
        }
    }
}
