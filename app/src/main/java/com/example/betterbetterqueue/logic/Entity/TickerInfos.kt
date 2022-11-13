package com.example.betterbetterqueue.logic.Entity

import java.time.LocalDateTime

data class TickerInfos(var status: Boolean, var baseTime: Int, var recentTime: LocalDateTime?, var beginTime: LocalDateTime?, var des: String = "")
