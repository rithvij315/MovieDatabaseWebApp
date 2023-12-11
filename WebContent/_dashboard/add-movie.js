let add_movie_form = $("#movie-form");


function handleMessage(resultData) {
    // $("#add_movie_error_message").text(resultData["message"]);
    add_movie_form[0].reset();
    console.log('done!')
    console.log(resultData)
    let message = $("#add_movie_message");
    if (resultData["message"] === "success") {
        let p = $('<p>').text(`Success! movieId: ${resultData["movie_id"]}  
        starID: ${resultData["star_id"]}  
        genreID: ${resultData["genre_id"]}`);

        message.append(p);
    } else {
        let p = $('<p>').text("Error: Duplicated movie!");
        message.append(p);
    }
}

function handleAddMovie(event) {
    console.log("sending to results");
    event.preventDefault();


    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/add-movie",
        data: add_movie_form.serialize(),
        success: (resultData) => handleMessage(resultData)
    });

}


add_movie_form.submit(handleAddMovie);