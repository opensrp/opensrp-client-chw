function loadData(reportKey) {
  const data = JSON.parse(Android.getData(reportKey));
  const keys = Object.keys(data.nameValuePairs);
  const reportPeriod = document.getElementById("report_period");


  reportPeriod.innerHTML = Android.getDataPeriod();
}
