function FnCheckNumberOnPress_fr_FR(evt){
    return FnCheckNumberOnPressValue(evt,48,57);
}

function FnCheckNumber_fr_FR(event,min,max,objTextField) {
    return FnCheckNumberEnglish(event,min,max,objTextField.value)
}

function FnCheckNumCharsOnPress_fr_FR(evt) {
    var keyCodePress = (window.event)?event.keyCode:evt.which;
    if((keyCodePress==60) || (keyCodePress==62))
        return false;
    else
        return true;
}

function FnCheckNumChars_fr_FR(event,objTextField) {
    return true;
}
