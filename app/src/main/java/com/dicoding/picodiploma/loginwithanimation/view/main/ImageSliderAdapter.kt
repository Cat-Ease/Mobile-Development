package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.dicoding.picodiploma.loginwithanimation.R

class ImageSliderAdapter(private val context: Context, private val images: List<Int>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = LayoutInflater.from(context).inflate(R.layout.image_slider_item, container, false) as ImageView
        imageView.setImageResource(images[position])
        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int = images.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`
}