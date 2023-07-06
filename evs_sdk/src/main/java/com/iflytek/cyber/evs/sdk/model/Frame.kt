package com.iflytek.cyber.evs.sdk.model

@Suppress("ArrayInDataClass")
/**
 * 图像帧数据结构
 * @param version 版本
 * @param fuid 图像编号
 * @param type 图像类型
 * @param fmt 图像格式
 * @param xsft X偏移
 * @param ysft Y偏移
 * @param width 图像宽度
 * @param height 图像高度
 * @param depth 像素字长
 * @param reserved 保留字节
 * @param size 图像数据长度
 * @param data 图像数据
 */
data class Frame(
    val version: Byte,
    val fuid: Int,
    val type: Byte,
    val fmt: Byte,
    val xsft: Byte,
    val ysft: Byte,
    val width: Int,
    val height: Int,
    val depth: Byte,
    val reserved: Int,
    val size: Int,
    val data: ByteArray
)
