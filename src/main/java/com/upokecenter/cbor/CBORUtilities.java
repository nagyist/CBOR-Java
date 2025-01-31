package com.upokecenter.cbor;
/*
Written by Peter O.
Any copyright to this work is released to the Public Domain.
In case this is not possible, this work is also
licensed under the Unlicense: https://unlicense.org/

 */

import com.upokecenter.numbers.*;

  /**
   * Contains auxiliary methods that may have use outside the CBORObject class.
   */
  final class CBORUtilities {
private CBORUtilities() {
}
    private static final long DoublePosInfinity = (0x7ffL << 52);
    private static final String HexAlphabet = "0123456789ABCDEF";
    // Fractional seconds used in date conversion methods
    public static final int FractionalSeconds = 1000 * 1000 * 1000;
    private static final EInteger EInteger1970 = EInteger.FromInt32(1970);
    private static final EInteger EInteger86400 = EInteger.FromInt32(86400);

    public static int CompareStringsAsUtf8LengthFirst(String strA, String
      strB) {
      if (strA == null) {
        return (strB == null) ? 0 : -1;
      }
      if (strB == null) {
        return 1;
      }
      int alen = strA.length();
      int blen = strB.length();
      if (alen == 0) {
        return blen == 0 ? 0 : -1;
      }
      if (blen == 0) {
        return alen == 0 ? 0 : 1;
      }
      int cmp;
      if (alen < 128 && blen < 128) {
        int istrAUpperBound = alen * 3;
        if (istrAUpperBound < blen) {
          return -1;
        }
        int istrBUpperBound = blen * 3;
        if (istrBUpperBound < alen) {
          return 1;
        }
        cmp = 0;
        if (alen == blen) {
          boolean equalStrings = true;
          for (int i = 0; i < alen; ++i) {
            char sai = strA.charAt(i);
            char sbi = strB.charAt(i);
            if (sai != sbi) {
              equalStrings = false;
              cmp = (sai < sbi) ? -1 : 1;
              break;
            }
          }
          if (equalStrings) {
            return 0;
          }
        }
        boolean nonAscii = false;
        for (int i = 0; i < alen; ++i) {
          if (strA.charAt(i) >= 0x80) {
            nonAscii = true;
            break;
          }
        }
        for (int i = 0; i < blen; ++i) {
          if (strB.charAt(i) >= 0x80) {
            nonAscii = true;
            break;
          }
        }
        if (!nonAscii) {
          if (alen != blen) {
            return alen < blen ? -1 : 1;
          }
          if (alen == blen) {
            return cmp;
          }
        }
      } else {
        long strAUpperBound = alen * 3;
        if (strAUpperBound < blen) {
          return -1;
        }
        long strBUpperBound = blen * 3;
        if (strBUpperBound < alen) {
          return 1;
        }
      }
      // System.out.println("slow path "+alen+","+blen);
      int sapos = 0;
      int sbpos = 0;
      long sautf8 = 0L;
      long sbutf8 = 0L;
      cmp = 0;
      boolean haveboth = true;
      while (true) {
        int sa = 0, sb = 0;
        if (sapos == alen) {
          haveboth = false;
          if (sbutf8 > sautf8) {
            return -1;
          } else if (sbpos == blen) {
            break;
          } else if (cmp == 0) {
            cmp = -1;
          }
        } else {
          sa = com.upokecenter.util.DataUtilities.CodePointAt(strA, sapos, 1);
          if (sa < 0) {
            throw new IllegalArgumentException("strA has unpaired surrogate");
          }
          if (sa >= 0x10000) {
            sautf8 += 4;
            sapos += 2;
          } else if (sa >= 0x800) {
            sautf8 += 3;
            ++sapos;
          } else if (sa >= 0x80) {
            sautf8 += 2;
            ++sapos;
          } else {
            ++sautf8;
            ++sapos;
          }
        }
        if (sbpos == blen) {
          haveboth = false;
          if (sautf8 > sbutf8) {
            return 1;
          } else if (sapos == alen) {
            break;
          } else if (cmp == 0) {
            cmp = 1;
          }
        } else {
          sb = com.upokecenter.util.DataUtilities.CodePointAt(strB, sbpos, 1);
          if (sb < 0) {
            throw new IllegalArgumentException("strB has unpaired surrogate");
          }
          if (sb >= 0x10000) {
            sbutf8 += 4;
            sbpos += 2;
          } else if (sb >= 0x800) {
            sbutf8 += 3;
            ++sbpos;
          } else if (sb >= 0x80) {
            ++sbpos;
            sbutf8 += 2;
          } else {
            ++sbpos;
            ++sbutf8;
          }
        }
        if (haveboth && cmp == 0 && sa != sb) {
          cmp = (sa < sb) ? -1 : 1;
        }
      }
      return (sautf8 != sbutf8) ? (sautf8 < sbutf8 ? -1 : 1) : cmp;
    }

    public static int CompareUtf16Utf8LengthFirst(String utf16, byte[] utf8) {
      if (utf16 == null) {
        return (utf8 == null) ? 0 : -1;
      }
      if (utf8 == null) {
        return 1;
      }
      if (utf16.length() == 0) {
        return utf8.length == 0 ? 0 : -1;
      }
      if (utf16.length() == 0) {
        return utf8.length == 0 ? 0 : 1;
      }
      long strAUpperBound = utf16.length() * 3;
      if (strAUpperBound < utf8.length) {
        return -1;
      }
      long strBUpperBound = utf16.length() * 3;
      if (strBUpperBound < utf8.length) {
        return 1;
      }
      int u16pos = 0;
      int u8pos = 0;
      long u16u8length = 0L;
      int cmp = 0;
      boolean haveboth = true;
      while (true) {
        int u16 = 0, u8 = 0;
        if (u16pos == utf16.length()) {
          haveboth = false;
          if (u8pos > u16u8length) {
            return -1;
          } else if (u8pos == utf8.length) {
            break;
          } else if (cmp == 0) {
            cmp = -1;
          }
        } else {
          u16 = com.upokecenter.util.DataUtilities.CodePointAt(utf16, u16pos, 1);
          if (u16 < 0) {
            throw new IllegalArgumentException("utf16 has unpaired surrogate");
          }
          if (u16 >= 0x10000) {
            u16u8length += 4;
            u16pos += 2;
          } else if (u16 >= 0x800) {
            u16u8length += 3;
            ++u16pos;
          } else if (u16 >= 0x80) {
            u16u8length += 2;
            ++u16pos;
          } else {
            ++u16u8length;
            ++u16pos;
          }
        }
        if (u8pos == utf8.length) {
          haveboth = false;
          if (cmp == 0) {
            return 1;
          } else if (u16pos == utf16.length()) {
            break;
          } else if (cmp == 0) {
            cmp = 1;
          }
        } else {
          u8 = Utf8CodePointAt(utf8, u8pos);
          if (u8 < 0) {
            throw new IllegalArgumentException("utf8 has invalid encoding");
          }
          if (u8 >= 0x10000) {
            u8pos += 4;
          } else if (u8 >= 0x800) {
            u8pos += 3;
          } else if (u8 >= 0x80) {
            u8pos += 2;
          } else {
            ++u8pos;
          }
        }
        if (haveboth && cmp == 0 && u16 != u8) {
          cmp = (u16 < u8) ? -1 : 1;
        }
      }
      return (u16u8length != u8pos) ? (u16u8length < u8pos ? -1 : 1) : cmp;
    }

    public static int Utf8CodePointAt(byte[] utf8, int offset) {
      int endPos = utf8.length;
      if (offset < 0 || offset >= endPos) {
        return -1;
      }
      int c = utf8[offset] & 0xff;
      if (c <= 0x7f) {
        return c;
      } else if (c >= 0xc2 && c <= 0xdf) {
        ++offset;
        int c1 = offset < endPos ? utf8[offset] & 0xff : -1;
        return (
            c1 < 0x80 || c1 > 0xbf) ? -2 : (((c - 0xc0) << 6) |
            (c1 - 0x80));
      } else if (c >= 0xe0 && c <= 0xef) {
        ++offset;
        int c1 = offset < endPos ? utf8[offset++] & 0xff : -1;
        int c2 = offset < endPos ? utf8[offset] & 0xff : -1;
        int lower = (c == 0xe0) ? 0xa0 : 0x80;
        int upper = (c == 0xed) ? 0x9f : 0xbf;
        return (c1 < lower || c1 > upper || c2 < 0x80 || c2 > 0xbf) ?
          -2 : (((c - 0xe0) << 12) | ((c1 - 0x80) << 6) | (c2 - 0x80));
      } else if (c >= 0xf0 && c <= 0xf4) {
        ++offset;
        int c1 = offset < endPos ? utf8[offset++] & 0xff : -1;
        int c2 = offset < endPos ? utf8[offset++] & 0xff : -1;
        int c3 = offset < endPos ? utf8[offset] & 0xff : -1;
        int lower = (c == 0xf0) ? 0x90 : 0x80;
        int upper = (c == 0xf4) ? 0x8f : 0xbf;
        return c1 < lower || c1 > upper || c2 < 0x80 || c2 > 0xbf ||
          c3 < 0x80 || c3 > 0xbf ? -2 :
          ((c - 0xf0) << 18) | ((c1 - 0x80) << 12) | ((c2 - 0x80) <<
          6) | (c3 - 0x80);
      } else {
        return -2;
      }
    }

    // NOTE: StringHashCode and Utf8HashCode must
    // return the same hash code for the same sequence
    // of Unicode code points. Both must treat an illegally
    // encoded subsequence as ending the sequence for
    // this purpose.
    public static int StringHashCode(String str) {
      int upos = 0;
      int code = 0x7edede19;
      while (true) {
        if (upos == str.length()) {
          return code;
        }
        int sc = com.upokecenter.util.DataUtilities.CodePointAt(str, upos, 1);
        if (sc < 0) {
          return code;
        }
        code = ((code * 31) + sc);
        if (sc >= 0x10000) {
          upos += 2;
        } else {
          ++upos;
        }
      }
    }

    public static int Utf8HashCode(byte[] utf8) {
      int upos = 0;
      int code = 0x7edede19;
      while (true) {
        int sc = Utf8CodePointAt(utf8, upos);
        if (sc == -1) {
          return code;
        }
        if (sc == -2) {
          return code;
        }
        code = ((code * 31) + sc);
        if (sc >= 0x10000) {
          upos += 4;
        } else if (sc >= 0x800) {
          upos += 3;
        } else if (sc >= 0x80) {
          upos += 2;
        } else {
          ++upos;
        }
      }
    }

    public static boolean CheckUtf16(String str) {
      int upos = 0;
      while (true) {
        if (upos == str.length()) {
          return true;
        }
        int sc = com.upokecenter.util.DataUtilities.CodePointAt(str, upos, 1);
        if (sc < 0) {
          return false;
        }
        if (sc >= 0x10000) {
          upos += 2;
        } else {
          ++upos;
        }
      }
    }

    public static boolean CheckUtf8(byte[] utf8) {
      int upos = 0;
      while (true) {
        int sc = Utf8CodePointAt(utf8, upos);
        if (sc == -1) {
          return true;
        }
        if (sc == -2) {
          return false;
        }
        if (sc >= 0x10000) {
          upos += 4;
        } else if (sc >= 0x800) {
          upos += 3;
        } else if (sc >= 0x80) {
          upos += 2;
        } else {
          ++upos;
        }
      }
    }

    public static boolean StringEqualsUtf8(String str, byte[] utf8) {
      if (str == null) {
        return utf8 == null;
      }
      if (utf8 == null) {
        return false;
      }
      long strAUpperBound = str.length() * 3;
      if (strAUpperBound < utf8.length) {
        return false;
      }
      long strBUpperBound = utf8.length * 3;
      if (strBUpperBound < str.length()) {
        return false;
      }
      int spos = 0;
      int upos = 0;
      while (true) {
        int sc = com.upokecenter.util.DataUtilities.CodePointAt(str, spos, 1);
        int uc = Utf8CodePointAt(utf8, upos);
        if (uc == -2) {
          throw new IllegalStateException("Invalid encoding");
        }
        if (sc == -1) {
          return uc == -1;
        }
        if (sc != uc) {
          return false;
        }
        if (sc >= 0x10000) {
          spos += 2;
          upos += 4;
        } else if (sc >= 0x800) {
          ++spos;
          upos += 3;
        } else if (sc >= 0x80) {
          ++spos;
          upos += 2;
        } else {
          ++spos;
          ++upos;
        }
      }
    }
    public static boolean ByteArrayEquals(byte[] a, byte[] b) {
      if (a == null) {
        return b == null;
      }
      if (b == null) {
        return false;
      }
      if (a.length != b.length) {
        return false;
      }
      for (int i = 0; i < a.length; ++i) {
        if (a[i] != b[i]) {
          return false;
        }
      }
      return true;
    }

    public static int ByteArrayHashCode(byte[] a) {
      if (a == null) {
        return 0;
      }
      int ret = 19;
      {
        ret = (ret * 31) + a.length;
        for (int i = 0; i < a.length; ++i) {
          ret = (ret * 31) + a[i];
        }
      }
      return ret;
    }

    public static int ByteArrayCompare(byte[] a, byte[] b) {
      if (a == null) {
        return (b == null) ? 0 : -1;
      }
      if (b == null) {
        return 1;
      }
      int c = Math.min(a.length, b.length);
      for (int i = 0; i < c; ++i) {
        byte ai = a[i];
        byte bi = b[i];
        if (ai != bi) {
          return ((ai & 0xff) < (bi & 0xff)) ? -1 : 1;
        }
      }
      return (a.length != b.length) ? ((a.length < b.length) ? -1 : 1) : 0;
    }

    public static int ByteArrayCompareLengthFirst(byte[] a, byte[] b) {
      if (a == null) {
        return (b == null) ? 0 : -1;
      }
      if (b == null) {
        return 1;
      }
      if (a.length != b.length) {
        return a.length < b.length ? -1 : 1;
      }
      for (int i = 0; i < a.length; ++i) {
        byte ai = a[i];
        byte bi = b[i];
        if (ai != bi) {
          return ((ai & 0xff) < (bi & 0xff)) ? -1 : 1;
        }
      }
      return 0;
    }

    public static String TrimDotZero(String str) {
      return (str.length() > 2 && str.charAt(str.length() - 1) == '0' && str.charAt(str.length()
            - 2) == '.') ? str.substring(0,str.length() - 2) :
        str;
    }

    public static long DoubleToInt64Bits(double dbl) {
      return Double.doubleToRawLongBits(dbl);
    }

    public static int SingleToInt32Bits(float flt) {
      return Float.floatToRawIntBits(flt);
    }

    public static double Int64BitsToDouble(long bits) {
      return Double.longBitsToDouble(bits);
    }

    public static float Int32BitsToSingle(int bits) {
      return Float.intBitsToFloat(bits);
    }

/**
 * @deprecated
 */
@Deprecated
    public static String DoubleToString(double dbl) {
      return EFloat.FromDouble(dbl).ToShortestString(EContext.Binary64);
    }

    public static String DoubleBitsToString(long dblbits) {
      return EFloat.FromDoubleBits(dblbits).ToShortestString(EContext.Binary64);
    }

/**
 * @deprecated
 */
@Deprecated
    public static String SingleToString(float sing) {
      return EFloat.FromSingle(sing).ToShortestString(EContext.Binary32);
    }

    public static String LongToString(long longValue) {
      if (longValue == Long.MIN_VALUE) {
        return "-9223372036854775808";
      }
      if (longValue == 0L) {
        return "0";
      }
      if (longValue == Integer.MIN_VALUE) {
        return "-2147483648";
      }
      boolean neg = longValue < 0;
      char[] chars;
      int intlongValue = ((int)longValue);
      int count;
      if (intlongValue == longValue) {
        chars = new char[12];
        count = 11;
        if (neg) {
          intlongValue = -intlongValue;
        }
        while (intlongValue > 43698) {
          int intdivValue = intlongValue / 10;
          char digit = HexAlphabet.charAt(intlongValue - (intdivValue *
                10));
          chars[count--] = digit;
          intlongValue = intdivValue;
        }
        while (intlongValue > 9) {
          int intdivValue = (intlongValue * 26215) >> 18;
          char digit = HexAlphabet.charAt(intlongValue - (intdivValue *
                10));
          chars[count--] = digit;
          intlongValue = intdivValue;
        }
        if (intlongValue != 0) {
          chars[count--] = HexAlphabet.charAt(intlongValue);
        }
        if (neg) {
          chars[count] = '-';
        } else {
          ++count;
        }
        return new String(chars, count, 12 - count);
      } else {
        chars = new char[24];
        count = 23;
        if (neg) {
          longValue = -longValue;
        }
        while (longValue > 43698) {
          long divValue = longValue / 10;
          char digit = HexAlphabet.charAt((int)(longValue - (divValue * 10)));
          chars[count--] = digit;
          longValue = divValue;
        }
        while (longValue > 9) {
          long divValue = (longValue * 26215) >> 18;
          char digit = HexAlphabet.charAt((int)(longValue - (divValue * 10)));
          chars[count--] = digit;
          longValue = divValue;
        }
        if (longValue != 0) {
          chars[count--] = HexAlphabet.charAt((int)longValue);
        }
        if (neg) {
          chars[count] = '-';
        } else {
          ++count;
        }
        return new String(chars, count, 24 - count);
      }
    }

    private static EInteger FloorDiv(EInteger a, EInteger n) {
      return a.signum() >= 0 ? a.Divide(n) : EInteger.FromInt32(-1).Subtract(
        EInteger.FromInt32(-1).Subtract(a).Divide(n));
    }

    private static long FloorDiv(long longA, int longN) {
      return longA >= 0 ? longA / longN : (-1 - ((-1 - longA) / longN));
    }

    private static EInteger FloorMod(EInteger a, EInteger n) {
      return a.Subtract(FloorDiv(a, n).Multiply(n));
    }

    private static long FloorModLong(long longA, int longN) {
      {
        return longA - (FloorDiv(longA, longN) * longN);
      }
    }

    private static final int[] ValueNormalDays = {
      0, 31, 28, 31, 30, 31, 30,
      31, 31, 30,
      31, 30, 31,
    };

    private static final int[] ValueLeapDays = {
      0, 31, 29, 31, 30, 31, 30,
      31, 31, 30,
      31, 30, 31,
    };

    private static final int[] ValueNormalToMonth = {
      0, 0x1f, 0x3b, 90, 120,
      0x97, 0xb5,
      0xd4, 0xf3, 0x111, 0x130, 0x14e, 0x16d,
    };

    private static final int[] ValueLeapToMonth = {
      0, 0x1f, 60, 0x5b, 0x79,
      0x98, 0xb6,
      0xd5, 0xf4, 0x112, 0x131, 0x14f, 0x16e,
    };

    public static void GetNormalizedPartProlepticGregorian(
      EInteger year,
      int month,
      EInteger day,
      EInteger[] outYear,
      int[] outMonthDay) {
      // NOTE: This method assumes month is 1 to 12
      if (month <= 0 || month > 12) {
        throw new IllegalArgumentException("month");
      }
      int[] dayArray;
      if (year.CanFitInInt32() && day.CanFitInInt32()) {
        long longYear = year.ToInt32Checked();
        int intDay = day.ToInt32Checked();
        if (intDay > 100) {
          // Number of days in a 400-year block
          int intCount = intDay / 146097;
          intDay = (intDay - (intCount * 146097));
          longYear = (longYear + (intCount * 400));
        }
        if (intDay < -101) {
          // Number of days in a 400-year block
          int intCount = (intDay == Integer.MIN_VALUE) ? 14699 : Math.abs(intDay)
            / 146097;
          intDay = (intDay + (intCount * 146097));
          longYear = (longYear - (intCount * 400));
        }
        if (longYear == 1970 && month == 1 && intDay > 0 && intDay >= 10957) {
          // Add enough days to move from 1/1970 to 1/2000
          longYear = 2000;
          intDay -= 10957;
        }
        if (longYear == 2000 && month == 1 && intDay > 0 && intDay < 35064) {
          // Add enough days to move from 1/2000 to closest 4-year block
          // in the century.
          int intCount = intDay / 1461;
          intDay += intCount * 1461;
          longYear -= intCount * 4;
        }
        while (intDay > 366) {
          if ((longYear & 0x03) != 0 || (longYear % 100 == 0 && longYear %
            400 != 0)) {
            ++longYear;
            intDay -= 365;
          } else {
            ++longYear;
            intDay -= 366;
          }
        }
        dayArray = ((longYear & 0x03) != 0 || (
          longYear % 100 == 0 && longYear % 400 != 0)) ? ValueNormalDays :
          ValueLeapDays;
        while (true) {
          int intDays = dayArray[month];
          if (intDay > 0 && intDay <= intDays) {
            break;
          }
          if (intDay > intDays) {
            intDay -= intDays;
            if (month == 12) {
              month = 1;
              ++longYear;
              dayArray = ((longYear & 0x03) != 0 || (
                longYear % 100 == 0 &&
                longYear % 400 != 0)) ? ValueNormalDays : ValueLeapDays;
            } else {
              ++month;
            }
          }
          if (intDay <= 0) {
            --month;
            if (month <= 0) {
              --longYear;
              month = 12;
              dayArray = ((longYear & 0x03) != 0 || (
                longYear % 100 == 0 && longYear % 400 != 0)) ?
                ValueNormalDays : ValueLeapDays;
            }
            intDay += dayArray[month];
          }
        }
        outMonthDay[0] = month;
        outMonthDay[1] = intDay;
        outYear[0] = EInteger.FromInt64(longYear);
      } else {
        if (day.compareTo(100) > 0) {
          // Number of days in a 400-year block
          EInteger count = day.Divide(146097);
          day = day.Subtract(count.Multiply(146097));
          year = year.Add(count.Multiply(400));
        }
        if (day.compareTo(-101) < 0) {
          // Number of days in a 400-year block
          EInteger count = day.Abs().Divide(146097);
          day = day.Add(count.Multiply(146097));
          year = year.Subtract(count.Multiply(400));
        }
        dayArray = (year.Remainder(4).signum() != 0 || (
          year.Remainder(100).signum() == 0 && year.Remainder(400).signum() !=
          0)) ? ValueNormalDays : ValueLeapDays;
        while (true) {
          EInteger days = EInteger.FromInt32(dayArray[month]);
          if (day.signum() > 0 && day.compareTo(days) <= 0) {
            break;
          }
          if (day.compareTo(days) > 0) {
            day = day.Subtract(days);
            if (month == 12) {
              month = 1;
              year = year.Add(1);
              dayArray = (year.Remainder(4).signum() != 0 || (
                year.Remainder(100).signum() == 0 &&
                year.Remainder(400).signum() != 0)) ? ValueNormalDays :
                ValueLeapDays;
            } else {
              ++month;
            }
          }
          if (day.signum() <= 0) {
            --month;
            if (month <= 0) {
              year = year.Add(-1);
              month = 12;
              dayArray = (year.Remainder(4).signum() != 0 || (
                year.Remainder(100).signum() == 0 &&
                year.Remainder(400).signum() != 0)) ? ValueNormalDays :
                ValueLeapDays;
            }
            day = day.Add(dayArray[month]);
          }
        }
        outMonthDay[0] = month;
        outMonthDay[1] = day.ToInt32Checked();
        outYear[0] = year;
      }
    }

    /*
       // Example: Apple Time is a 32-bit unsigned integer
       // of the number of seconds since the start of 1904.
       // EInteger appleTime = GetNumberOfDaysProlepticGregorian(
            year,
            month ,
         day)
       // .Subtract(GetNumberOfDaysProlepticGregorian(
       // EInteger.FromInt32(1904),
       1 // ,
       s1));*/
    public static EInteger GetNumberOfDaysProlepticGregorian(
      EInteger year,
      int month,
      int mday) {
      // NOTE: month = 1 is January, year = 1 is year 1
      if (month <= 0 || month > 12) {
        throw new IllegalArgumentException("month");
      }
      if (mday <= 0 || mday > 31) {
        throw new IllegalArgumentException("mday");
      }
      EInteger numDays = EInteger.FromInt32(0);
      int startYear = 1970;
      if (year.compareTo(startYear) < 0) {
        EInteger currentYear = EInteger.FromInt32(startYear - 1);
        EInteger diff = currentYear.Subtract(year);

        if (diff.compareTo(401) > 0) {
          EInteger blocks = diff.Subtract(401).Divide(400);
          numDays = numDays.Subtract(blocks.Multiply(146097));
          diff = diff.Subtract(blocks.Multiply(400));
          currentYear = currentYear.Subtract(blocks.Multiply(400));
        }

        numDays = numDays.Subtract(diff.Multiply(365));
        int decrement = 1;
        for (;
          currentYear.compareTo(year) > 0;
          currentYear = currentYear.Subtract(decrement)) {
          if (decrement == 1 && currentYear.Remainder(4).signum() == 0) {
            decrement = 4;
          }
          if (!(currentYear.Remainder(4).signum() != 0 || (
            currentYear.Remainder(100).signum() == 0 &&
            currentYear.Remainder(400).signum() !=
            0))) {
            numDays = numDays.Subtract(1);
          }
        }
        numDays = year.Remainder(4).signum() != 0 || (
          year.Remainder(100).signum() == 0 && year.Remainder(400).signum() != 0) ?
          numDays.Subtract(365 - ValueNormalToMonth[month])
          .Subtract(ValueNormalDays[month] - mday + 1) :
          numDays.Subtract(366 - ValueLeapToMonth[month])
          .Subtract(ValueLeapDays[month] - mday + 1);
      } else {
        boolean isNormalYear = year.Remainder(4).signum() != 0 ||
          (year.Remainder(100).signum() == 0 && year.Remainder(400).signum() != 0);

        EInteger currentYear = EInteger.FromInt32(startYear);
        if (currentYear.Add(401).compareTo(year) < 0) {
          EInteger y2 = year.Subtract(2);
          numDays = numDays.Add(
              y2.Subtract(startYear).Divide(400).Multiply(146097));
          currentYear = y2.Subtract(
              y2.Subtract(startYear).Remainder(400));
        }

        EInteger diff = year.Subtract(currentYear);
        numDays = numDays.Add(diff.Multiply(365));
        EInteger eileap = currentYear;
        if (currentYear.Remainder(4).signum() != 0) {
          eileap = eileap.Add(4 - eileap.Remainder(4).ToInt32Checked());
        }
        numDays = numDays.Add(year.Subtract(eileap).Add(3).Divide(4));
        if (currentYear.Remainder(100).signum() != 0) {
          currentYear = currentYear.Add(100 -
              currentYear.Remainder(100).ToInt32Checked());
        }
        while (currentYear.compareTo(year) < 0) {
          if (currentYear.Remainder(400).signum() != 0) {
            numDays = numDays.Subtract(1);
          }
          currentYear = currentYear.Add(100);
        }
        int yearToMonth = isNormalYear ? ValueNormalToMonth[month - 1] :
          ValueLeapToMonth[month - 1];
        numDays = numDays.Add(yearToMonth)
          .Add(mday - 1);
      }
      return numDays;
    }

    public static void BreakDownSecondsSinceEpoch(
      long seconds,
      EInteger[] year,
      int[] lesserFields) {
      EInteger[] normPart = new EInteger[3];
      long longDays = FloorDiv(seconds, 86400) + 1;
      long longSecondsInDay = FloorModLong(seconds, 86400);

      int secondsInDay = ((int)longSecondsInDay);

      GetNormalizedPartProlepticGregorian(
        EInteger1970,
        1,
        EInteger.FromInt64(longDays),
        year,
        lesserFields); // Fills out month and day in lesserFields[0]/[1]
      lesserFields[2] = secondsInDay / 3600;
      lesserFields[3] = (secondsInDay % 3600) / 60;
      lesserFields[4] = secondsInDay % 60;
      lesserFields[5] = 0;
      lesserFields[6] = 0;
    }

    public static void BreakDownSecondsSinceEpoch(
      EDecimal edec,
      EInteger[] year,
      int[] lesserFields) {
      EInteger integerPart = edec.Quantize(0, ERounding.Floor)
        .ToEInteger();
      EDecimal fractionalPart = edec.Subtract(
          EDecimal.FromEInteger(integerPart)).Abs();
      int fractionalSeconds = fractionalPart.Multiply(FractionalSeconds)
        .ToInt32Checked();
      EInteger days = FloorDiv(
          integerPart,
          EInteger86400).Add(1);
      int secondsInDay = FloorMod(
          integerPart,
          EInteger86400).ToInt32Checked();

      GetNormalizedPartProlepticGregorian(
        EInteger1970,
        1,
        days,
        year,
        lesserFields); // Fills out month and day in lesserFields[0]/[1]
      lesserFields[2] = secondsInDay / 3600;
      lesserFields[3] = (secondsInDay % 3600) / 60;
      lesserFields[4] = secondsInDay % 60;
      lesserFields[5] = fractionalSeconds;
      lesserFields[6] = 0;
    }

    public static boolean NameStartsWithWord(String name, String word) {
      int wl = word.length();
      return name.length() > wl && name.substring(0,wl).equals(word) && !(name.charAt(wl) >= 'a' && name.charAt(wl) <=
          'z') && !(name.charAt(wl) >= '0' && name.charAt(wl) <= '9');
    }

    public static String FirstCharLower(String name) {
      if (name.length() > 0 && name.charAt(0) >= 'A' && name.charAt(0) <= 'Z') {
        StringBuilder sb = new StringBuilder();
        sb.append((char)(name.charAt(0) + 0x20));
        sb.append(name.substring(1));
        return sb.toString();
      }
      return name;
    }

    public static String FirstCharUpper(String name) {
      if (name.length() > 0 && name.charAt(0) >= 'a' && name.charAt(0) <= 'z') {
        StringBuilder sb = new StringBuilder();
        sb.append((char)(name.charAt(0) - 0x20));
        sb.append(name.substring(1));
        return sb.toString();
      }
      return name;
    }

    private static boolean IsValidDateTime(int[] dateTime) {
      if (dateTime == null || dateTime.length < 8) {
        return false;
      }
      if (dateTime[1] < 1 || dateTime[1] > 12 || dateTime[2] < 1) {
        return false;
      }
      boolean leap = IsLeapYear(dateTime[0]);
      if (dateTime[1] == 4 || dateTime[1] == 6 || dateTime[1] == 9 ||
        dateTime[1] == 11) {
        if (dateTime[2] > 30) {
          return false;
        }
      } else if (dateTime[1] == 2) {
        if (dateTime[2] > (leap ? 29 : 28)) {
          return false;
        }
      } else {
        if (dateTime[2] > 31) {
          return false;
        }
      }
      return !(dateTime[3] < 0 || dateTime[4] < 0 || dateTime[5] < 0 ||
          dateTime[3] >= 24 || dateTime[4] >= 60 || dateTime[5] >= 61 ||
          dateTime[6] < 0 ||
          dateTime[6] >= FractionalSeconds || dateTime[7] <= -1440 ||
          dateTime[7] >= 1440);
    }

    private static boolean IsLeapYear(int yr) {
      yr %= 400;
      if (yr < 0) {
        yr += 400;
      }
      return (((yr % 4) == 0) && ((yr % 100) != 0)) || ((yr % 400) == 0);
    }

    public static void ParseAtomDateTimeString(
      String str,
      EInteger[] bigYearArray,
      int[] lf) {
      int[] d = ParseAtomDateTimeString(str);
      bigYearArray[0] = EInteger.FromInt32(d[0]);
      System.arraycopy(d, 1, lf, 0, 7);
    }

    private static int[] ParseAtomDateTimeString(String str) {
      boolean bad = false;
      if (str.length() < 19) {
        throw new IllegalArgumentException("Invalid date/time");
      }
      for (int i = 0; i < 19 && !bad; ++i) {
        if (i == 4 || i == 7) {
          bad |= str.charAt(i) != '-';
        } else if (i == 13 || i == 16) {
          bad |= str.charAt(i) != ':';
        } else if (i == 10) {
          bad |= str.charAt(i) != 'T';
          /*lowercase t not used to separate date/time,
          following RFC 4287 sec. 3.3*/
        } else {
          bad |= str.charAt(i) < '0' || str.charAt(i) > '9';
        }
      }
      if (bad) {
        throw new IllegalArgumentException("Invalid date/time");
      }
      int year = ((str.charAt(0) - '0') * 1000) + ((str.charAt(1) - '0') * 100) +
        ((str.charAt(2) - '0') * 10) + (str.charAt(3) - '0');
      int month = ((str.charAt(5) - '0') * 10) + (str.charAt(6) - '0');
      int day = ((str.charAt(8) - '0') * 10) + (str.charAt(9) - '0');
      int hour = ((str.charAt(11) - '0') * 10) + (str.charAt(12) - '0');
      int minute = ((str.charAt(14) - '0') * 10) + (str.charAt(15) - '0');
      int second = ((str.charAt(17) - '0') * 10) + (str.charAt(18) - '0');
      int index = 19;
      int nanoSeconds = 0;
      if (index <= str.length() && str.charAt(index) == '.') {
        int icount = 0;
        ++index;
        while (index < str.length()) {
          if (str.charAt(index) < '0' || str.charAt(index) > '9') {
            break;
          }
          if (icount < 9) {
            nanoSeconds = (nanoSeconds * 10) + (str.charAt(index) - '0');
            ++icount;
          }
          ++index;
        }
        while (icount < 9) {
          nanoSeconds *= 10;
          ++icount;
        }
      }
      int utcToLocal;
      if (index + 1 == str.length() && str.charAt(index) == 'Z') {
        /*lowercase z not used to indicate UTC,
          following RFC 4287 sec. 3.3*/ utcToLocal = 0;
      } else if (index + 6 == str.length()) {
        bad = false;
        for (int i = 0; i < 6 && !bad; ++i) {
          if (i == 0) {
            bad |= str.charAt(index + i) != '-' && str.charAt(index + i) != '+';
          } else if (i == 3) {
            bad |= str.charAt(index + i) != ':';
          } else {
            bad |= str.charAt(index + i) < '0' || str.charAt(index + i) > '9';
          }
        }
        if (bad) {
          throw new IllegalArgumentException("Invalid date/time");
        }
        boolean neg = str.charAt(index) == '-';
        int tzhour = ((str.charAt(index + 1) - '0') * 10) + (str.charAt(index + 2) - '0');
        int tzminute = ((str.charAt(index + 4) - '0') * 10) + (str.charAt(index + 5) - '0');
        if (tzminute >= 60) {
          throw new IllegalArgumentException("Invalid date/time");
        }
        utcToLocal = ((neg ? -1 : 1) * tzhour * 60) + tzminute;
      } else {
        throw new IllegalArgumentException("Invalid date/time");
      }
      int[] dt = {
        year, month, day, hour, minute, second,
        nanoSeconds, utcToLocal,
      };
      if (!IsValidDateTime(dt)) {
 throw new IllegalArgumentException("Invalid" +
        "\u0020date/time");
}
 return dt;
    }

    public static EFloat DateTimeToIntegerOrDouble(
      EInteger bigYear,
      int[] lesserFields,
      int[] status) {
      if (bigYear == null) {
        throw new NullPointerException("bigYear");
      }
      if (lesserFields == null) {
        throw new NullPointerException("lesserFields");
      }
      if (7 < 0) {
        throw new IllegalArgumentException(" (" + 7 + ") is not greater or equal to" +
          "\u00200");
      }
      if (lesserFields.length < 7) {
        throw new IllegalArgumentException(" (" + 7 + ") is not less or equal to " +
          lesserFields.length);
      }
      if (lesserFields.length < 7) {
        throw new IllegalArgumentException("\"lesserFields\" + \"'s length\" (" +
          lesserFields.length + ") is not greater or equal to 7");
      }
      if (status == null) {
        throw new NullPointerException("status");
      }
      if (status.length < 1) {
        throw new IllegalArgumentException("\"status\" + \"'s length\" (" +
          status.length + ") is not greater or equal to 1");
      }
      // Status is 0 for integer, 1 for (lossy) double, 2 for failure
      if (lesserFields[6] != 0) {
        throw new UnsupportedOperationException(
          "Local time offsets not supported");
      }
      EInteger seconds = GetNumberOfDaysProlepticGregorian(
          bigYear,
          lesserFields[0],
          lesserFields[1]);
      seconds = seconds.Multiply(24).Add(lesserFields[2])
        .Multiply(60).Add(lesserFields[3]).Multiply(60).Add(lesserFields[4]);
      if (lesserFields[5] == 0 && seconds.GetUnsignedBitLengthAsInt64() <= 64) {
        // Can fit in major type 1 or 2
        status[0] = 0;
        return EFloat.FromEInteger(seconds);
      }
      // Add seconds and incorporate nanoseconds
      EDecimal d = EDecimal.FromInt32(lesserFields[5]).Divide(FractionalSeconds)
        .Add(EDecimal.FromEInteger(seconds));
      double dbl = d.ToDouble();
      if (((dbl) == Double.POSITIVE_INFINITY) ||
        ((dbl) == Double.NEGATIVE_INFINITY) ||
        Double.isNaN(dbl)) {
        status[0] = 2;
        return null;
      }
      status[0] = 1;
      return EFloat.FromDouble(dbl);
    }

    public static void CheckYearAndLesserFields(int smallYear, int[]
      lesserFields) {
      CheckLesserFields(lesserFields);
      if (lesserFields[0] == 2 && lesserFields[1] == 29 &&
        !IsLeapYear(smallYear)) {
        throw new IllegalArgumentException();
      }
    }

    public static void CheckYearAndLesserFields(EInteger bigYear, int[]
      lesserFields) {
      CheckLesserFields(lesserFields);
      if (lesserFields[0] == 2 && lesserFields[1] == 29 &&
        (bigYear.Remainder(4).signum() != 0 || (
        bigYear.Remainder(100).signum() == 0 && bigYear.Remainder(400).signum() !=
        0))) {
        throw new IllegalArgumentException();
      }
    }

    public static void CheckLesserFields(int[] lesserFields) {
      if (lesserFields == null) {
        throw new NullPointerException("lesserFields");
      }
      if (lesserFields.length < 7) {
        throw new IllegalArgumentException(" (" + 7 + ") is not less or equal to " +
          lesserFields.length);
      }
      if (lesserFields.length < 7) {
        throw new IllegalArgumentException("\"lesserFields\" + \"'s length\" (" +
          lesserFields.length + ") is not greater or equal to 7");
      }
      if (lesserFields[0] < 1) {
        throw new IllegalArgumentException("\"month\" (" + lesserFields[0] + ") is" +
          "\u0020not" + "\u0020greater or equal to 1");
      }
      if (lesserFields[0] > 12) {
        throw new IllegalArgumentException("\"month\" (" + lesserFields[0] + ") is" +
          "\u0020not less" + "\u0020or equal to 12");
      }
      if (lesserFields[1] < 1) {
        throw new IllegalArgumentException("\"intDay\" (" + lesserFields[1] + ") is" +
          "\u0020not greater or" + "\u0020equal to 1");
      }
      if (lesserFields[1] > 31) {
        throw new IllegalArgumentException("\"day\" (" + lesserFields[1] + ") is" +
          "\u0020not less or" + "\u0020equal to 31");
      }
      if (lesserFields[1] > ValueLeapDays[lesserFields[0]]) {
        throw new IllegalArgumentException();
      }
      if (lesserFields[2] < 0) {
        throw new IllegalArgumentException("\"hour\" (" + lesserFields[2] + ") is" +
          "\u0020not greater" + "\u0020or equal to 0");
      }
      if (lesserFields[2] > 23) {
        throw new IllegalArgumentException("\"hour\" (" + lesserFields[2] + ") is" +
          "\u0020not less or" + "\u0020equal to 23");
      }
      if (lesserFields[3] < 0) {
        throw new IllegalArgumentException("\"minute\" (" + lesserFields[3] + ") is" +
          "\u0020not" + "\u0020greater or equal to 0");
      }
      if (lesserFields[3] > 59) {
        throw new IllegalArgumentException("\"minute\" (" + lesserFields[3] + ") is" +
          "\u0020not less" + "\u0020or equal to 59");
      }
      if (lesserFields[4] < 0) {
        throw new IllegalArgumentException("\"second\" (" + lesserFields[4] + ") is" +
          "\u0020not" + "\u0020greater or equal to 0");
      }
      if (lesserFields[4] > 59) {
        throw new IllegalArgumentException("\"second\" (" + lesserFields[4] + ") is" +
          "\u0020not less" + "\u0020or equal to 59");
      }
      if (lesserFields[5] < 0) {
        throw new IllegalArgumentException("\"lesserFields[5]\" (" +
          lesserFields[5] + ") is not greater or equal to 0");
      }
      if (lesserFields[5] >= FractionalSeconds) {
        throw new IllegalArgumentException("\"lesserFields[5]\" (" +
          lesserFields[5] + ") is not less than " + FractionalSeconds);
      }
      if (lesserFields[6] < -1439) {
        throw new IllegalArgumentException("\"lesserFields[6]\" (" +
          lesserFields[6] + ") is not greater or equal to " + (-1439));
      }
      if (lesserFields[6] > 1439) {
        throw new IllegalArgumentException("\"lesserFields[6]\" (" +
          lesserFields[6] + ") is not less or equal to 1439");
      }
    }

    public static String ToAtomDateTimeString(
      EInteger bigYear,
      int[] lesserFields) {
      if (lesserFields == null) {
        throw new NullPointerException("lesserFields");
      }
      if (lesserFields.length < 7) {
        throw new IllegalArgumentException(" (" + 7 + ") is not less or equal to " +
          lesserFields.length);
      }
      if (lesserFields.length < 7) {
        throw new IllegalArgumentException("\"lesserFields\" + \"'s length\" (" +
          lesserFields.length + ") is not greater or equal to 7");
      }
      if (lesserFields[6] != 0) {
        throw new UnsupportedOperationException(
          "Local time offsets not supported");
      }
      int smallYear = bigYear.ToInt32Checked();
      if (smallYear < 0) {
        throw new IllegalArgumentException("year(" + smallYear +
          ") is not greater or equal to 0");
      }
      if (smallYear > 9999) {
        throw new IllegalArgumentException("year(" + smallYear +
          ") is not less or equal to 9999");
      }
      CheckYearAndLesserFields(smallYear, lesserFields);
      int month = lesserFields[0];
      int intDay = lesserFields[1];
      int hour = lesserFields[2];
      int minute = lesserFields[3];
      int second = lesserFields[4];
      int fracSeconds = lesserFields[5];
      char[] charbuf = new char[32];
      charbuf[0] = (char)('0' + ((smallYear / 1000) % 10));
      charbuf[1] = (char)('0' + ((smallYear / 100) % 10));
      charbuf[2] = (char)('0' + ((smallYear / 10) % 10));
      charbuf[3] = (char)('0' + (smallYear % 10));
      charbuf[4] = '-';
      charbuf[5] = (char)('0' + ((month / 10) % 10));
      charbuf[6] = (char)('0' + (month % 10));
      charbuf[7] = '-';
      charbuf[8] = (char)('0' + ((intDay / 10) % 10));
      charbuf[9] = (char)('0' + (intDay % 10));
      charbuf[10] = 'T';
      charbuf[11] = (char)('0' + ((hour / 10) % 10));
      charbuf[12] = (char)('0' + (hour % 10));
      charbuf[13] = ':';
      charbuf[14] = (char)('0' + ((minute / 10) % 10));
      charbuf[15] = (char)('0' + (minute % 10));
      charbuf[16] = ':';
      charbuf[17] = (char)('0' + ((second / 10) % 10));
      charbuf[18] = (char)('0' + (second % 10));
      int charbufLength = 19;
      if (fracSeconds > 0) {
        charbuf[19] = '.';
        ++charbufLength;
        int digitdiv = FractionalSeconds;
        digitdiv /= 10;
        int index = 20;
        while (digitdiv > 0 && fracSeconds != 0) {
          int digit = (fracSeconds / digitdiv) % 10;
          fracSeconds -= digit * digitdiv;
          charbuf[index++] = (char)('0' + digit);
          ++charbufLength;
          digitdiv /= 10;
        }
        charbuf[index] = 'Z';
        ++charbufLength;
      } else {
        charbuf[19] = 'Z';
        ++charbufLength;
      }
      return new String(charbuf, 0, charbufLength);
    }

    public static long IntegerToDoubleBits(int i) {
      if (i == Integer.MIN_VALUE) {
        return (long)0xc1e0000000000000L;
      }
      if (i == 0) {
        return 0L;
      }
      long longmant = Math.abs(i);
      int expo = 0;
      while (longmant < (1L << 10)) {
        longmant <<= 42;
        expo -= 42;
      }
      while (longmant < (1L << 52)) {
        longmant <<= 1;
        --expo;
      }
      // Clear the high bits where the exponent and sign are
      longmant &= 0xfffffffffffffL;
      longmant |= (long)(expo + 1075) << 52;
      if (i < 0) {
        longmant |= (1L << 63);
      }
      /* System.out.println("" + i + "->" + (longmant==DoubleToInt64Bits(i)));
      */ return longmant;
    }

    public static boolean IsBeyondSafeRange(long bits) {
      // Absolute value of double is greater than 9007199254740991.0,
      // or value is NaN
      bits &= ~(1L << 63);
      return bits >= DoublePosInfinity || bits > 0x433fffffffffffffL;
    }

    public static boolean IsIntegerValue(long bits) {
      bits &= ~(1L << 63);
      if (bits == 0) {
        return true;
      }
      // Infinity and NaN
      if (bits >= DoublePosInfinity) {
        return false;
      }
      // Beyond noninteger range
      if ((bits >> 52) >= 0x433) {
        return true;
      }
      // Less than 1
      if ((bits >> 52) <= 0x3fe) {
        return false;
      }
      int exp = (int)(bits >> 52);
      long mant = bits & ((1L << 52) - 1);
      int shift = 52 - (exp - 0x3ff);
      return ((mant >> shift) << shift) == mant;
    }

    public static long GetIntegerValue(long bits) {
      long sgn;
      sgn = ((bits >> 63) != 0) ? -1L : 1L;
      bits &= ~(1L << 63);
      if (bits == 0) {
        return 0;
      }
      // Infinity and NaN
      if (bits >= DoublePosInfinity) {
        throw new UnsupportedOperationException();
      }
      // Beyond safe range
      if ((bits >> 52) >= 0x434) {
        throw new UnsupportedOperationException();
      }
      // Less than 1
      if ((bits >> 52) <= 0x3fe) {
        throw new UnsupportedOperationException();
      }
      int exp = (int)(bits >> 52);
      long mant = bits & ((1L << 52) - 1);
      mant |= 1L << 52;
      int shift = 52 - (exp - 0x3ff);
      return (mant >> shift) * sgn;
    }

/**
 * @deprecated
 */
@Deprecated
    public static EInteger EIntegerFromDouble(double dbl) {
      return EIntegerFromDoubleBits(Double.doubleToRawLongBits(dbl));
    }

    public static EInteger EIntegerFromDoubleBits(long lvalue) {
      int value0 = ((int)(lvalue & 0xffffffffL));
      int value1 = ((int)((lvalue >> 32) & 0xffffffffL));
      int floatExponent = (value1 >> 20) & 0x7ff;
      boolean neg = (value1 >> 31) != 0;
      if (floatExponent == 2047) {
        throw new ArithmeticException("Value is infinity or NaN");
      }
      value1 &= 0xfffff; // Mask out the exponent and sign
      if (floatExponent == 0) {
        ++floatExponent;
      } else {
        value1 |= 0x100000;
      }
      if ((value1 | value0) != 0) {
        while ((value0 & 1) == 0) {
          value0 >>= 1;
          value0 &= 0x7fffffff;
          value0 = (value0 | (value1 << 31));
          value1 >>= 1;
          ++floatExponent;
        }
      }
      floatExponent -= 1075;
      byte[] bytes = new byte[9];
      EInteger bigmantissa;
      bytes[0] = (byte)(value0 & 0xff);
      bytes[1] = (byte)((value0 >> 8) & 0xff);
      bytes[2] = (byte)((value0 >> 16) & 0xff);
      bytes[3] = (byte)((value0 >> 24) & 0xff);
      bytes[4] = (byte)(value1 & 0xff);
      bytes[5] = (byte)((value1 >> 8) & 0xff);
      bytes[6] = (byte)((value1 >> 16) & 0xff);
      bytes[7] = (byte)((value1 >> 24) & 0xff);
      bytes[8] = 0;
      bigmantissa = EInteger.FromBytes(bytes, true);
      if (floatExponent == 0) {
        if (neg) {
          bigmantissa = bigmantissa.Negate();
        }
        return bigmantissa;
      }
      if (floatExponent > 0) {
        // Value is an integer
        bigmantissa = bigmantissa.ShiftLeft(floatExponent);
        if (neg) {
          bigmantissa = bigmantissa.Negate();
        }
        return bigmantissa;
      } else {
        // Value has a fractional part
        int exp = -floatExponent;
        bigmantissa = bigmantissa.ShiftRight(exp);
        if (neg) {
          bigmantissa = bigmantissa.Negate();
        }
        return bigmantissa;
      }
    }

    public static boolean DoubleBitsNaN(long bits) {
      // Is NaN
      bits &= ~(1L << 63);
      return bits > (0x7ffL << 52);
    }

    public static boolean DoubleBitsFinite(long bits) {
      // Neither NaN nor infinity
      bits &= ~(1L << 63);
      return bits < (0x7ffL << 52);
    }

    private static int RoundedShift(long mant, int shift) {
      long mask = (1L << shift) - 1;
      long half = 1L << (shift - 1);
      long shifted = mant >> shift;
      long masked = mant & mask;
      return (masked > half || (masked == half && (shifted & 1L) != 0)) ?
        ((int)shifted) + 1 : ((int)shifted);
    }

    private static int RoundedShift(int mant, int shift) {
      int mask = (1 << shift) - 1;
      int half = 1 << (shift - 1);
      int shifted = mant >> shift;
      int masked = mant & mask;
      return (masked > half || (masked == half && (shifted & 1) != 0)) ?
        shifted + 1 : shifted;
    }

    public static int DoubleToHalfPrecisionIfSameValue(long bits) {
      int exp = ((int)((bits >> 52) & 0x7ffL));
      long mant = bits & 0xfffffffffffffL;
      int sign = ((int)(bits >> 48)) & (1 << 15);
      int sexp = exp - 1008;
      // System.out.println("bits={0:X8}, exp=" + exp + " sexp=" + (sexp),bits);
      if (exp == 2047) { // Infinity and NaN
        int newmant = ((int)(mant >> 42));
        return ((mant & ((1L << 42) - 1)) == 0) ? (sign | 0x7c00 | newmant) :
          -1;
      } else if (exp == 0 && mant == 0) { // positive or negative zero
        return sign;
      } else if (sexp >= 31) { // overflow
        return -1;
      } else if (sexp < -10) { // underflow
        return -1;
      } else if (sexp > 0) { // normal
        return ((mant & ((1L << 42) - 1)) == 0) ? (sign | (sexp << 10) |
          RoundedShift(mant, 42)) : -1;
      } else { // subnormal and nonzero
        int rs = RoundedShift(mant | (1L << 52), 42 - (sexp - 1));
        // System.out.println("mant=" + mant + " rs=" + (rs));
        return sexp == -10 && rs == 0 ? -1 :
          ((mant & ((1L << (42 - (sexp - 1))) - 1)) == 0) ? (sign | rs) :
          -1;
      }
    }

    public static boolean DoubleRetainsSameValueInSingle(long bits) {
      if ((bits & ~(1L << 63)) == 0) {
        return true;
      }
      int exp = ((int)((bits >> 52) & 0x7ffL));
      long mant = bits & 0xfffffffffffffL;
      int sexp = exp - 896;
      // System.out.println("sng mant={0:X8}, exp=" + exp + " sexp=" + (sexp));
      return exp == 2047 ? (mant & ((1L << 29) - 1)) == 0 :
        sexp >= -23 && sexp < 255 && (sexp > 0 ?
          (mant & ((1L << 29) - 1)) == 0 : sexp == -23 ?
          (mant & ((1L << (29 - (sexp - 1))) - 1)) == 0 &&
          RoundedShift(mant | (1L << 52), 29 - (sexp - 1)) != 0 :
          (mant & ((1L << (29 - (sexp - 1))) - 1)) == 0);
    }

    // NOTE: Rounds to nearest, ties to even
    public static int SingleToRoundedHalfPrecision(int bits) {
      int exp = ((bits >> 23) & 0xff);
      int mant = bits & 0x7fffff;
      int sign = (bits >> 16) & (1 << 15);
      int sexp = exp - 112;
      if (exp == 255) { // Infinity and NaN
        int newmant = (mant >> 13);
        return (mant != 0 && newmant == 0) ?
          // signaling NaN truncated to have mantissa 0
          (sign | 0x7c01) : (sign | 0x7c00 | newmant);
      } else { // overflow
        return sexp >= 31 ? sign | 0x7c00 : sexp < -10 ? sign :
          sexp > 0 ? sign | (sexp << 10) | RoundedShift(mant, 13) :
          sign | RoundedShift(mant | (1 << 23), 13 - (sexp - 1));
      }
    }

    // NOTE: Rounds to nearest, ties to even
    public static int DoubleToRoundedHalfPrecision(long bits) {
      int exp = ((int)((bits >> 52) & 0x7ffL));
      long mant = bits & 0xfffffffffffffL;
      int sign = ((int)(bits >> 48)) & (1 << 15);
      int sexp = exp - 1008;
      if (exp == 2047) { // Infinity and NaN
        int newmant = ((int)(mant >> 42));
        return (mant != 0 && newmant == 0) ?
          // signaling NaN truncated to have mantissa 0
          (sign | 0x7c01) : (sign | 0x7c00 | newmant);
      } else { // overflow
        return sexp >= 31 ? sign | 0x7c00 : sexp < -10 ? sign :
          sexp > 0 ? sign | (sexp << 10) | RoundedShift(mant, 42) :
          sign | RoundedShift(mant | (1L << 52), 42 - (sexp - 1));
      }
    }

    // NOTE: Rounds to nearest, ties to even
    public static int DoubleToRoundedSinglePrecision(long bits) {
      int exp = ((int)((bits >> 52) & 0x7ffL));
      long mant = bits & 0xfffffffffffffL;
      int sign = ((int)(bits >> 32)) & (1 << 31);
      int sexp = exp - 896;
      if (exp == 2047) { // Infinity and NaN
        int newmant = ((int)(mant >> 29));
        return (mant != 0 && newmant == 0) ?
          // signaling NaN truncated to have mantissa 0
          (sign | 0x7f800001) : (sign | 0x7f800000 | newmant);
      } else { // overflow
        return sexp >= 255 ? sign | 0x7f800000 : sexp < -23 ? sign :
          sexp > 0 ? sign | (sexp << 23) | RoundedShift(mant, 29) :
          sign | RoundedShift(mant | (1L << 52), 29 - (sexp - 1));
      }
    }

    public static int SingleToHalfPrecisionIfSameValue(int bits) {
      int exp = (bits >> 23) & 0xff;
      int mant = bits & 0x7fffff;
      int sign = (bits >> 16) & 0x8000;
      if (exp == 255) { // Infinity and NaN
        return (bits & 0x1fff) == 0 ? sign + 0x7c00 + (mant >> 13) : -1;
      } else if (exp == 0) { // Subnormal
        return (bits & 0x1fff) == 0 ? sign + (mant >> 13) : -1;
      }
      if (exp <= 102 || exp >= 143) { // Overflow or underflow
        return -1;
      } else if (exp <= 112) { // Subnormal
        int shift = 126 - exp;
        int rs = (1024 >> (145 - exp)) + (mant >> shift);
        return (mant != 0 && exp == 103) ? (-1) : ((bits & ((1 << shift) -
          1)) == 0 ? sign + rs : -1);
      } else {
        return (bits & 0x1fff) == 0 ? sign + ((exp - 112) << 10) +
          -(mant >> 13) : -1;
      }
    }

    public static long SingleToDoublePrecision(int bits) {
      long negvalue = (long)((bits >> 31) & 1) << 63;
      int exp = (bits >> 23) & 0xff;
      int mant = bits & 0x7fffff;
      long value;
      if (exp == 255) {
        value = 0x7ff0000000000000L | ((long)mant << 29) | negvalue;
      } else if (exp == 0) {
        if (mant == 0) {
          value = negvalue;
        } else {
          ++exp;
          while (mant < 0x800000) {
            mant <<= 1;
            --exp;
          }
          value = ((long)(exp + 896) << 52) | ((long)(mant & 0x7fffff) <<
            29) | negvalue;
        }
      } else {
        value = ((long)(exp + 896) << 52) | ((long)mant << 29) | negvalue;
      }
      return value;
    }

    public static long HalfToDoublePrecision(int bits) {
      long negvalue = (long)(bits & 0x8000) << 48;
      int exp = (bits >> 10) & 31;
      int mant = bits & 0x3ff;
      long value;
      if (exp == 31) {
        value = 0x7ff0000000000000L | ((long)mant << 42) | negvalue;
      } else if (exp == 0) {
        if (mant == 0) {
          value = negvalue;
        } else {
          ++exp;
          while (mant < 0x400) {
            mant <<= 1;
            --exp;
          }
          value = ((long)(exp + 1008) << 52) | ((long)(mant & 0x3ff) << 42) |
            negvalue;
        }
      } else {
        value = ((long)(exp + 1008) << 52) | ((long)mant << 42) | negvalue;
      }
      return value;
    }
  }
