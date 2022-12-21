package com.rw.keyboardlistener.com.go.sispentra.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go.sispentra.R
import com.rw.keyboardlistener.com.go.sispentra.data.Nasabah
import com.rw.keyboardlistener.com.go.sispentra.data.Staff
import com.rw.keyboardlistener.com.go.sispentra.data.Tabungan

class NasabahListAdapter (var nasabahs:ArrayList<Nasabah>,
                          var bukuTabungans:ArrayList<Tabungan>,
                          var staffs:ArrayList<Staff>,
                          val listener: NasabahListAdapter.OnAdapterListener): RecyclerView.Adapter<NasabahListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val nasabah_nama_nasabah: TextView =itemView.findViewById(R.id.nasabah_nama_nasabah)
        val nasabah_no_tabungan_nasabah: TextView =itemView.findViewById(R.id.nasabah_no_tabungan_nasabah)
        val nasabah_no_telepon_nasabah: TextView =itemView.findViewById(R.id.nasabah_no_telepon_nasabah)
        val nasabah_nama_kolektor_nasabah: TextView =itemView.findViewById(R.id.nasabah_nama_kolektor_nasabah)
        val nasabah_saldo_nasabah: TextView =itemView.findViewById(R.id.nasabah_nama_kolektor_nasabah)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.cardview_nasabah_list,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder:MyViewHolder, position: Int) {
        val nasabah=nasabahs[position]
        val bukuTabungan=bukuTabungans[position]
        val staff=staffs[position]
        holder.nasabah_nama_nasabah.text=nasabah.fullname
        holder.nasabah_saldo_nasabah.text=bukuTabungan.saldo.toString()
        holder.nasabah_no_tabungan_nasabah.text=bukuTabungan.no_tabungan
        holder.nasabah_no_telepon_nasabah.text=nasabah.no_telepon
        holder.nasabah_nama_kolektor_nasabah.text=staff.fullname
        holder.itemView.setOnClickListener { listener.onClick( nasabah,bukuTabungan,staff,position) }
    }

    override fun getItemCount(): Int {
        return nasabahs.size
    }

    interface OnAdapterListener {
        fun onClick(nasabah:Nasabah,bukuTabungan:Tabungan,staff:Staff,position:Int)
    }
}