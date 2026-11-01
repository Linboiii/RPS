package com.example.rps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class Rps(val emoji: String, val label: String) {
    ROCK("✊", "Rock"),
    PAPER("✋", "Paper"),
    SCISSORS("✌️", "Scissors")
}

enum class RpsState { INITIAL, PICK, REVEAL, FINISHED }
enum class RpsResult(val text: String) { WIN("You Win!"), LOSE("You Lose"), DRAW("Draw") }

class RockPaperScissorsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RockPaperScissorsScreen()
            }
        }
    }
}

@Composable
fun RockPaperScissorsScreen(bestOfN: Int = 5) {
    val winningScore = (bestOfN / 2) + 1

    var gameState by remember { mutableStateOf(RpsState.INITIAL) }
    var playerScore by remember { mutableIntStateOf(0) }
    var cpuScore by remember { mutableIntStateOf(0) }
    var bestScore by remember { mutableIntStateOf(0) }

    var playerChoice by remember { mutableStateOf<Rps?>(null) }
    var cpuChoice by remember { mutableStateOf<Rps?>(null) }
    var roundResult by remember { mutableStateOf<RpsResult?>(null) }
    var shuffledMoves by remember { mutableStateOf(Rps.entries.shuffled()) }

    fun playRound(choice: Rps) {
        playerChoice = choice
        cpuChoice = Rps.entries.random()
        val cpu = cpuChoice!!

        roundResult = when {
            choice == cpu -> RpsResult.DRAW
            (choice == Rps.ROCK && cpu == Rps.SCISSORS) ||
                    (choice == Rps.PAPER && cpu == Rps.ROCK) ||
                    (choice == Rps.SCISSORS && cpu == Rps.PAPER) -> RpsResult.WIN
            else -> RpsResult.LOSE
        }

        when (roundResult) {
            RpsResult.WIN -> playerScore++
            RpsResult.LOSE -> cpuScore++
            RpsResult.DRAW -> {}
            null -> {}
        }

        val diff = playerScore - cpuScore
        if (diff > bestScore) {
            bestScore = diff
        }

        gameState = RpsState.REVEAL
    }

    LaunchedEffect(gameState) {
        if (gameState == RpsState.REVEAL) {
            delay(700)
            if (playerScore >= winningScore || cpuScore >= winningScore) {
                gameState = RpsState.FINISHED
            } else {
                shuffledMoves = Rps.entries.shuffled()
                playerChoice = null
                cpuChoice = null
                roundResult = null
                gameState = RpsState.PICK
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9)),
        contentAlignment = Alignment.Center
    ) {
        when (gameState) {
            RpsState.INITIAL -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("👨 $playerScore - $cpuScore 🤖", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Best of $bestOfN", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(Modifier.height(32.dp))
                    Text("Rock • Paper • Scissors", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = {
                        playerScore = 0
                        cpuScore = 0
                        shuffledMoves = Rps.entries.shuffled()
                        gameState = RpsState.PICK
                    }) {
                        Text("Start")
                    }
                }
            }

            RpsState.PICK -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👨 $playerScore - $cpuScore 🤖", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Best of $bestOfN", fontSize = 12.sp, color = Color.Gray)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pick your move!", fontSize = 16.sp, color = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Text("❓ VS ❓", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        shuffledMoves.forEach { move ->
                            Button(
                                onClick = { playRound(move) },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("${move.emoji} ${move.label}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            RpsState.REVEAL -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👨 $playerScore - $cpuScore 🤖", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Best of $bestOfN", fontSize = 12.sp, color = Color.Gray)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${playerChoice?.emoji ?: "❓"} VS ${cpuChoice?.emoji ?: "❓"}",
                            fontSize = 48.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            roundResult?.text ?: "",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }

            RpsState.FINISHED -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("👨 $playerScore - $cpuScore 🤖", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Best of $bestOfN", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(Modifier.height(32.dp))
                    Text(
                        if (playerScore >= winningScore) "You Win the Match!" else "You Lose the Match",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Best Score: $bestScore", fontSize = 14.sp, color = Color.Gray)
                    Spacer(Modifier.height(32.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = {
                            playerScore = 0
                            cpuScore = 0
                            shuffledMoves = Rps.entries.shuffled()
                            gameState = RpsState.PICK
                        }) {
                            Text("Restart")
                        }
                        Button(
                            onClick = { gameState = RpsState.INITIAL },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Exit", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}