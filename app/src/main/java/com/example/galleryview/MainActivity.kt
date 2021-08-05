package com.example.galleryview

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.galleryview.databinding.ActivityMainBinding
import com.example.galleryview.viewmodels.MainViewModel
import com.example.galleryview.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var viewModel : MainViewModel
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myVersion = Build.VERSION.SDK_INT;
        if (myVersion > Build.VERSION_CODES.M) {
            if (!checkSelfPermission()) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 101
                )
            }
        }
        viewModelFactory = MainViewModelFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel.onClickPicture.observe(this, {
            if(it) {
                binding.picturesBtn.typeface = Typeface.DEFAULT_BOLD
                binding.storiesBtn.typeface = Typeface.DEFAULT
                binding.albumsBtn.typeface = Typeface.DEFAULT
            }
        })

        viewModel.onClickAlbum.observe(this, {
            if(it) {
                binding.picturesBtn.typeface = Typeface.DEFAULT
                binding.storiesBtn.typeface = Typeface.DEFAULT
                binding.albumsBtn.typeface = Typeface.DEFAULT_BOLD
            }
        })

        viewModel.onClickStory.observe(this, {
            if(it) {
                binding.picturesBtn.typeface = Typeface.DEFAULT
                binding.storiesBtn.typeface = Typeface.DEFAULT_BOLD
                binding.albumsBtn.typeface = Typeface.DEFAULT
            }
        })
        onClickBottomButton()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Granted")
            } else {
                Log.d("Permission", "Denied")
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkSelfPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private fun onClickBottomButton() {
        binding.apply {
            albumsBtn.setOnClickListener(View.OnClickListener {
                viewModel.openAlbumFragment()
            })
            picturesBtn.setOnClickListener(View.OnClickListener {
                viewModel.openPictureFragment()
            })
            storiesBtn.setOnClickListener(View.OnClickListener {
                viewModel.openStoryFragment()
            })
        }
    }
}