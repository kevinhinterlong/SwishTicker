package com.hinterlong.kevin.swishticker.service.data

enum class ActionResult constructor(val code: Int) {
    NONE(0),
    SHOT_MISS(1),
    SHOT_HIT(2)
}