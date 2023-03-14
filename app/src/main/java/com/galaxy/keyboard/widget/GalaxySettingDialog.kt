package com.galaxy.keyboard.widget

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.galaxy.keyboard.R
import com.galaxy.keyboard.databinding.DialogSettingBinding
import com.galaxy.keyboard.helper.GalaxyAppHelper
import com.galaxy.keyboard.helper.GalaxyConstant
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

class GalaxySettingDialog: AppCompatActivity(), View.OnClickListener, Slider.OnSliderTouchListener,
    ChipGroup.OnCheckedStateChangeListener {

    private lateinit var binding: DialogSettingBinding
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        context = this
        binding = DialogSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setFinishOnTouchOutside(true)
        val window: Window = getWindow()
        val wlp: WindowManager.LayoutParams = window.getAttributes()
        wlp.gravity = Gravity.TOP
        window.setAttributes(wlp)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        fontSizeText = binding.fontSizeText
        val cats = resources.getStringArray((R.array.font_size_array))
        val arrayAdapter = ArrayAdapter(this, R.layout.item_dropdown, cats)
        fontSizeText.setAdapter(arrayAdapter)
        binding.glassySlider.setLabelFormatter { value: Float ->
            return@setLabelFormatter "${value.roundToInt()}%"
        }
        fontSizeText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable) {
                Log.e("___Text Size___", s.toString())
                when (s.toString()) {
                    resources.getStringArray(R.array.font_size_array)[0] -> GalaxyAppHelper.SetCurrentFontSizeIndex(context, 0)
                    resources.getStringArray(R.array.font_size_array)[1] -> GalaxyAppHelper.SetCurrentFontSizeIndex(context, 1)
                    resources.getStringArray(R.array.font_size_array)[2] -> GalaxyAppHelper.SetCurrentFontSizeIndex(context, 2)
                }
            }
        })
        fontSizeText.setText(resources.getStringArray(R.array.font_size_array)[GalaxyAppHelper.GetCurrentFontSizeIndex(this)], false)
        binding.glassySlider.addOnSliderTouchListener(this)
        binding.glassySlider.value = GalaxyConstant.GLASSY_STATE_LIST[GalaxyAppHelper.GetCurrentGlassyStateIndex(this)].toFloat()
        binding.glassyStateText.text = GalaxyConstant.GLASSY_STATE_LIST[GalaxyAppHelper.GetCurrentGlassyStateIndex(this)].toString() + "%"
        binding.skinColorGroup.setOnCheckedStateChangeListener(this)
        binding.skinColorGroup.check(colorChipIdArray[GalaxyAppHelper.GetCurrentSkinColorIndex(this)])

    }

    var colorChipIdArray = intArrayOf(R.id.redChip, R.id.blueChip, R.id.greenChip, R.id.orangeChip, R.id.purpleChip, R.id.blackChip)
    lateinit var fontSizeText: AutoCompleteTextView

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    override fun onStartTrackingTouch(slider: Slider) {
        Log.e("___Sliding Started___", slider.value.toString())
    }

    override fun onStopTrackingTouch(slider: Slider) {
        val glassyState = slider.value.roundToInt()
        Log.e("___Glass State___", glassyState.toString())
        binding.glassyStateText.text = glassyState.toString() + "%"
        var i = 0
        GalaxyConstant.GLASSY_STATE_LIST.forEach galaxyForeach@ {
            if (it == glassyState) {
                GalaxyAppHelper.SetCurrentGlassyState(this, i)
                return
            } else {
                i++
            }
        }

        Log.e("___Current Glassy___", i.toString())
        GalaxyAppHelper.SetCurrentGlassyState(this, i)
    }

    override fun onCheckedChanged(group: ChipGroup, checkedIds: MutableList<Int>) {
        var selectedColorIndex = 0
        when (checkedIds[0]) {
            R.id.redChip -> selectedColorIndex = 0
            R.id.blueChip -> selectedColorIndex = 1
            R.id.greenChip -> selectedColorIndex = 2
            R.id.orangeChip -> selectedColorIndex = 3
            R.id.purpleChip -> selectedColorIndex = 4
            R.id.blackChip -> selectedColorIndex = 5
        }
        binding.skinStateText.text = resources.getStringArray(R.array.skin_array)[selectedColorIndex]
        GalaxyAppHelper.SetCurrentSkinColorIndex(this, selectedColorIndex)
    }

}