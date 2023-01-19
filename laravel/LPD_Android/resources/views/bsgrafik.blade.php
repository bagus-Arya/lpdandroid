<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Grafik Kolektor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-GLhlTQ8iRABdZLl6O3oVMWSktQOp6b7In1Zl3/Jr59b6EGGoI1aFkw7cmDA6j6gD" crossorigin="anonymous">
  </head>
  <body>
    <div class="container py-4">
        <form class="row g-3" action="javascript:void(0);">
            <div class="col-6">
              <label for="inputFromDate" class="form-label">From Date</label>
              <input type="date" class="form-control" id="inputFromDate" value="{{  now()->format('Y-m-d')}}">
            </div>
            <div class="col-6">
              <label for="inputToDate" class="form-label">To Date</label>
              <input type="date" class="form-control" id="inputToDate" value="{{  now()->format('Y-m-d')}}">
            </div>
            <div class="col-12 d-grid">
              <button id="submit" class="btn btn-success">Submit</button>
            </div>
        </form>
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-body" >
                      <h5 class="card-title">Bar Chart</h5>
                      <div >
                        <canvas id="barChart"></canvas>
                      </div>
                    </div>
                  </div>
            </div>
            <div class="col-12 mt-2">
                <div class="card">
                    <div class="card-body">
                      <h5 class="card-title">Pie Chart</h5>
                      <div>
                        <canvas id="pieChart"></canvas>
                      </div>
                    </div>
                  </div>
            </div>
        </div>
        
    </div>



    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js" integrity="sha384-oBqDVmMz9ATKxIep9tiCxS/Z9fNfEXiDAYTujMAeBAsjFuCZSmKbSSUnQlmh/jp3" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.min.js" integrity="sha384-mQ93GR66B00ZXjt0YO5KlohRA5SY2XofN4zfuZxLkoj1gXtW8ANNCe9d5Y3eG5eD" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" integrity="sha384-w76AqPfDkMBDXo30jS1Sgez6pr3x5MlQ1ZAGC+nuZB+EYdgRZgiwxhTBTkF7CXvN" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@^3"></script>
    <script src="https://cdn.jsdelivr.net/npm/moment@^2"></script>
    <script src="https://cdn.jsdelivr.net/npm/chartjs-adapter-moment@^1"></script>
    <script>
      const ctxBar = document.getElementById('barChart');
      const ctxPie = document.getElementById('pieChart');
      var pieChartInstance=new Chart();
      var barChartInstance=new Chart();

      buttonSubmit=document.getElementById('submit');
        buttonSubmit.addEventListener("click", ()=>{
            basicUrl='{{ route('bendaharaGrafikApi', ['token'=>$token]) }}'
            fromDate=document.getElementById('inputFromDate').value;
            toDate=document.getElementById('inputToDate').value;
            livesearchurl=basicUrl+'?from_date='+fromDate+'&to_date='+toDate
            fetch(livesearchurl)
            .then((response) => response.json())
            .then((data) => {
                  const barchart = {
                  type: 'bar',
                  data: {
                    datasets:data.barchart.dataArray
                  },
                  options: {
                  scales: {
                      x: {
                          type: 'time',
                          time: {
                              unit:'day'
                          }
                          },
                      y:{
                          min:0
                      }
                      }
                  }       
                  };
                  barChartInstance.destroy();
                  barChartInstance=new Chart(ctxBar,barchart);

                  const dataPie = {
                  labels:data.piechart.labels,
                  datasets: [{
                      label: 'Data Count',
                      data: data.piechart.data,
                      backgroundColor:data.piechart.backgroundColor,
                      hoverOffset: 4
                  }]
                  };
                  const configPie = {
                  type: 'pie',
                  data: dataPie,
                  };
                  pieChartInstance.destroy();
                  pieChartInstance=new Chart(ctxPie,configPie);
                  console.log(data);
                }
            )
            .catch((error) => console.error('Error:', error));
        });
      buttonSubmit.click();
    </script>
    <script>
        // const ctxBar = document.getElementById('barChart');
        // const ctxPie = document.getElementById('pieChart');

        // new Chart(ctxBar, {
        //   type: 'bar',
        //   data: {
        //     labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
        //     datasets: [{
        //       label: '# of Votes',
        //       data: [12, 19, 3, 5, 2, 3],
        //       borderWidth: 1
        //     }]
        //   },
        //   options: {
        //     scales: {
        //       y: {
        //         beginAtZero: true
        //       }
        //     }
        //   }
        // });

        // const data = {
        // labels: [
        //     'Red',
        //     'Blue',
        //     'Yellow'
        // ],
        // datasets: [{
        //     label: 'Data Count',
        //     data: [300, 50, 100],
        //     backgroundColor: [
        //     'rgb(255, 99, 132)',
        //     'rgb(54, 162, 235)',
        //     'rgb(255, 205, 86)'
        //     ],
        //     hoverOffset: 4
        // }]
        // };

        // const config = {
        // type: 'pie',
        // data: data,
        // };

       
        // new Chart(ctxPie,config);
      </script>
  </body>
</html>