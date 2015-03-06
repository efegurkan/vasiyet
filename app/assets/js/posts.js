var templates = {
    newTemplate: function () {
        return $('#newtemplate');
    },
    editTemplate: function () {
    },
    loadTemplate: function () {
        return $('#loadtemplate');
    }
};

var dataOperations = (function () {
    var pub = {};
    //todo work on this
    pub.initialData = function () {
        var deferred = $.Deferred();
        var getgroups = dataOperations.getGroups();
        var getposts = dataOperations.getPosts();

        var fnGroupDoneState = false;
        var fnPostDoneState = false;

        getgroups.done(function (data, textStatus, jqXHR) {
            $.datastore.groups = data;
            fnGroupDoneState = true;
            checkFn();
        });

        getgroups.fail(function (jqXHR, textStatus, errorThrown) {
            var msg = JSON.parse(jqXHR.responseText);
            checkFn(msg);
        });

        getposts.done(function (data, textStatus, jqXHR) {
            $.datastore.posts = data;
            fnPostDoneState = true;
            checkFn();
        });

        getposts.fail(function (jqXHR, textStatus, errorThrown) {
            var msg = JSON.parse(jqXHR.responseText);
            checkFn(msg);
        });

        function checkFn(message) {
            if (fnGroupDoneState && fnPostDoneState) {
                if (getgroups.isRejected || getposts.isRejected) {
                    deferred.fail(message);
                } else {
                    deferred.resolve();
                }
            }
        }

        return deferred.promise();
    };

    pub.getGroups = function () {//return ajax promise
        return dataOperations.ajaxPost('/getgroups');
    };

    pub.getPosts = function () {
        return dataOperations.ajaxPost('/getposts');
    };

    pub.savePost = function (element) {
        var title = element.find('.new-post-title');
        //todo picture ?
        var content = element.find('.new-post-content');
        var group = element.find('select').select2('val');
        var deferred = $.Deferred();

        if (title.val().length > 0 && content.val().length > 0) {//not empty

            //todo add other properties
            var saveData = {
                'id': '0',
                'title': title.val(),
                'content': content.val(),
                'group': group
            };
            //clean form
            title.val('');
            content.val('');
            element.find('select').val('0').trigger('change');

            var prom = dataOperations.ajaxPost('/editpost', saveData);
            prom.done(function (data) {
                //saveData.id= data.postId;
                console.log(data);
                deferred.resolve(data.post);
            });

            prom.fail(function (jqXHR, textStatus, errorThrown) {
                var msg = JSON.parse(jqXHR.responseText);
                deferred.fail(msg.message);
            });

        } else {//empty
            deferred.fail("Your post has some empty areas!");
        }

        return deferred.promise();
    };

    //edit save operation
    pub.editPost = function (event, element) {
        event.preventDefault();
        //todo
    };

    pub.removePost = function (element) {
        var id = element.closest('div.panel.panel-primary').data('wallPost');
        var data = {'postId': String(id)};
        return this.ajaxPost('/deletepost', data);

    };

    pub.ajaxPost = function (url, data) {
        data = (typeof data === "undefined") ? JSON : data;
        var promise = $.ajax({
            type: 'POST',
            url: url,
            data: JSON.stringify(data),
            contentType: 'application/json'
        });
        return promise;
    };

    return pub;
}());

var DOMOperations = (function () {
    var pub = {};
    pub.clone = function (element) {
        return element.clone();
    };

    pub.destroy = function (element) {
        element.remove();
    };

    pub.fill = function (template, data) {
        //fill instance with content
        template.attr('data-wall-post', data.id);
        template.find('h4.postheader').text(data.title);
        template.find('p.post-content').text(data.content);
        template.find('a.btn.btn-default.disabled').text(data.visibility);
        template.find('#time').text(data.date);
        //cleanup unnecessary props and classes
        template.removeAttr('id');
        template.removeClass('hidden');
        return template;
    };

    pub.replace = function (oldElement, newElement) {
        oldElement.replaceWith(newElement);
    };

    pub.enable = function (element) {
    };

    pub.disable = function (element) {
    };

    pub.showError = function (message) {
        $('#errorpanel').show().text(message);
    };

    pub.showLoading = function () {
        $('#loadingpanel').show();
        $('.loading').show();
        $('.nopost').hide();
    };

    pub.hideError = function () {
        $('#errorpanel').hide().after('p').empty();
    };

    pub.hideLoading = function () {
        $('#loadingpanel').hide();
        $('.loading').hide();
        $('.nopost').hide();
    };

    pub.showNoPost = function () {
        $('#loadingpanel').show();
        $('.loading').hide();
        $('.nopost').show();
    };

    pub.hideNoPost = function () {
        $('#loadingpanel').hide();
        $('.loading').hide();
        $('.nopost').hide();
    };

    pub.enablePlugins = function (element, func) {
    };

    pub.renderPosts = function () {

        for (var i = 0; i < $.datastore.posts.length; ++i) {

            var instance = DOMOperations.clone(templates.loadTemplate());
            instance = DOMOperations.fill(instance, $.datastore.posts[i]);
            instance.show();
            $('#maincontent').append(instance);

        }
    };

    return pub;
}());

$.datastore = {};

var utilityOperations = (function () {
    var pub = {};

    pub.groupCheck = function () {
        if (typeof $.datastore.groups !== 'undefined' && $.datastore.groups.length >= 0) {
            var groups = $.datastore.groups;
            for (var i = 0; i < groups.length; ++i) {
                var select = $('#groupsdropdown');
                select.append('<option></option>');
                var option = $('option').last();
                option.val(groups[i].id);
                option.text(groups[i].name);
            }
            templates.newTemplate().show();
            $("#groupsdropdown").select2();
        }
    };

    pub.postCheck = function () {
        DOMOperations.hideLoading();
        if (typeof $.datastore.posts !== "undefined" && $.datastore.posts.length <= 0) {
            DOMOperations.showNoPost();
        } else {
            DOMOperations.renderPosts();
        }
    };

    pub.removePostHandler = function (element) {
        var promise = dataOperations.removePost(element);
        promise.done(function () {
            DOMOperations.destroy(element.closest('div.panel.panel-primary'));
        });
        promise.fail(function (jqXHR, textStatus, errorThrown) {
            var msg = JSON.parse(jqXHR.responseText);
            DOMOperations.showError(msg.message);
        });
    };

    pub.savePostHandler = function (element) {
        var promise = dataOperations.savePost(element.closest($('#newtemplate')));
        
        promise.done(function (data) {
            console.log(data);
            var instance = DOMOperations.clone(templates.loadTemplate());
            instance = DOMOperations.fill(instance, data);
            instance.show();
            $('#maincontent').prepend(instance);
        });
        
        promise.fail(function (error) {
            console.log(error);
            DOMOperations.showError(error);
        });
    };

    pub.registerHandlers = function () {
        var maincontent = $('#maincontent');
        maincontent.on('click', '.deletepost', function (e) {
            e.preventDefault();
            utilityOperations.removePostHandler($(this));
        });

        $('.btn-newPostSave').on('click', function (e) {
            e.preventDefault();
            utilityOperations.savePostHandler($(this));
        });
    };
    return pub;
}());

$('document').ready(function () {
    dataOperations.initialData().done(function () {
        DOMOperations.hideLoading();
        utilityOperations.groupCheck();
        utilityOperations.postCheck();
    }).fail(function (error) {
        DOMOperations.hideLoading();
        DOMOperations.showError(error);
    }).always(function () {
        utilityOperations.registerHandlers();
    });

});