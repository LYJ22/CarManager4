package com.autoever.carmanager4.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.autoever.carmanager4.R
import com.bumptech.glide.Glide

class ImageAdapter(private val context: Context, private val images: List<Uri>) : BaseAdapter() {
    override fun getCount(): Int = images.size
    override fun getItem(position: Int):Any = images[position]
    override fun getItemId(position: Int):Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_image, parent, false)
        val imageView: ImageView = view.findViewById(R.id.image_view)
        Glide.with(context).load(images[position]).into(imageView)
        return view
    }
}