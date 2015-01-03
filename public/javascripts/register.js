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
            var obj = JSON.parse(data)
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

};