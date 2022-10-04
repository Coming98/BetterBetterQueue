package com.example.betterbetterqueue.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.betterbetterqueue.MainActivity
import com.example.betterbetterqueue.R

class ToolbarFragment(val handleButtonLeft: () -> Unit, val handleButtonRight: () -> Unit): Fragment() {

    lateinit var toolbarName: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.toolbar_fragment, container, false)
        toolbarName = view.findViewById(R.id.toolbar_name)
        val toolbarLeft: Button = view.findViewById(R.id.toolbar_left)
        val toolbarRight: Button = view.findViewById(R.id.toolbar_right)

        toolbarName.text = arguments?.getString("toolbarName")
        update_toolbar_button("toolbarLeft", toolbarLeft, handleButtonLeft)
        update_toolbar_button("toolbarRight", toolbarRight, handleButtonRight)
        return view
    }


    fun update_toolbar_button(button_name: String, button_obj: Button, handleButtonClicked: () -> Unit) {
        if(arguments?.containsKey(button_name) == true) {
            button_obj.visibility = View.VISIBLE
            button_obj.isClickable = true
            button_obj.setOnClickListener { handleButtonClicked() }
            button_obj.setBackgroundResource(arguments?.getInt(button_name) ?: R.drawable.ic_category)
        } else {
            button_obj.visibility = View.INVISIBLE
            button_obj.isClickable = false
        }
    }

    fun refreshToolbarName(name: String) {
        if(toolbarName != null) {
            toolbarName.text = name
        }
    }



}