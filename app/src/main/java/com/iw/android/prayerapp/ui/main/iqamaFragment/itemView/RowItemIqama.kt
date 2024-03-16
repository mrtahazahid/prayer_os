package com.iw.android.prayerapp.ui.main.iqamaFragment.itemView

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.databinding.RowItemIqamaBinding
import com.iw.android.prayerapp.ui.main.iqamaFragment.IqamaViewModel
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum

class RowItemIqama(
    private val data: IqamaData,
    val recyclerView: RecyclerView,
    val fragment: Fragment,
    val viewModel: IqamaViewModel
) : ViewType<IqamaData> {
    private var iqamaReminderTime = 0
    override fun layoutId(): Int {
        return R.layout.row_item_iqama
    }

    override fun data(): IqamaData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemIqamaBinding).also { binding ->
            binding.view4.visibility = if (data.namazName == "Isha") View.GONE else View.VISIBLE
            binding.textViewIqamaReminder.text = data.namazName
            binding.imageViewIqamaAdd.setOnClickListener {
                binding.textViewIqamaSetTime.text = incrementDuaMinute()
            }
            binding.imageViewIqamaMinus.setOnClickListener {
                binding.textViewIqamaSetTime.text = decrementDuaMinute()
            }
            spinnerDua(binding)
        }
    }

    private fun spinnerDua(binding: RowItemIqamaBinding) {

        val adapter = ArrayAdapter.createFromResource(
            binding.textViewIqamaReminder.context,
            R.array.dua,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerIqamaReminderSwitch.adapter = adapter


        when (data.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {
                binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                binding.cardViewIqamaTime.visibility = View.GONE
                binding.textViewIqamaSetTime.visibility = View.GONE
                binding.spinnerIqamaReminderSwitch.setSelection(0)
            }

            DuaTypeEnum.TIME.getValue() -> {
                binding.cardViewIqamaAdjustmentTime.visibility = View.VISIBLE
                binding.cardViewIqamaTime.visibility = View.GONE
                binding.textViewIqamaSetTime.visibility = View.VISIBLE
                binding.textViewIqamaTime.text = data.iqamaTime?.iqamaTime
                binding.spinnerIqamaReminderSwitch.setSelection(1)

            }

            DuaTypeEnum.MINUTES.getValue() -> {
                binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                binding.cardViewIqamaTime.visibility = View.VISIBLE
                binding.textViewIqamaSetTime.visibility = View.GONE
                binding.textViewIqamaSetTime.text = "${data.iqamaTime?.iqamaMinutes} mins"
                binding.spinnerIqamaReminderSwitch.setSelection(2)

            }


        }

        binding.spinnerIqamaReminderSwitch.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                when (position) {
                    0 -> {
                        binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                        binding.cardViewIqamaTime.visibility = View.GONE
                        binding.textViewIqamaSetTime.visibility = View.GONE
                    }

                    1 -> {
                        binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                        binding.cardViewIqamaTime.visibility = View.VISIBLE
                        binding.textViewIqamaSetTime.visibility = View.GONE
                    }

                    2 -> {
                        binding.cardViewIqamaAdjustmentTime.visibility = View.VISIBLE
                        binding.cardViewIqamaTime.visibility = View.GONE
                        binding.textViewIqamaSetTime.visibility = View.VISIBLE
                    }
                }
                data.iqamaType = selectedItem
                // savePrayerDetailData()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun incrementDuaMinute(): String {
        iqamaReminderTime++
        return "$iqamaReminderTime min"
    }

    private fun decrementDuaMinute(): String {

        return if (iqamaReminderTime > 0) {
            iqamaReminderTime--
            "$iqamaReminderTime min"
        } else {
            "off"
        }
    }

}