package com.galaxy.keyboard.fragment

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.galaxy.keyboard.R
import com.galaxy.keyboard.databinding.FragmentHomeBinding
import com.galaxy.keyboard.helper.GalaxyAppHelper
import com.google.android.material.button.MaterialButton

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var _context: Context? = null

    private var mEnableButton: MaterialButton? = null
    private var mLoginButton: MaterialButton? = null
    private var mSettingButton: MaterialButton? = null

    private var mShouldEnable = true
    private var mShouldLogin = true

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _context = requireContext()
        mEnableButton = binding.enableButton
        mLoginButton = binding.loginButton
        mSettingButton = binding.settingButton

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mShouldEnable = !GalaxyAppHelper.IsGalaxyKeyboardEnabled(_context!!)

        if (mShouldEnable) {
            mEnableButton!!.setBackgroundTintList((ContextCompat.getColorStateList(_context!!,
                R.color.enableButtonColor)))
            mEnableButton!!.setText(_context!!.getString(R.string.enable_keyboard))

            binding.enableButton.setOnClickListener {
                enableKeyboard()
            }

        } else {
            mEnableButton!!.setBackgroundTintList((ContextCompat.getColorStateList(_context!!,
                R.color.switchButtonColor)))
            mEnableButton!!.setText(_context!!.getString(R.string.switch_keyboard))

            binding.enableButton.setOnClickListener {
                GalaxyAppHelper.SwitchKeyboard(_context!!)
            }
        }

        mShouldLogin = !GalaxyAppHelper.GetLoginState()
        mLoginButton!!.setBackgroundTintList((ContextCompat.getColorStateList(_context!!,
            if (mShouldLogin) R.color.loginButtonColor else R.color.logoutButtonColor)))
        mLoginButton!!.setText(_context!!.getString(if (mShouldLogin) R.string.login else R.string.logout))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_LoginFragment)
        }
    }

    private fun enableKeyboard() {
        val enableIntent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
        enableIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(enableIntent)
    }

    private fun setAsDefaultKeyboard(isSet: Boolean) {
        val imeManager: InputMethodManager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imeManager.showInputMethodPicker()

        Settings.Secure.putString(
            _context!!.contentResolver,
            Settings.Secure.ENABLED_INPUT_METHODS,
            _context!!.resources.getString(if (isSet) R.string.galaxy_keyboard_id else R.string.galaxy_no_keyboard_id)
        )

        Settings.Secure.putString(
            _context!!.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD,
            _context!!.resources.getString(if (isSet) R.string.galaxy_keyboard_id else R.string.galaxy_no_keyboard_id)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}