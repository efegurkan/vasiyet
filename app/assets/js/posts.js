$(document).ready(function () {
    $('select').select2({width: 'style'});

    $('#saveBtn').on('click', function (e) {
        e.preventDefault();
        submitSaveRequest();
    });

    $('.deletepost').on('click', function (e) {
        console.log("deletepost");
        e.preventDefault();
        submitDeleteRequest($(this));
    });
});

function submitDeleteRequest(target) {

    var postId = {
        'postId': target.parents('.panel').attr('id')
    };

    $.ajax({
        type: 'POST',
        url: '/deletepost',
        data: JSON.stringify(postId),
        contentType: 'application/json',
        success: function (data, textstatus, jqXHR) {
            console.log(jqXHR.status);
            console.log(textstatus);
            console.log(data);
            alert(data.message);
            $('#' + target.val()).remove();
            window.location.href = '/posts';
        },
        error: function (jqXHR, textstatus, errorThrown) {
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
            var msg = JSON.parse(jqXHR.responseText);
            alert(msg.message);
        }
    });
}

function submitSaveRequest() {

    //todo add other properties + visibility
    var postData = {
        'id': '0',
        'title': $('#newPostTitle').val(),
        'content': $('#newPostContent').val()
    };

    $.ajax({
        type: 'POST',
        url: '/editpost',
        data: JSON.stringify(postData),
        contentType: 'application/json',
        success: function (data, textstatus, jqXHR) {
            console.log(jqXHR.status);
            console.log(textstatus);
            console.log(data);
            alert(data.message);
            window.location.href = '/posts';
        },
        error: function (jqXHR, textstatus, errorThrown) {
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
            var msg = JSON.parse(jqXHR.responseText);
            alert(msg.message);
        }
    });


}