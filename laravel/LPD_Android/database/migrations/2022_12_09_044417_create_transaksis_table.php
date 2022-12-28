<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('transaksis', function (Blueprint $table) {
            $table->id();
            $table->enum('type_transaksi',['Setoran','Penarikan']);
            $table->integer('nominal');
            // $table->foreignId('nasabah_id')->constrained('nasabahs')->onUpdate('cascade')->onDelete('cascade');
            $table->enum('status',['unvalidated','validated-bendahara','validated-kolektor','rejected-bendahara','rejected-kolektor']);
            $table->foreignId('buku_tabungan_id')->constrained('buku_tabungans')->onUpdate('cascade')->onDelete('cascade');
            $table->date('tgl_transaksi');
            $table->date('tgl_validasi_bendahara')->nullable();;
            $table->date('tgl_validasi_kolektor')->nullable();;
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('transaksis');
    }
};
