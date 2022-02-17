package by.seka.clevertec.hometask3.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.seka.clevertec.hometask3.R
import by.seka.clevertec.hometask3.data.preferences.ContactPreferences
import by.seka.clevertec.hometask3.databinding.SelectionFragmentBinding
import by.seka.clevertec.hometask3.presentation.observer.ContactObserver
import by.seka.clevertec.hometask3.util.CONTACTS_READ_REQ_CODE
import by.seka.clevertec.hometask3.util.hasPermission
import by.seka.clevertec.hometask3.util.requestPermissionWithRationale
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SelectionFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: ContactPreferences

    private val viewModel: SelectionViewModel by viewModels()

    private var _binding: SelectionFragmentBinding? = null
    private val binding get() = _binding!!
    private var observer: ContactObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observer = ContactObserver(requireActivity().activityResultRegistry, requireContext())
        observer?.let { lifecycle.addObserver(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SelectionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            chooseContact.setOnClickListener {
                checkPermissionAndShowContacts()
            }

            showContacts.setOnClickListener {
                showSavedContactsDialog()
            }

            showFromSp.setOnClickListener {
                showSnackBarNumberFromSP()
            }

            showNotification.setOnClickListener {

            }
        }
    }

    private fun checkPermissionAndShowContacts() {
        if (activity?.hasPermission(Manifest.permission.READ_CONTACTS) == true) {
            addContactToDatabase()

        } else {
            activity?.requestPermissionWithRationale(
                Manifest.permission.READ_CONTACTS,
                CONTACTS_READ_REQ_CODE,
                "Contacts permission"
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CONTACTS_READ_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addContactToDatabase()
        }
    }

    private fun addContactToDatabase() {
        observer?.selectImage()
        lifecycleScope.launchWhenStarted {
            observer?.contact?.collectLatest {
                if (it.number.isNotEmpty()) {
                    viewModel.addContact(it)
                    Toast.makeText(
                        context,
                        context?.getText(R.string.save_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    cancel()
                }
            }
        }
    }

    private fun showSavedContactsDialog() {
        lifecycleScope.launchWhenStarted {

            val dialogTitle = context?.getString(R.string.select_contact).toString()
            val listOfNumbers = viewModel.getNumbersList().toTypedArray()
            val dialog = AlertDialog.Builder(context).setTitle(dialogTitle)

            if (listOfNumbers.isNotEmpty()) {

                dialog.setSingleChoiceItems(
                    listOfNumbers,
                    -1
                ) { dialogInterface, index ->
                    sharedPreferences.addContactToPreferences(
                        listOfNumbers[index]
                    )
                    dialogInterface.dismiss()
                }
                    .show()
            } else {
                dialog.setMessage(context?.getString(R.string.no_entries).toString())
                    .setPositiveButton("OK") { dialogInterface, _ -> dialogInterface.dismiss() }
                    .show()
            }
            cancel()
        }
    }

    private fun showSnackBarNumberFromSP() {
        val snackBarText = sharedPreferences.getContactFromPreferences()
            .ifEmpty { context?.getString(R.string.no_entries) }.toString()

        Snackbar.make(
            binding.root,
            snackBarText,
            Snackbar.LENGTH_SHORT
        ).setAnchorView(binding.showFromSp)
            .show()
    }
}
