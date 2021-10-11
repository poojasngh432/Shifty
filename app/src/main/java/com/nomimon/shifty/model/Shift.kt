package com.nomimon.shifty.model

data class Shift(
    val bonus: Int,
    val bonusType: String,
    val canBeConfirmed: Boolean,
    val canBeDropped: Boolean,
    val canBeDroppedLate: Boolean,
    val deliveryZoneId: String,
    val earlyCheckoutRequested: Boolean,
    val id: String,
    val isConfirmed: Boolean,
    val location: String,
    val shiftTime: ShiftTimeX,
    val shiftType: String,
    val status: String,
    val vehicle: String
)