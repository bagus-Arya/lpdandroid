package com.rw.keyboardlistener.com.go.sispentra.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go.sispentra.R
import com.rw.keyboardlistener.com.go.sispentra.data.Transaksi

class HistoryTransaksiAdapter (val transaksis:ArrayList<Transaksi>): RecyclerView.Adapter<HistoryTransaksiAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cv_nama_nasabah: TextView =itemView.findViewById(R.id.cv_nama_nasabah)
        val cv_nama_kolektor: TextView =itemView.findViewById(R.id.cv_nama_kolektor)
        val cv_no_tabungan: TextView =itemView.findViewById(R.id.cv_no_tabungan)
        val cv_tipe_transaksi: TextView =itemView.findViewById(R.id.cv_tipe_transaksi)
        val cv_tgl_transaksi: TextView =itemView.findViewById(R.id.cv_tgl_transaksi)
        val cv_tgl_validasi_bendahara: TextView =itemView.findViewById(R.id.cv_tgl_validasi_bendahara)
        val cv_nominal: TextView =itemView.findViewById(R.id.cv_nominal)
        val cv_status_penarikan: TextView =itemView.findViewById(R.id.cv_status_penarikan)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.cardview_transaksi_history,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var green="#0af248"
        var textPenarikan="#323332"
        var pending="#fae607"
        var red="#e80909"
        val currentItem=transaksis[position]
        holder.cv_tipe_transaksi.text=currentItem.type_transaksi
        holder.cv_status_penarikan.text=currentItem.status
        holder.cv_no_tabungan.text=currentItem.no_tabungan
        holder.cv_nama_kolektor.text=currentItem.kolektor_name
        holder.cv_nama_nasabah.text=currentItem.nasabah_name
        if(currentItem.type_transaksi=="Setoran"){
            holder.cv_nominal.text="Rp."+currentItem.nominal.toString()
            holder.cv_nominal.setTextColor(Color.parseColor(green))
            if(currentItem.status=="unvalidated"){
                holder.cv_status_penarikan.setTextColor(Color.parseColor(pending))
            }
            else if(currentItem.status=="validated-bendahara"){
                holder.cv_status_penarikan.setTextColor(Color.parseColor(green))
            }
            else{
                holder.cv_status_penarikan.setTextColor(Color.parseColor(red))
            }
        }
        else{
            holder.cv_nominal.text="-"+currentItem.nominal.toString()
            holder.cv_nominal.setTextColor(Color.parseColor(red))
            if(currentItem.status=="unvalidated" || currentItem.status=="validated-bendahara"){
                holder.cv_status_penarikan.setTextColor(Color.parseColor(pending))
            }
            else if( currentItem.status=="validated-kolektor"){
                holder.cv_status_penarikan.setTextColor(Color.parseColor(green))
            }
            else{
                holder.cv_status_penarikan.setTextColor(Color.parseColor(red))
            }
        }
        holder.cv_tgl_transaksi.text=currentItem.tgl_transaksi
        holder.cv_tgl_validasi_bendahara.text=currentItem.tgl_validasi_bendahara
    }

    override fun getItemCount(): Int {
        return transaksis.size
    }
}