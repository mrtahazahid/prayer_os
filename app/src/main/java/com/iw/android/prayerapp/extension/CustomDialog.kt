package com.iw.android.prayerapp.extension

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.DialogToolsBinding

class CustomDialog(context: Context, private val title: String, private val des: String) :
    Dialog(context), View.OnClickListener {

    private lateinit var binding: DialogToolsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_tools,
            null,
            false
        )
        setContentView(binding.root)
        val window = window
        window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.round_corner_bg))
        binding.textViewActionTitle.text = title
        binding.textViewDes.text = des
        binding.textViewGotIt.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.textViewGotIt -> dismiss()
        }
    }
}
