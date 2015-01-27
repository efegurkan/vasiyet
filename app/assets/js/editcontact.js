$(document).ready(function () {
    validateForm();

});
    $("#delete").on('click', submitDeleteReq());

function validateForm() {
    $('#editform').bootstrapValidator({
        message: "Please enter a valid value",
        feedbackIcons: {
            valid: "glyphicon glyphicon-ok",
            invalid: "glyphicon glyphicon-remove"
        },
        fields: {
            contactId: {
                message: "An error occured please contact with us!",
                validators: {
                    numeric: "Error occured!Cannot send the information!",
                    notEmpty: "Error occured!Cannot send the information!"
                }
            },
            name: {
                message: "Please enter a valid name",
                validators: {
                    notEmpty: "Name field can not be empty!"
                }
            },
            surname: {
                message: "Please enter a valid surname",
                validators: {
                    notEmpty: "Surname field can not be empty!"
                }
            },
            email: {
                message: "Please enter a valid e-mail address.",
                validators: {
                    notEmpty: "Please enter an e-mail address.",
                    emailAddress: "This is not a valid e-mail address."
                }
            }

        }
    }).on('success.form.bv', function (e) {
        e.preventDefault();
        submitFormData();
    });

}

//Form submit function
function submitFormData() {
    var formdata = {
        'id': $('#contactId').val(),
        'name': $('#name').val(),
        'surname': $('#surname').val(),
        'email': $('#email').val()
    };

    $.ajax({
        type: 'POST',
        url: '/editcontact',
        data: JSON.stringify(formdata),
        contentType: 'application/json',
        success: function (data, textstatus, jqXHR) {
            //todo inform user about save
            console.log(jqXHR.status);
            console.log(textstatus);
            console.log(data);
        },
        error: function (jqXHR, textstatus, errorThrown) {
            //todo inform user about failure
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
        }
    });

}

function submitDeleteReq() {
    console.log("submitdelete");
    $('#editform').bootstrapValidator({
        message: 'Cannot send delete request!',
        fields: {
            contactId: {
                validators: {
                    notEmpty: "Cannot send delete request!",
                    numeric: "Page data is changed, cannot send delete request! "
                }
            }
        }
    }).on('success.form.bv', function () {
        var contactId = {'id': $('#contactId').val()};

        $.ajax({
            type: 'POST',
            url: '/deletecontact',
            data: JSON.stringify(contactId),
            contentType: 'application/json',
            success: function (data) {
                //todo inform user
                console.log(data);
            },
            error: function (jqXHR) {
                console.log(jqXHR.responseText);
            }
        });
    });


}
