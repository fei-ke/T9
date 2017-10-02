package com.fei_ke.t9

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.fei_ke.common.base.BaseEpoxyHolder
import com.fei_ke.common.kotterknife.bindView

@EpoxyModelClass(layout = R.layout.app_item)
abstract class AppModel(val app: App) : EpoxyModelWithHolder<AppModel.ViewHolder>() {
    init {
        id(app.className)
    }

    @EpoxyAttribute
    protected var onAppClickListener: View.OnClickListener? = null

    override fun bind(holder: ViewHolder) {
        super.bind(holder)
        with(holder) {
            text.text = app.label
            icon.setImageDrawable(app.icon)
            touchArea.setOnClickListener(onAppClickListener)
        }
    }

    class ViewHolder : BaseEpoxyHolder() {
        val icon: ImageView by bindView(R.id.imageViewIcon)
        val text: TextView by bindView(R.id.textViewName)
        val touchArea: View by bindView(R.id.touchArea)
    }
}
