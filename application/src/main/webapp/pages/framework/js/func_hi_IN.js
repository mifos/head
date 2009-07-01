function hindiToAsci(char_code) {
	return convertorMap(char_code,2406,2415);
}

function FnCheckNumberOnPress_hi_IN(evt){
	return FnCheckNumberOnPressValue(evt,2406,2415);
}

function FnCheckNumber_hi_IN(event,min,max,objTextField) {
	var  value=objTextField.value;
	var asci_txt=genericConvertor(value,"hindiToAsci");
    return FnCheckNumberEnglish(event,min,max,asci_txt);
}

function FnCheckNumCharsOnPress_hi_IN(evt) {
}

function FnCheckNumChars_hi_IN(event,objTextField) {
}