package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.PrayerSoundData
import com.iw.android.prayerapp.databinding.RowItemPraySoundBinding
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.PrayerSoundFragment
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.PrayerSoundFragmentDirections
import com.iw.android.prayerapp.ui.main.soundFragment.OnDataSelected
import com.iw.android.prayerapp.ui.main.soundFragment.SoundDialog

class RowItemPrayerSound(
    private val data: PrayerSoundData,
    private val currentNamazName:String,
    private val fragment: Fragment,
    val listener: OnClick
) : ViewType<PrayerSoundData>, OnDataSelected {
    private var isViewShow = false
    private var currentMinute = 20

    override fun layoutId(): Int {
        return R.layout.row_item_pray_sound
    }

    override fun data(): PrayerSoundData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemPraySoundBinding).also { binding ->
            binding.imageViewCheck.visibility = if (data.isItemSelected) View.VISIBLE else View.GONE

            //binding.view4.visibility = if (data.title == "Last Third") View.GONE else View.VISIBLE
            binding.imageViewDropDownMenu.visibility =
                if (data.isImageForwardShow) View.VISIBLE else View.GONE
            binding.imageView.setImageResource(data.icon)
            binding.textViewTitle.text = data.title
            binding.textViewSelectSoundTitle.text = data.selectedItemTitle

            binding.imageViewDropDownMenu.setOnClickListener {

            }
            binding.mainView.setOnClickListener {
                listener.onItemClick(position)
                when (data.type) {
                    PrayerEnumType.ADHAN.getValue() -> {
                        openSoundDialogFragment("Adhan Sound", currentNamazName, true)
                    }

                    PrayerEnumType.TONES.getValue() -> {
                        openSoundDialogFragment("Tones Sound", currentNamazName, true)
                    }
                }
            }

        }
    }

    override fun onDataPassed(
        soundName: String,
        soundPosition: Int,
        sound:Int,

        isSoundForNotification: Boolean
    ) {
    }

    private fun openSoundDialogFragment(
        title: String,
        subTitle: String,
        isForNotification: Boolean
    ) {
        val soundDialog = SoundDialog()
        soundDialog.listener = this
        soundDialog.title = title
        soundDialog.subTitle = subTitle
        soundDialog.isForNotification = isForNotification
        soundDialog.show(fragment.childFragmentManager, "SoundDialogFragment")
    }

}

 interface OnClick {
    fun onItemClick(position: Int)
    fun onSoundSelected(soundName:String,soundPosition: Int,sound:Int,position: Int)
}
