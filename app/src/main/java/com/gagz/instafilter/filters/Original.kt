package com.gagz.instafilter.filters

import android.content.Context
import com.gagz.instafilter.R
import jp.co.cyberagent.android.gpuimage.GPUImageColorMatrixFilter


class Original(context: Context) : AbstractFilter(R.drawable.rgb, Original::class.java.simpleName) {

    override fun initialize() {

        filter = GPUImageColorMatrixFilter()
    }
}