/**
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData!!");
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData showing top 20 movies by rating
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] +
            '">'+resultData[i]["movie_title"]+"</a>" +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>"
        let genre_names = resultData[i]["movie_genres"].split(', ');
        let genre_ids = resultData[i]["movie_genre_ids"].split(', ');
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
        let star_names = resultData[i]["movie_stars"].split(', ');
        let star_ids = resultData[i]["movie_star_ids"].split(', ');

        for (j = 0; j < star_names.length; j++) {
            rowHTML += '<a href="single-star.html?id=' + star_ids[j] +'">'
                + star_names[j]   // display star_name for the link text

            rowHTML += "</a>"
            if (j < star_names.length - 1)  {
                rowHTML += ', '
            }
        }

        rowHTML += "</th>"
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";


        let movieEncoded = encodeURIComponent(resultData[i]["movie_title"]);

        rowHTML += "<th><button onclick=addToCart('" +
            movieEncoded + "','" + resultData[i]['movie_id'] + "')>Add to Cart</button></th>";

        rowHTML += "</tr>";


        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
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
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/top20", // Setting request url, which is mapped by MovieServlet in MovieServlet.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});