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
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData!!");
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");
    movieTableBodyElement.empty();
    if (resultData.length > 0) {
        $('select[name=sort-dropdown]').val(resultData[0]['sortBy']);
    }
    if (resultData.length > 0) {
        $('select[name=count-dropdown]').val(resultData[0]['count']);
    }
    if (resultData.length > 0) {
        $('#page-num').text(resultData[0]['page']);
        if (parseInt(resultData[0]['page']) > 1) {
            $('#prev').parent().removeClass('disabled');
        }
    }
    if (parseInt($('select[name=count-dropdown]').val()) > resultData.length ) {
        $('#next').parent().addClass('disabled');
    }
    for (let i = 0; i < resultData.length; i++) {

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

let sortBy = null;
let newPage = null;
$(document).ready(function() {
    $('#prev').on('click', function(event) {
        console.log('Previous button clicked');
        event.preventDefault();
        let page = $("#page-num");
        let pageNum = parseInt(page.text());
        newPage = pageNum - 1;
        page.text(newPage);
        $('#next').parent().removeClass('disabled');
        if (parseInt($('#page-num').text()) === 1) {
            console.log("can't previous")
            $('#prev').parent().addClass('disabled');
        }
        loadResults();
    });

    $('#next').on('click', function(event) {
        console.log('Next button clicked');
        event.preventDefault();
        let page = $("#page-num")
        let pageNum = parseInt(page.text());
        newPage = pageNum + 1;
        page.text(newPage);
        if (parseInt($('#page-num').text()) !== 1){
            console.log("CAN previous")
            $('#prev').parent().removeClass('disabled');
        }
        loadResults();
    });

    $('select[name=sort-dropdown]').change(function() {
        let selectedOption = $(this).val();
        sortBy = selectedOption;
        console.log('Selected option:', selectedOption);
        newPage = 1;
        $('#prev').parent().addClass('disabled');
        loadResults();
        // You can perform further actions based on the selected option value here
    });
});

let count = null;
$(document).ready(function() {
    $('select[name=count-dropdown]').change(function() {
        let selectedOption = $(this).val();
        count = selectedOption;
        console.log('Selected option:', selectedOption);
        newPage = 1;
        $('#prev').parent().addClass('disabled');
        loadResults();
        // You can perform further actions based on the selected option value here
    });
});

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


function loadResults() {
    let type = getParameterByName('type');
    let val = getParameterByName('val');
    let title = getParameterByName('title');
    let year = getParameterByName('year');
    let director = getParameterByName('director');
    let star = getParameterByName('star');
    if (type == null) {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/search-results" + "?sortBy=" + sortBy + "&count=" + count + "&page=" + newPage,
            success: (resultData) => handleMovieResult(resultData)
        });
    } else if (type === "genre" || type === "char") {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/search-results?type=" + type + "&val=" + val +
                "&sortBy=" + sortBy + "&count=" + count + "&page=" + newPage,
            success: (resultData) => handleMovieResult(resultData)
        });
    } else if (type === "search") {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/search-results?type=" + type + "&title=" + title + "&year="
                + year + "&director=" + director + "&star=" + star +
                "&sortBy=" + sortBy + "&count=" + count + "&page=" + newPage,
            success: (resultData) => handleMovieResult(resultData)
        });
    } else if (type === "fulltext") {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/search-results?type=" + type + "&title=" + title +
            "&sortBy=" + sortBy + "&count=" + count + "&page=" + newPage,
            success: (resultData) => handleMovieResult(resultData)
        });
    }
}
loadResults();
// Makes the HTTP GET request and registers on success callback function handleMovieResult
// jQuery.ajax({
//     dataType: "json", // Setting return data type
//     method: "GET", // Setting request method
//     url: "api/top20", // Setting request url, which is mapped by MovieServlet in MovieServlet.java
//     success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
// });