package vn.edu.student.fooddelivery.domain.validation

object InputValidator {

    fun isValidName(name: String): Boolean =
        name.isNotBlank()

    fun isValidPhone(phone: String): Boolean =
        phone.matches(Regex("^0\\d{9}$"))

    fun isValidAddress(address: String): Boolean =
        address.trim().length >= 5

    fun isValidWeight(weightGram: Int): Boolean =
        weightGram > 0
}
