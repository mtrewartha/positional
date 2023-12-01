package io.trewartha.positional.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavBackStackEntry
import kotlin.math.roundToInt

fun bottomNavEnterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    { fadeIn() }

fun bottomNavExitTransition(
    helpRoute: String? = null
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    {
        if (targetState.destination.route == helpRoute) {
            fadeOut() + slideOutOfContainer(
                End,
                targetOffset = { -(it * OFFSET_FACTOR).roundToInt() })
        } else {
            fadeOut()
        }
    }

fun bottomNavPopEnterTransition(
    helpRoute: String? = null
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    if (initialState.destination.route == helpRoute) {
        fadeIn() + slideIntoContainer(
            End,
            initialOffset = { (it * OFFSET_FACTOR).roundToInt() })
    } else {
        fadeIn()
    }
}

fun bottomNavPopExitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    { fadeOut() }

fun defaultEnterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    { fadeIn() + slideIntoContainer(Start, initialOffset = { (it * OFFSET_FACTOR).roundToInt() }) }

fun defaultExitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    { fadeOut() + slideOutOfContainer(Start, targetOffset = { (it * OFFSET_FACTOR).roundToInt() }) }

fun defaultPopEnterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    { fadeIn() + slideIntoContainer(End, initialOffset = { (it * OFFSET_FACTOR).roundToInt() }) }

fun defaultPopExitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    { fadeOut() + slideOutOfContainer(End, targetOffset = { (it * OFFSET_FACTOR).roundToInt() }) }

private const val OFFSET_FACTOR = 0.4f
