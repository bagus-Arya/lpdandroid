<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Laporan Penarikan</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-GLhlTQ8iRABdZLl6O3oVMWSktQOp6b7In1Zl3/Jr59b6EGGoI1aFkw7cmDA6j6gD" crossorigin="anonymous">
  </head>
  <body>
    <div class="container py-4">
        <form class="row g-3" action="javascript:void(0);">
            <div class="col-6">
              <label for="startDate" class="form-label">Start Date</label>
              <input type="date" class="form-control" id="startDate" value="{{now()->format('Y-m-d')}}">
            </div>
            <div class="col-6">
              <label for="endDate" class="form-label">End Date</label>
              <input type="date" class="form-control" id="endDate" value="{{now()->format('Y-m-d')}}">
            </div>
            <div class="col-12">
              <label for="inputKolektor" class="form-label">Kolektor Name</label>
              <select class="form-select" id="inputKolektor" aria-label="Default select example">
                @if (count($staffs) > 0)
                    @foreach ($staffs as $staff)
                        <option value="{{$staff->id}}">{{ $staff->fullname }}</option>
                    @endforeach
                @endif
              </select>
            </div>
            <div class="col-12 d-grid">
              <button id="submit" class="btn btn-success">Submit</button>
            </div>
        </form>
        <div class="row mt-4 mb-2 justify-content-end">
            <div class="col-12 col-sm-6 col-md-4  d-grid">
                <button type="button" id="download" class="btn btn-secondary">Donwload Laporan (PDF)</button>
            </div>
        </div>
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-body">
                      <div class="row justify-content-md-start justify-content-center">
                        <div class="col-lg-2 col-md-2 col-sm-3 col-4 text-sm-end text-center"><img 
                            style="width: 100%;object-fit: cover;"
                            src="{{ asset('assets/img/icon_lpd.png') }}">
                        </div>
                        <div class="col-lg-5 col-md-5 col-sm-8 col-12 text-sm-start text-center">
                          <div class="d-flex align-items-center h-100">
                            <div class="w-100">
                                <h2>Sispentra</h2>
                                <h4 class="text-muted">Sistem Pencatatan Tabungan</h4>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="row justify-content-center">
                        <div class="col-12 text-center mt-4"><h1>Laporan Penarikan</h1></div>
                      </div>
                      <div class="row mt-2 ps-sm-4 ps-2">
                        <div class="col-auto px-0 fs-5">Nama Kolektor</div>
                        <div class="col fs-5"> : <span id="kolektor_fullname"></span></div>
                      </div>
                      <div class="row ps-sm-4 ps-2">
                        <div class="col-auto px-0 fs-5">Role         </div>
                        <div class="col fs-5"> : <span id="kolektor_role"></span></div>
                      </div>
                      <div class="row ps-sm-4 ps-2">
                        <div class="col-auto px-0 fs-5">No Telepon   </div>
                        <div class="col fs-5"> : (+62)<span id="kolektor_no_telepon"></span></div>
                      </div>
                      <div class="row ps-sm-4 ps-2">
                        <div class="col-auto px-0 fs-5">Tgl Transaksi   </div>
                        <div class="col fs-5"> : <span id="kolektor_start_date"></span> ~ <span id="kolektor_end_date"></span></div>
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
                                     
                                      
                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <td colspan="4"><h3>Total</h3></td>
                                            <td><h3>Rp.<span id='totalTransaksi'></span></h3></td>
                                          </tr>
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
    {{-- <script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.3.1/html2canvas.min.js"></script> --}}
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js" integrity="sha512-GsLlZN/3F2ErC5ifS5QtgpiJtWd43JWSuIgh7mbzZ8zBps+dvLusV+eNQATqgA/HdeKFVgA5v3S/cIrLF7QnIg==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
    
    <script>
        const startDate=document.getElementById('startDate');
        const endDate=document.getElementById('endDate');
        const table_laporan_body=document.getElementById('table_laporan_body');
        const inputKolektor = document.getElementById('inputKolektor');
        const basicUrl='{{ route('getLaporanPenarikanData', ['token'=> $userLoginData->token]) }}'
        const buttonSubmit=document.getElementById('submit');
        const buttonDownload=document.getElementById('download');
        buttonSubmit.addEventListener("click", ()=>{
            const selectedOption = inputKolektor.options[inputKolektor.selectedIndex];
            const value = selectedOption.value;
            getData(basicUrl,startDate.value,endDate.value,value);
        });
        buttonDownload.addEventListener("click", ()=>{
            const selectedOption = inputKolektor.options[inputKolektor.selectedIndex];
            const value = selectedOption.value;
            livesdownload='{{ route('downloadPenarikan', ['token'=> $userLoginData->token]) }}'+'?id_kolektor='+value+'&start_date='+startDate.value+'&end_date='+endDate.value;
            // console.log(livesdownload);
            // window.jsPDF = window.jspdf.jsPDF;
            // var pdf = new jsPDF();
            fetch(livesdownload)
            .then(response => {
                if (response.status === 200) {
                    
                    return response.text();
                } else {
                    console.log('Error!');
                }
            })
            .then(html => {
                const parser = new DOMParser();
                htmlParser=parser.parseFromString(html, "text/html");
                const containerParser=htmlParser.getElementById('idContainer');
                var worker = html2pdf().set({filename:'laporanp-penarikan.pdf'}).from(containerParser).outputPdf().then((pdf) => {
                  // Save the PDF to a variable
                  // console.log(typeof pdf);
                  console.log("data:application/pdf;base64,"+btoa(pdf));
                  // console.log(pdfData);
                  // return pdf;
                  var pdfBase64="data:application/pdf;base64,"+btoa(pdf);
                  const link = document.createElement('a');
                  link.download = 'encodedPDF.txt';
                  link.href = pdfBase64;
                  link.click();

                  // Remove the element
                  element.parentNode.removeChild(element);
                });
            })
            .catch(error => console.log(error));
        });
        buttonSubmit.click();
        function getData(url,startDate,endDate,inputKolektor) {
            livesearchurl=basicUrl+'?id_kolektor='+inputKolektor+'&start_date='+startDate+'&end_date='+endDate;
            console.log(livesearchurl);
            fetch(livesearchurl)
            .then(response => {
                if (response.status === 200) {
                    return response.json();
                } else {
                console.log('Error!');
                }
            })
            .then(json => {
                // console.log(json);
                updateView(json);
            })
            .catch(error => console.log(error));
        }
        function updateView(json){
            const kolektor_fullname=document.getElementById('kolektor_fullname');
            const kolektor_role=document.getElementById('kolektor_role');
            const kolektor_no_telepon = document.getElementById('kolektor_no_telepon');
            const kolektor_start_date = document.getElementById('kolektor_start_date');
            const kolektor_end_date = document.getElementById('kolektor_end_date');
            const totalTransaksi = document.getElementById('totalTransaksi');
            kolektor_fullname.textContent=json.staffData.fullname;
            kolektor_role.textContent=json.staffData.role;
            kolektor_no_telepon.textContent=json.staffData.no_telepon;
            kolektor_start_date.textContent=startDate.value;
            kolektor_end_date.textContent=endDate.value;
            if(json.transaksiArray.length>0){
                table_laporan_body.innerHTML='';
                i=0;
                for (const rowData of json.transaksiArray) {
                    i++;
                    const tr = document.createElement('tr');
                    const dt = rupiah(rowData.nominal);
                    tr.innerHTML = `<td>${i}</td><td>${rowData.bukutabungan.nasabah.fullname}</td><td>${rowData.bukutabungan.no_tabungan}</td><td>${rowData.tgl_transaksi}</td><td>Rp.${dt}</td>`;
                    table_laporan_body.appendChild(tr);
                }
                totalTransaksi.textContent=json.transaksiJml;
            }else{
                table_laporan_body.innerHTML='';
                totalTransaksi.textContent=0;
            }  
        }
        function rupiah(value){
          let val = (value/1).toFixed(0).replace('.', ',')
          return val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".")
        }
        
    </script>
    </body>
</html>