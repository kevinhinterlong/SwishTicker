package com.hinterlong.kevin.swishticker.service.converters

import androidx.room.TypeConverter
import com.hinterlong.kevin.swishticker.service.data.ActionResult


class ActionResultConverter {

    @TypeConverter
    fun fromInteger(action: Int) = ActionResult.values().asList().first { action == it.code }

    @TypeConverter
    fun toInteger(action: ActionResult) = action.code
}