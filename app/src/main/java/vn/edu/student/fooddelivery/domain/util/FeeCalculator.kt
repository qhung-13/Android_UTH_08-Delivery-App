package vn.edu.student.fooddelivery.domain.util

/**
 * Rule tính phí ship — SỐ LIỆU MẪU, cả nhóm cần thống nhất lại số thật
 * rồi cập nhật vào đây (không sửa ở nơi khác) trước khi dùng chính thức.
 */
object FeeCalculator {
    const val BASE_FEE = 10_000.0
    const val FEE_PER_KM = 3_000.0
    const val WEIGHT_THRESHOLD_GRAM = 2000
    const val EXTRA_WEIGHT_FEE = 5_000.0

    data class Breakdown(
        val baseFee: Double,
        val distanceFee: Double,
        val extraWeightFee: Double,
        val total: Double
    )

    fun calculate(distanceKm: Double, weightGram: Int): Double {
        return breakdown(distanceKm, weightGram).total
    }

    fun breakdown(distanceKm: Double, weightGram: Int): Breakdown {
        require(distanceKm.isFinite() && distanceKm >= 0) { "Khoảng cách không hợp lệ" }
        require(weightGram > 0) { "Cân nặng không hợp lệ" }

        val distanceFee = distanceKm * FEE_PER_KM
        val extraWeightFee = if (weightGram > WEIGHT_THRESHOLD_GRAM) EXTRA_WEIGHT_FEE else 0.0
        return Breakdown(
            baseFee = BASE_FEE,
            distanceFee = distanceFee,
            extraWeightFee = extraWeightFee,
            total = BASE_FEE + distanceFee + extraWeightFee
        )
    }
}
