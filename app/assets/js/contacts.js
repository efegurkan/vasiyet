
//Enable tabs
$('#tabs a').click(function(e){
    e.preventDefault();
    $(this).tab('show');
});



function logout(){
    console.log("works");
    $.ajax({
        type : "POST",
        url : "/logout",
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
}

