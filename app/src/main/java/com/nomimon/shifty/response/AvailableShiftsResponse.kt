package com.nomimon.shifty.response

data class AvailableShiftsResponse(
 val availableShifts: List<Shift>?
)

data class Shift (
    val id: String,
    val location: String,
    val shiftType: String,
    val status: String
)