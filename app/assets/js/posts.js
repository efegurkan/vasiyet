$(document).ready(function () {
    $('select').select2({width: 'style'});
    $('textarea').autosize();

    loadPosts();
    registerNewSave();
    registerDelete();

});

function loadPosts() {
    console.debug("Loading posts");
    var sessionid = {'loggedUser': $('#loggedUser').val()};

    var promise = $.ajax({
        type: 'POST',
        url: '/getposts',
        data: JSON.stringify(sessionid),
        contentType: 'application/json',
        error: function (jqXHR, textstatus, errorThrown) {
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
            var msg = JSON.parse(jqXHR.responseText);
            alert(msg.message);
        }
    });

    promise.done(function (posts) {

        reloadPosts(posts);
    });
}

function reloadPosts(posts) {
    console.debug("Reload");
    $('#loadingpanel').nextAll().remove();
    fillContentArea(posts);
}

function fillContentArea(json) {
    if (json.length <= 0) {
        console.debug('hello');
        noPostPanel();
    }
    for (i = 0; i < json.length; i++) {
        $('#loadingpanel').remove();
        var templ = loadTemplate(json[i]);
        $('#middlecontentarea').append(templ);
    }
}
function noPostPanel() {
    $('.loader').hide();
    $('#loadingpanel').find('p').show();
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
function registerNewSave() {
    $('.btn-newPostSave').on('click', function (e) {
        savePost(e);
    });
}
function registerDelete() {
    $('#middlecontentarea').on('click', '.deletepost', function (e) {
        console.log($(this));
        deletePost(e, $(this));
    });
}
function savePost(event) {
    event.preventDefault();

    var title = $('input.new-post-title');
    console.log(title);
    //todo picture ?
    var content = $('textarea.new-post-content');

    if (title.val().length > 0 && content.val().length > 0) {//not empty

        //todo add other properties + visibility
        var saveData = {
            'id': '0',
            'title': title.val(),
            'content': content.val()
        };

        postAjax('/editpost', saveData, function (data) {
            alert(data.message);
            loadPosts();
        });
    } else {//empty
        alert("Your post has some empty areas!");
    }

}
function deletePost(event, button) {
    event.preventDefault();
    var id = button.closest('div.panel.panel-primary').data('wallPost');
    data = {
        'postId': String(id)
    };
    //submitDeleteRequest(data);
    postAjax('/deletepost', data, function (data) {
        alert(data.message);
        window.location.href = '/posts';
    });
}
