package by.seka.clevertec.hometask3.presentation

import androidx.lifecycle.ViewModel
import by.seka.clevertec.hometask3.data.Repository
import by.seka.clevertec.hometask3.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    fun getContacts() : Flow<List<Contact>>{
       return repository.getAll()
    }
}