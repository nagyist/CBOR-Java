# com.upokecenter.util.ExtendedFloat

    @Deprecated public final class ExtendedFloat extends Object implements Comparable<ExtendedFloat>

Deprecated.&nbsp;
<div class='block'>Use EFloat from PeterO.Numbers/com.upokecenter.numbers and the output of
this class's toString method.</div>

## Fields

* `static ExtendedFloat NaN`<br>
 Deprecated.  A not-a-number value.
* `static ExtendedFloat NegativeInfinity`<br>
 Deprecated.  Negative infinity, less than any other number.
* `static ExtendedFloat NegativeZero`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `static ExtendedFloat One`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `static ExtendedFloat PositiveInfinity`<br>
 Deprecated.  Positive infinity, greater than any other number.
* `static ExtendedFloat SignalingNaN`<br>
 Deprecated.  A not-a-number value that signals an invalid operation flag when it's
 passed as an argument to any arithmetic operation in
 arbitrary-precision binary float.
* `static ExtendedFloat Ten`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `static ExtendedFloat Zero`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

## Methods

* `int compareTo(ExtendedFloat other)`<br>
 Deprecated.  Not documented yet.
* `static ExtendedFloat Create(BigInteger mantissa,
      BigInteger exponent)`<br>
 Deprecated.  Creates a number with the value exponent*2^mantissa.
* `static ExtendedFloat Create(int mantissaSmall,
      int exponentSmall)`<br>
 Deprecated.  Creates a number with the value exponent*2^mantissa.
* `boolean equals(ExtendedFloat other)`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `boolean equals(Object obj)`<br>
 Deprecated.  Determines whether this object's mantissa and exponent are equal to
 those of another object and that other object is an
 arbitrary-precision decimal number.
* `boolean EqualsInternal(ExtendedFloat otherValue)`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `static ExtendedFloat FromString(String str)`<br>
 Deprecated.  Not documented yet.
* `static ExtendedFloat FromString(String str,
          int offset,
          int length,
          PrecisionContext ctx)`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `BigInteger getExponent()`<br>
 Deprecated.  Gets this object's exponent.
* `BigInteger getMantissa()`<br>
 Deprecated.  Gets this object's un-scaled value.
* `BigInteger getUnsignedMantissa()`<br>
 Deprecated.  Gets the absolute value of this object's un-scaled value.
* `int hashCode()`<br>
 Deprecated.  Calculates this object's hash code.
* `boolean IsInfinity()`<br>
 Deprecated.  Gets a value indicating whether this object is positive or negative
 infinity.
* `boolean IsNaN()`<br>
 Deprecated.  Returns whether this object is a not-a-number value.
* `boolean isNegative()`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `boolean IsNegativeInfinity()`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `boolean IsPositiveInfinity()`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `boolean IsQuietNaN()`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `boolean IsSignalingNaN()`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `int signum()`<br>
 Deprecated.
Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
 Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
* `String toString()`<br>
 Deprecated.  Converts this value to a string.

## Field Details

### One
    @Deprecated public static final ExtendedFloat One
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
### Zero
    @Deprecated public static final ExtendedFloat Zero
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
### NegativeZero
    @Deprecated public static final ExtendedFloat NegativeZero
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
### Ten
    @Deprecated public static final ExtendedFloat Ten
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.
### NaN
    public static final ExtendedFloat NaN
Deprecated.&nbsp;
### SignalingNaN
    public static final ExtendedFloat SignalingNaN
Deprecated.&nbsp;
### PositiveInfinity
    public static final ExtendedFloat PositiveInfinity
Deprecated.&nbsp;
### NegativeInfinity
    public static final ExtendedFloat NegativeInfinity
Deprecated.&nbsp;
## Method Details

### getExponent
    public final BigInteger getExponent()
Deprecated.&nbsp;

**Returns:**

* This object's exponent. This object's value will be an integer if
 the exponent is positive or zero.

### getUnsignedMantissa
    public final BigInteger getUnsignedMantissa()
Deprecated.&nbsp;

**Returns:**

* The absolute value of this object's un-scaled value.

### getMantissa
    public final BigInteger getMantissa()
Deprecated.&nbsp;

**Returns:**

* This object's un-scaled value. Will be negative if this object's
 value is negative (including a negative NaN).

### EqualsInternal
    @Deprecated public boolean EqualsInternal(ExtendedFloat otherValue)
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Parameters:**

* <code>otherValue</code> - An arbitrary-precision binary float.

**Returns:**

* <code>true</code> if this object's mantissa and exponent are equal to
 those of another object; otherwise, <code>false</code> .

**Throws:**

* <code>NullPointerException</code> - The parameter <code>otherValue</code> is
 null.

### equals
    @Deprecated public boolean equals(ExtendedFloat other)
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Parameters:**

* <code>other</code> - An arbitrary-precision binary float.

**Returns:**

* <code>true</code> if this object's mantissa and exponent are equal to
 those of another object; otherwise, <code>false</code> .

**Throws:**

* <code>NullPointerException</code> - The parameter <code>other</code> is null.

### equals
    public boolean equals(Object obj)
Deprecated.&nbsp;

**Overrides:**

* <code>equals</code>&nbsp;in class&nbsp;<code>Object</code>

**Parameters:**

* <code>obj</code> - An arbitrary object.

**Returns:**

* <code>true</code> if the objects are equal; otherwise, <code>false</code>.

### hashCode
    public int hashCode()
Deprecated.&nbsp;

**Overrides:**

* <code>hashCode</code>&nbsp;in class&nbsp;<code>Object</code>

**Returns:**

* This object's hash code.

### Create
    public static ExtendedFloat Create(int mantissaSmall, int exponentSmall)
Deprecated.&nbsp;

**Parameters:**

* <code>mantissaSmall</code> - The un-scaled value.

* <code>exponentSmall</code> - The binary exponent.

**Returns:**

* An arbitrary-precision binary float.

### Create
    public static ExtendedFloat Create(BigInteger mantissa, BigInteger exponent)
Deprecated.&nbsp;

**Parameters:**

* <code>mantissa</code> - The un-scaled value.

* <code>exponent</code> - The binary exponent.

**Returns:**

* An arbitrary-precision binary float.

**Throws:**

* <code>NullPointerException</code> - The parameter <code>mantissa</code> or
 <code>exponent</code> is null.

### FromString
    @Deprecated public static ExtendedFloat FromString(String str, int offset, int length, PrecisionContext ctx)
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Parameters:**

* <code>str</code> - A text string.

* <code>offset</code> - A zero-based index showing where the desired portion of <code>str</code> begins.

* <code>length</code> - The length, in code units, of the desired portion of <code>str</code> (but not more than <code>str</code> 's length).

* <code>ctx</code> - A PrecisionContext object specifying the precision, rounding, and
 exponent range to apply to the parsed number. Can be null.

**Returns:**

* The parsed number, converted to arbitrary-precision binary float.

**Throws:**

* <code>NullPointerException</code> - The parameter <code>str</code> is null.

* <code>IllegalArgumentException</code> - Either <code>offset</code> or <code>length</code> is
 less than 0 or greater than <code>str</code> 's length, or <code>str</code> ' s
 length minus <code>offset</code> is less than <code>length</code>.

### FromString
    public static ExtendedFloat FromString(String str)
Deprecated.&nbsp;

**Parameters:**

* <code>str</code> - The parameter <code>str</code> is not documented yet.

**Returns:**

* An ExtendedFloat object.

### toString
    public String toString()
Deprecated.&nbsp;

**Overrides:**

* <code>toString</code>&nbsp;in class&nbsp;<code>Object</code>

**Returns:**

* A string representation of this object. The value is converted to
 decimal and the decimal form of this number's value is returned.

### IsNegativeInfinity
    @Deprecated public boolean IsNegativeInfinity()
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Returns:**

* <code>true</code> if this object is negative infinity; otherwise, <code>false</code>.

### IsPositiveInfinity
    @Deprecated public boolean IsPositiveInfinity()
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Returns:**

* <code>true</code> if this object is positive infinity; otherwise, <code>false</code>.

### IsNaN
    public boolean IsNaN()
Deprecated.&nbsp;

**Returns:**

* <code>true</code> if this object is a not-a-number value; otherwise,
 <code>false</code>.

### IsInfinity
    public boolean IsInfinity()
Deprecated.&nbsp;

**Returns:**

* <code>true</code> if this object is positive or negative infinity;
 otherwise, <code>false</code>.

### isNegative
    @Deprecated public final boolean isNegative()
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Returns:**

* <code>true</code> If this object is negative, including negative zero;
 otherwise, <code>false</code>.

### IsQuietNaN
    @Deprecated public boolean IsQuietNaN()
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Returns:**

* <code>true</code> if this object is a quiet not-a-number value;
 otherwise, <code>false</code>.

### IsSignalingNaN
    @Deprecated public boolean IsSignalingNaN()
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Returns:**

* <code>true</code> if this object is a signaling not-a-number value;
 otherwise, <code>false</code>.

### compareTo
    public int compareTo(ExtendedFloat other)
Deprecated.&nbsp;

**Specified by:**

* <code>compareTo</code>&nbsp;in interface&nbsp;<code>Comparable&lt;ExtendedFloat&gt;</code>

**Parameters:**

* <code>other</code> - The parameter <code>other</code> is not documented yet.

**Returns:**

* A 32-bit signed integer.

### signum
    @Deprecated public final int signum()
Deprecated.&nbsp;Use EFloat from PeterO.Numbers/com.upokecenter.numbers.

**Returns:**

* This value's sign: -1 if negative; 1 if positive; 0 if zero.
