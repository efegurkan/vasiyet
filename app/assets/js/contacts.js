//Enable tabs
$('#tabs').find('a').click(function(e){
    e.preventDefault();
    $(this).tab('show');
});
