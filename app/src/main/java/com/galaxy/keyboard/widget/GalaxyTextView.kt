package com.galaxy.keyboard.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.galaxy.keyboard.helper.GalaxyConstant

class GalaxyTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    init {
        setTypeface(Typeface.createFromAsset(context.assets, GalaxyConstant.SAN_MYANMAR_FONT_ASSET_URL))
    }
}