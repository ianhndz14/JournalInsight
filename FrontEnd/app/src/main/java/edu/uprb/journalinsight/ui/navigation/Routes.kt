package edu.uprb.journalinsight.ui.navigation

object Routes {
    const val LOGIN           = "login"
    const val REGISTER        = "register"
    const val PATIENT_HOME    = "patient_home"
    const val NEW_ENTRY       = "new_entry"
    const val ENTRY_DETAIL    = "entry_detail"      // args: entryId (Long)
    const val PROFESSIONAL_HOME  = "professional_home"
    const val PROFESSIONAL_DAY   = "professional_day"        // args: patientId, date
    const val PROF_ENTRY_DETAIL  = "prof_entry_detail"       // args: entryId (Long)
    const val REDEEM_CODE        = "redeem_code"
}