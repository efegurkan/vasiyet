$(document).ready(function () {
    $('select').select2({width: 'style'});
    $('textarea').autosize();

    getPostsAndFill();
    $('.btn-newPostSave').on('click', function (e) {
        registerNewSave(e);
    });
    $('#middlecontentarea').on('click', '.deletepost', function (e) {
        console.log($(this));
        registerDeleteRequest(e, $(this));
    });
});

function getPostsAndFill() {
    console.info("Getting posts");
    var sessionid = {'loggedUser': $('#loggedUser').val()};

    postAjax('/getposts', sessionid, function (data) {
        if (data.length <= 0) {
            noPostPanel();
        }
        for (i = 0; i < data.length; i++) {
            $('#loadingpanel').remove();
            var templ = loadTemplate(data[i]);
            $('#middlecontentarea').append(templ);
        }
    });

}

function noPostPanel() {
    $('.loader').remove();
    $('#loadingpanel').find('.panel-body').append("<p style='text-align: center;'>No posts found.<p>");
}

function loadTemplate(postdata) {
    var instance = $('#loadtemplate').clone();

    //fill instance with content
    instance.attr('data-wall-post', postdata.id);
    instance.find('h4.postheader').append(postdata.title);
    instance.find('p.post-content').append(postdata.content);
    instance.find('a.btn.btn-default.disabled').append(postdata.visibility);
    instance.find('#time').append(postdata.date);
    //cleanup unnecessary props and classes
    instance.removeAttr('id');
    instance.removeClass('hidden');
    return instance;
}

function postAjax(url, data, success) {
    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(data),
        contentType: 'application/json',
        success: success,
        error: function (jqXHR, textstatus, errorThrown) {
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
            var msg = JSON.parse(jqXHR.responseText);
            alert(msg.message);
        }
    });
}

function registerNewSave(event) {
    console.log("register new save event");
    event.preventDefault();

    var title = $('input.new-post-title');
    console.log(title);
    //todo picture ?
    var content = $('textarea.new-post-content');

    if (title.val().length > 0 && content.val().length > 0) {//not empty

        //todo add other properties + visibility
        var newSaveData = {
            'id': '0',
            'title': title.val(),
            'content': content.val()
        };

        submitSaveRequest(newSaveData);
    } else {//empty
        alert("Your post has some empty areas!");
    }

}

function registerDeleteRequest(event, button) {
    event.preventDefault();
    var id = button.closest('div.panel.panel-primary').data('wallPost');
    console.log(id);
    data = {
        'postId': String(id)
    };
    submitDeleteRequest(data);
}

function submitDeleteRequest(postId) {
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

function submitSaveRequest(postData) {

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