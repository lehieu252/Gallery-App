package com.example.galleryview

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.galleryview.databinding.ActivityMainBinding
import com.example.galleryview.viewmodels.MainViewModel
import com.example.galleryview.viewmodels.MainViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var viewModel: MainViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeColorBar(this, R.color.white, true)
        val myVersion = Build.VERSION.SDK_INT;
        if (myVersion > Build.VERSION_CODES.LOLLIPOP) {
            if (!checkSelfPermission()) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 101
                )
            }
            if (myVersion == Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }
        }


        viewModelFactory = MainViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        viewModel.getAllAlbums(this)

        viewModel.onClickPicture.observe(this, {
            if (it) {
                binding.picturesBtn.typeface = Typeface.DEFAULT_BOLD
                binding.storiesBtn.typeface = Typeface.DEFAULT
                binding.albumsBtn.typeface = Typeface.DEFAULT
            }
        })

        viewModel.onClickAlbum.observe(this, {
            if (it) {
                binding.picturesBtn.typeface = Typeface.DEFAULT
                binding.storiesBtn.typeface = Typeface.DEFAULT
                binding.albumsBtn.typeface = Typeface.DEFAULT_BOLD
            }
        })

        viewModel.onClickStory.observe(this, {
            if (it) {
                binding.picturesBtn.typeface = Typeface.DEFAULT
                binding.storiesBtn.typeface = Typeface.DEFAULT_BOLD
                binding.albumsBtn.typeface = Typeface.DEFAULT
            }
        })

        viewModel.showBottomNavigation()
        viewModel.hideBottomNav.observe(this, {
            if (it) {
                binding.bottomNavigation.visibility = View.GONE
                val animate = TranslateAnimation(
                    0f, 0f, 0f, binding.bottomNavigation.height.toFloat()
                )
                animate.duration = 100
                binding.bottomNavigation.startAnimation(animate)
            } else {
                val animate =
                    TranslateAnimation(0F, 0F, binding.bottomNavigation.height.toFloat(), 0F)
                animate.duration = 100
                binding.bottomNavigation.startAnimation(animate)
                binding.bottomNavigation.visibility = View.VISIBLE
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
                val navController =
                    Navigation.findNavController(this@MainActivity, R.id.myNavHostFragment)
                navController.popBackStack()
                navController.navigateUp()
                navController.navigate(R.id.albumFragment)
            })
            picturesBtn.setOnClickListener(View.OnClickListener {
                viewModel.openPictureFragment()
                val navController =
                    Navigation.findNavController(this@MainActivity, R.id.myNavHostFragment)
                navController.popBackStack()
                navController.navigateUp()
                navController.navigate(R.id.pictureFragment)
            })
            storiesBtn.setOnClickListener(View.OnClickListener {
                viewModel.openStoryFragment()
                val navController =
                    Navigation.findNavController(this@MainActivity, R.id.myNavHostFragment)
                navController.popBackStack()
                navController.navigateUp()
                navController.navigate(R.id.storyFragment)
            })
            hamburgerBtn.setOnClickListener {
                Toast.makeText(
                    this@MainActivity,
                    "Album is exist, try another name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun changeColorBar(activity: Activity, color: Int, light: Boolean) {
        val window = activity.window
        val osdColor = activity.getColor(color)
        window.statusBarColor = osdColor
        window.navigationBarColor = osdColor
        var visibility = window.decorView.systemUiVisibility
        if (light) {
            visibility = visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                visibility = visibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        } else {
            visibility = visibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                visibility = visibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
        window.decorView.systemUiVisibility = visibility
    }
}