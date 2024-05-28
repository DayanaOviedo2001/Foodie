package com.example.foodie.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.foodie.R
import com.example.foodie.databinding.FragmentAccountDetailBinding
import com.example.foodie.datastore.LoginPref
import com.example.foodie.ui.viewmodel.AccountDetailViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountDetailFragment : Fragment() {
    private lateinit var binding: FragmentAccountDetailBinding
    @Inject lateinit var loginPref: LoginPref
    private val bundle: AccountDetailFragmentArgs by navArgs()
    private lateinit var viewModel: AccountDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_detail, container, false)
        binding.accountDetailFragment = this

        when (bundle.action) {
            "email" -> {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.accountDetailTitle = "¿Quieres cambiar tu dirección de correo electrónico?"
                    binding.isPassword = false
                    binding.currentText = loginPref.readEmail()
                    binding.hintText = "Email"
                }
            }

            "password" -> {
                binding.accountDetailTitle = "¿Podrías proporcionar la nueva contraseña?"
                binding.isPassword = true
                binding.hintText = "Nueva Contraseña"
                binding.passwordEyeDrawable = R.drawable.custom_password_eye
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { updateStatus ->
            when (updateStatus[0]) {
                "email" -> {
                    Snackbar.make(binding.tvAcoountDetailTitle, "¿Estás seguro de que quieres cambiar tu dirección de correo electrónico?", Snackbar.LENGTH_SHORT)
                        .setAction("Sí") {
                            CoroutineScope(Dispatchers.Main).launch {
                                viewModel.updateAccount(updateStatus)
                                loginPref.createEmail(updateStatus[1])
                                Snackbar.make(binding.tvAcoountDetailTitle, "Tu dirección de correo electrónico ha sido cambiada con éxito", Snackbar.LENGTH_SHORT).show()
                                goToPreviousPage()
                            }
                        }
                        .show()
                }

                "password" -> {
                    Snackbar.make(binding.tvAcoountDetailTitle, "¿Estás seguro de que quieres cambiar tu contraseña?", Snackbar.LENGTH_SHORT)
                        .setAction("Sí") {
                            CoroutineScope(Dispatchers.Main).launch {
                                viewModel.updateAccount(updateStatus)
                                loginPref.createPassword(updateStatus[1])
                                Snackbar.make(binding.tvAcoountDetailTitle, "Su contraseña se ha cambiado exitosamente", Snackbar.LENGTH_SHORT).show()
                                goToPreviousPage()
                            }
                        }
                        .show()
                }
            }
        }

        viewModel.currentTextError.observe(viewLifecycleOwner) {
            binding.layoutCurrent.error = it
        }

        viewModel.newTextError.observe(viewLifecycleOwner) {
            binding.layoutNew.error = it
        }

        viewModel.againNewTextError.observe(viewLifecycleOwner) {
            binding.layoutAgainNew.error = it
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: AccountDetailViewModel by viewModels()
        viewModel = tempViewModel
    }

    fun updateAccount(currentText: String, newText: String, againNewText: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val email = loginPref.readEmail()
            val username = loginPref.readUsername()
            val password = loginPref.readPassword()
            viewModel.checkUpdate(bundle.action, currentText, newText, againNewText, email, username, password)
        }
    }

    fun goToPreviousPage() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    fun errorDisable() {
        binding.isErrorEnabled = false
    }
}