package com.upokecenter.test;

import com.upokecenter.util.*;

  final class StringAndBigInt {
    private static final String ValueDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String ValueDigitsLower =
          "0123456789abcdefghijklmnopqrstuvwxyz";

    private String stringValue;

    public final String getStringValue() {
        return this.stringValue;
      }

    private BigInteger bigintValue;

    public final BigInteger getBigIntValue() {
        return this.bigintValue;
      }

    public static StringAndBigInt Generate(FastRandom rand, int radix) {
      BigInteger bv = BigInteger.valueOf(0);
      StringAndBigInt sabi = new StringAndBigInt();
      int numDigits = 1 + rand.NextValue(100);
      boolean negative = rand.NextValue(2) == 0;
      StringBuilder builder = new StringBuilder();
      if (negative) {
        builder.append('-');
      }
      for (int i = 0; i < numDigits; ++i) {
        int digit = rand.NextValue(radix);
        if (rand.NextValue(2) == 0) {
          builder.append(ValueDigits.charAt(digit));
        } else {
          builder.append(ValueDigitsLower.charAt(digit));
        }
        // BigInteger bigintTmp = BigInteger.valueOf(radix);
        // bv = bv.multiply(bigintTmp);
        // bigintTmp = BigInteger.valueOf(digit);
        // bv = bv.add(bigintTmp);
      }
      // if (negative) {
      // bv = bv.negate();
      // }
      bv = BigInteger.fromRadixString(builder.toString(), radix);
      sabi.bigintValue = bv;
      sabi.stringValue = builder.toString();
      return sabi;
    }
  }
