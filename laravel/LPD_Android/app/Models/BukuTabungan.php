<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class BukuTabungan extends Model
{
    use HasFactory;
    protected $fillable = [
        'no_tabungan',
        'nasabah_id',
    ];

    public function transaksis(){
        return $this->hasMany(Transaksi::class);
    }

    public function nasabah(){
        return $this->belongsTo(Nasabah::class);
    }
}
