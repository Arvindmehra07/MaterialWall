package com.example.materialwall.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.example.materialwall.data.Util.JsonParser
import com.example.materialwall.domain.model.Image
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
) {
    @TypeConverter
    fun fromImagesJson(json: String): Image? {
        return jsonParser.fromJson<Image>(
            json,
            object : TypeToken<Image>() {}.type
        )
    }

    @TypeConverter
    fun toImagesJson(image: Image): String {
        return jsonParser.toJson(
            image,
            object : TypeToken<Image>() {}.type
        ) ?: "[]"
    }
}