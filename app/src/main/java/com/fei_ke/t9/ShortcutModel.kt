package com.fei_ke.t9

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fei_ke.common.base.BaseEpoxyHolder

@EpoxyModelClass(layout = R.layout.app_item)
abstract class ShortcutModel(val shortcut: Shortcut) :
    EpoxyModelWithHolder<ShortcutModel.ViewHolder>() {
    init {
        id(shortcut.className)
    }

    @EpoxyAttribute
    protected var onShortcutClickListener: View.OnClickListener? = null
    @EpoxyAttribute
    protected var onShortcutLongClickListener: View.OnLongClickListener? = null

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        with(holder) {
            text.text = shortcut.label
            IconLoader.load(icon, shortcut.pkgName, shortcut.className)
            touchArea.setOnClickListener(onShortcutClickListener)
            touchArea.setOnLongClickListener(onShortcutLongClickListener)
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val icon: ImageView by lazy { itemView.findViewById(R.id.imageViewIcon) }
        val text: TextView by lazy { itemView.findViewById(R.id.textViewName) }
        val touchArea: View by lazy { itemView.findViewById(R.id.touchArea) }
    }
}
