package com.gagz.instafilter.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.widget.AdapterView
import com.gagz.instafilter.R
import com.gagz.instafilter.adapters.AbstractFilterAdapter
import com.gagz.instafilter.filters.AbstractFilter
import com.gagz.instafilter.filters.Amartoka
import com.gagz.instafilter.filters.Juno
import com.gagz.instafilter.filters.Original
import jp.co.cyberagent.android.gpuimage.GPUImage
import kotlinx.android.synthetic.main.activity_photo_filter.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PhotoFilterActivity : AppCompatActivity() {

    companion object {

        val REQUEST_CODE_PHOTO_PICK = 1
    }

    private val filters by lazy {

        Array<AbstractFilter>(3, { i ->

            when (i) {

                0 -> Original(this@PhotoFilterActivity)
                1 -> Juno(this@PhotoFilterActivity)
                2 -> Amartoka(this@PhotoFilterActivity)
                else -> throw IndexOutOfBoundsException()
            }
        })
    }

    private var lastFilterIndex = 0
    private var lastUsedFilter: AbstractFilter? = null

    private val adapter: AbstractFilterAdapter by lazy {

        AbstractFilterAdapter.lastPosition = lastFilterIndex
        AbstractFilterAdapter(this@PhotoFilterActivity,
                R.layout.list_filter, filters)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_filter)

        bindViews()
        initAdapter()
        pickPhoto()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {

            REQUEST_CODE_PHOTO_PICK -> {

                when (resultCode) {

                    Activity.RESULT_OK -> {

                        gpuImage.setImage(data?.data)
                        gpuImage.setScaleType(GPUImage.ScaleType.CENTER_CROP)
                    }

                    else -> finish()
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindViews() {

        saveButton.setOnClickListener {

            saveImage()
        }
    }

    private fun initAdapter() {

        AbstractFilterAdapter.lastPosition = lastFilterIndex
        horizontalList.adapter = adapter
        horizontalList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            try {

                if (position == lastFilterIndex) return@OnItemClickListener
                lastFilterIndex = position
                AbstractFilterAdapter.lastPosition = position
                adapter.notifyDataSetChanged()

                filters[position].context = this@PhotoFilterActivity
                filters[position].initialize()
                val imageFilter = filters[position].filter

                gpuImage.filter = imageFilter
                gpuImage.requestRender()

                if (lastUsedFilter == null) {
                    lastUsedFilter = filters[position]
                } else {
                    lastUsedFilter?.release()
                    lastUsedFilter = filters[position]
                }
            } catch (e: OutOfMemoryError) {

                Log.e("APP:", "Error $e")
            }
        }
    }

    private fun pickPhoto() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO_PICK)
    }

    private fun saveImage() {

        val filename = "${System.currentTimeMillis()}.jpg"
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(path, "${getString(R.string.app_name)}/$filename")
        file.parentFile.mkdirs()

        var out: FileOutputStream? = null
        val bitmap = gpuImage.gpuImage.bitmapWithFilterApplied
        try {
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
