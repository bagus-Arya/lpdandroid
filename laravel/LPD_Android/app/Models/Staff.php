<?php

namespace App\Models;

use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Staff extends Model
{
    use HasFactory;
    use SoftDeletes;
    protected $fillable = [
        'fullname',
        'username',
        'role',
        'no_telepon',
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
}
