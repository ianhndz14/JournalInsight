package edu.uprb.journalinsight.data

object SessionManager {
    var phoneNumber: String = ""
    var accountType: String = ""   // "P" = Patient, "R" = Professional
    var firstName: String   = ""

    val isPatient: Boolean get() = accountType == "P"

    fun clear() {
        phoneNumber = ""
        accountType = ""
        firstName   = ""
    }
}
