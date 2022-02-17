package by.seka.clevertec.hometask3.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.seka.clevertec.hometask3.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}