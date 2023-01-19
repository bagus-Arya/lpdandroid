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
        Schema::create('nasabahs', function (Blueprint $table) {
            $table->id();
            $table->string('fullname');
            $table->string('username')->unique();
            $table->enum('jenis_kelamin',['Laki-Laki','Perempuan']);
            $table->foreignId('staff_id')->constrained('staff')->onUpdate('cascade')->onDelete('cascade');
            $table->date('tgl_lahir');
            $table->mediumText('ktp_photo');
            $table->string('no_telepon');
            $table->string('no_ktp');
            $table->string('password');
            $table->string('alamat');
            $table->timestamps();
            $table->softDeletes();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('nasabahs');
    }
};
