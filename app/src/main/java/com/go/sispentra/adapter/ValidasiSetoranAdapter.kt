package com.rw.keyboardlistener.com.go.sispentra.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go.sispentra.R
import com.rw.keyboardlistener.com.go.sispentra.data.Transaksi
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ValidasiSetoranAdapter (val transaksis:ArrayList<Transaksi>, val listener: ValidasiSetoranAdapter.OnAdapterListener): RecyclerView.Adapter<ValidasiSetoranAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cv_nama_nasabah: TextView =itemView.findViewById(R.id.cv_nama_nasabah)
        val cv_nama_kolektor: TextView =itemView.findViewById(R.id.cv_nama_kolektor)
        val cv_no_tabungan: TextView =itemView.findViewById(R.id.cv_no_tabungan)
        val cv_tgl_transaksi: TextView =itemView.findViewById(R.id.cv_tgl_transaksi)
        val cv_jumlah_setoran: TextView =itemView.findViewById(R.id.cv_jumlah_setoran)
        val cv_status_setoran: TextView =itemView.findViewById(R.id.cv_status_setoran)
        val cv_saldo_nasabah: TextView =itemView.findViewById(R.id.cv_saldo_nasabah)
        val btn_reject: Button =itemView.findViewById(R.id.btn_reject)
        val btn_validasi: TextView =itemView.findViewById(R.id.btn_validasi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.cardview_validasi_setoran,parent,false)
        return MyViewHolder(itemView)
    }

    private fun formatRupiah(number: Double): String? {
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var green="#0af248"
        var textPenarikan="#323332"
        var pending="#fae607"
        var red="#e80909"
        val currentItem=transaksis[position]
        holder.cv_status_setoran.text=currentItem.status
        holder.cv_no_tabungan.text=currentItem.no_tabungan
        holder.cv_nama_kolektor.text=currentItem.kolektor_name
        holder.cv_nama_nasabah.text=currentItem.nasabah_name
        holder.cv_saldo_nasabah.text= currentItem.saldo?.let { formatRupiah(it) }
        if(currentItem.type_transaksi=="Setoran"){
            holder.cv_jumlah_setoran.text=formatRupiah(currentItem.nominal)
            holder.cv_jumlah_setoran.setTextColor(Color.parseColor(green))
            if(currentItem.status=="unvalidated"){
                holder.cv_status_setoran.setTextColor(Color.parseColor(pending))
            }
            else if(currentItem.status=="validated-bendahara"){
                holder.cv_status_setoran.setTextColor(Color.parseColor(green))
            }
            else{
                holder.cv_status_setoran.setTextColor(Color.parseColor(red))
            }
        }
        else{
            holder.cv_jumlah_setoran.text="-"+formatRupiah(currentItem.nominal)
            holder.cv_jumlah_setoran.setTextColor(Color.parseColor(red))
            if(currentItem.status=="unvalidated" || currentItem.status=="validated-bendahara" ||  currentItem.status=="validated-kolektor"){
                holder.cv_status_setoran.setTextColor(Color.parseColor(pending))
            }
            else if( currentItem.status=="validated-nasabah"){
                holder.cv_status_setoran.setTextColor(Color.parseColor(green))
            }
            else{
                holder.cv_status_setoran.setTextColor(Color.parseColor(red))
            }
        }
        holder.cv_tgl_transaksi.text=currentItem.tgl_transaksi
        holder.btn_reject.setOnClickListener { listener.onReject( currentItem,position) }
        holder.btn_validasi.setOnClickListener { listener.onValidasi( currentItem,position) }
    }

    override fun getItemCount(): Int {
        return transaksis.size
    }


    interface OnAdapterListener {
        fun onReject(currentItem: Transaksi, position:Int)
        fun onValidasi(currentItem: Transaksi, position:Int)
    }
}