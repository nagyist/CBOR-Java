package com.upokecenter.test;

import java.util.*;

import com.upokecenter.util.*;

  public final class JSONGenerator {
    private static final class ByteWriter {
      private byte[] bytes = new byte[64];

      public ByteWriter Write(int b) {
        if (this.getByteLength() < this.bytes.length) {
          this.bytes[this.getByteLength()] = (byte)b;
          this.setByteLength(1 + this.getByteLength());
        } else {
          byte[] newbytes = new byte[this.bytes.length * 2];
          System.arraycopy(this.bytes, 0, newbytes, 0, this.bytes.length);
          this.bytes = newbytes;
          this.bytes[this.getByteLength()] = (byte)b;
          this.setByteLength(1 + this.getByteLength());
        }
        return this;
      }

      public final int getByteLength() { return propVarbytelength; }
public final void setByteLength(int value) { propVarbytelength = value; }
private int propVarbytelength;

      public byte[] ToBytes() {
        byte[] newbytes = new byte[this.getByteLength()];
        System.arraycopy(this.bytes, 0, newbytes, 0, this.getByteLength());
        return newbytes;
      }
    }

    private static final int[] ValueMajorTypes = {
      0, 1, 3, 4, 5,
    };

    private static final int[] ValueMajorTypesTop = {
      0, 1, 3, 4, 4, 4, 4, 4, 5, 5, 5,
      5, 5, 5, 5, 5, 5, 5, 5, 5,
    };

    private static final int[] ValueEscapes = {
      '\\', '/', '\"',
      'b', 'f', 'n', 'r', 't', 'u',
    };

    private static final char[] ValueEscapeChars = {
      '\\', '/', '\"',
      (char)8, (char)12, '\n', '\r', '\t', (char)0,
    };

    private static void GenerateCodeUnit(
      IRandomGenExtended ra,
      ByteWriter bs,
      int cu) {
      int c;
      int shift = 12;
      for (int i = 0; i < 4; ++i) {
        c = (cu >> shift) & 0xf;
        int bw = c < 10 ? (0x30 + c) : (0x41 + (c - 10) +
          (ra.GetInt32(2) * 0x20));
        bs.Write(bw);
        shift -= 4;
      }
    }

    private static void GenerateUtf16(
      IRandomGenExtended ra,
      ByteWriter bs,
      StringBuilder sb) {
      int r = ra.GetInt32(0x110000 - 0x800);
      if (r >= 0xd800) {
        r += 0x800;
      }
      if (r >= 0x10000) {
        int rc = (((r - 0x10000) >> 10) & 0x3ff) | 0xd800;
        GenerateCodeUnit(ra, bs, rc);
        if (sb != null) {
          sb.append((char)rc);
        }
        bs.Write('\\');
        bs.Write('u');
        rc = ((r - 0x10000) & 0x3ff) | 0xdc00;
        GenerateCodeUnit(ra, bs, rc);
        if (sb != null) {
          sb.append((char)rc);
        }
      } else {
        GenerateCodeUnit(ra, bs, r);
        if (sb != null) {
          sb.append((char)r);
        }
      }
    }

    private static void GenerateWhitespace(
      IRandomGenExtended ra,
      ByteWriter bs) {
      if (ra.GetInt32(10) == 0) {
        int len = ra.GetInt32(20);
        int[] ws = { 0x09, 0x0d, 0x0a, 0x20 };
        if (ra.GetInt32(100) == 0) {
          len = ra.GetInt32(100);
        }
        for (int i = 0; i < len; ++i) {
          bs.Write(ws[ra.GetInt32(ws.length)]);
        }
      }
    }

    private static void GenerateJsonNumber(
      IRandomGenExtended ra,
      ByteWriter bs) {
      if (ra.GetInt32(2) == 0) {
        bs.Write('-');
      }
      boolean shortLen = ra.GetInt32(100) < 75;
      int len;
      if (ra.GetInt32(100) < 2) {
        // Integer part is zero
        bs.Write(0x30);
      } else {
        // Integer part
        len = shortLen ? ra.GetInt32(10) + 1 :
          (ra.GetInt32(2000) * ra.GetInt32(2000) / 2000) + 1;
        bs.Write(0x31 + ra.GetInt32(9));
        for (int i = 0; i < len; ++i) {
          bs.Write(0x30 + ra.GetInt32(10));
        }
      }
      // Fractional part
      if (ra.GetInt32(2) == 0) {
        bs.Write(0x2e);
        len = shortLen ? ra.GetInt32(10) + 1 :
          (ra.GetInt32(2000) * ra.GetInt32(2000) / 2000) + 1;
        for (int i = 0; i < len; ++i) {
          bs.Write(0x30 + ra.GetInt32(10));
        }
      }
      if (ra.GetInt32(2) == 0) {
        int rr = ra.GetInt32(3);
        if (rr == 0) {
          bs.Write('E');
        } else if (rr == 1) {
          bs.Write('E').Write('+');
        } else if (rr == 2) {
          bs.Write('E').Write('-');
        }
        len = 1 + ra.GetInt32(5);
        if (ra.GetInt32(10) == 0) {
          len = 1 + (ra.GetInt32(2000) * ra.GetInt32(2000) / 2000);
        }
        for (int i = 0; i < len; ++i) {
          bs.Write(0x30 + ra.GetInt32(10));
        }
      }
    }

    private static int GenerateUtf8(
      IRandomGenExtended ra,
      ByteWriter bs,
      StringBuilder sb,
      int len) {
      int r = ra.GetInt32(3);
      int r2, r3, r4;
      if (r == 0 && len >= 2) {
        r = 0xc2 + ra.GetInt32(0xdf - 0xc2 + 1);
        bs.Write(r);
        r2 = 0x80 + ra.GetInt32(0x40);
        bs.Write(r2);
        if (sb != null) {
          sb.append((char)(((r - 0x80) << 6) | r2));
        }
        return 2;
      } else if (r == 1 && len >= 3) {
        r = 0xe0 + ra.GetInt32(16);
        bs.Write(r);
        int lower = (r == 0xe0) ? 0xa0 : 0x80;
        int upper = (r == 0xed) ? 0x9f : 0xbf;
        r2 = lower + ra.GetInt32(upper - lower + 1);
        bs.Write(r2);
        r3 = 0x80 + ra.GetInt32(0x40);
        bs.Write(r3);
        if (sb != null) {
          sb.append((char)(((r - 0x80) << 12) | ((r2 - 0x80) << 6) | r3));
        }
        return 3;
      } else if (r == 2 && len >= 4) {
        r = 0xf0 + ra.GetInt32(5);
        bs.Write(r);
        int lower = (r == 0xf0) ? 0x90 : 0x80;
        int upper = (r == 0xf4) ? 0x8f : 0xbf;
        r2 = lower + ra.GetInt32(upper - lower + 1);
        bs.Write(r2);
        r3 = 0x80 + ra.GetInt32(0x40);
        bs.Write(r3);
        r4 = 0x80 + ra.GetInt32(0x40);
        bs.Write(r4);
        r = ((r - 0x80) << 18) | ((r2 - 0x80) << 12) | ((r3 - 0x80) << 6) | r4;
        if (sb != null) {
          sb.append((char)(((r - 0x10000) >> 10) | 0xd800));
          sb.append((char)(((r - 0x10000) & 0x3ff) | 0xdc00));
        }
        return 4;
      }
      return 0;
    }

    private static void GenerateJsonKey(
      IRandomGenExtended ra,
      ByteWriter bskey,
      int depth,
      Map<String, String> keys) {
      while (true) {
        StringBuilder sb = new StringBuilder();
        ByteWriter bs = new ByteWriter();
        int len = ra.GetInt32(1000) * ra.GetInt32(1000);
        len /= 1000;
        if (ra.GetInt32(50) == 0 && depth < 2) {
          // Exponential curve that strongly favors small numbers
          long v = (long)ra.GetInt32(1000000) * ra.GetInt32(1000000);
          len = (int)(v / 1000000);
        }
        bs.Write(0x22);
        for (int i = 0; i < len;) {
          int r = ra.GetInt32(10);
          if (r > 2) {
            int x = 0x20 + ra.GetInt32(60);
            if (x == '\"') {
              bs.Write('\\').Write(x);
              sb.append('\"');
            } else if (x == '\\') {
              bs.Write('\\').Write(x);
              sb.append('\\');
            } else {
              bs.Write(x);
              sb.append((char)x);
            }
            ++i;
          } else if (r == 1) {
            bs.Write('\\');
            int escindex = ra.GetInt32(ValueEscapes.length);
            int esc = ValueEscapes[escindex];
            bs.Write(esc);
            if (esc == 'u') {
              GenerateUtf16(ra, bs, sb);
            } else {
              sb.append(ValueEscapeChars[escindex]);
            }
          } else {
            GenerateUtf8(ra, bs, sb, len - i);
          }
        }
        bs.Write(0x22);
        String key = sb.toString();
        if (!keys.containsKey(key)) {
          keys.put(key,"");
          byte[] bytes = bs.ToBytes();
          for (int i = 0; i < bytes.length; ++i) {
            bskey.Write(bytes[i] & 0xff);
          }
          return;
        }
      }
    }

    private static void GenerateJsonString(
      IRandomGenExtended ra,
      ByteWriter bs,
      int depth) {
      int len = ra.GetInt32(1000) * ra.GetInt32(1000);
      len /= 1000;
      if (ra.GetInt32(50) == 0 && depth < 2) {
        // Exponential curve that strongly favors small numbers
        long v = (long)ra.GetInt32(1000000) * ra.GetInt32(1000000);
        len = (int)(v / 1000000);
      }
      bs.Write(0x22);
      for (int i = 0; i < len;) {
        int r = ra.GetInt32(10);
        if (r > 2) {
          int x = 0x20 + ra.GetInt32(60);
          if (x == '\"' || x == '\\') {
            bs.Write('\\');
          }
          bs.Write(x);
          ++i;
        } else if (r == 1) {
          bs.Write('\\');
          int esc = ValueEscapes[ra.GetInt32(ValueEscapes.length)];
          bs.Write(esc);
          if (esc == 'u') {
            GenerateUtf16(ra, bs, null);
          }
        } else {
          GenerateUtf8(ra, bs, null, len - i);
        }
      }
      bs.Write(0x22);
    }

    private void Generate(IRandomGenExtended r, int depth, ByteWriter bs) {
      int majorType;
      majorType = ValueMajorTypes[r.GetInt32(ValueMajorTypes.length)];
      if (depth == 0) {
        majorType = ValueMajorTypesTop[r.GetInt32(ValueMajorTypes.length)];
      }
      GenerateWhitespace(r, bs);
      if (bs.getByteLength() > 2000000) {
        majorType = r.GetInt32(2); // either 0 or 1
      }
      if (majorType == 0) {
        GenerateJsonNumber(r, bs);
      } else if (majorType == 1) {
        switch (r.GetInt32(3)) {
          case 0:
            bs.Write('t').Write('r').Write('u').Write(
              'e');
            break;
          case 1:
            bs.Write('n').Write('u').Write('l').Write(
              'l');
            break;
          case 2:

            bs.Write('f').Write('a').Write('l').Write(
              's').Write(
                'e');
            break;
        }
      } else if (majorType == 3) {
        GenerateJsonString(r, bs, depth);
      } else if (majorType == 4 || majorType == 5) {
        int len = r.GetInt32(8);
        if (r.GetInt32(50) == 0 && depth < 2) {
          long v = (long)r.GetInt32(1000) * r.GetInt32(1000);
          len = (int)(v / 1000);
        }
        if (depth > 6) {
          len = r.GetInt32(100) == 0 ? 1 : 0;
        }
        if (majorType == 4) {
          bs.Write('[');
          for (int i = 0; i < len; ++i) {
            if (i > 0) {
              bs.Write(',');
            }
            this.Generate(r, depth + 1, bs);
          }
          bs.Write(']');
        }
        if (majorType == 5) {
          bs.Write('{');
          HashMap<String, String> keys = new HashMap<String, String>();
          for (int i = 0; i < len; ++i) {
            if (i > 0) {
              bs.Write(',');
            }
            GenerateWhitespace(r, bs);
            GenerateJsonKey(r, bs, depth, keys);
            GenerateWhitespace(r, bs);
            bs.Write(':');
            GenerateWhitespace(r, bs);
            this.Generate(r, depth + 1, bs);
          }
          bs.Write('}');
        }
      }
      GenerateWhitespace(r, bs);
    }

    public byte[] Generate(IRandomGenExtended random) {
      ByteWriter bs = new ByteWriter();
      if (random == null) {
        throw new NullPointerException("random");
      }
      this.Generate(random, 0, bs);
      byte[] ret = bs.ToBytes();
      return ret;
    }
  }
