package com.gagz.instafilter.filters

import android.content.Context
import com.gagz.instafilter.R
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter


class Juno(context: Context) : AbstractFilter(R.drawable.clean, Juno::class.java.simpleName) {

    override fun initialize() {

        val filters = List<GPUImageFilter>(3, { i ->
            when (i) {
                0 -> GPUImageBrightnessFilter(0.1f)
                1 -> GPUImageContrastFilter(1.1f)
                2 -> GPUImageSharpenFilter(0.3f)
                else -> throw IndexOutOfBoundsException()
            }
        })

        filter = GPUImageFilterGroup(filters)
    }
}