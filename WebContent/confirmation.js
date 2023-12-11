/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    handleConfirmationArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleConfirmationArray(resultArray) {
    console.log(resultArray);
    let confirmation_table_body = $("#confirmation_table_body");
    confirmation_table_body.empty();
    // change it to html list
    let total_cost = 0;
    let rowHTML = "";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        let movieEncoded = encodeURIComponent(resultArray[i]["item_name"]);
        rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultArray[i]["sale_id"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["item_name"] + "</th>";
        rowHTML += "<th>" + resultArray[i]["item_count"] + "</th>";
        rowHTML += "<th>$" + resultArray[i]["item_cost"] + "</th>";
        rowHTML += "<th>$" + (resultArray[i]["item_cost"]*resultArray[i]["item_count"]) + "</th>";
        rowHTML += "</tr>";
        total_cost += (resultArray[i]["item_cost"]*resultArray[i]["item_count"]);
        // res += "<li>" + resultArray[i]["item_name"] + " quantity: " + resultArray[i]["item_count"] + "</li>";
        confirmation_table_body.append(rowHTML);
    }
    rowHTML = "<tr>";
    rowHTML += `<th></th><th></th><th></th><th></th><th class="table-success">Total Cost: $${total_cost}</th>`
    rowHTML += "</tr>";
    confirmation_table_body.append(rowHTML);
    // clear the old array and show the new array in the frontend
}


$.ajax("api/cart", {
    method: "GET",
    success: handleSessionData
});