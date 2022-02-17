package by.seka.clevertec.hometask3.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import dagger.hilt.android.AndroidEntryPoint
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


            showContacts.setOnClickListener {
                lifecycleScope.launchWhenStarted {
                    viewModel.getContacts().collectLatest {
                        if (it.isNotEmpty()){
                            sharedPreferences.addContactToPreferences(it[0])
                        }
                    }
                }
            }
            showFromSp.setOnClickListener {
                Log.i("##", sharedPreferences.getContactFromPreferences().toString())
            }
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

    private fun addContactToDatabase(){
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
                }
            }
        }
    }
}
