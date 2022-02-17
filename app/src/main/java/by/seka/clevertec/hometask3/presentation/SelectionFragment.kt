package by.seka.clevertec.hometask3.presentation

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.seka.clevertec.hometask3.R
import by.seka.clevertec.hometask3.data.preferences.ContactPreferences
import by.seka.clevertec.hometask3.databinding.SelectionFragmentBinding
import by.seka.clevertec.hometask3.presentation.observer.ContactObserver
import by.seka.clevertec.hometask3.util.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
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
        createChannel()
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
                showContactInNotification()
            }
        }
    }

    private fun createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                context?.getString(R.string.saved_contact),

                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

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
                    Toast.makeText(
                        context,
                        context?.getText(R.string.save_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    delay(500)
                    viewModel.addContact(it)
                    cancel()
                }
            }
        }
    }

    private fun showSavedContactsDialog() {
        lifecycleScope.launchWhenStarted {

            val listOfNumbers = viewModel.getNumbersList().toTypedArray()
            val dialogTitle = context?.getString(R.string.select_contact).toString()
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
                cancel()
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

    private fun showContactInNotification() {

        lifecycleScope.launchWhenStarted {
            if (sharedPreferences.getContactFromPreferences().isNotEmpty()) {

                val contact =
                    viewModel.getContact(sharedPreferences.getContactFromPreferences())

                val firstName = contact.firstName
                val lastName = contact.lastName
                val number = "${context?.getString(R.string.phone_number)} ${contact.number}"
                val email = "${context?.getString(R.string.email)} ${contact.email} "

                val notificationManager = ContextCompat.getSystemService(
                    requireContext(),
                    NotificationManager::class.java
                ) as NotificationManager

                notificationManager.sendNotification(
                    "$firstName $lastName",
                    "$number \n$email ",
                    requireContext()
                )
                cancel()
            } else {
                Snackbar.make(
                    binding.root,
                    context?.getString(R.string.no_entries).toString(),
                    Snackbar.LENGTH_LONG
                ).setAnchorView(binding.showNotification)
                    .show()
                cancel()
            }
        }
    }
}
