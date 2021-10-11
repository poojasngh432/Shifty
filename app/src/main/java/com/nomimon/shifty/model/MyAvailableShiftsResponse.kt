package com.nomimon.shifty.model

data class MyAvailableShiftsResponse(
    val availableShifts: List<AvailableShift>?,
    val notifications: Notifications,
    val scheduledShifts: List<ScheduledShift>
)