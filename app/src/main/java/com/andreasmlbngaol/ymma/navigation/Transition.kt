@file:Suppress("unused")

package com.andreasmlbngaol.ymma.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.ui.NavDisplay

fun slideInFromBottom(animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)) = slideInVertically(
    initialOffsetY = { it },
    animationSpec = animationSpec
)

fun slideOutFromTop(animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)) = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = animationSpec
)

fun slideInFromLeft(animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)) = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = animationSpec
)

fun slideInFromRight(animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)) = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = animationSpec
)

fun slideOutFromRight(animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)) = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = animationSpec
)

fun slideFromBottomTransition(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = NavDisplay.transitionSpec {
    slideInFromBottom(animationSpec) togetherWith ExitTransition.KeepUntilTransitionsFinished
}

fun slideFromTopPopTransition(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = NavDisplay.popTransitionSpec {
    EnterTransition.None togetherWith slideOutFromTop(animationSpec)
}

fun slideFromTopPredictivePopTransition(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = NavDisplay.predictivePopTransitionSpec {
    // Slide old content down, revealing the new content in place underneath
    EnterTransition.None togetherWith slideOutFromTop(animationSpec)
}

fun slideInFromBottomAndOutFromTop(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = slideFromBottomTransition(animationSpec) + slideFromTopPopTransition(animationSpec) + slideFromTopPredictivePopTransition(animationSpec)

fun slideFromLeftTransition(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = NavDisplay.transitionSpec {
    slideInFromLeft(animationSpec) togetherWith ExitTransition.KeepUntilTransitionsFinished
}

fun slideFromRightTransition(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = NavDisplay.transitionSpec {
    slideInFromRight(animationSpec) togetherWith ExitTransition.KeepUntilTransitionsFinished
}

fun slideFromRightPopTransition(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = NavDisplay.popTransitionSpec {
    EnterTransition.None togetherWith slideOutFromRight(animationSpec)
}

fun slideFromRightPredictivePopTransition(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = NavDisplay.predictivePopTransitionSpec {
    EnterTransition.None togetherWith slideOutFromRight(animationSpec)
}

fun slideInFromLeftAndOutFromTop(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = slideFromLeftTransition(animationSpec) + slideFromTopPopTransition(animationSpec) + slideFromTopPredictivePopTransition(animationSpec)

fun slideInFromRightAndOutFromTop(
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(500)
) = slideFromRightTransition(animationSpec) + slideFromTopPopTransition(animationSpec) + slideFromTopPredictivePopTransition(animationSpec)