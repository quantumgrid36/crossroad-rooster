package com.Gugcegu3.Rogzal.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Gugcegu3.Rogzal.R
import com.Gugcegu3.Rogzal.core.di.AppViewModelFactory
import com.Gugcegu3.Rogzal.core.theme.BarnRed
import com.Gugcegu3.Rogzal.core.theme.CreamLight
import com.Gugcegu3.Rogzal.core.theme.CreamPaint
import com.Gugcegu3.Rogzal.core.theme.DuskMid
import com.Gugcegu3.Rogzal.core.theme.SandDeep
import com.Gugcegu3.Rogzal.core.ui.DuskBackground
import com.Gugcegu3.Rogzal.core.ui.DuskOverlays

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    SplashScreen(finished = finished, onFinished = onFinished)
}

@Composable
fun SplashScreen(finished: Boolean, onFinished: () -> Unit) {
    var handled by remember { mutableStateOf(false) }
    LaunchedEffect(finished) {
        if (finished && !handled) {
            handled = true
            onFinished()
        }
    }

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        appeared = true
    }
    val entry by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 220f),
        label = "splashEntry"
    )

    val transition = rememberInfiniteTransition(label = "splashLoop")
    val beam by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "beam"
    )
    val dash by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart),
        label = "dash"
    )
    val glow by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "glow"
    )

    DuskBackground(overlay = DuskOverlays.splash) {
        SplashDecor(beam = beam, dash = dash, modifier = Modifier.fillMaxSize())
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(188.dp)
                    .graphicsLayer {
                        val scale = 0.86f + 0.14f * entry
                        scaleX = scale
                        scaleY = scale
                        alpha = entry
                    }
                    .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = BarnRed)
                    .clip(RoundedCornerShape(28.dp))
                    .background(DuskMid.copy(alpha = 0.72f))
                    .border(BorderStroke(2.dp, SandDeep.copy(alpha = 0.35f)), RoundedCornerShape(28.dp))
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(BarnRed.copy(alpha = glow))
                )
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(132.dp)
                )
            }

            Box(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                color = CreamLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = entry }
            )
            Box(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.app_tagline_short),
                style = MaterialTheme.typography.labelSmall,
                color = SandDeep,
                textAlign = TextAlign.Center
            )

            Box(modifier = Modifier.height(36.dp))

            LinearProgressIndicator(
                color = BarnRed,
                trackColor = SandDeep.copy(alpha = 0.22f),
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .width(180.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Box(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                color = SandDeep.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SplashDecor(beam: Float, dash: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val dashLength = 34f
        val gap = 22f
        val phase = dash * (dashLength + gap)
        drawLine(
            color = CreamPaint.copy(alpha = 0.32f),
            start = Offset(0f, height * 0.72f),
            end = Offset(width, height * 0.72f),
            strokeWidth = 7f,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gap), phase)
        )
        listOf(0f, 0.5f).forEach { offsetPhase ->
            val progress = (beam + offsetPhase) % 1f
            val centerX = width * (progress * 1.4f - 0.2f)
            val alpha = 0.22f * (1f - abs(progress - 0.5f) * 2f).coerceIn(0f, 1f)
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        CreamLight.copy(alpha = alpha),
                        Color.Transparent
                    ),
                    startX = centerX - width * 0.18f,
                    endX = centerX + width * 0.18f
                ),
                topLeft = Offset(centerX - width * 0.18f, height * 0.60f),
                size = Size(width * 0.36f, height * 0.22f)
            )
        }
    }
}

