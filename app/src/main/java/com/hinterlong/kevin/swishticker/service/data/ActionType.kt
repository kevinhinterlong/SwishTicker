package com.hinterlong.kevin.swishticker.service.data

enum class ActionType constructor(val code: Int) {
    TWO_POINT(0),
    THREE_POINT(1),
    FREE_THROW(2),
    REBOUND(3),
    BLOCK(4),
    STEAL(5),
    ASSIST(6),
    FOUL(7)
}

fun toPoints(type: ActionType) = when (type) {
    ActionType.FREE_THROW -> 1
    ActionType.TWO_POINT -> 2
    ActionType.THREE_POINT -> 3
    else -> 0
}

fun toPeriodName(period: Long) = if (period < 3) {
    (period + 1).toString()
} else {
    "OT${period - 3}"
}