package com.example.betterbetterqueue.ui.EditableSpinner

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.betterbetterqueue.R

class EditableSpinner: LinearLayout, AdapterView.OnItemClickListener {

    lateinit var et_button: ImageButton
    lateinit var et_input: EditText
    lateinit var listPopupWindow: ListPopupWindow
    lateinit var etArrayAdapter: ArrayAdapter<String>
    lateinit var etOnItemClickListener: AdapterView.OnItemClickListener

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.editable_spinner, this)

        et_button = findViewById(R.id.et_button)
        et_input = findViewById(R.id.et_input)
        listPopupWindow = ListPopupWindow(context)

        et_button.setOnClickListener{
            listPopupWindow.show()
        }
    }

    fun setAdapter(adapter: ArrayAdapter<String>): EditableSpinner {
        etArrayAdapter = adapter
        listPopupWindow.setAdapter(adapter)
        listPopupWindow.anchorView = et_input
        listPopupWindow.isModal = true
        listPopupWindow.setOnItemClickListener(this)
        return this
    }

    fun setOnItemClickListener(onItemClickListener: AdapterView.OnItemClickListener): EditableSpinner {
        etOnItemClickListener = onItemClickListener
        return this
    }


    fun getSelectedItem() = et_input.text.toString()

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        listPopupWindow.dismiss()
        et_input.setText(etArrayAdapter.getItem(position).toString())
        if (etOnItemClickListener != null) {
            // etOnItemClickListener.onItemClick(position) Sth Wrong
        }
    }



}