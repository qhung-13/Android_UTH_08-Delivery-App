package vn.edu.student.fooddelivery.domain.util

/**
 * Rule tính phí ship — SỐ LIỆU MẪU, cả nhóm cần thống nhất lại số thật
 * rồi cập nhật vào đây (không sửa ở nơi khác) trước khi dùng chính thức.
 */
object FeeCalculator {
    private const val BASE_FEE = 10_000.0
    private const val FEE_PER_KM = 3_000.0
    private const val WEIGHT_THRESHOLD_GRAM = 2000
    private const val EXTRA_WEIGHT_FEE = 5_000.0

    fun calculate(distanceKm: Double, weightGram: Int): Double {
        require(distanceKm >= 0) { "Khoảng cách không hợp lệ" }
        require(weightGram > 0) { "Cân nặng không hợp lệ" }

        var fee = BASE_FEE + distanceKm * FEE_PER_KM
        if (weightGram > WEIGHT_THRESHOLD_GRAM) {
            fee += EXTRA_WEIGHT_FEE
        }
        return fee
    }
}
