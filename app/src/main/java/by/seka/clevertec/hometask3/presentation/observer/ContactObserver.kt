package by.seka.clevertec.hometask3.presentation.observer

import android.content.Context
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import by.seka.clevertec.hometask3.domain.model.Contact
import by.seka.clevertec.hometask3.util.EMPTY_STRING
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ContactObserver(
    private val registry: ActivityResultRegistry,
    private val context: Context
) : DefaultLifecycleObserver {

    private var getContactInfo: ActivityResultLauncher<Void>? = null

    val contact = MutableSharedFlow<Contact>()

    override fun onCreate(owner: LifecycleOwner) {

        var phone = EMPTY_STRING
        var firstName = EMPTY_STRING
        var lastName = EMPTY_STRING
        var email = EMPTY_STRING

        getContactInfo = registry.register("key", owner, ActivityResultContracts.PickContact()) {

            var displayName = EMPTY_STRING

            it?.also { contactUri ->
                val projection = arrayOf(
                    ContactsContract.Data.DISPLAY_NAME,
                )
                context.contentResolver?.query(
                    contactUri, projection, null,
                    null, null
                )?.apply {
                    moveToFirst()
                    displayName = getString(0) ?: EMPTY_STRING
                    close()
                }

                context.contentResolver?.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null
                )?.apply {
                    while (moveToNext()) {
                        if (getString(
                                getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                    ?: 0
                            ) == displayName
                        ) {
                            phone =
                                getString(
                                    getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
                                        ?: 0
                                ) ?: EMPTY_STRING

                            break
                        }
                    }
                    close()
                }
                context.contentResolver?.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null, null, null, null
                )?.apply {
                    while (moveToNext()) {
                        if (getString(
                                getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                    ?: 0
                            ) == displayName
                        ) {
                            email =
                                getString(
                                    getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA) ?: 0
                                ) ?: EMPTY_STRING

                            break
                        }
                    }
                    close()
                }
                val whereName = ContactsContract.Data.MIMETYPE + " = ?"
                val whereNameParams =
                    arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                context.contentResolver?.query(
                    ContactsContract.Data.CONTENT_URI,
                    null, whereName, whereNameParams, null
                )?.apply {
                    while (moveToNext()) {
                        if (getString(
                                getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                    ?: 0
                            ) == displayName
                        ) {

                            firstName =
                                getString(
                                    getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)
                                        ?: 0
                                ) ?: EMPTY_STRING
                            lastName =
                                getString(
                                    getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
                                        ?: 0
                                ) ?: EMPTY_STRING
                            break
                        }
                    }
                    close()
                }
            }
            owner.lifecycleScope.launch {
                contact.emit(
                    Contact(0, phone, firstName, lastName, email)
                )
            }
        }
    }


    fun selectImage() {
        getContactInfo?.launch(null)

    }
}