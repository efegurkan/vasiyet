$(document).ready(function () {
    validateForm();

    $('#delete').click(function (e) {
        e.preventDefault();
        submitDeleteReq();
    });

    $('#save').click(function (e) {
        e.preventDefault();
        submitFormData();
    });

    enableDeleteButton();

});

function enableDeleteButton() {
    var btn = $('#delete');
    var contactid = $('#contactId');
    if (contactid.length !== 0 && contactid.val() ==='0') {
        btn.prop('disabled',true);
    }
    else {
        btn.prop('disabled',false);
    }
}

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
            console.log(jqXHR.status);
            console.log(textstatus);
            console.log(data);
            alert(data.message);
            window.location.href = '/';
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
    console.log("submitdelete");
    var contactId = {'id': $('#contactId').val()};

    if (confirm('You are about to delete this Contact! This cannot be undone! Are you sure?')) {
        console.log('Contact delete confirmed');
        $.ajax({
            type: 'POST',
            url: '/deletecontact',
            data: JSON.stringify(contactId),
            contentType: 'application/json',
            success: function (data) {
                console.log(data);

                alert(data.message);
                window.location.href = '/contacts';

            },
            error: function (jqXHR, textstatus, errorThrown) {
                console.log(jqXHR.responseText);
                console.log(textstatus);
                console.log(errorThrown);
                var msg = JSON.parse(jqXHR);
                alert(msg.message);
            }
        });
    } else {
        console.log('Contact delete cancelled');
    }


}
