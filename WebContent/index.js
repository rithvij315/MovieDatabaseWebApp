let search_form = $("#search_form");
/**
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleGenres(resultData) {
    console.log("handleGenres: populating genres from resultData!!");
    // Find the empty table body by id "movie_table_body"
    let genreListElement = jQuery("#genres_list");

    // Iterate through resultData showing top 20 movies by rating
    for (let i = 0; i < resultData.length - 1; i++) {

        // Concatenate the html tags with resultData jsonObject

        let rowHTML =' <a href="results.html?type=genre&val=' + resultData[i]['genreId'] +
             '">'+resultData[i]["genre"]+"</a> &#x2022;";

        // Append the row created to the table body, which will refresh the page
        genreListElement.append(rowHTML);
    }

    // genreListElement.append(` <a href="#">${resultData[resultData.length - 1]["genre"]}</a>`);
    genreListElement.append(' <a href="results.html?type=genre&val=' + resultData[resultData.length - 1]['genreId'] +
        '">'+resultData[resultData.length - 1]["genre"]+"</a>");


    let charsListElement = jQuery("#chars_list");
    let characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ*"

    for (let i = 0; i < characters.length - 1; i++) {

        // Concatenate the html tags with resultData jsonObject

        let rowHTML =' <a href="results.html?type=char&val=' + characters[i] +
            '">'+characters[i]+"</a> &#x2022;";


        charsListElement.append(rowHTML);
    }
    // charsListElement.append(` <a href="#">${characters[characters.length - 1]}</a>`);
    charsListElement.append(' <a href="results.html?type=char&val=' + characters[characters.length - 1] +
        '">'+characters[characters.length - 1]+"</a>");

}

function handleSearch(event) {
    console.log("sending to results");
    event.preventDefault();
    window.location.href = "results.html?type=search&" + search_form.serialize();
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/genres", // Setting request url, which is mapped by GenreServlet in GenreServlet.java
    success: (resultData) => handleGenres(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

search_form.submit(handleSearch);