package com.iflytek.cyber.evs.sdk.model

import android.os.Parcelable
import com.alibaba.fastjson.annotation.JSONField
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageMetaData(
    val height: Int,
    val reverse: Boolean? = null,
    val tokenization: Boolean? = null,
    val dictionary: Boolean? = null,
    @JSONField(name = "dict_type") val dictType: String? = null,
    val resources: Boolean? = null,
    @JSONField(name = "image_debug") val imageDebug: Boolean? = null,
    val debug: Boolean? = null,
    @JSONField(name = "searchtopic_ability") val searchtopicAbility: String? = null,
    @JSONField(name = "searchtopic_service") val searchtopicService: String? = null,
    @JSONField(name = "searchtopic_resolutionratio") val searchtopicResolutionratio: String? = null
) : Parcelable