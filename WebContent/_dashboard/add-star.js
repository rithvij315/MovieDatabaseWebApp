let add_star_form = $("#star-form");


function handleMessage(resultData) {
    add_star_form[0].reset();
    console.log('done!')
    console.log(resultData)
    let message = $("#add_star_message");

    let p = $('<p>').text(`Success! starID: ${resultData["star_id"]}`);

    message.append(p);
}

function handleAddStar(event) {
    console.log("sending to results");
    event.preventDefault();


    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/add-star",
        data: add_star_form.serialize(),
        success: (resultData) => handleMessage(resultData)
    });

}


add_star_form.submit(handleAddStar);