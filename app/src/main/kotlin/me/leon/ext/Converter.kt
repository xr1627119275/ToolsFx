package me.leon.ext

import java.nio.charset.Charset
import me.leon.encode.base.BYTE_BITS
import me.leon.encode.base.BYTE_MASK
import tornadofx.*

const val HEX_RADIX = 16
const val DECIMAL_RADIX = 10
const val OCTAL_RADIX = 8

/** 16进制编解码 */
fun ByteArray.toHex() = hex

fun String.toHex() = toByteArray().toHex()

fun String.hex2String(charset: String = "UTF-8") = String(hex2ByteArray(), Charset.forName(charset))

fun String.hex2ByteArray() = chunked(2).map { it.toInt(HEX_RADIX).toByte() }.toByteArray()

fun ByteArray.toBinaryString() =
    joinToString("") {
        with((it.toInt() and BYTE_MASK).toString(2)) {
            this.takeIf { it.length == BYTE_BITS } ?: ("0".repeat(BYTE_BITS - this.length) + this)
        }
    }

/** 二进制编解码 */
fun String.toBinaryString() = toByteArray().toBinaryString()

fun String.binary2Ascii() = String(binary2ByteArray(), Charsets.UTF_8)

fun String.binary2ByteArray() =
    toList().chunked(BYTE_BITS).map { it.joinToString("").toInt(2).toByte() }.toByteArray()

/** unicode编解码 */
fun String.toUnicodeString() =
    fold(StringBuilder()) { acc, c -> acc.append("\\u").append(c.code.toString(HEX_RADIX)) }.toString()

/** js hex 编解码 \x61 */
fun String.toJsHexEncodeString() =
    fold(StringBuilder()) { acc, c -> acc.append("\\x").append(c.code.toString(HEX_RADIX)) }
        .toString()

/** js hex 编解码 \x61 */
fun String.jsHexDecodeString() =
    split("(?i)\\\\x".toRegex())
        .filterIndexed { index, _ -> index != 0 }
        .fold(StringBuilder()) { acc, c -> acc.append(c.toInt(HEX_RADIX).toChar()) }
        .toString()

/** js hex 编解码 \x61 */
fun String.jsOctalDecodeString() =
    split("(?i)\\\\".toRegex())
        .filterIndexed { index, _ -> index != 0 }
        .fold(StringBuilder()) { acc, c -> acc.append(c.toInt(OCTAL_RADIX).toChar()) }
        .toString()

/** js hex 编解码编解码 \141 */
fun String.toJsOctalEncodeString() =
    fold(StringBuilder()) { acc, c -> acc.append("\\").append(c.code.toString(OCTAL_RADIX)) }
        .toString()

fun String.unicode2String() =
    if (contains("&#", true))
        "(?i)&#x([0-9a-f]+);|&#(\\d+);"
            .toRegex()
            .findAll(this)
            .map {
                it.groupValues[1].ifEmpty { it.groupValues[2] } to
                    if (it.groupValues[0].contains("x", true)) HEX_RADIX else DECIMAL_RADIX
            }
            .fold(StringBuilder()) { acc, (c, radix) -> acc.append(c.toInt(radix).toChar()) }
            .toString()
    else
        split("(?i)\\\\u\\+?".toRegex())
            .filterIndexed { index, _ -> index != 0 }
            .fold(StringBuilder()) { acc, c -> acc.append(c.toInt(HEX_RADIX).toChar()) }
            .toString()

fun String.unicodeMix2String() =
    StringBuilder(this).replace("(?i:\\\\u\\+?[0-9a-zA-Z]{1,5})+".toRegex()) {
        it.value.unicode2String()
    }
