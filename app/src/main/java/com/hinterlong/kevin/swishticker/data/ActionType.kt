package com.hinterlong.kevin.swishticker.data

enum class ActionType constructor(val code:Int) {
    TWO_POINT(0),
    THREE_POINT(1),
    FREE_THROW(2),
    REBOUND(3),
    BLOCK(4),
    STEAL(5),
    ASSIST(6),
    FOUL(7)
}