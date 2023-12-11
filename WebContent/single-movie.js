/**
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating single movie info from resultData");

    // find the empty h3 body by id "single_movie_info"
    let movieInfoElement = jQuery("#single_movie_info");

    movieInfoElement.append("<p>Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Year Released: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>");

    console.log("handleResult: populating single movie table from resultData");


    // Populate the single movie table
    // Find the empty table body by id "single_movie_table_body"
    let movieTableBodyElement = jQuery("#single_movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    let rowHTML = "";
    rowHTML += "<tr>";

    rowHTML += "<th>"
    let genre_names = resultData[0]["movie_genres"].split(', ');
    let genre_ids = resultData[0]["movie_genre_ids"].split(', ');
    for (j = 0; j < genre_names.length; j++) {
        rowHTML += '<a href="results.html?type=genre&val=' + genre_ids[j] +'">'
            + genre_names[j]   // display star_name for the link text

        rowHTML += "</a>"
        if (j < genre_names.length - 1)  {
            rowHTML += ', '
        }
    }
    rowHTML += "</th>"


    rowHTML += "<th>"
    let star_names = resultData[0]["movie_stars"].split(', ');
    let star_ids = resultData[0]["movie_star_ids"].split(', ');

    for (j = 0; j < star_names.length; j++) {
        rowHTML += '<a href="single-star.html?id=' + star_ids[j] + '">'
            + star_names[j]   // display star_name for the link text

        rowHTML += "</a>"
        if (j < star_names.length - 1)  {
            rowHTML += ', '
        }
    }

    rowHTML += "</th><th></th>"

    let movieEncoded = encodeURIComponent(resultData[0]["movie_title"]);

    rowHTML += "<th><button onclick=addToCart('" +
        movieEncoded + "','" + resultData[0]['movie_id'] + "')>Add to Cart</button></th>";

    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
}

function addToCart(title, movieId) {
    console.log("Adding (encoded movie data): " + title + " to the cart")
    $.ajax("api/cart", {
        method: "POST",
        data: "item=" + title + "&id=" + movieId
    });
    window.alert(decodeURIComponent(title) + " Added to Cart");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by SingleMovieServlet in SingleMovieServlet.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});