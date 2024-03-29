package com.iw.android.prayerapp.ui.main.moreFragment

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentMoreBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.moreFragment.itemView.RowItemMore

class MoreFragment : BaseFragment(R.layout.fragment_more), View.OnClickListener {

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
        (requireActivity() as MainActivity).showBottomSheet()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        setRecyclerView()

    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in viewModel.moreList) {
            viewTypeArray.add(
                RowItemMore(data)
            )
        }
        adapter.items = viewTypeArray
    }

    override fun setOnClickListener() {
        binding.instagramViewClick.setOnClickListener(this)
        binding.carViewProject.setOnClickListener(this)
        binding.twitteriewClick.setOnClickListener(this)
        binding.policyViewClick.setOnClickListener(this)
        binding.disclaimerClickView.setOnClickListener(this)
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }
    override fun onClick(v: View?) {
        when(v?.id){
            binding.instagramViewClick.id->{
                openCustomTab("https://www.instagram.com/praywatchapp/?hl=en")
            }

            binding.twitteriewClick.id->{
                openCustomTab("https://www.instagram.com/praywatchapp/?hl=en")
            }

            binding.carViewProject.id->{
                openCustomTab("https://quranplus.app/")
            }
            binding.policyViewClick.id->{
                openCustomTab("https://praywatch.app/")
            }
            binding.disclaimerClickView.id->{
                openCustomTab("https://praywatch.app/")
            }
        }
    }


    fun openCustomTab(url: String?) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }
}