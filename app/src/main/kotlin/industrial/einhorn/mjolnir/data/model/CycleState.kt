package industrial.einhorn.mjolnir.data.model

import com.google.gson.annotations.SerializedName

data class CycleState(
    @SerializedName("version") val version: Int = 0,
    @SerializedName("cycle_number") val cycleNumber: Int = 0,
    @SerializedName("last_cycle_at") val lastCycleAt: String? = null,
    @SerializedName("active_task_id") val activeTaskId: String? = null,
    @SerializedName("active_task") val activeTask: ActiveTask? = null,
    @SerializedName("metrics") val metrics: CycleMetrics = CycleMetrics(),
    @SerializedName("next_cycle_plan") val nextCyclePlan: String? = null,
)

data class CycleMetrics(
    @SerializedName("total_cycles") val totalCycles: Int = 0,
    @SerializedName("iters_run") val itersRun: Int = 0,
    @SerializedName("tasks_completed") val tasksCompleted: Int = 0,
    @SerializedName("consec_failures") val consecFailures: Int = 0,
)

data class ActiveTask(
    @SerializedName("id") val id: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("max_iters") val maxIters: Int = 0,
    @SerializedName("iterations") val iterations: List<TaskIteration>? = null,
)

data class TaskIteration(
    @SerializedName("iter") val iter: Int = 0,
    @SerializedName("tokens_used") val tokensUsed: Int = 0,
    @SerializedName("outcome") val outcome: String = "",
)
