package com.upokecenter.cbor;
/*
Written by Peter O.
Any copyright is dedicated to the Public Domain.
http://creativecommons.org/publicdomain/zero/1.0/
If you like this, you should donate to Peter O.
at: http://peteroupc.github.io/
 */

import java.io.*;

import com.upokecenter.util.*;
import com.upokecenter.numbers.*;

  final class StringOutput {
    private final StringBuilder builder;
    private final OutputStream outputStream;

    public StringOutput(StringBuilder builder) {
      this.builder = builder;
      this.outputStream = null;
    }

    public StringOutput(OutputStream outputStream) {
      this.outputStream = outputStream;
      this.builder = null;
    }

    public void WriteString(String str) throws java.io.IOException {
      if (this.outputStream != null) {
        if (str.length() == 1) {
          this.WriteCodePoint((int)str.charAt(0));
        } else {
          if (DataUtilities.WriteUtf8(
            str,
            0,
            str.length(),
            this.outputStream,
            false) < 0) {
            throw new IllegalArgumentException("str has an unpaired surrogate");
          }
        }
      } else {
        this.builder.append(str);
      }
    }

    public void WriteString(String str, int index, int length) throws java.io.IOException {
      if (this.outputStream == null) {
        this.builder.append(str, index, (index)+(length));
      } else {
        if (length == 1) {
          this.WriteCodePoint((int)str.charAt(index));
        } else {
          if (
            DataUtilities.WriteUtf8(
              str,
              index,
              length,
              this.outputStream,
              false) < 0) {
            throw new IllegalArgumentException("str has an unpaired surrogate");
          }
        }
      }
    }

    public void WriteCodePoint(int codePoint) throws java.io.IOException {
      if ((codePoint >> 7) == 0) {
        // Code point is in the Basic Latin range (U+0000 to U+007F)
        if (this.outputStream == null) {
          this.builder.append((char)codePoint);
        } else {
 this.outputStream.write((byte)codePoint);
}
        return;
      }
      if (codePoint < 0) {
        throw new IllegalArgumentException("codePoint(" + codePoint +
          ") is less than 0");
      }
      if (codePoint > 0x10ffff) {
        throw new IllegalArgumentException("codePoint(" + codePoint +
          ") is more than " + 0x10ffff);
      }
      if (this.outputStream != null) {
        if (codePoint < 0x80) {
          this.outputStream.write((byte)codePoint);
        } else if (codePoint <= 0x7ff) {
          this.outputStream.write((byte)(0xc0 | ((codePoint >> 6) &
                0x1f)));
          this.outputStream.write((byte)(0x80 | (codePoint & 0x3f)));
        } else if (codePoint <= 0xffff) {
          if ((codePoint & 0xf800) == 0xd800) {
            throw new IllegalArgumentException("ch is a surrogate");
          }
          this.outputStream.write((byte)(0xe0 | ((codePoint >> 12) &
                0x0f)));
          this.outputStream.write((byte)(0x80 | ((codePoint >> 6) &
                0x3f)));
          this.outputStream.write((byte)(0x80 | (codePoint & 0x3f)));
        } else {
          this.outputStream.write((byte)(0xf0 | ((codePoint >> 18) &
                0x07)));
          this.outputStream.write((byte)(0x80 | ((codePoint >> 12) &
                0x3f)));
          this.outputStream.write((byte)(0x80 | ((codePoint >> 6) &
                0x3f)));
          this.outputStream.write((byte)(0x80 | (codePoint & 0x3f)));
        }
      } else {
        if ((codePoint & 0xfff800) == 0xd800) {
          throw new IllegalArgumentException("ch is a surrogate");
        }
        if (codePoint <= 0xffff) {
          { this.builder.append((char)codePoint);
          }
        } else if (codePoint <= 0x10ffff) {
          this.builder.append((char)((((codePoint - 0x10000) >> 10) & 0x3ff) |
              0xd800));
          this.builder.append((char)(((codePoint - 0x10000) & 0x3ff) |
              0xdc00));
        }
      }
    }
  }
