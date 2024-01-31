package com.iw.android.prayerapp.fragments.timeFragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.adapter.GenericListAdapter
import com.iw.android.prayerapp.adapter.OnItemClickListener
import com.iw.android.prayerapp.adapter.ViewType
import com.iw.android.prayerapp.databinding.FragmentTimeBinding
import com.iw.android.prayerapp.fragments.timeFragment.itemView.RowItemTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimeFragment : Fragment() {

    private var _binding: FragmentTimeBinding? = null
    val binding
        get() = _binding!!
    private val viewModel: TimeViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var isItemClick = true

    //    private var dialogBinding: PostDialogBinding? = null
//    private var dialogExitBinding: ExitDialogBinding? = null
    private var dialog: AlertDialog? = null

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimeBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setOnClickListener()
    }

    private fun initialize() {
        setRecyclerView()
        viewTypeArray.clear()
        for (data in viewModel.prayTimeArray) {
            viewTypeArray.add(
                RowItemTime(data,binding.recyclerView)
            )
        }
        adapter.items = viewTypeArray
    }

    private fun setOnClickListener() {

    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

}