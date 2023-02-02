package com.rw.keyboardlistener.com.go.sispentra.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.go.sispentra.R
import com.rw.keyboardlistener.com.go.sispentra.data.Transaksi
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryTransaksiAdapter (val transaksis:ArrayList<Transaksi>): RecyclerView.Adapter<HistoryTransaksiAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cv_nama_nasabah: TextView =itemView.findViewById(R.id.cv_nama_nasabah)
        val cv_nama_kolektor: TextView =itemView.findViewById(R.id.cv_nama_kolektor)
        val cv_no_tabungan: TextView =itemView.findViewById(R.id.cv_no_tabungan)
        val cv_tipe_transaksi: TextView =itemView.findViewById(R.id.cv_tipe_transaksi)
        val cv_tgl_transaksi: TextView =itemView.findViewById(R.id.cv_tgl_transaksi)
        val cv_tgl_validasi_bendahara: TextView =itemView.findViewById(R.id.cv_tgl_validasi_bendahara)
        val cv_tgl_validasi_kolektor: TextView =itemView.findViewById(R.id.cv_tgl_validasi_kolektor)
        val cv_tgl_validasi_nasabah: TextView =itemView.findViewById(R.id.cv_tgl_validasi_nasabah)
        val cv_nominal: TextView =itemView.findViewById(R.id.cv_nominal)
        val cv_status_penarikan: TextView =itemView.findViewById(R.id.cv_status_penarikan)
        val tglValKolektor: TextView =itemView.findViewById(R.id.tglValKolektor)
        val tglValKolektorKoma: TextView =itemView.findViewById(R.id.tglValKolektorKoma)
        val tglValNasabah: TextView =itemView.findViewById(R.id.tglValNasabah)
        val tglValNasabahKoma: TextView =itemView.findViewById(R.id.tglValNasabahKoma)
        val textViewNominal:TextView= itemView.findViewById(R.id.textViewNominal)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.cardview_transaksi_history,parent,false)
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
        holder.cv_tipe_transaksi.text=currentItem.type_transaksi
        holder.cv_status_penarikan.text=currentItem.status
        holder.cv_no_tabungan.text=currentItem.no_tabungan
        holder.cv_nama_kolektor.text=currentItem.kolektor_name
        holder.cv_nama_nasabah.text=currentItem.nasabah_name
        if(currentItem.type_transaksi=="Setoran"){
            holder.cv_nominal.text=formatRupiah(currentItem.nominal)
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
            holder.tglValKolektor.visibility= GONE
            holder.tglValKolektorKoma.visibility= GONE
            holder.tglValNasabah.visibility= GONE
            holder.tglValNasabahKoma.visibility= GONE
            holder.cv_tgl_validasi_kolektor.visibility= GONE
            holder.cv_tgl_validasi_nasabah.visibility= GONE
//            holder.textViewNominal
        }
        else{
            holder.cv_nominal.text="-"+formatRupiah(currentItem.nominal)
            holder.cv_nominal.setTextColor(Color.parseColor(red))
            if(currentItem.status=="unvalidated" || currentItem.status=="validated-bendahara" || currentItem.status=="validated-kolektor"){
                holder.cv_status_penarikan.setTextColor(Color.parseColor(pending))
            }
            else if( currentItem.status=="validated-nasabah"){
                holder.cv_status_penarikan.setTextColor(Color.parseColor(green))
            }
            else{
                holder.cv_status_penarikan.setTextColor(Color.parseColor(red))
            }
        }
        holder.cv_tgl_transaksi.text=currentItem.tgl_transaksi
        holder.cv_tgl_validasi_bendahara.text=currentItem.tgl_validasi_bendahara
        holder.cv_tgl_validasi_kolektor.text=currentItem.tgl_validasi_kolektor
        holder.cv_tgl_validasi_nasabah.text=currentItem.tgl_validasi_nasabah
    }

    override fun getItemCount(): Int {
        return transaksis.size
    }
}