$(document).ready(function () {
    $('select').select2({width:'style'});

    $('#saveBtn').on('click',function(e){
        e.preventDefault();
        //submitSaveRequest();
    });

    $('.deletepost').on('click',function(e){
        console.log("deletepost");
    });
});