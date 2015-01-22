function logout(){
    $.ajax({
        type : "POST",
        url : "/logout",
        success: function (data, textstatus, jqXHR) {
            console.log(jqXHR.status);
            window.location.href = data;
        },
        failure: function (data, textstatus, jqXHR) {
            var obj = JSON.parse(data);
            console.log(jqXHR.status);
            console.log(obj.message);
            window.location.href='/';
        }
    });
}