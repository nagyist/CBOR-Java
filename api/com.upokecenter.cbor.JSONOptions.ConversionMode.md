# com.upokecenter.cbor.JSONOptions.ConversionMode

    public static enum JSONOptions.ConversionMode extends java.lang.Enum<JSONOptions.ConversionMode>

Specifies how JSON numbers are converted to CBOR when decoding JSON.

## Enum Constants

* `Decimal128 CBORObject.FromObject(EDecimal)`<br>
 JSON numbers are decoded to CBOR as their closest-rounded approximation to
 an IEEE 854 decimal128 value, using the rules for the EDecimal
 form of that approximation as given in the
 CBORObject.FromObject(EDecimal) method.
* `Double`<br>
 JSON numbers are decoded to CBOR as their closest-rounded approximation as
 64-bit binary floating-point numbers.
* `Full`<br>
 JSON numbers are decoded to CBOR using the full precision given in the JSON
 text.
* `IntOrFloat`<br>
 A JSON number is decoded to CBOR either as a CBOR integer (major type 0 or
 1) if the JSON number represents an integer at least -(2^53)+1
 and less than 2^53, or as their closest-rounded approximation as
 64-bit binary floating-point numbers otherwise.
* `IntOrFloatFromDouble`<br>
 A JSON number is decoded to CBOR either as a CBOR integer (major type 0 or
 1) if the number's closest-rounded approximation as a 64-bit
 binary floating-point number represents an integer at least
 -(2^53)+1 and less than 2^53, or as that approximation otherwise.

## Methods

* `static JSONOptions.ConversionMode valueOf​(java.lang.String name)`<br>
 Returns the enum constant of this type with the specified name.
* `static JSONOptions.ConversionMode[] values()`<br>
 Returns an array containing the constants of this enum type, in
the order they are declared.

## Method Details

### Full
    public static final JSONOptions.ConversionMode Full
### Double
    public static final JSONOptions.ConversionMode Double
### IntOrFloat
    public static final JSONOptions.ConversionMode IntOrFloat
### IntOrFloatFromDouble
    public static final JSONOptions.ConversionMode IntOrFloatFromDouble
### Decimal128
    public static final JSONOptions.ConversionMode Decimal128
### values
    public static JSONOptions.ConversionMode[] values()
### valueOf
    public static JSONOptions.ConversionMode valueOf​(java.lang.String name)
## Enum Constant Details

### Full
    public static final JSONOptions.ConversionMode Full
JSON numbers are decoded to CBOR using the full precision given in the JSON
 text. The number will be converted to a CBOR object as follows:
 If the number's exponent is 0 (after shifting the decimal point
 to the end of the number without changing its value), using the
 rules given in the <code>CBORObject.FromObject(EInteger)</code> method;
 otherwise, using the rules given in the
 <code>CBORObject.FromObject(EDecimal)</code> method. An exception in
 version 4.x involves negative zeros; if the negative zero's
 exponent is 0, it's written as a CBOR floating-point number;
 otherwise the negative zero is written as an EDecimal.
### Double
    public static final JSONOptions.ConversionMode Double
JSON numbers are decoded to CBOR as their closest-rounded approximation as
 64-bit binary floating-point numbers. (In some cases, numbers
 extremely close to zero may underflow to positive or negative
 zero, and numbers of extremely large magnitude may overflow to
 infinity.).
### IntOrFloat
    public static final JSONOptions.ConversionMode IntOrFloat
A JSON number is decoded to CBOR either as a CBOR integer (major type 0 or
 1) if the JSON number represents an integer at least -(2^53)+1
 and less than 2^53, or as their closest-rounded approximation as
 64-bit binary floating-point numbers otherwise. For example, the
 JSON number 0.99999999999999999999999999999999999 is not an
 integer, so it's converted to its closest floating-point
 approximation, namely 1.0. (In some cases, numbers extremely
 close to zero may underflow to positive or negative zero, and
 numbers of extremely large magnitude may overflow to infinity.).
### IntOrFloatFromDouble
    public static final JSONOptions.ConversionMode IntOrFloatFromDouble
A JSON number is decoded to CBOR either as a CBOR integer (major type 0 or
 1) if the number's closest-rounded approximation as a 64-bit
 binary floating-point number represents an integer at least
 -(2^53)+1 and less than 2^53, or as that approximation otherwise.
 For example, the JSON number
 0.99999999999999999999999999999999999 is the integer 1 when
 rounded to its closest floating-point approximation (1.0), so
 it's converted to the CBOR integer 1 (major type 0). (In some
 cases, numbers extremely close to zero may underflow to zero, and
 numbers of extremely large magnitude may overflow to infinity.).
### Decimal128
    public static final JSONOptions.ConversionMode Decimal128
JSON numbers are decoded to CBOR as their closest-rounded approximation to
 an IEEE 854 decimal128 value, using the rules for the EDecimal
 form of that approximation as given in the
 <code>CBORObject.FromObject(EDecimal)</code> method.
