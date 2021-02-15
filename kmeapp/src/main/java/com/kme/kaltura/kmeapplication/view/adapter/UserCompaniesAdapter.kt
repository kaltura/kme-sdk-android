package com.kme.kaltura.kmeapplication.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kme.kaltura.kmeapplication.R
import com.kme.kaltura.kmesdk.rest.response.user.KmeUserCompany
import kotlinx.android.synthetic.main.item_user_company_layout.view.*

class UserCompaniesAdapter(
    context: Context,
    private val inflater: LayoutInflater = LayoutInflater.from(context)
) : ArrayAdapter<KmeUserCompany>(context, R.layout.item_user_company_layout) {

    var onCompanyClick: ((userCompany: KmeUserCompany) -> Unit)? = null
    private val companies = mutableListOf<KmeUserCompany>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: UserCompanyViewHolder?

        return if (convertView == null) {
            val view = inflater.inflate(R.layout.item_user_company_layout, parent, false)
            holder = UserCompanyViewHolder(view)
            view.tag = holder
            holder.bind(getItem(position))
            view
        } else {
            holder = convertView.tag as UserCompanyViewHolder
            holder.bind(getItem(position))
            convertView
        }
    }

    override fun getItem(position: Int): KmeUserCompany {
        return companies[position]
    }

    override fun getItemId(position: Int): Long {
        return companies[position].id ?: 0L
    }

    fun addData(data: List<KmeUserCompany>) {
        companies.clear()
        companies.addAll(data)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return companies.size
    }

    inner class UserCompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(userCompany: KmeUserCompany) {
            with(itemView) {
                tvItem.text = userCompany.name

                setOnClickListener {
                    onCompanyClick?.invoke(userCompany)
                }
            }
        }
    }
}