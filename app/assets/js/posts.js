var templates = {
    newTemplate: function () {
        var groups = $.datastore.groups;
        var select = $('#groupsdropdown');
        for (var i = 0; i < groups.length; ++i) {

            select.append('<option></option>');
            var option = select.find('option').last();
            option.val(groups[i].id);
            option.text(groups[i].name);
        }
        return $('#newtemplate');
    },
    editTemplate: function (postElement) {
        var instance = $('#edittemplate').clone();
        var data = postElement.data('fd');
        instance.addClass('editpanel');
        instance.attr('data-wall-post', data.id);
        instance.find('h4.postheader').text(data.title);
        instance.find('p.post-content').text(data.content);
        var groups = $.datastore.groups;
        console.log(groups);
        var select = instance.find('#editdropdown');
        console.log(select);
        for (var i = 0; i < groups.length; ++i) {

            select.append('<option></option>');
            var option = select.find('option').last();
            console.log(option);
            option.val(groups[i].id);
            option.text(groups[i].name);
        }
        instance.find('#time').text(data.date);
        instance.removeClass('hidden');
        instance.removeAttr('id');

        return instance;
    },
    loadTemplate: function (data) {
        var template = $('#loadtemplate').clone();
        //fill instance with content
        template.data('fd', data);
        template.attr('data-wall-post', data.id);
        template.find('h4.postheader').text(data.title);
        template.find('p.post-content').text(data.content);
        template.find('a.btn.btn-default.disabled').text(data.visibility);
        template.find('#time').text(data.date);
        //cleanup unnecessary props and classes
        template.removeAttr('id');
        template.removeClass('hidden');
        return template;

        //return $('#loadtemplate');
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
                //$.datastore.posts = data;
                dataOperations.initiatePostsForDatastore(data);
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

        pub.initiatePostsForDatastore = function (data) {
            $.datastore.posts = {};
            data.forEach(function (post) {
                $.datastore.posts[post.id] = post;
            });
        };

        pub.addPostToStore = function (post) {
            //check if store is empty,
            $.datastore.posts[post.id] = post;
        };

        pub.removePostFromStore = function (post) {
            //check if store is empty.
            if (!$.isEmptyObject($.datastore.posts)) {
                delete $.datastore.posts[post.id];
            }
            else {
                DOMOperations.showError("The post you are trying to remove does not exists!");
            }
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
            //todo implementation
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
    }()
    )
    ;

var DOMOperations = (function () {
    var pub = {};
    pub.clone = function (element) {
        return element.clone();
    };

    pub.destroy = function (element) {
        element.remove();
    };


    pub.replace = function (oldElement, newElement) {
        oldElement.replaceWith(newElement);
        newElement.show();
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

    pub.enablePlugins = function (element) {
        element.find('select').select2();
        element.find('textarea').autosize();
    };

    pub.renderPosts = function () {
        console.log("hello");
        $.each($.datastore.posts, function (index, post) {
            console.log(post);
            var instance = templates.loadTemplate(post);
            instance.show();
            $('#maincontent').append(instance);

        });
    };

    return pub;
}());

$.datastore = {};

var utilityOperations = (function () {
    var pub = {};

    pub.groupCheck = function () {
        if (typeof $.datastore.groups !== 'undefined' && $.datastore.groups.length >= 0) {
            var templ = templates.newTemplate();
            templ.show();
            DOMOperations.enablePlugins(templ);
        }
    };

    pub.postCheck = function () {
        console.log("postcheck");

        DOMOperations.hideLoading();
        if ($.isEmptyObject($.datastore.posts)) {
            DOMOperations.showNoPost();
        } else {
            DOMOperations.hideNoPost();
            //DOMOperations.renderPosts();
        }
    };

    pub.removePostHandler = function (element) {
        var elementdata = element.closest('div.panel.panel-primary').data('fd');
        var promise = dataOperations.removePost(element);
        promise.done(function (data) {
            console.log('promise ret');
            console.log(data);
            dataOperations.removePostFromStore(elementdata);
            DOMOperations.destroy(element.closest('div.panel.panel-primary'));

        });
        promise.fail(function (jqXHR, textStatus, errorThrown) {
            var msg = JSON.parse(jqXHR.responseText);
            DOMOperations.showError(msg.message);
        });
        promise.always(function () {
            utilityOperations.postCheck();
        });
    };

    pub.savePostHandler = function (element) {
        var promise = dataOperations.savePost(element.closest($('#newtemplate')));

        promise.done(function (data) {
            var instance = templates.loadTemplate(data);
            instance.show();
            $('#maincontent').prepend(instance);
            dataOperations.addPostToStore(data);
            utilityOperations.postCheck();
        });

        promise.fail(function (error) {
            console.log(error);
            DOMOperations.showError(error);
        });
    };

    pub.activateEditTemplate = function (element) {
        utilityOperations.cancelEdit();
        var div = element.closest('.panel.panel-primary');
        var instance = templates.editTemplate(div);
        $.datastore.editState = true;
        $.datastore.lastEdited = div.data('fd');
        $.datastore.lastEditedSelector = div;
        DOMOperations.replace(div, instance);
        DOMOperations.enablePlugins(instance);


    };

    pub.cancelEdit = function () {
        //defined and true
        var div = $('.editpanel');
        if ($.datastore.editState !== "undefined" && $.datastore.editState) {
            DOMOperations.replace(div, templates.loadTemplate($.datastore.lastEdited));
        }
        else {//undefined or false means its clear
            $.datastore.editState = false;
        }
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

        maincontent.on('click', '.btn-activateEdit', function (e) {
            e.preventDefault();
            utilityOperations.activateEditTemplate($(this));
        });

        maincontent.on('click', '.canceledit', function (e) {
            e.preventDefault();
            utilityOperations.cancelEdit($(this));
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
        //utilityOperations.groupCheck();
        //utilityOperations.postCheck();
        DOMOperations.renderPosts();
    });

});