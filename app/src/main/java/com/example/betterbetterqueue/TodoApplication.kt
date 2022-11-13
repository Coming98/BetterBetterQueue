package com.example.betterbetterqueue

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.betterbetterqueue.ui.ToolbarFragment
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.pow
// Done
class TodoApplication: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context // 全局 Context 对象
        lateinit var inputMethodManager: InputMethodManager // 全局 IMM 对象
        val DEFAULT_CATEGORY_NAME: String = "星海" // 默认类别名称
        val SLIDE_THRESHOLD: Float = 400F // 左右滑动的响应阈值
        val DAY_IN_MILSECOND: Long = 3600 * 1000 * 24 // 一天内的毫秒数
        val WORKDAY_IN_SECOND: Int = (24 - 12) * 3600 // 一天内工作时间（单位秒）
        // 初始化全局的 json 解析器
        val todoGson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java,
            object : JsonSerializer<LocalDateTime> {
                override fun serialize(
                    src: LocalDateTime?,
                    typeOfSrc: Type?,
                    context: JsonSerializationContext?
                ): JsonElement {
                    return JsonPrimitive(src?.toLong())
                }

            }
        ).registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDateTime {
                return json!!.asJsonPrimitive.asString.toLong().toLocalDateTime()
            }
        }).serializeNulls().create()
        // 缓存数据使用的 json 解析器
        val cacheGson = GsonBuilder().serializeNulls().setLongSerializationPolicy(LongSerializationPolicy.STRING).create()
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

}

/**
 * 加载标题栏
 *      指定标题栏的名称
 *      左右两侧是否有按钮，如果有则指定按钮的 ResourceID
 *      是否支持改名事件
 *      左右两侧按钮的点击事件处理
 */
fun AppCompatActivity.loadToolbarFragment(toolbarNmae: String,
                                          toolbarLeft: Int?,
                                          toolbarRight: Int?,
                                          rightNow: Boolean = false,
                                          changeToolbarName: ((newName: String) -> Unit)?,
                                          handleButtonLeft: () -> Unit,
                                          handleButtonRight: (() -> Unit)?
): ToolbarFragment {
    val toolbarFragment = ToolbarFragment(changeToolbarName, handleButtonLeft, handleButtonRight)

    val bundle = Bundle().apply {
        putString("toolbarName", toolbarNmae)
        toolbarLeft?.let { putInt("toolbarLeft", it) }
        toolbarRight?.let { putInt("toolbarRight", it) }
    }
    toolbarFragment.arguments = bundle

    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    transaction.replace(R.id.toolbar_fragment, toolbarFragment)

    if(rightNow){
        transaction.commitNow()
    } else {
        transaction.commit()
    }

    return toolbarFragment
}

/**
 * 输出日期时间格式
 *      dateSplit = "-", timeSplit = ":", minTime = "min": 2022-10-31 16:52
 *      dateSplit = "chinese", timeSplit = "chinese", minTime = "min": 2022年10月31日 16时52分
 */
fun LocalDateTime.dateTimeFormatter(dateSplit: String = "-", timeSplit: String = ":", minTime: String = "min"): String {
    val datePattern: String = if (dateSplit == "chinese") "yyyy年MM月dd日" else "yyyy${dateSplit}MM${dateSplit}dd"
    val timePattern: String = if (timeSplit == "chinese") "HH时mm分ss秒" else "HH${timeSplit}mm${timeSplit}ss"
    val datetimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(datePattern + " " + timePattern)
    var cutIndex: Int = 0
    when (minTime) {
        "hour" -> cutIndex = 6
        "min" -> cutIndex = 3
        "minute" -> cutIndex = 3
        else -> cutIndex = 0
    }
    val dateTimeString = datetimeFormatter.format(this).toString()
    return dateTimeString.substring(0, dateTimeString.length - cutIndex)
}

/**
 * 输出日期格式
 */
fun LocalDateTime.dateFormatter(dateSplit: String = "-"): String {
    return this.dateTimeFormatter(dateSplit = dateSplit).split(" ").first()
}

/**
 * 输出时间格式
 */
fun LocalDateTime.timeFormatter(timeSplit: String = ":", minTime: String = "min"): String {
    return this.dateTimeFormatter(timeSplit = timeSplit, minTime = minTime).split(" ").last()
}

/**
 * LocalDateTime 2 Long
 */
fun LocalDateTime.toLong(): Long {
    return this.toInstant(ZoneOffset.of("+8")).toEpochMilli()
}

/**
 * Long 2 LocalDateTime: 损失了毫秒的精度
 */
fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofEpochSecond(this/1000, 0, ZoneOffset.ofHours(8))
}

/**
 * 两个 LocalDateTime 之间的秒级距离
 */
fun LocalDateTime.distence(targetLocalDateTime: LocalDateTime): Long {
    return abs(this.toLong() - targetLocalDateTime.toLong()) / 1000
}

/**
 * 保留小数点后 keepLength 位
 * 如果小数点后 keepLength 位为 0 转为整数
 */
fun Float.toTypedString(keepLength: Int): String {
    val checkValue = 10.0.pow(keepLength).toInt()
    // 后 keepLength 位为 0 转为整数
    if((this * checkValue).toInt() % checkValue == 0) {
        return this.toInt().toString()
    } else {
        return String.format("%.${keepLength}f", this)
    }
}

/**
 * 扩展函数添加 open 方法 接收函数类型为 SharedPreferences.Editor
 */
fun SharedPreferences.open(block: SharedPreferences.Editor.() -> Unit) {
    // open 函数拥有 SharedPreferences 方法的上下文, 可以直接调用 edit 方法
    val editor = edit()
    editor.block()
    editor.apply()
}



