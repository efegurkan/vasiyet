function submitData() {
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



function validateForm(){
   $("#registerForm").bootstrapValidator({
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
            var $form = $(e.target);
            var bv = $form.data('bootstrapValidator');
            submitData();
        }
    )


}

$(document).ready(function(){
    validateForm();
});