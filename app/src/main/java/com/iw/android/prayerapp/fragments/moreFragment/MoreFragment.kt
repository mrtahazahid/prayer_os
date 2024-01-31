package com.iw.android.prayerapp.fragments.moreFragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.iw.android.prayerapp.adapter.GenericListAdapter
import com.iw.android.prayerapp.adapter.OnItemClickListener
import com.iw.android.prayerapp.adapter.ViewType
import com.iw.android.prayerapp.databinding.FragmentMoreBinding
import com.iw.android.prayerapp.fragments.moreFragment.itemView.RowItemMore
import com.iw.android.prayerapp.fragments.timeFragment.itemView.RowItemTime

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    val binding
        get() = _binding!!
    private val viewModel: MoreViewModel by viewModels()
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
        _binding = FragmentMoreBinding.inflate(inflater)
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
        for (data in viewModel.moreList) {
            viewTypeArray.add(
                RowItemMore(data)
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