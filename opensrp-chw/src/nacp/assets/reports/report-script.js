function loadData() {
  const data = JSON.parse(Android.getDataForReport());

  const tableBody = document.getElementById("table-body");
  if(typeof data!== undefined && data !== ""){
    const reportData = data.nameValuePairs.reportData.values;

    reportData.forEach((dataPoint) => {
        //append to the table body a row with data
        const row = document.createElement("tr");
        const dataPointKeys = Object.keys(dataPoint.nameValuePairs);
        dataPointKeys.forEach((key) => {
            const cell = document.createElement("td");
            cell.innerHTML = dataPoint.nameValuePairs[key];
            row.appendChild(cell);
        }
        );
        tableBody.appendChild(row);
    });
  }
  const reportPeriod = document.getElementById("report_period");
  reportPeriod.innerHTML = Android.getDataPeriod();
}
