package me.leon.misc

import me.leon.ext.readResourceText

/**
 * @author Leon
 * @since 2023-08-01 11:09
 * @email deadogone@gmail.com
 */
object CodeMapping {

    val HTTP_CODE_DICT =
        readResourceText("/mapping/httpcode.txt")
            .lines()
            .map { it.split("\t") }
            .fold(mutableMapOf<String, String>()) { acc, p -> acc.also { acc[p.first()] = p[1] } }
            .toMap()

    /** data from https://www.iana.org/assignments/media-types/media-types.xhtml */
    val MIME_DICT =
        readResourceText("/mapping/mime.txt")
            .lines()
            .map { it.split("\t") }
            .fold(mutableMapOf<String, String>()) { acc, p ->
                acc.also { acc[p.first().lowercase()] = p[1] }
            }
            .toMap()

    /** refer https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers */
    val PORT_DICT =
        readResourceText("/mapping/ports.txt")
            .lines()
            .map { it.split("\t") }
            .fold(mutableMapOf<String, String>()) { acc, p ->
                acc.also {
                    val range = p.first().split("-")
                    val startPort = range.first().toInt()
                    val ports =
                        if (range.size == 2) {
                            startPort..range[1].toInt()
                        } else {
                            startPort..startPort
                        }
                    for (port in ports) {
                        acc[port.toString()] =
                            if (acc[port.toString()] == null) {
                                p[1]
                            } else {
                                acc[port.toString()] + "/" + p[1]
                            }
                    }
                }
            }
            .toMap()

    /** data from https://zh.wikipedia.org/wiki/%E4%B8%AD%E5%8D%8E%E4%BA%BA%E6%B0%91%E5%85%B1%E5%92%8C%E5%9B%BD%E6%B0%91%E7%94%A8%E6%9C%BA%E5%8A%A8%E8%BD%A6%E5%8F%B7%E7%89%8C */
    val CN_CAR_NO =
        readResourceText("/mapping/CNcarNumber.txt")
            .lines()
            .filterNot { it.isBlank() }
            .map { it.split("\t") }
            .fold(mutableMapOf<String, String>()) { acc, p ->
                acc.also {
                    val codes = p.first().split("/")
                    acc[codes.first()] = p[1]
                    codes.drop(1).forEach {
                        acc[codes.first().first() + it] = p[1]
                    }
                }
            }
            .toMap()

    val TYPE =
        mapOf(
            "PORT" to PORT_DICT,
            "HTTP RESPONSE CODE" to HTTP_CODE_DICT,
            "MIME" to MIME_DICT,
            "China CAR NO" to CN_CAR_NO,
        )
}
