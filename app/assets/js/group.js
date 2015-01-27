$(document).ready(function () {

    var numbers = new Bloodhound({
        datumTokenizer: function (d) {
            return Bloodhound.tokenizers.whitespace(d.num);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        local: [
            {num: 'one'},
            {num: 'two'},
            {num: 'three'},
            {num: 'four'},
            {num: 'five'},
            {num: 'six'},
            {num: 'seven'},
            {num: 'eight'},
            {num: 'nine'},
            {num: 'ten'}
        ]
    });

    numbers.initialize();

    $('#addcontact').typeahead(null, {
        name: 'example',
        displayKey: 'num',
        source: numbers.ttAdapter()
    });

    //validators
    validateName();
});

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
        'id': $('#id').val(),
        'name': $('#name').val()
    };

    $.ajax({
        type: 'POST',
        url: '/editgroup',
        data: JSON.stringify(formdata),
        contentType: 'application/json',
        success: function (data, textstatus, jqXHR) {
            //todo inform user
            console.log(jqXHR.status);
            console.log(textstatus);
            console.log(data);
        },
        error: function (jqXHR, textstatus, errorThrown) {
            //todo inform user
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
        }

    });
}

function submitDeleteReq(){
    var id = {'id':$('#groupId').val()};

    $.ajax({
        type:   'POST',
        url:    '/deletegroup',
        data:   JSON.stringify(id),
        contentType:'application/json',
        success: function (data) {
            //todo inform user
            console.log(data);
        },
        error: function (jqXHR, textstatus, errorThrown) {
            console.log(jqXHR.responseText);
            console.log(textstatus);
            console.log(errorThrown);
        }
    });
}