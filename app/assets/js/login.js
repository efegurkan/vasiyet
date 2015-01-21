$(document).ready(validateForm());

$('#togglebtn').on('click',function(){
    $target = $('#registerInfo');
    $target.toggle();

    if($target.is(':visible')){
        $('#togglebtn').text("Login");
        $('#loginBtn').text("Register");
    }
    else{
        $('#togglebtn').text("Don't have an account?");
        $('#loginBtn').text("Login");
        $('#loginForm').data('bootstrapValidator').disableSubmitButtons(false);
    }


});

function validateForm(){
    $("#loginForm")
        .bootstrapValidator({
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
                            message: 'This is not a valid email address'
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
                        },
                        identical:{
                            field: 'verify',
                            message: 'The passwords are not matching'
                        }
                    }
                },
                verify:{
                    validators: {
                        notEmpty:{
                            message: 'This cannot be empty'
                        },
                        stringLength:{
                            min:6,
                            message: 'Password length is too short'
                        },
                        identical:{
                            field: 'password',
                            message: 'The passwords are not matching'
                        }
                    }
                },
                name:{
                    validators:{
                        notEmpty:{
                            message: 'Name is required'
                        }
                    }
                },
                surname:{
                    validators:{
                        notEmpty:{
                            message: 'Surname is required'
                        }
                    }
                }
            }


        }
    ).on('success.form.bv', function(e) {
            e.preventDefault();
            if(!$('#registerInfo').is(':visible')){
                submitLoginData();
            }
            else
            {
                submitRegisterData();
            }

        }
    );
}

function submitLoginData() {
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
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
        }

    });
}


function submitRegisterData() {
    var formdata = {
        'email': $('#email').val(),
        'password': $('#password').val(),
        'name': $('#name').val(),
        'surname': $('#surname').val()
    };
    console.log(formdata);
    $.ajax({
        type: "POST",
        url: "/registerjson",
        data: JSON.stringify(formdata),
        contentType: "application/json",
        success: function (data, textstatus, jqXHR) {
            //                var obj = JSON.parse(data)
            console.log(jqXHR.status);
            window.location.href = data;
        },
        failure: function (data, textstatus, jqXHR) {
            var obj = JSON.parse(data);
            console.log(jqXHR.status);
            console.log(obj.message);
        }
    });
    $(document).keypress(
        function (e) {
            if (e.which === 13) {
                $("#submit").first().click();
            }
        }
    );

}

