<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Transaksi extends Model
{
    use HasFactory;
    protected $fillable = [
        'type_transaksi',
        'nasabah_id',
        'buku_tabungan_id',
        'tgl_transaksi',
        'tgl_validasi_bendahara',
        'tgl_validasi_kolektor',
        'tgl_validasi_nasabah',
        'nominal',
        'status'
    ];
    public function bukutabungan(){
        return $this->belongsTo(BukuTabungan::class,'buku_tabungan_id','id');
    }
}
