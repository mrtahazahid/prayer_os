package com.iw.android.prayerapp.base.fragment

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment(layout: Int) : Fragment(layout) {

    protected abstract fun initialize()

    protected abstract fun setObserver()

    protected abstract fun setOnClickListener()



    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    fun View.hide() {
        this.visibility = View.INVISIBLE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

}