package edu.uprb.journalinsight.ui.navigation

import androidx.compose.runtime.Composable
import edu.uprb.journalinsight.data.SessionManager
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.uprb.journalinsight.ui.screens.*

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onPatient      = { nav.navigate(Routes.PATIENT_HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onProfessional = { nav.navigate(Routes.PROFESSIONAL_HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onRegister     = { nav.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onPatient      = { nav.navigate(Routes.PATIENT_HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onProfessional = { nav.navigate(Routes.PROFESSIONAL_HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onBack         = { nav.popBackStack() }
            )
        }

        composable(Routes.PATIENT_HOME) {
            PatientHomeScreen(
                onNewEntry         = { nav.navigate(Routes.NEW_ENTRY) },
                onOpenEntry        = { entryId -> nav.navigate("${Routes.ENTRY_DETAIL}/$entryId") },
                onLinkProfessional = { nav.navigate(Routes.REDEEM_CODE) },
                onSignOut          = {
                    SessionManager.clear()
                    nav.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.NEW_ENTRY) {
            NewEntryScreen(onDone = { nav.popBackStack() })
        }

        composable(
            route     = "${Routes.ENTRY_DETAIL}/{entryId}",
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: return@composable
            EntryDetailScreen(
                entryId   = entryId,
                onBack    = { nav.popBackStack() },
                onDeleted = { nav.popBackStack() }
            )
        }

        composable(Routes.PROFESSIONAL_HOME) {
            ProfessionalHomeScreen(
                onViewDay = { patientId, date ->
                    nav.navigate("${Routes.PROFESSIONAL_DAY}/$patientId/$date")
                },
                onSignOut = {
                    SessionManager.clear()
                    nav.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.REDEEM_CODE) {
            RedeemCodeScreen(onBack = { nav.popBackStack() })
        }

        composable(
            route     = "${Routes.PROFESSIONAL_DAY}/{patientId}/{date}",
            arguments = listOf(
                navArgument("patientId") { type = NavType.StringType },
                navArgument("date")      { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: return@composable
            val date      = backStackEntry.arguments?.getString("date")      ?: return@composable
            ProfessionalDayViewScreen(
                patientId    = patientId,
                dateIso      = date,
                onBack       = { nav.popBackStack() },
                onEntryClick = { entryId -> nav.navigate("${Routes.PROF_ENTRY_DETAIL}/$entryId") }
            )
        }

        composable(
            route     = "${Routes.PROF_ENTRY_DETAIL}/{entryId}",
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: return@composable
            EntryDetailScreen(
                entryId   = entryId,
                canDelete = false,
                onBack    = { nav.popBackStack() }
            )
        }
    }
}
