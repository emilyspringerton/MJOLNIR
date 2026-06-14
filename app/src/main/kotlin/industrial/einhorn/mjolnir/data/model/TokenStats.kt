package industrial.einhorn.mjolnir.data.model

import com.google.gson.annotations.SerializedName

data class DailyTokenStat(
    val date: String,
    val tokens: Long,
)

data class DailyTokenStatsResponse(
    val days: Int,
    val stats: List<DailyTokenStat>,
)
