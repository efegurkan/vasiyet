function submitData() {
    var formData = {
        'email': $('#email').val(),
        'password': $('#password').val()
    };

    $.ajax({
        type: "POST",
        url: "http://localhost:9000/loginjson",
        data: JSON.stringify(formData),
        contentType: "application/json",
        success: function (data, textStatus, jqXHR) {
            console.log(jqXHR.status);
            console.log(textStatus);
            window.location.href = data;
        },
        error: function (jqXHR, textstatus, errorThrown) {
            //            var resp = JSON.parse(jqXHR.responseText);
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
        }

    });
}


$(document).keypress(function (e) {
    if (e.which === 13) {
        $("#submit").first().click();
    }
});

$(document).ready(validate());

function validate(){
    $("#loginForm").bootstrapValidator({
            message: 'This value is not valid',
            feedbackIcons:{
                valid : 'glyphicon glyphicon-ok',
                invalid : 'glyphicon glyphicon-remove',
                validating : 'glyphicon glyphicon-refresh'
            },
            fields: {
                email:{
                    message: 'Email is not valid',
                    validators:{
                        notEmpty: {
                            message : 'Email is required'
                        },
                        emailAddress:{
                            message: 'This is not a valid email adress'
                        }
                    }
                },
                password:{
                    validators: {
                        notEmpty:{
                            message: 'Password is required'
                        },
                        stringLength:{
                            min: 6,
                            message : 'Password length is too short'
                        }
                    }
                }
            }
        }
    ).on('success.form.bv', function(e) {
            var $form = $(e.target);
            var bv = $form.data('bootstrapValidator');
            submitData();
        }
    )


}