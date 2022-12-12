<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;
use \App\Models\Staff;
use \App\Models\Nasabah;
use \App\Models\Token;


class LoginMobileController extends Controller
{
    public function login(Request $request){
        // validate input
        $validate = $request->validate([
            'username'=>'required|string',
            'password'=>'required|string',
        ]);
        $Staff=Staff::where('username', '=', $validate['username'])->where('password', '=', $validate['password'])->first();
        $Nasabah=nasabah::where('username', '=', $validate['username'])->where('password', '=', $validate['password'])->first();
        if ($Staff!==null) {
          
            $data=Token::create([
                'type'=>'Staff',
                'user_id'=>$Staff->id,
                'token'=>(Token::max('id')+1).'-'.uniqid("",true)
            ]);
            $data['role']=$Staff->role;
            return  $data;
        }
        else if ($Nasabah!==null) {
            $data=Token::create([
                'type'=>'Nasabah',
                'user_id'=>$Nasabah->id,
                'token'=>(Token::max('id')+1).'-'.uniqid("",true)
            ]);
            $data['role']="Nasabah";
            return $data;
        }
        return response()->json(['message' => 'Username Atau Password Salah'], 401);
    }

    public function logout(Request $request){
        $validate = $request->validate([
            'token'=>'required|string',
        ]);
        $token=Token::where('token', '=', $validate['token']);
        if ($token->exists()) {
            $token->delete();
            return response()->json(['message' => 'Delete Berhasil'], 200);
        }
        return response()->json(['message' => 'Token Invalid'], 401);
    }
}
