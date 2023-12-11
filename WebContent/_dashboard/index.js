
/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
// function getParameterByName(target) {
//     // Get request URL
//     let url = window.location.href;
//     // Encode target parameter name to url encoding
//     target = target.replace(/[\[\]]/g, "\\$&");
//
//     // Ues regular expression to find matched parameter value
//     let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
//         results = regex.exec(url);
//     if (!results) return null;
//     if (!results[2]) return '';
//
//     // Return the decoded parameter value
//     return decodeURIComponent(results[2].replace(/\+/g, " "));
// }

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
$(document).ready(function() {
    function handleResult(resultData) {
        console.log(resultData)

//     <table id=single_movie_table class="table table-striped">
//         <thead>
//         <tr>
//         <th>Genres</th>
//     <th>Stars</th>
//     <th></th>
//     <th></th>
// </tr>
// </thead>
//
//     <!-- single-movie.js will find this html element by it's id "single_movie_table_body" -->
//     <tbody id=single_movie_table_body></tbody>
// </table>


        const container = $('#metadata-tables');

        for (const [tableName, columnData] of Object.entries(resultData)) {
            // Create table element
            const tableContainer = $('<div>').addClass('container mt-4');
            const table = $('<table>').addClass('table table-bordered');

            const thead = $('<thead>');
            const headerRow = $('<tr>');
            headerRow.append($('<th>').text('Attribute'));
            headerRow.append($('<th>').text('Type'));
            thead.append(headerRow);
            table.append(thead);

            // Create table body
            const tbody = $('<tbody>');
            for (const [columnName, dataType] of Object.entries(columnData)) {
                const row = $('<tr>');
                row.append($('<td>').text(columnName));
                row.append($('<td>').text(dataType));
                tbody.append(row);
            }
            table.append(tbody);

            // Append the table to the container
            tableContainer.append($('<h3>').addClass('text-center mb-3').text(tableName));
            tableContainer.append(table);

            container.append(tableContainer);
        }
    }

    /**
     * Once this .js is loaded, following scripts will be executed by the browser\
     */


// Makes the HTTP GET request and registers on success callback function handleResult
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/metadata",
        success: (resultData) => handleResult(resultData)
    });
});