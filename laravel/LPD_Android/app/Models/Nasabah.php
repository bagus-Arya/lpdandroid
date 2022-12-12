<?php

namespace App\Models;

use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Nasabah extends Model
{
    use HasFactory;
    use SoftDeletes;
    protected $fillable = [
        'fullname',
        'username',
        'staff_id',
        'tgl_lahir',
        'ktp_photo',
        'no_telepon',
        'no_ktp',
        'password',
        'jenis_kelamin'
        
    ];

    protected $hidden = [
        'password',
    ];
    protected $dates = ['deleted_at'];

    public function scopeFilter($query,array $filters)
    {
        $query->when($filters['fullname'] ?? false,function($query,$fullname){
            return $query->where('fullname','like','%'.$fullname.'%');
        });
    }

    public function bukutabungan(){
        return $this->hasOne(BukuTabungan::class);
    }

    public function kolektor(){
        return $this->belongsTo(Staff::class,'staff_id','id');
    }

    public function transaksis(){
        return $this->hasManyThrough(Transaksi::class,BukuTabungan::class);
    }
}
