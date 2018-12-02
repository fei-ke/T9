package com.fei_ke.t9

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fei_ke.common.base.BaseEpoxyHolder
import kotlinx.android.synthetic.main.app_item.view.*

@EpoxyModelClass(layout = R.layout.app_item)
abstract class AppModel(val app: App) : EpoxyModelWithHolder<AppModel.ViewHolder>() {
    init {
        id(app.className)
    }

    @EpoxyAttribute
    protected var onAppClickListener: View.OnClickListener? = null
    @EpoxyAttribute
    protected var onAppLongClickListener: View.OnLongClickListener? = null

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        with(holder) {
            text.text = app.label
            icon.setImageDrawable(app.icon)
            touchArea.setOnClickListener(onAppClickListener)
            touchArea.setOnLongClickListener(onAppLongClickListener)
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val icon: ImageView by lazy { itemView.imageViewIcon }
        val text: TextView by lazy { itemView.textViewName }
        val touchArea: View by lazy { itemView.touchArea }
    }
}
