package com.example.betterbetterqueue.ui.Config

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.betterbetterqueue.R
import com.example.betterbetterqueue.loadToolbarFragment
import com.example.betterbetterqueue.logic.Entity.TodoItem
import com.google.gson.*
import java.io.File
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZoneOffset

class ConfigActivity : AppCompatActivity(), View.OnClickListener {

    val btnExportDB: Button by lazy { findViewById(R.id.btn_export_db) }
    val btnImportDB: Button by lazy { findViewById(R.id.btn_import_db) }

    companion object {
        fun onActionStart(context: Context, ) {
            val intent = Intent(context, ConfigActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        arrayOf(btnExportDB, btnImportDB).forEach { it.setOnClickListener(this) }

        //////////////////////////////////////// 加载标题栏
        loadToolbarFragment(
            toolbarNmae = getString(R.string.configToolbarName),
            toolbarLeft = R.drawable.ic_back,
            toolbarRight = null,
            changeToolbarName = null,
            handleButtonLeft = { finish()},
            handleButtonRight = { }
        )
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_export_db -> {
                // val dbInJson: DBInJson = DBInJson(listOf(TodoItem(0, "cjc", LocalDateTime.now(), 666L, 0L)))
                val dbInJson: DBInJson = DBInJson(listOf(), listOf(), listOf(), listOf())
                val outputPath = filesDir.absolutePath + File.separator + "db2json" + File.separator + "db.json"

                val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java,
                    object : JsonSerializer<LocalDateTime> {
                        override fun serialize(
                            src: LocalDateTime?,
                            typeOfSrc: Type?,
                            context: JsonSerializationContext?
                        ): JsonElement {
                            return JsonPrimitive(src?.toInstant(ZoneOffset.of("+8"))?.toEpochMilli())
                        }

                    }
                ).registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
                    override fun deserialize(
                        json: JsonElement?,
                        typeOfT: Type?,
                        context: JsonDeserializationContext?
                    ): LocalDateTime {
                        // Log.d("TEMP", json?.asJsonPrimitive?.asString!!)
                        // return LocalDateTime.now()
                        return LocalDateTime.ofEpochSecond(json!!.asJsonPrimitive.asString.toLong()/1000, 0, ZoneOffset.ofHours(8))
                    }
                })
                    .serializeNulls().create()
                val dbJson = gson.toJson(dbInJson)
                // val jsonStr = "{\"todoItemList\":[{\"createTime\":1665753726388,\"id\":0,\"name\":\"cjc\",\"topTime\":0,\"totalTime\":666}]}"
                Log.d("TEMP", outputPath)
                Log.d("TEMP", dbJson)
                // Log.d("TEMP", jsonObj.toString())
                // val jsonObj = gson.fromJson(jsonStr, DBInJson::class.java)
            }
            R.id.btn_import_db -> {

            }
        }
    }
}