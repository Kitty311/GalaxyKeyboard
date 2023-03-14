package com.galaxy.keyboard.widget

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.galaxy.keyboard.R
import com.galaxy.keyboard.databinding.DialogFeedbackBinding
import com.galaxy.keyboard.helper.GalaxyAppHelper.Companion.GalaxyToast

class GalaxyFeedbackDialog: AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: DialogFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = DialogFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setFinishOnTouchOutside(false)
        val window: Window = getWindow()
        val wlp: WindowManager.LayoutParams = window.getAttributes()
        wlp.gravity = Gravity.TOP
        window.setAttributes(wlp)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.btnSend.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSend -> {
                if (binding.feedbackField.text?.isEmpty() == true) {
                    binding.feedbackField.error = resources.getString(R.string.feedback_validation_error_message)
                }
                else {
                    GalaxyToast(R.string.feedback_successfully_sent)
                }
            }
            R.id.btnCancel -> {
                finish()
            }
        }
    }

}