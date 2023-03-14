package com.galaxy.keyboard.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.galaxy.keyboard.helper.GalaxyConstant

class GalaxyEditText(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatEditText(context, attrs) {
    init {
        setTypeface(Typeface.createFromAsset(context.assets, GalaxyConstant.SAN_MYANMAR_FONT_ASSET_URL))
    }
}