package com.hinterlong.kevin.swishticker.service.converters

import androidx.room.TypeConverter
import com.hinterlong.kevin.swishticker.service.data.ActionType


class ActionTypeConverter {

    @TypeConverter
    fun fromInteger(action: Int) = ActionType.values().asList().first { action == it.code }

    @TypeConverter
    fun toInteger(action: ActionType) = action.code
}