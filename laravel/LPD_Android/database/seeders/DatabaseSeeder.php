<?php

namespace Database\Seeders;

// use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use \App\Models\Staff;
use \App\Models\Nasabah;
use \App\Models\BukuTabungan;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     *
     * @return void
     */
    public function run()
    {
        // \App\Models\User::factory(10)->create();

        // \App\Models\User::factory()->create([
        //     'name' => 'Test User',
        //     'email' => 'test@example.com',
        // ]);
        Staff::create([
            'fullname'=>'Ketua Dummy',
            'username'=>'ketua123',
            'role'=>'Ketua',
            'jenis_kelamin'=>'Laki-Laki',
            'no_telepon'=>'123456789',
            'password'=>'password',
        ]);

        Staff::create([
            'fullname'=>'Bendahara Dummy',
            'username'=>'bendahara23',
            'role'=>'Bendahara',
            'jenis_kelamin'=>'Laki-Laki',
            'no_telepon'=>'123456789',
            'password'=>'password',
        ]);

        $kolektor=Staff::create([
            'fullname'=>'kolektor Dummy',
            'username'=>'kolektor123',
            'role'=>'Kolektor',
            'jenis_kelamin'=>'Laki-Laki',
            'no_telepon'=>'123456789',
            'password'=>'password',
        ]);

        $nasabah=Nasabah::create([
            'fullname'=>'Nasabah Dummy',
            'username'=>'nasabah123',
            'jenis_kelamin'=>'Laki-Laki',
            'tgl_lahir'=>\Carbon\Carbon::now()->timezone('Asia/Kuala_Lumpur'),
            'staff_id'=>$kolektor->id,
            'ktp_photo'=>'lol',
            'no_telepon'=>'123456789',
            'no_ktp'=>'123456789',
            'password'=>'password',
        ]);

        BukuTabungan::create([
            'no_tabungan'=>(BukuTabungan::max('id')+1).'-'.rand(100000,999999),
            'nasabah_id'=>$nasabah->id,
        ]);
    }
}
