function loadJSON() {
    var resturl = $('#resturl').val();
    $.ajax({
          url: resturl,
          dataType: 'json',
          success: function(data) {
                      $('#restdata').html('');
                      $('#restdata').html(JSON.stringify(data));
                      },
          error: function() {$('#restdata').html('failed');}
        });
}

function loadPrettyJSON() {
    var resturl = $('#resturl').val();
    $.ajax({
          url: resturl,
          dataType: 'json',
          success: function(data) {
                      var table = prettyPrint( data, { /*optional options object */ } );
                      $('#restdata').html('');
                      $('#restdata').append(table);
                      },
          error: function() {$('#restdata').html('failed');}
        });
}
