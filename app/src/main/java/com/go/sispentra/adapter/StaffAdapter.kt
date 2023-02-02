package com.rw.keyboardlistener.com.go.sispentra.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.go.sispentra.R
import com.rw.keyboardlistener.com.go.sispentra.data.LoginData
import com.rw.keyboardlistener.com.go.sispentra.data.Staff
import java.sql.Array

class StaffAdapter (val staff:ArrayList<Staff>,val loginData: LoginData,val listener: OnAdapterListener):RecyclerView.Adapter<StaffAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val cv_nama_Staff:TextView=itemView.findViewById(R.id.cv_nama_Staff)
        val cv_jenis_kelamin:TextView=itemView.findViewById(R.id.cv_jenis_kelamin)
        val cv_no_telepon:TextView=itemView.findViewById(R.id.cv_no_telepon)
        val cv_role:TextView=itemView.findViewById(R.id.cv_role)
        val cv_hapus_staff:Button=itemView.findViewById(R.id.cv_hapus_staff)
        val btn_nasabah_staff:Button=itemView.findViewById(R.id.btn_nasabah_staff)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.cardview_staff,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=staff[position]
        holder.cv_nama_Staff.text=currentItem.fullname
        holder.cv_jenis_kelamin.text=currentItem.jenis_kelamin
        holder.cv_no_telepon.text=currentItem.no_telepon
        holder.cv_role.text=currentItem.role
        if(currentItem.role!="Kolektor"){
            holder.btn_nasabah_staff.visibility= View.GONE
        }
        if(loginData.role=="Bendahara"){
            holder.cv_hapus_staff.visibility=View.GONE
        }
        holder.btn_nasabah_staff.setOnClickListener { listener.onNasabah( currentItem,position) }
        holder.cv_hapus_staff.setOnClickListener { listener.onDelete( currentItem,position) }
    }

    override fun getItemCount(): Int {
        return staff.size
    }

    interface OnAdapterListener {
        fun onDelete(currentItem:Staff,position:Int)
        fun onNasabah(currentItem:Staff,position:Int)
    }
}