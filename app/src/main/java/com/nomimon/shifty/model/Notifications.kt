package com.nomimon.shifty.model

data class Notifications(
    val beforeShift: BeforeShift,
    val confirmShift: ConfirmShift,
    val initialConfirmShift: InitialConfirmShift,
    val lateShift: LateShift
)