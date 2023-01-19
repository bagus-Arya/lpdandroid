<?php 
namespace App\Helper;

class Helper{
    public static function rupiah($angka){

		$hasil_rupiah = number_format($angka, 0, ',', '.');
		return $hasil_rupiah;
	 
	}
}