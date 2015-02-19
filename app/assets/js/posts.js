$(document).ready(function () {
    $('select').select2({width: 'style'});
    $('textarea').autosize();

    $('.btn-newPostSave').on('click', function (e) {
        registerNewSave(e);
    });
    $('.deletepost').on('click', function (e) {
        registerDeleteRequest(e, $(this));
    });

    getPostsAndFill();

});

function getPostsAndFill() {
    console.info("Getting posts");
    var sessionid = {'loggedUser':$('#loggedUser').val()};

    postAjax('/getposts',sessionid, function(data){
        for(i = 0; i<data.length;i++){
            console.log(data[i]);
            createLoadTemplate(data[i]);
        }
    });

}

function createLoadTemplate(postdata){
    var instance = $('#loadtemplate').clone();

    //fill instance with content
    instance.prop('data-wall-post',postdata.id);
    instance.find('h4.postheader').append(postdata.title);
    instance.find('p.post-content').append(postdata.content);
    instance.find('a.btn.btn-default.disabled').append(postdata.visibility);
    //cleanup unnecessary props and classes
    instance.removeProp('id');
    instance.removeClass('hidden');
    //append template
    $('#middlecontentarea').append(instance);
}

function postAjax(url,data,success){
    $.ajax({
        type: 'POST',
        url: url,
        data: JSON.stringify(data),
        contentType: 'application/json',
        success:success,
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