package edu.uprb.journalinsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import edu.uprb.journalinsight.ui.navigation.AppNav
import edu.uprb.journalinsight.ui.theme.JournalInsightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JournalInsightTheme {
                AppNav()
            }
        }
    }
}