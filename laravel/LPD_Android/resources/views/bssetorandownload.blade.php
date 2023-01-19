<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Laporan Setoran</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-GLhlTQ8iRABdZLl6O3oVMWSktQOp6b7In1Zl3/Jr59b6EGGoI1aFkw7cmDA6j6gD" crossorigin="anonymous">
  </head>
  <body id="idBody">
    <div class="container-fluid py-4" id="idContainer">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-body" >
                      <div class="row justify-content-md-start justify-content-center">
                        <div class="col-lg-2 col-md-2 col-sm-3 col-4 text-sm-end text-center"><img 
                            style="width: 100%;object-fit: cover;"
                            src="{{ asset('assets/img/icon_lpd.png') }}">
                        </div>
                        <div class="col-lg-5 col-md-6 col-sm-8 col-12 text-sm-start text-center">
                          <div class="d-flex align-items-center h-100">
                            <div class="w-100">
                                <h2>Sispentra</h2>
                                <h4 class="text-muted">Sistem Pencatatan Tabungan</h4>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="row justify-content-center">
                        <div class="col-12 text-center mt-4"><h1>Laporan Setoran</h1></div>
                      </div>
                      <div class="row mt-2 ps-sm-4 ps-2">
                        <div class="col-auto px-0 fs-5">Nama Kolektor</div>
                        <div class="col fs-5"> : <span id="kolektor_fullname">{{ $userLoginData->fullname }}</span></div>
                      </div>
                      <div class="row ps-sm-4 ps-2">
                        <div class="col-auto px-0 fs-5">Role         </div>
                        <div class="col fs-5"> : <span id="kolektor_role">{{ $userLoginData->role }}</span></div>
                      </div>
                      <div class="row ps-sm-4 ps-2">
                        <div class="col-auto px-0 fs-5">No Telepon   </div>
                        <div class="col fs-5"> : <span id="kolektor_no_telepon">(+62){{ $userLoginData->no_telepon }}</span></div>
                      </div>
                      <div class="row ps-sm-4 ps-2">
                        <div class="col-auto px-0 fs-5">Tgl Transaksi   </div>
                        <div class="col fs-5"> : <span id="kolektor_start_date">{{ $validate['start_date'] }}</span> ~ <span id="kolektor_end_date">{{ $validate['end_date'] }}</span></div>
                      </div>
                      <div class="row mt-4 ps-sm-4">
                        <div class="col-12">
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                      <tr>
                                        <th scope="col">No</th>
                                        <th scope="col">Nama Nasabah</th>
                                        <th scope="col">No Buku Tabungan</th>
                                        <th scope="col">Tanggal Transaksi</th>
                                        <th scope="col">Nominal</th>
                                      </tr>
                                    </thead>
                                    <tbody id="table_laporan_body">
                                        @if (count($transaksiArray)>0)
                                            @php
                                                $i=0;        
                                            @endphp
                                            @foreach ($transaksiArray as $transaksi)
                                                @php
                                                    $i++;        
                                                @endphp
                                                <tr>
                                                    <td scope="col">{{$i}}</td>
                                                    <td scope="col">{{$transaksi->bukutabungan->nasabah->fullname}}</td>
                                                    <td scope="col">{{$transaksi->bukutabungan->no_tabungan}}</td>
                                                    <td scope="col">{{$transaksi->tgl_transaksi}}</td>
                                                    <td scope="col">Rp.{{rupiah($transaksi->nominal)}}</td>
                                                </tr>
                                            @endforeach
                                        @endif
                                      
                                    </tbody>
                                    <tfoot>
                                        @if (count($transaksiArray)>0)
                                         <tr>
                                            <td colspan="4"><h3>Total</h3></td>
                                            <td><h3>Rp.<span id='totalTransaksi'>{{ $transaksiJml }}</span></h3></td>
                                          </tr>
                                        @else
                                        <tr>
                                            <td colspan="4"><h3>Total</h3></td>
                                            <td><h3>Rp.<span id='totalTransaksi'></span></h3></td>
                                          </tr>
                                        @endif
                                      </tfoot>
                                </table>
                            </div>
                        </div>
                      </div>
                      <div class="row mt-5 pe-sm-4 justify-content-end">
                        <div class="col-auto">Denpasar, <span>{{ \Carbon\Carbon::now()->day . ' ' . \Carbon\Carbon::now()->format('F') . ' ' . \Carbon\Carbon::now()->year }}</span></div>
                      </div>
                      <div class="row pe-sm-4 justify-content-end">
                        <div class="col-auto" style="height: 75px"></div>
                      </div>
                      <div class="row pe-sm-4 justify-content-end">
                        <div class="col-auto">{{ $userLoginData->fullname }}</div>
                      </div>
                    </div>
                  </div>
            </div>
        </div>
        
    </div>


    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js" integrity="sha384-oBqDVmMz9ATKxIep9tiCxS/Z9fNfEXiDAYTujMAeBAsjFuCZSmKbSSUnQlmh/jp3" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.min.js" integrity="sha384-mQ93GR66B00ZXjt0YO5KlohRA5SY2XofN4zfuZxLkoj1gXtW8ANNCe9d5Y3eG5eD" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" integrity="sha384-w76AqPfDkMBDXo30jS1Sgez6pr3x5MlQ1ZAGC+nuZB+EYdgRZgiwxhTBTkF7CXvN" crossorigin="anonymous"></script>
    </body>
</html>