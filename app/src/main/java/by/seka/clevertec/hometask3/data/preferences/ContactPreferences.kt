package by.seka.clevertec.hometask3.data.preferences

import android.content.Context
import by.seka.clevertec.hometask3.domain.model.Contact
import by.seka.clevertec.hometask3.util.EMPTY_STRING
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

const val PREFERENCE_NAME = "contact"

class ContactPreferences @Inject constructor(
    @ApplicationContext appContext: Context
) {
    private val sharedPreferences = appContext.getSharedPreferences(
        PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    fun addContactToPreferences(contact: Contact) {
        val contactString = Gson().toJson(contact)
        sharedPreferences.edit().putString(PREFERENCE_NAME, contactString).apply()
    }

    fun getContactFromPreferences(): Contact {
        val contactString: String? = sharedPreferences.getString(PREFERENCE_NAME, EMPTY_STRING)
        return Gson().fromJson(contactString, Contact::class.java)
    }
}