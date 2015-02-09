$(document).ready(function () {

    var contacts = new Bloodhound({
        datumTokenizer: function (d) {
            return Bloodhound.tokenizers.whitespace(d.num);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url:'/contactsauto',
            filter:function(contacts){
                return $.map(contacts, function(contact){
                    return {
                        value: contact.id,
                        name:  contact.name + " " + contact.surname
                    };
                });
            },
            ajax:{
                type: 'POST'
            }
        }
    });

    contacts.initialize();

    $('#addcontact').typeahead(null, {
        name: 'contactsAuto',
        displayKey: 'name',
        source: contacts.ttAdapter()
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
        'id': $('#groupId').val(),
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