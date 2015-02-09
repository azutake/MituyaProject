// $Id$
/*
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.chantake.MituyaProject.Tool;

import java.util.Collection;

/**
 * String utilities.
 *
 * @author sk89q
 */
public class StringUtil {

    /**
     * Trim a string if it is longer than a certain length.
     *
     * @param str
     * @param len
     * @return
     */
    public static String trimLength(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len);
        }

        return str;
    }

    /**
     * Join an array of strings into a string.
     *
     * @param str
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(String[] str, String delimiter,
            int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(str[initialIndex]);
        for (int i = initialIndex + 1; i < str.length; i++) {
            buffer.append(delimiter).append(str[i]);
        }
        return buffer.toString();
    }

    /**
     * Join an array of strings into a string.
     *
     * @param str
     * @param delimiter
     * @param initialIndex
     * @param quote
     * @return
     */
    public static String joinQuotedString(String[] str, String delimiter,
            int initialIndex, String quote) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(quote);
        buffer.append(str[initialIndex]);
        buffer.append(quote);
        for (int i = initialIndex + 1; i < str.length; i++) {
            buffer.append(delimiter).append(quote).append(str[i]).append(quote);
        }
        return buffer.toString();
    }

    /**
     * Join an array of strings into a string.
     *
     * @param str
     * @param delimiter
     * @return
     */
    public static String joinString(String[] str, String delimiter) {
        return joinString(str, delimiter, 0);
    }

    /**
     * Join an array of strings into a string.
     *
     * @param str
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(Object[] str, String delimiter,
            int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(str[initialIndex].toString());
        for (int i = initialIndex + 1; i < str.length; i++) {
            buffer.append(delimiter).append(str[i].toString());
        }
        return buffer.toString();
    }

    /**
     * Join an array of strings into a string.
     *
     * @param str
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(int[] str, String delimiter,
            int initialIndex) {
        if (str.length == 0) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(Integer.toString(str[initialIndex]));
        for (int i = initialIndex + 1; i < str.length; i++) {
            buffer.append(delimiter).append(Integer.toString(str[i]));
        }
        return buffer.toString();
    }

    /**
     * Join an list of strings into a string.
     *
     * @param str
     * @param delimiter
     * @param initialIndex
     * @return
     */
    public static String joinString(Collection<?> str, String delimiter,
            int initialIndex) {
        if (str.isEmpty()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        for (Object o : str) {
            if (i >= initialIndex) {
                if (i > 0) {
                    buffer.append(delimiter);
                }

                buffer.append(o.toString());
            }
            i++;
        }
        return buffer.toString();
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Unicode文字列に変換する("あ" -> "\u3042")
     * @param original
     * @return
     */
    public static String convertToUnicode(String original)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            sb.append(String.format("\\u%04X", Character.codePointAt(original, i)));
        }
        String unicode = sb.toString();
        return unicode;
    }

    /**
     * Unicode文字列から元の文字列に変換する ("\u3042" -> "あ")
     * @param unicode
     * @return
     */
    public static String convertToOiginal(String unicode)
    {
        String[] codeStrs = unicode.split("\\\\u");
        int[] codePoints = new int[codeStrs.length - 1];
        for (int i = 0; i < codePoints.length; i++) {
            codePoints[i] = Integer.parseInt(codeStrs[i + 1], 16);
        }
        String encodedText = new String(codePoints, 0, codePoints.length);
        return encodedText;
    }
}
