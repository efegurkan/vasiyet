$(document).ready(function () {
    //$.fn.modal.Constructor.prototype.enforceFocus = function() {};
    $('select').select2({width: '420'});
    $('.list-group-item').click(function (e) {
        var thingie = e.currentTarget;
        e.preventDefault();
        //registerCloseEvent(thingie);
        console.log(e);
    });
    registerCloseEvent();
    //validators
    validateName();
});

function openmodal() {
    $('#addmodal').modal('show');
}

function validateName() {
    $('#editform').bootstrapValidator({
        message: "Please enter a valid name",
        feedbackIcons: {
            valid: "glyphicon glyphicon-ok",
            invalid: "glyphicon glyphicon-remove"
        },
        fields: {
            groupId: {
                message: "Error occured! Cannot send data!",
                validators: {
                    notEmpty: "Id field is empty",
                    numeric: "Id field is changed"
                }
            },
            name: {
                message: "Please enter a valid name.",
                validators: {
                    notEmpty: "Name field should not be empty"
                }
            }
        }
    }).on('success.form.bv', function (e) {
        e.preventDefault();
        submitNameData();
    });
}

function submitNameData() {
    var formdata = {
        'id': $('#groupId').val(),
        'name': $('#name').val()
    };

    $.ajax({
        type: 'POST',
        url: '/editgroup',
        data: JSON.stringify(formdata),
        contentType: 'application/json',
        success: function (data, textstatus, jqXHR) {
            console.log(jqXHR.status);
            console.log(textstatus);
            console.log(data);
            alert(data.message);
            $('#groupId').val(data.groupId);
            //todo redirect? save id etc.
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

function submitDeleteReq() {
    var id = {'id': $('#groupId').val()};

    if (confirm('You are about to delete the group!. This cannot be undone. Are you sure?')) {
        console.log('Group deletion confirmed.!');
        $.ajax({
            type: 'POST',
            url: '/deletegroup',
            data: JSON.stringify(id),
            contentType: 'application/json',
            success: function (data) {
                console.log(data);
                alert(data.message);
                window.location.href = "/";
            },
            error: function (jqXHR, textstatus, errorThrown) {
                console.log(jqXHR.responseText);
                console.log(textstatus);
                console.log(errorThrown);
                var msg = JSON.parse(jqXHR);
                alert(msg.message);
            }
        });
    }
    else {
        console.log("Group deletion cancelled.");
    }

}

function addmember() {
    var id = {'groupId': $('#groupId').val(), 'contactId': $('#selectcontact').val()};
    $.ajax({
        type: 'POST',
        url: '/addgroupmember',
        data: JSON.stringify(id),
        contentType: 'application/json',
        success: function (data) {
            console.log(data);
            alert(data.message);
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

function registerCloseEvent(target) {

    $('.deleteContactBtn').click(function (e) {
        e.preventDefault();

        //Confirm delete
        if (confirm("You are about to delete the contact from this group! Are you sure ?")) {

            //id of contact which will be deleted.
            var deleteId = $(this).val();
            var postdata = {'groupId': $('#groupId').val(), 'contactId': deleteId};
            $.ajax({
                type: 'POST',
                url: '/deletegroupmember',
                data: JSON.stringify(postdata),
                contentType: 'application/json',
                success: function (data) {
                    console.log(data);
                    //delete list item
                    $('a[href$="/editcontact/' + deleteId + '"]').remove();
                    alert(data.message);
                },
                error: function (jqXHR, textstatus, errorThrown) {
                    console.log(jqXHR.responseText);
                    console.log(textstatus);
                    console.log(errorThrown);
                    var response = JSON.parse(jqXHR.responseText);
                    alert(response.message);
                }
            });


        }
        else {
            console.log('Contact delete cancelled!');
        }
    });

}