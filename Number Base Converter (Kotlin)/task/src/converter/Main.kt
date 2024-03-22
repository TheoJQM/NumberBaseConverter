package converter

import java.math.BigInteger
import java.math.RoundingMode

class Converter {
    private var bases = Pair<BigInteger, BigInteger>(BigInteger.ZERO, BigInteger.ZERO)
    private var exit = false
    private var back = false

    fun convert() {
        while (!exit) {
            print("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
            val input = readln()
            val inputs: List<BigInteger>
            if (input == "/exit") break else inputs = input.split(" ").toList().map { it.toBigInteger() }
            bases = bases.copy(inputs.first(), inputs.last())
            back = false

            while (!back) {
                print("Enter number in base ${bases.first} to convert to base ${bases.second} (To go back type /back)")
                val number = readln()
                when  {
                    number == "/back" -> back = true
                    number.contains(".") -> {
                        val result = fractionalOtherToDecimal(number)
                        println("Conversion result: $result")
                    }
                    else -> {
                        val sourceToDecimal = if (bases.first == BigInteger.TEN) number else otherToDecimal(number, bases.first)
                        val decimalToTarget = decimalToOther(sourceToDecimal.toBigInteger(), bases.second)
                        println("Conversion result: $decimalToTarget")
                    }
                }
            }
        }
    }

    private fun otherToDecimal(number: String, source: BigInteger): String {
        var power = 0
        var value = number.reversed()
        var result = BigInteger.ZERO
        while (value.isNotEmpty()) {
            val lastPart = if (value.first() > '9') (value.uppercase().first().code - 55).toBigInteger() else value.first().toString().toBigInteger()
            result += (lastPart * source.pow(power))
            value = value.removeRange(0, 1)
            power++
        }
        return result.toString()
    }

    private fun decimalToOther(number: BigInteger, target: BigInteger): String {
        var quotient = number
        var result = ""

        while (quotient >= target) {
            val remainder = quotient % target
            result += if (remainder > 9.toBigInteger()) (remainder + 55.toBigInteger()).toInt().toChar().lowercase() else remainder
            quotient = (quotient - remainder) / target
        }
        result += if (quotient > 9.toBigInteger()) (quotient + 55.toBigInteger()).toInt().toChar().lowercase() else quotient
        return result.reversed()
    }

    private fun fractionalOtherToDecimal(number: String): String {
        var (integer, fractional) = number.split(".")
        integer = otherToDecimal(integer, bases.first)

        var fractionalResult = 0.0

        for (i in fractional.indices) {
            val temp = fractional[i]
            val a = if (temp > '9') (temp.uppercase().first().code - 55).toString().toDouble() else temp.toString().toDouble()
            fractionalResult += a * Math.pow(bases.first.toDouble(), -(i + 1).toDouble())
            fractional.removeRange(0, 1)
        }
        var result = integer.toBigDecimal() + fractionalResult.toBigDecimal()
        if (result.toString().split(".").last().length < 5) {
            result = result.setScale(5, RoundingMode.UNNECESSARY)
        } else if (result.toString().split(".").last().length > 5) {
            result = result.setScale(5, RoundingMode.FLOOR)
        }

        return fractionalDecimalToOther(result.toString())
    }

    private fun fractionalDecimalToOther(number: String): String {
        val list = number.split(".")
        val integer = decimalToOther(list[0].toBigInteger(), bases.second)

        var fractional = ("0." + list[1]).toBigDecimal()
        var counter = 0
        var res = "."

        while (fractional != (0.0).toBigDecimal() && counter <= 5) {
            fractional *= bases.second.toBigDecimal()
            val int = fractional.toString().split(".").first()
            res += if (int.toInt() > 9) (int.toInt() + 55).toChar().lowercase() else int
            fractional -= int.toBigDecimal()
            counter++
        }

        if (res.length > 6) {
            res = res.removeRange(6, res.length)
        } else if (res.split(".").last().length > 5) {
            res = res.removeRange(6, res.length)
        }

        res = integer + res
        return res
    }
}

fun main() {
    val myConverter = Converter()
    myConverter.convert()
}