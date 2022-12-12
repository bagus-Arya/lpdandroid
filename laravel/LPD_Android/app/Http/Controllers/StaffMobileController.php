<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use \App\Models\Staff;
use \App\Models\Token;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;


class StaffMobileController extends Controller
{
    public function index(Request $request,Token $token){
        $filters=$request->validate([
            'fullname'=>'string',
        ]);
        return Staff::latest()->filter($filters)->get()->except($request->get('login_user')->id);
    }
    
    public function store(Request $request,Token $token){
         // validate input
         $validate = $request->validate([
            'fullname'=>'required|string',
            'username'=>'required|unique:nasabahs|unique:staff',
            'no_telepon'=>'required|string',
            'password'=>'required|string',
            'role'=>[
                'required',
                'string',
                Rule::in(['Kolektor','Bendahara','Ketua'])
            ],
            'jenis_kelamin'=>[
                'required',
                'string',
                Rule::in(['Laki-Laki','Perempuan'])
            ]
        ]);
        return Staff::create($validate);
    }

    public function destory(Request $request,Token $token,Staff $staff){
        return $staff->delete();
    }
}
