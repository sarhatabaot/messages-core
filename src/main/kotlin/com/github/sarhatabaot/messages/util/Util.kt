package com.github.sarhatabaot.messages.util

import java.util.*

/**
 *
 * @author sarhatabaot
 */
object Util {
    fun getAsClassName(sourceFileName: String): String {
        return getAsFileName(sourceFileName).replace(".java", "")
    }

    fun getAsFileName(sourceFileName: String): String {
        val names: MutableList<String?> = ArrayList()
        for (string in sourceFileName.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            names.add(capitalize(string))
        }
        return java.lang.String.join("", names)
    }


    fun capitalize(str: String?): String? {
        if (str.isNullOrEmpty())
            return null

        return str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
    }
}