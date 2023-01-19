<?php

namespace App\Http\Middleware;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;
use \App\Models\Token;
use \App\Models\Staff;
use \App\Models\Nasabah;

use Closure;
use Illuminate\Http\Request;

class CustomAuth
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure(\Illuminate\Http\Request): (\Illuminate\Http\Response|\Illuminate\Http\RedirectResponse)  $next
     * @return \Illuminate\Http\Response|\Illuminate\Http\RedirectResponse
     */
    public function handle(Request $request, Closure $next)
    {
        // return response()->json(['message' => 'Token Invalid'], 404);
        // $validate = $request->validate([
        //     'token'=>'required|string',
        // ]);
        
        $token=Token::where('token', $request->token)->first();

        if ($token==null) {
            return response()->json(['message' => 'Token Invalid'], 401);
        }
        
        if($token->type=="Staff"){
            $user=Staff::where('id','=',$token->user_id)->firstOrFail()->makeVisible(['password']);
            $user['token']=$token->token;
            $user['token_type']="Staff";
            $request->attributes->add(['login_user' => $user]);
        }
        else{
            $user=Nasabah::where('id','=',$token->user_id)->firstOrFail()->makeVisible(['password']);
            $user['token']=$token->token;
            $user['token_type']="Nasabah";
            $user['role']="Nasabah";
            $request->attributes->add(['login_user' => $user]);
        }
        

        return $next($request);
    }
}
