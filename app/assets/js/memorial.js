var templates = {
    textonly: function (data) {
        var template = $('#loadtemplate').clone();
        var title = template.find('.postheader');
        var loadbody = template.find('.loadbody');
        var sender = template.find('.sender');
        var group = template.find('.group');
        var posttime = data.date;
        template.removeAttr('id');
        template.data('fd', data);
        template.attr('data-wall-post', data.id);
        title.text(data.title);
        loadbody.text(data.content);
        sender.data('title', 'Posted on: ' + posttime.toString());
        sender.text(data.sender);
        group.text(data.visibility);
        return template;
    }
};

var DataOperations = (function () {
    var pub = {};

    pub.initialize = function () {
        var deferred = $.Deferred();
        var posts = DataOperations.getPaginatedPosts();
        posts.done(function (data) {
            console.log(data);
            console.log("in initialize");
            DataOperations.initiatePostsForDatastore(data);
            deferred.resolve();
        });

        posts.fail(function (data) {
            deferred.fail(data);
        });
        return deferred.promise();

    };

    pub.getPaginatedPosts = function () {
        var urlparams = UtilityOperations.getUrlParams();
        console.log(urlparams);
        var data = {
            "pagenum": urlparams.pagenum.toString(),
            "token": urlparams.token.toString()
        };
        return DataOperations.ajaxPost("/getpagination", data);
    };

    pub.initiatePostsForDatastore = function (data) {
        console.log(data);
        console.log(data.posts, data.postOrder);
        if (typeof data !== "undefined" && typeof data.posts !== "undefined" && typeof data.orders !== "undefined") {
         console.log('Inside if');
            console.log(data);
            $.datastore.posts = {};
            $.datastore.postOrder = [];
            data.posts.forEach(function (post) {
                $.datastore.posts[post.id] = post;
                console.log('posts for');
            });

            $.datastore.postOrder = data.orders;
            $.datastore.activePage = data.activePage;
            $.datastore.maxPage = data.maxPage;
        } else {
            $.datastore.posts = {};
            $.datastore.postOrder = [];
            $.datastore.activePage = 1;
            $.datastore.maxPage = 1;
        }

    };

    pub.ajaxPost = function (url, data) {
        data = (typeof data === "undefined") ? JSON : data;
        return $.ajax({
            type: 'POST',
            url: url,
            data: JSON.stringify(data),
            contentType: 'application/json'
        });
    };

    return pub;
}());

var UtilityOperations = (function () {

    var pub = {};


    pub.fillPagination = function () {

    };

    pub.getUrlParams = function () {
        var pairs = location.search.split("&");
        var params = {"token": "", "pagenum": 1};
        for (var i = 0; i < pairs.length; i++) {
            console.log(pairs);
            if (typeof pairs[i] !== 'undefined' && pairs[i] !== "") {
                var thingies = pairs.split("=");
                params[thingies[0]] = thingies[1];
                console.log(params);
            }
        }

        return params;
    };


    return pub;
}());

$.datastore = {};
var DOMOperations = (function () {
    var pub = {};

    pub.hideError = function () {
        $('#errorpanel').hide();
    };

    pub.showError = function () {
        $('#errorpanel').show();
    };

    pub.hideLoading = function () {
        $('#loadingpanel').hide();
    };

    pub.showLoading = function () {
        $('#loadingpanel').show();
    };
    pub.showPagination = function () {
        $('#paginationarea').show();
    };
    pub.hidePagination = function () {
        $('#paginationarea').hide();
    };

    pub.createPagination = function () {

        var prev = $('#prev');
        var prevlink = $('#prevlink');
        var next = $('#next');
        var nextlink = $('#nextlink');

        var activePage = parseInt($.datastore.activePage);
        var prevNum = activePage - 1;
        var nextNum = activePage + 1;
        if (isNaN(activePage)) {
            prevNum = 0;
            nextNum = 0;
        } else if (prevNum < 0) {
            prevNum = 0;
        } else if (nextNum < 0) {
            nextNum = 0;
        }

        prevlink.attr("href", '?p=' + prevNum);
        nextlink.attr("href", '?p=' + nextNum);

        if ($.datastore.activePage === 0) {
            prev.addClass('disabled');
            next.addClass('disabled');
            prevlink.removeAttr('href');
            nextlink.removeAttr('href');
        }
        else if ($.datastore.activePage === 1) {
            prev.addClass('disabled');
            prevlink.removeAttr('href');
        }

        if ($.datastore.activePage === $.datastore.maxPage) {
            next.addClass('disabled');
            nextlink.removeAttr('href');
        }

        for (var i = $.datastore.maxPage; i >= 1; i--) {
            if ($.datastore.activePage === i) {
                prev.after('<li><a class="active" href="?p=' + i + ' ">' + i + '</a></li>');
            } else {
                prev.after('<li><a href="?p=' + i + ' ">' + i + '</a></li>');
            }
        }

    };

    pub.renderPosts = function () {
        $.datastore.postOrder.forEach(function (id) {
            //todo add new templates in case of photo
            //check if it is a photo card
            //if yes use that template
            var post = $.datastore.posts[id];
            var instance = templates.textonly(post);
            instance.show();
            $('#maincontent').append(instance);
        });
    };
    return pub;
}());


$('document').ready(function () {
    DataOperations.initialize().done(function () {
        DOMOperations.hideLoading();
        DOMOperations.createPagination();
        console.log('done');
    }).fail(function (error) {
        DOMOperations.hideLoading();
        DOMOperations.showError(error);
        console.log('fail');
    }).always(function () {
        DOMOperations.renderPosts();
        $('.rendertt').tooltip();
        console.log('always');
    });
});
