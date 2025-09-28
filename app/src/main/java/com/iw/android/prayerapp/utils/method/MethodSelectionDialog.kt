package com.iw.android.prayerapp.utils.method

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.MethodData
import com.iw.android.prayerapp.databinding.DialogMethodBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.utils.method.itemView.OnItemClick
import com.iw.android.prayerapp.utils.method.itemView.RowItemMethod

class MethodSelectionDialog : DialogFragment(), View.OnClickListener, OnItemClick {

    private var _binding: DialogMethodBinding? = null
    private val binding get() = _binding!!

    var selectedPosition = 0
    private var previousPosition: Int? = null
    private var isAnyMethodSelected: Boolean = false
    private var methodList = listOf<MethodData>()

    var listener: OnMethodSelected? = null

    private var viewTypeArray = ArrayList<ViewType<*>>()

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
        isCancelable = false

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMethodBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= 35) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            ViewCompat.setOnApplyWindowInsetsListener(binding.mainView) { v: View, insets: WindowInsetsCompat ->
                val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(0, systemBars.top, 0, systemBars.bottom)
                insets
            }
        }

        setStatusBarWithBlackIcon(R.color.black)
        initialize()
        setObserver()
        setOnClickListener()
    }

    private fun initialize() {
        setOnBackPressedListener()
        setRecyclerView()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setObserver() {
        viewTypeArray.clear()
        previousPosition = selectedPosition
        methodList = getMethodList(selectedPosition)
        for (data in methodList) {
            viewTypeArray.add(
                RowItemMethod(data, this)
            )
        }
        adapter.items = viewTypeArray
    }


    private fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)
        binding.backView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.backView.id, binding.imageViewBack.id -> {
                listener?.onItemSelected(
                    selectedPosition,
                    isAnyMethodSelected
                )
                dismiss()
            }
        }

    }

    override fun onClick(position: Int) {
        isAnyMethodSelected = true
        previousPosition?.let {
            methodList[it].isSelected = false
            adapter.notifyItemChanged(it)
        }
        // Update current item
        methodList[position].isSelected = true
        adapter.notifyItemChanged(position)
        previousPosition = position
        selectedPosition = position
    }


    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }


    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    listener?.onItemSelected(
                        selectedPosition,
                        isAnyMethodSelected
                    )
                    dismiss()
                }
            })
    }
}

fun interface OnMethodSelected {
    fun onItemSelected(position: Int, isAnyMethodSelected: Boolean)
}
