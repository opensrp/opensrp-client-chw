function loadData() {
  const data = JSON.parse(Android.getDataForReport());
  const tableBody = document.getElementById("table-body");
  if(typeof data!== undefined && data !== ""){
    const reportData = data.reportData;
    reportData.forEach((dataPoint) => {
        //append to the table body a row with data
        const row = document.createElement("tr");
        const dataPointKeys = Object.keys(dataPoint);
        dataPointKeys.forEach((key) => {
            const cell = document.createElement("td");
            cell.innerHTML = dataPoint[key];
            row.appendChild(cell);
        }
        );
        tableBody.appendChild(row);
    });
  }
  const reportPeriod = document.getElementById("report_period");
  reportPeriod.innerHTML = Android.getDataPeriod();
}
