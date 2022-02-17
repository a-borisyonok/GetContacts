package by.seka.clevertec.hometask3.data.preferences

import android.content.Context
import by.seka.clevertec.hometask3.util.EMPTY_STRING
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

    fun addContactToPreferences(number: String) {
        sharedPreferences.edit().putString(PREFERENCE_NAME, number).apply()
    }

    fun getContactFromPreferences(): String {
        return sharedPreferences.getString(PREFERENCE_NAME, EMPTY_STRING).toString()
    }
}