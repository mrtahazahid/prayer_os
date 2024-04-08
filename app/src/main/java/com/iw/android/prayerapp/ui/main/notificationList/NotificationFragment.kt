package com.iw.android.prayerapp.ui.main.notificationList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentNotificationListBinding
import com.iw.android.prayerapp.ui.main.notificationList.itemView.RowItemNotificationList

class   NotificationFragment : BaseFragment(R.layout.fragment_notification_list),
    View.OnClickListener {


    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationListViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

    val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }


    override fun initialize() {
        setRecyclerView()
        if(viewModel.notificationList.isEmpty()){
            binding.cardViewMain.gone()
            binding.imageViewEmpty.show()
        }else{
            binding.cardViewMain.show()
            binding.imageViewEmpty.gone()
        }
    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in viewModel.notificationList) {
            viewTypeArray.add(
                RowItemNotificationList(data)
            )
        }
        adapter.items = viewTypeArray
    }


    override fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)
        binding.textViewDateTitle.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.imageViewBack.id -> {
                findNavController().popBackStack()
            }

            binding.textViewDateTitle.id -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

}