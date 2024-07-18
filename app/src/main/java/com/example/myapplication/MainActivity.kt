package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedClock()
                }
            }
        }
    }
}

@Composable
fun AnimatedClock(modifier: Modifier = Modifier) {
    var wavePhase by remember { mutableStateOf(0f) }
    var amplitude by remember { mutableStateOf(1f) }
    var circleColor by remember { mutableStateOf(Color.Gray) }
    var isBlinking by remember { mutableStateOf(false) }
    val beatRadius = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Simulate music vibes and color changes
        while (true) {
            wavePhase += 5f // Adjust speed of wave animation
            amplitude = Random.nextFloat().coerceIn(0.5f, 1.5f)
            circleColor = if (isBlinking) {
                Color(
                    red = Random.nextFloat().coerceIn(0.5f, 1.0f),
                    green = Random.nextFloat().coerceIn(0.5f, 1.0f),
                    blue = Random.nextFloat().coerceIn(0.5f, 1.0f)
                )
            } else {
                Color.Gray // Default color when not blinking
            }
            delay(200) // Adjust delay as needed
        }
    }

    LaunchedEffect(Unit) {
        // Blink animation effect synchronized with music
        while (true) {
            isBlinking = true
            delay(100) // Blink duration
            isBlinking = false
            delay(400) // Time between blinks
        }
    }

    LaunchedEffect(Unit) {
        // Beat animation effect
        beatRadius.animateTo(
            targetValue = 30f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 4

        // Draw black background
        drawRect(
            color = Color.Black,
            topLeft = Offset.Zero,
            size = Size(size.width, size.height)
        )

        // Number of wave segments
        val numSegments = 360
        val segmentLength = 2f

        // Draw dynamic waves around the circle's border
        val path = Path()

        for (i in 0 until numSegments) {
            val angle = i * segmentLength + wavePhase
            val waveOffset = radius * 0.1f * sin(angle * PI / 180f) * amplitude

            val x = (centerX + (radius + waveOffset) * cos(angle * PI / 180f)).toFloat()
            val y = (centerY + (radius + waveOffset) * sin(angle * PI / 180f)).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        path.close()

        drawPath(
            path = path,
            color = circleColor.copy(alpha = 0.8f),
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw needles (fixed angles)
        val angleRedNeedle = 120f * PI / 180f
        val angleGreenNeedle = 100f * PI / 180f

        drawLine(
            color = Color.Red,
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + (radius * 0.5 * cos(angleRedNeedle)).toFloat(),
                centerY + (radius * 0.5 * sin(angleRedNeedle)).toFloat()
            ),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color.Green,
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + (radius * 0.8 * cos(angleGreenNeedle)).toFloat(),
                centerY + (radius * 0.8 * sin(angleGreenNeedle)).toFloat()
            ),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        AnimatedClock()
    }
}
