package com.iw.android.prayerapp.utils.asset

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.databinding.AssetDialogBinding
import com.iw.android.prayerapp.utils.PaginationScrollListener

class AssetDialog : DialogFragment() {
    private lateinit var binding: AssetDialogBinding

    private var viewTypeArray = ArrayList<ViewType<*>>()
    private lateinit var viewModel: AssetViewModel

    val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment =
            parentFragmentManager.fragments[0].childFragmentManager.fragments[0]
        viewModel = ViewModelProvider(fragment)[AssetViewModel::class.java]
        setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AssetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= 35) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            ViewCompat.setOnApplyWindowInsetsListener(binding.secondView) { v: View, insets: WindowInsetsCompat ->
                val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(0, systemBars.top, 0, systemBars.bottom)
                insets
            }
        }

        initialize()
        setObserver()
        setOnClickListener()
    }

    private fun initialize() {
        setRecyclerView()
    }

    private fun setObserver() {
        viewModel.pagedAssetList.observe(viewLifecycleOwner) { pagedList ->
            viewTypeArray.clear()
            for (data in pagedList) {
                viewTypeArray.add(RowItemAsset(data))
            }
            adapter.items = viewTypeArray
        }
    }

    private fun setOnClickListener() {
        binding.backView.setOnClickListener {
            dismiss()
        }

    }


    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        binding.recyclerView.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun onScrolled(dy: Int) {
            }

            override fun loadMoreItems() {
                viewModel.loadNextPage()
            }
        })
    }
}